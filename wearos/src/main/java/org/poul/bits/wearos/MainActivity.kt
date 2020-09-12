package org.poul.bits.wearos

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import eu.depau.kotlet.android.extensions.ui.context.toast
import eu.depau.kotlet.android.extensions.ui.view.snackbar
import eu.depau.kotlet.extensions.builtins.round
import kotlinx.android.synthetic.main.activity_main.*
import org.poul.bits.android.lib.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.lib.controllers.appsettings.enum.TemperatureUnit
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.lib.misc.*
import org.poul.bits.android.lib.model.BitsData
import org.poul.bits.android.lib.model.BitsMessage
import org.poul.bits.android.lib.model.BitsSensorData
import org.poul.bits.android.lib.model.enum.BitsDataSource
import org.poul.bits.android.lib.model.enum.BitsSensorType
import org.poul.bits.android.lib.model.enum.BitsStatus
import org.poul.bits.android.lib.mqtt_stub.MQTTHelperFactory
import org.poul.bits.android.lib.mqtt_stub.StubMQTTServiceHelper
import org.poul.bits.android.lib.services.BitsRetrieveStatusService
import org.poul.bits.android.lib.misc.SimpleHtml as html

class MainActivity : WearableActivity() {

    private val bitsDataIntentFilter = IntentFilter(BitsStatusReceivedBroadcast.ACTION)
    private val bitsErrorIntentFilter = IntentFilter(BitsStatusErrorBroadcast.ACTION)

    private lateinit var appSettings: IAppSettingsHelper

    private val bitsDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BitsStatusReceivedBroadcast.ACTION -> {
                    updateGuiWithStatusData(intent.getParcelableExtra(BitsStatusReceivedBroadcast.BITS_DATA)!!)
                    stopRefresh()
                }
                BitsStatusErrorBroadcast.ACTION -> {
                    updateGuiError()
                    toast(getString(R.string.check_network_connection), Toast.LENGTH_SHORT)
                    stopRefresh()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appSettings = AppSettingsHelper(this).apply {
            migrate()
        }
        setAmbientEnabled()
        setContentView(R.layout.activity_main)

        status_card.visibility = View.GONE
        sensors_card.visibility = View.GONE
        message_card.visibility = View.GONE

        status_button.setOnClickListener {
            playGialla()
        }

        settings_button.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        refresh_button.setOnClickListener {
            startRefresh()
        }

        swiperefreshlayout.setOnRefreshListener {
            startRefresh()
        }

        startRefresh()
    }

    fun startRefresh() {
        swiperefreshlayout.isRefreshing = true
        BitsRetrieveStatusService.startActionRetrieveStatus(this)
    }

    fun stopRefresh() {
        swiperefreshlayout.isRefreshing = false
    }


    fun updateGuiError() {
        message_card.visibility = View.GONE
        sensors_card.visibility = View.GONE
        status_card.visibility = View.VISIBLE

        status_card.text = Html.fromHtml(
            html.italic(getString(R.string.status_retrieve_failure_card))
        )

        status_button.setBackgroundColor(resources.getColor(R.color.colorHQsGialla, theme))
        status_button.text = getString(R.string.headquarters_gialla)
    }

    fun updateGuiWithStatusData(bitsData: BitsData) {
        if (bitsData.status != null) {
            status_button.text = getTextForStatus(bitsData.status!!)
            status_button.setBackgroundColor(
                resources.getColor(
                    getColorForStatus(bitsData.status!!),
                    theme
                )
            )

            if (bitsData.source != BitsDataSource.MQTT)
                updateStatusCardWithStatusData(bitsData)
        }

        if (bitsData.source != BitsDataSource.MQTT)
            updateMessageCardWithMessage(bitsData)

        updateSensorCardWithTempData(bitsData)

        // Refresh because MQTT data doesn't have a lot of fields
        if (bitsData.source == BitsDataSource.MQTT)
            startRefresh()
    }

    fun updateStatusCardWithStatusData(bitsData: BitsData) {
        status_card.visibility = View.VISIBLE

        if (bitsData.lastModified != null) {
            val text = getStatusCardText(this, bitsData)
            status_card.text = text
        }
    }

    private fun getSensorValueWithUserPreferredUnit(reading: BitsSensorData): Double =
        when (reading.type!!) {
            BitsSensorType.TEMPERATURE ->
                when (appSettings.temperatureUnit) {
                    TemperatureUnit.CELSIUS    -> reading.value
                    TemperatureUnit.FAHRENHEIT -> celsiusToFahrenheit(reading.value)
                    TemperatureUnit.KELVIN     -> celsiusToKelvin(reading.value)
                }
            BitsSensorType.HUMIDITY ->
                reading.value
        }

    private fun getUserPreferredUnitStringForSensorReading(reading: BitsSensorData): String =
        when (reading.type!!) {
            BitsSensorType.TEMPERATURE ->
                when (appSettings.temperatureUnit) {
                    TemperatureUnit.CELSIUS    -> "°C"
                    TemperatureUnit.FAHRENHEIT -> "°F"
                    TemperatureUnit.KELVIN     -> "K"

                }
            BitsSensorType.HUMIDITY ->
                "%"
        }

    @SuppressLint("SetTextI18n")
    fun updateSensorCardWithTempData(bitsData: BitsData) {
        val sensorData = bitsData.sensors ?: return

        sensors_card.visibility = if (sensorData.isEmpty()) View.GONE else View.VISIBLE

        sensorDataLoop@ for (reading in sensorData) {
            val view = when (reading.type) {
                BitsSensorType.TEMPERATURE -> temperature_textview
                BitsSensorType.HUMIDITY -> humidity_textview
                null -> continue@sensorDataLoop
            }

            val value = getSensorValueWithUserPreferredUnit(reading).round(1)
            val unit = getUserPreferredUnitStringForSensorReading(reading)

            view.text = "$value$unit"
            view.visibility = View.VISIBLE
        }
    }

    fun updateMessageCardWithMessage(bitsData: BitsData) {
        val msgData = bitsData.message ?: return

        if (msgData.empty) {
            message_card.visibility = View.GONE
            return
        } else {
            message_card.visibility = View.VISIBLE
        }

        message_card.text = getMessageCardText(this, msgData)
    }

    fun optionallyStartStopMqttService() {
        if (!appSettings.mqttEnabled)
            return stopMqttService()

        val mqttHelper = MQTTHelperFactory.getMqttHelper(appSettings)
        if (mqttHelper is StubMQTTServiceHelper) {
            Log.w(
                "MainActivity",
                "Stub MQTT service is in use and you're running the MQTT build flavor"
            )
        } else {
            Log.d("MainActivity", "Using proper MQTT service helper")
        }

        mqttHelper.startService(this)
    }

    fun stopMqttService() {
        MQTTHelperFactory.getMqttHelper(appSettings).stopService(this)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(bitsDataReceiver, bitsDataIntentFilter)
        registerReceiver(bitsDataReceiver, bitsErrorIntentFilter)
        optionallyStartStopMqttService()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(bitsDataReceiver)
        stopMqttService()
    }

    companion object {
        fun getStatusCardText(context: Context, bitsData: BitsData): Spanned? {
            bitsData.lastModified ?: return null
            bitsData.modifiedBy ?: return null

            val openedclosed = html.esc(
                context.getString(
                    when (bitsData.status) {
                        BitsStatus.OPEN   -> R.string.opened_from
                        BitsStatus.CLOSED -> R.string.closed_from
                        else              -> R.string.headquarters_gialla
                    }
                )
            )

            val changedTime = html.esc(
                DateUtils.getRelativeTimeSpanString(
                    bitsData.lastModified!!.time,
                    System.currentTimeMillis(),
                    0L,
                    DateUtils.FORMAT_ABBREV_ALL
                ) as String
            )

            return Html.fromHtml(
                "$openedclosed ${
                    html.bold(
                        html.color(
                            context,
                            html.esc(bitsData.modifiedBy!!),
                            R.color.colorAccent
                        )
                    )
                }<br>" +
                        "${context.getString(R.string.last_changed)} ${
                            html.bold(
                                html.color(
                                    context,
                                    changedTime,
                                    R.color.colorAccent
                                )
                            )
                        }"
            )!!
        }

        fun getMessageCardText(
            context: Context,
            bitsData: BitsMessage,
            maxMessageChars: Int? = null
        ): Spanned {
            val message =
                if (maxMessageChars == null || bitsData.message.length < maxMessageChars) {
                    bitsData.message
                } else {
                    bitsData.message.slice(0 until maxMessageChars) + "…"
                }

            val sentTime = html.esc(
                DateUtils.getRelativeTimeSpanString(
                    bitsData.lastModified.time,
                    System.currentTimeMillis(),
                    0L,
                    DateUtils.FORMAT_ABBREV_ALL
                ) as String
            )

            return Html.fromHtml(
                "${context.getString(R.string.last_msg_from)} ${
                    html.bold(
                        html.color(
                            context,
                            html.esc(bitsData.user),
                            R.color.colorAccent
                        )
                    )
                }, ${
                    html.bold(
                        html.color(
                            context,
                            html.esc(sentTime),
                            R.color.colorAccent
                        )
                    )
                } ${html.br}" +
                        html.italic("“${html.esc(message)}”")
            )!!
        }
    }
}