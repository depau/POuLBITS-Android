package org.poul.bits.android.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.accent
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.bold
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.br
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.esc
import eu.depau.commons.android.kotlin.ktexts.SimpleHtml.italic
import eu.depau.commons.android.kotlin.ktexts.getColorStateListCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.poul.bits.R
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.misc.getColorForStatus
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.misc.playGialla
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.services.BitsRetrieveStatusService


class MainActivity : AppCompatActivity() {

    private val bitsDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BitsStatusReceivedBroadcast.ACTION -> {
                    updateGuiWithStatusData(
                        intent.getParcelableExtra(BitsStatusReceivedBroadcast.BITS_DATA)
                    )
                    stopRefresh()
                }
            }
        }
    }
    private val bitsDataIntentFilter = IntentFilter(BitsStatusReceivedBroadcast.ACTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        status_card.visibility = View.GONE
        message_card.visibility = View.GONE

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
        BitsRetrieveStatusService.startActionRetrieveStatus(this)
    }

    fun stopRefresh() {
        swiperefreshlayout.isRefreshing = false
    }

    fun updateGuiWithStatusData(bitsData: BitsData) {
        extended_fab.text = getTextForStatus(bitsData.status)
        extended_fab.backgroundTintList = resources.getColorStateListCompat(getColorForStatus(bitsData.status), theme)

        updateStatusCardWithStatusData(bitsData)
        updateMessageCardWithMessage(bitsData.message)
    }

    fun updateStatusCardWithStatusData(bitsData: BitsData) {
        status_card.visibility = View.VISIBLE

        val openedclosed = esc(
            getString(
                when (bitsData.status) {
                    BitsStatus.OPEN -> R.string.opened_by
                    BitsStatus.CLOSED -> R.string.closed_by
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

        status_card_textview.text = Html.fromHtml(
            "$openedclosed ${bold(accent(esc(bitsData.modifiedBy)))}<br>" +
                    "${getString(R.string.last_changed)} ${bold(accent(changedTime))}"
        )
    }

    fun updateMessageCardWithMessage(bitsData: BitsMessage) {
        val messageEmpty = bitsData.message.trim().isEmpty()

        if (messageEmpty) {
            message_card.visibility = View.GONE
            return
        } else {
            message_card.visibility = View.VISIBLE
        }

        val sentTime = esc(DateUtils.getRelativeTimeSpanString(
                bitsData.lastModified.time,
                System.currentTimeMillis(),
                0L,
                DateUtils.FORMAT_ABBREV_ALL
            ) as String
        )

        message_card_textview.text = Html.fromHtml(
            "${getString(R.string.last_msg_from)} ${bold(accent(esc(bitsData.user)))}, ${bold(accent(sentTime))} $br" +
                    italic("“${esc(bitsData.message)}”")
        )
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(bitsDataReceiver, bitsDataIntentFilter)
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
}
