package org.poul.bits.android.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.poul.bits.R
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.misc.getColorForStatus
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.misc.playGialla
import org.poul.bits.android.model.BitsData
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
        extended_fab.backgroundTintList = resources.getColorStateList(getColorForStatus(bitsData.status))
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
