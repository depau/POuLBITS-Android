package org.poul.bits.android.ui.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.bold
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.br
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.color
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.esc
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.italic
import eu.depau.commons.android.kotlin.ktexts.getColorStateListCompat
import eu.depau.commons.android.kotlin.ktexts.round
import eu.depau.commons.android.kotlin.ktexts.snackbar
import eu.depau.commons.android.kotlin.ktexts.statusBarHeight
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.poul.bits.android.R
import org.poul.bits.android.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.controllers.appsettings.enum.TemperatureUnit
import org.poul.bits.android.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.misc.*
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.BitsSensorData
import org.poul.bits.android.model.enum.BitsSensorType
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.services.BitsRetrieveStatusService

const val PRESENCE_IMG_URL = "https://bits.poul.org/bits_presence.png"

class MainActivity : AppCompatActivity() {
    private val bitsDataIntentFilter = IntentFilter(BitsStatusReceivedBroadcast.ACTION)
    private val bitsErrorIntentFilter = IntentFilter(BitsStatusErrorBroadcast.ACTION)

    private lateinit var appSettings: IAppSettingsHelper

    private val bitsDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BitsStatusReceivedBroadcast.ACTION -> {
                    updateGuiWithStatusData(
                        intent.getParcelableExtra(BitsStatusReceivedBroadcast.BITS_DATA)
                    )
                    stopRefresh()
                }
                BitsStatusErrorBroadcast.ACTION    -> {
                    updateGuiError()
                    extended_fab.snackbar(getString(R.string.check_network_connection))
                    stopRefresh()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appSettings = AppSettingsHelper(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(left_constraintlayout)
            constraintSet.connect(
                R.id.toolbar,
                ConstraintSet.TOP,
                R.id.left_constraintlayout,
                ConstraintSet.TOP,
                if (!appSettings.fullscreen) statusBarHeight else 0
            )
            constraintSet.applyTo(left_constraintlayout)
        }


        status_card.visibility = View.GONE
        sensors_card.visibility = View.GONE
        message_card.visibility = View.GONE
        presence_card.visibility = View.GONE

        extended_fab.setOnClickListener {
            playGialla()
        }

        swiperefreshlayout.setProgressViewOffset(
            false,
            resources.getDimensionPixelSize(R.dimen.refresher_offset),
            resources.getDimensionPixelSize(R.dimen.refresher_offset_end)
        )

        swiperefreshlayout.setOnRefreshListener {
            startRefresh()
        }

        startRefresh()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (appSettings.fullscreen)
                hideSystemUI()
            else
                showSystemUI()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun startRefresh() {
        swiperefreshlayout.isRefreshing = true
        loadPresenceImage()
        BitsRetrieveStatusService.startActionRetrieveStatus(this)
    }

    fun stopRefresh() {
        swiperefreshlayout.isRefreshing = false
    }

    fun loadPresenceImage() {
        presence_card.visibility = View.GONE
        Picasso.get()
            .load(PRESENCE_IMG_URL)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(presence_card_imageview, object : com.squareup.picasso.Callback {
                override fun onSuccess() = onPresenceImageLoad()
                override fun onError(e: Exception?) = onPresenceImageLoadError(e)
            })
    }

    fun onPresenceImageLoad() {
        presence_card.visibility = View.VISIBLE
    }

    fun onPresenceImageLoadError(e: Exception? = null) {
        presence_card.visibility = View.GONE
    }

    fun updateGuiError() {
        message_card.visibility = View.GONE
        sensors_card.visibility = View.GONE
        status_card.visibility = View.VISIBLE

        status_card_textview.text = Html.fromHtml(
            italic(getString(R.string.status_retrieve_failure_card))
        )

        extended_fab.backgroundTintList = resources.getColorStateListCompat(R.color.colorHQsGialla, theme)
        extended_fab.text = getString(R.string.headquarters_gialla)
    }

    fun updateGuiWithStatusData(bitsData: BitsData) {
        extended_fab.text = getTextForStatus(bitsData.status)
        extended_fab.backgroundTintList = resources.getColorStateListCompat(getColorForStatus(bitsData.status), theme)

        updateStatusCardWithStatusData(bitsData)
        updateMessageCardWithMessage(bitsData.message)
        updateSensorCardWithTempData(bitsData.sensors)
    }

    fun updateStatusCardWithStatusData(bitsData: BitsData) {
        status_card.visibility = View.VISIBLE

        val text = getStatusCardText(this, bitsData)
        status_card_textview.text = text
    }

    private fun getSensorValueWithUserPreferredUnit(reading: BitsSensorData): Double = when (reading.type!!) {
        BitsSensorType.TEMPERATURE ->
            when (appSettings.temperatureUnit) {
                TemperatureUnit.CELSIUS    -> reading.value
                TemperatureUnit.FAHRENHEIT -> celsiusToFahrenheit(reading.value)
                TemperatureUnit.KELVIN     -> celsiusToKelvin(reading.value)
            }
        BitsSensorType.HUMIDITY    ->
            reading.value
    }

    private fun getUserPreferredUnitStringForSensorReading(reading: BitsSensorData): String = when (reading.type!!) {
        BitsSensorType.TEMPERATURE ->
            when (appSettings.temperatureUnit) {
                TemperatureUnit.CELSIUS    -> "°C"
                TemperatureUnit.FAHRENHEIT -> "°F"
                TemperatureUnit.KELVIN     -> "K"

            }
        BitsSensorType.HUMIDITY    ->
            "%"
    }

    @SuppressLint("SetTextI18n")
    fun updateSensorCardWithTempData(sensorData: List<BitsSensorData>) {
        sensors_card.visibility = if (sensorData.isEmpty()) View.GONE else View.VISIBLE

        sensorDataLoop@ for (reading in sensorData) {
            val view = when (reading.type) {
                BitsSensorType.TEMPERATURE -> temperature_textview
                BitsSensorType.HUMIDITY    -> humidity_textview
                null                       -> continue@sensorDataLoop
            }

            val value = getSensorValueWithUserPreferredUnit(reading).round(1)
            val unit = getUserPreferredUnitStringForSensorReading(reading)

            view.text = "$value$unit"
            view.visibility = View.VISIBLE
        }
    }

    fun updateMessageCardWithMessage(msgData: BitsMessage) {
        if (msgData.empty) {
            message_card.visibility = View.GONE
            return
        } else {
            message_card.visibility = View.VISIBLE
        }

        message_card_textview.text = getMessageCardText(this, msgData)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(bitsDataReceiver, bitsDataIntentFilter)
        registerReceiver(bitsDataReceiver, bitsErrorIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(bitsDataReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else                 -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getStatusCardText(context: Context, bitsData: BitsData): Spanned {
            val openedclosed = esc(
                context.getString(
                    when (bitsData.status) {
                        BitsStatus.OPEN   -> R.string.opened_from
                        BitsStatus.CLOSED -> R.string.closed_from
                        else              -> R.string.headquarters_gialla
                    }
                )
            )

            val changedTime = esc(
                DateUtils.getRelativeTimeSpanString(
                    bitsData.lastModified.time,
                    System.currentTimeMillis(),
                    0L,
                    DateUtils.FORMAT_ABBREV_ALL
                ) as String
            )

            return Html.fromHtml(
                "$openedclosed ${bold(color(context, esc(bitsData.modifiedBy), R.color.colorAccent))}<br>" +
                        "${context.getString(R.string.last_changed)} ${bold(
                            color(
                                context,
                                changedTime,
                                R.color.colorAccent
                            )
                        )}"
            )!!
        }

        fun getMessageCardText(context: Context, bitsData: BitsMessage): Spanned {
            val sentTime = esc(
                DateUtils.getRelativeTimeSpanString(
                    bitsData.lastModified.time,
                    System.currentTimeMillis(),
                    0L,
                    DateUtils.FORMAT_ABBREV_ALL
                ) as String
            )

            return Html.fromHtml(
                "${context.getString(R.string.last_msg_from)} ${bold(
                    color(
                        context,
                        esc(bitsData.user),
                        R.color.colorAccent
                    )
                )}, ${bold(
                    color(
                        context,
                        esc(sentTime),
                        R.color.colorAccent
                    )
                )} $br" +
                        italic("“${esc(bitsData.message)}”")
            )!!
        }
    }
}
