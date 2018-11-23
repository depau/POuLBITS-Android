package org.poul.bits.android.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.bold
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.br
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.color
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.esc
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.italic
import eu.depau.commons.android.kotlin.ktexts.getColorStateListCompat
import eu.depau.commons.android.kotlin.ktexts.snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.poul.bits.android.R
import org.poul.bits.android.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.misc.getColorForStatus
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.misc.playGialla
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.services.BitsRetrieveStatusService

const val PRESENCE_IMG_URL = "https://bits.poul.org/bits_presence.png"

class MainActivity : AppCompatActivity() {

    private val bitsDataIntentFilter = IntentFilter(BitsStatusReceivedBroadcast.ACTION)
    private val bitsErrorIntentFilter = IntentFilter(BitsStatusErrorBroadcast.ACTION)

    private val bitsDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BitsStatusReceivedBroadcast.ACTION -> {
                    updateGuiWithStatusData(
                        intent.getParcelableExtra(BitsStatusReceivedBroadcast.BITS_DATA)
                    )
                    stopRefresh()
                }
                BitsStatusErrorBroadcast.ACTION -> {
                    updateGuiError()
                    extended_fab.snackbar(getString(R.string.check_network_connection))
                    stopRefresh()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        status_card.visibility = View.GONE
        message_card.visibility = View.GONE
        presence_card.visibility = View.GONE

        extended_fab.setOnClickListener { view ->
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
    }

    fun updateStatusCardWithStatusData(bitsData: BitsData) {
        status_card.visibility = View.VISIBLE

        val text = getStatusCardText(this, bitsData)
        status_card_textview.text = text
    }

    fun updateMessageCardWithMessage(bitsData: BitsMessage) {
        val messageEmpty = bitsData.message.trim().isEmpty()

        if (messageEmpty) {
            message_card.visibility = View.GONE
            return
        } else {
            message_card.visibility = View.VISIBLE
        }

        message_card_textview.text = getMessageCardText(this, bitsData)
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getStatusCardText(context: Context, bitsData: BitsData): Spanned {
            val openedclosed = esc(
                context.getString(
                    when (bitsData.status) {
                        BitsStatus.OPEN -> R.string.opened_from
                        BitsStatus.CLOSED -> R.string.closed_from
                        else -> R.string.headquarters_gialla
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
