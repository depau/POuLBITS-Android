package org.poul.bits.addon.mqtt.services

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import eu.depau.kotlet.android.extensions.notification.buildCompat
import eu.depau.kotlet.android.extensions.ui.context.getNotificationBuilder
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.poul.bits.addon.mqtt.Constants.ACTION_START
import org.poul.bits.addon.mqtt.R
import org.poul.bits.addon.mqtt.services.dto.BitsMQTTSedeMessage
import org.poul.bits.android.lib.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.lib.model.BitsData
import org.poul.bits.android.lib.model.BitsSensorData
import org.poul.bits.android.lib.model.enum.BitsDataSource
import org.poul.bits.android.lib.model.enum.BitsSensorType
import org.poul.bits.android.lib.model.enum.BitsStatus
import org.poul.bits.android.lib.services.CHANNEL_BITS_RETRIEVE_STATUS
import java.util.*

private const val FOREGROUND_MQTT_SERVICE_ID = 5919
private const val LOG_TAG = "MQTTService"

class MQTTService : IntentService("MQTTService"), MqttCallback, IMqttActionListener {
    private var shouldStop: Boolean = false
    private lateinit var appSettings: IAppSettingsHelper
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        appSettings = AppSettingsHelper(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
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

    private fun getMQTT(): MqttAndroidClient {
        val broker = when (appSettings.mqttUseTls) {
            true -> "tls://${appSettings.mqttHostname}:${appSettings.mqttPort}"
            else -> "tcp://${appSettings.mqttHostname}:${appSettings.mqttPort}"
        }
        val clientId = "bits_android_client"

        return MqttAndroidClient(this, broker, clientId, MemoryPersistence())
    }

    private fun MqttAndroidClient.subscribeTopics() {
        val topics = arrayOf(
            appSettings.mqttSedeTopic,
            appSettings.mqttTempTopic,
            appSettings.mqttHumTopic
        )

        val qos = arrayOf(
            1, 1, 1
        ).toIntArray()

        this.subscribe(topics, qos)
    }

    private fun mqttMessageToBitsData(msg: BitsMQTTSedeMessage) = BitsData(
        when (msg.status) {
            "open" -> BitsStatus.OPEN
            "closed" -> BitsStatus.CLOSED
            else -> null
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

    private fun handleStatusMessage(message: MqttMessage) {
        try {
            val statusMessage =
                gson.fromJson(String(message.payload), BitsMQTTSedeMessage::class.java)

            // Ugly hack to wait for the HTTP server to get in sync
            Thread.sleep(500)

            BitsStatusReceivedBroadcast.broadcast(
                this, mqttMessageToBitsData(statusMessage)
            )

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing MQTT status JSON: ${String(message.payload)}", e)
        }
    }

    private fun handleSensorMessage(message: MqttMessage, sensorType: BitsSensorType) {
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

        mqtt.apply {
            setCallback(this@MQTTService)
            connect(
                MqttConnectOptions().apply {
                    isCleanSession = false
                    isAutomaticReconnect = true
                },
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        mqtt.subscribeTopics()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w(LOG_TAG, "Connection to MQTT broker failed", exception)
                    }
                })
        }

        while (!shouldStop)
            Thread.sleep(300)

        mqtt.disconnect()
        Log.i(LOG_TAG, "MQTT service stopped")
        stopForeground(true)
    }

    override fun messageArrived(topic: String, message: MqttMessage) {
        Log.d(LOG_TAG, "Incoming MQTT message from $topic: '${String(message.payload)}'")

        when (topic) {
            appSettings.mqttSedeTopic -> handleStatusMessage(message)
            appSettings.mqttTempTopic -> handleSensorMessage(message, BitsSensorType.TEMPERATURE)
            appSettings.mqttHumTopic -> handleSensorMessage(message, BitsSensorType.HUMIDITY)
        }
    }

    override fun connectionLost(cause: Throwable?) {
        if (!shouldStop) {
            Log.w(LOG_TAG, "MQTT connection lost, attempting reconnection in 1 second", cause)
            Thread.sleep(1000)
        }
        if (!shouldStop)
            handleActionStart()
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    override fun onDestroy() {
        shouldStop = true
        super.onDestroy()
    }

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        TODO("Not yet implemented")
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        TODO("Not yet implemented")
    }

}
