package org.poul.bits.android.services

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import eu.depau.commons.android.kotlin.ktexts.buildCompat
import eu.depau.commons.android.kotlin.ktexts.getNotificationBuilder
import org.fusesource.mqtt.client.*
import org.poul.bits.android.R
import org.poul.bits.android.addons.mqtt.Constants.ACTION_START
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsSensorData
import org.poul.bits.android.model.enum.BitsDataSource
import org.poul.bits.android.model.enum.BitsSensorType
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.services.dto.BitsMQTTSedeMessage
import java.util.*
import java.util.concurrent.TimeUnit

private const val FOREGROUND_MQTT_SERVICE_ID = 5919
private const val LOG_TAG = "MQTTService"

class MQTTService : IntentService("MQTTService") {
    private var shouldStop: Boolean = false
    private lateinit var appSettings: IAppSettingsHelper
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        appSettings = AppSettingsHelper(this)
    }

    override fun onHandleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_START -> {
                handleActionStart()
            }
        }
    }

    private fun getForegroundNotification(): Notification {
        return getNotificationBuilder(CHANNEL_BITS_RETRIEVE_STATUS)
            .setContentTitle(getString(R.string.mqtt_service_running))
            .setContentText(getString(R.string.mqtt_notification_desc))
            .buildCompat()
    }

    private fun getMQTT() = MQTT().apply {
        if (appSettings.mqttUseTls)
            setHost("tls://${appSettings.mqttHostname}:${appSettings.mqttPort}")
        else
            setHost(appSettings.mqttHostname, appSettings.mqttPort)

        setClientId("bits_android_client")
        isCleanSession = false
    }

    private fun subscribeTopics(connection: BlockingConnection): BlockingConnection {
        val topics = arrayOf(
            Topic(appSettings.mqttSedeTopic, QoS.AT_LEAST_ONCE),
            Topic(appSettings.mqttTempTopic, QoS.AT_LEAST_ONCE),
            Topic(appSettings.mqttHumTopic, QoS.AT_LEAST_ONCE)
        )
        return connection.apply { subscribe(topics) }
    }

    private fun handleMessage(connection: BlockingConnection) {
        val message: Message = connection.receive(300, TimeUnit.MILLISECONDS) ?: return

        Log.d(LOG_TAG, "Incoming MQTT message from ${message.topic}: '${String(message.payload)}'")

        when (message.topic) {
            appSettings.mqttSedeTopic -> handleStatusMessage(message)
            appSettings.mqttTempTopic -> handleSensorMessage(message, BitsSensorType.TEMPERATURE)
            appSettings.mqttHumTopic  -> handleSensorMessage(message, BitsSensorType.HUMIDITY)
        }
    }

    private fun mqttMessageToBitsData(msg: BitsMQTTSedeMessage) = BitsData(
        when (msg.status) {
            "open"   -> BitsStatus.OPEN
            "closed" -> BitsStatus.CLOSED
            else     -> null
        },
        null,
        Date(),
        null,
        null,
        null,
        BitsDataSource.MQTT
    )

    private fun mqttSensorMessageToBitsData(value: Double, sensorType: BitsSensorType): BitsData =
        BitsData(
            null,
            null,
            null,
            listOf(
                BitsSensorData(
                    value,
                    sensorType.sensorId.toLong(),
                    "BITS",
                    Date(),
                    sensorType
                )
            ),
            null,
            null,
            BitsDataSource.MQTT
        )

    private fun handleStatusMessage(message: Message) {
        try {
            val statusMessage = gson.fromJson(String(message.payload), BitsMQTTSedeMessage::class.java)

            // Ugly hack to wait for the HTTP server to get in sync
            Thread.sleep(500)

            BitsStatusReceivedBroadcast.broadcast(
                this, mqttMessageToBitsData(statusMessage)
            )

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing MQTT status JSON: ${String(message.payload)}", e)
        }
    }

    private fun handleSensorMessage(message: Message, sensorType: BitsSensorType) {
        try {
            val value = String(message.payload).toDouble()

            BitsStatusReceivedBroadcast.broadcast(
                this, mqttSensorMessageToBitsData(value, sensorType)
            )

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing MQTT sensor data: ${String(message.payload)}", e)
        }
    }

    private fun handleActionStart() {
        startForeground(FOREGROUND_MQTT_SERVICE_ID, getForegroundNotification())

        Log.i(LOG_TAG, "MQTT service started")

        val mqtt = getMQTT()
        val connection = mqtt.blockingConnection()
        connection.connect()
        subscribeTopics(connection)

        while (!shouldStop)
            handleMessage(connection)

        connection.disconnect()
        Log.i(LOG_TAG, "MQTT service stopped")
        stopForeground(true)
    }

    override fun onDestroy() {
        shouldStop = true
        super.onDestroy()
    }
}
