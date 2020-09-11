package org.poul.bits.wearos.tiles

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.poul.bits.android.lib.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusRetrieveStartBroadcast
import org.poul.bits.android.lib.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.lib.model.BitsData

class TileUpdateBroadcastReceiver : BroadcastReceiver() {
    private val LOG_TAG = "TileUpdBcsRecvr"

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BitsStatusRetrieveStartBroadcast.ACTION -> handleStatusRetrieveStartBroadcast(context)
            BitsStatusReceivedBroadcast.ACTION -> handleStatusReceivedBroadcast(context, intent)
            BitsStatusErrorBroadcast.ACTION -> handleStatusErrorBroadcast(context)
        }
    }

    private fun requestTileUpdate(context: Context) {
        // Run shim service because BroadcastReceivers cannot bind to services
        Log.d(LOG_TAG, "Starting tile update service")
        TileUpdateIntentService.doRequestTileUpdate(context)
    }

    private fun handleStatusRetrieveStartBroadcast(context: Context) {
        SharedPrefsWidgetStorageHelper(context).apply {
            loading = true
        }
        requestTileUpdate(context)
    }

    private fun handleStatusReceivedBroadcast(context: Context, intent: Intent) {
        val bitsData = intent.getParcelableExtra<BitsData>(
            BitsStatusReceivedBroadcast.BITS_DATA
        )!!
        SharedPrefsWidgetStorageHelper(context).apply {
            this.loading = false
            this.bitsData = bitsData
            this.lastDataUpdate = System.currentTimeMillis()
        }
        requestTileUpdate(context)
    }

    private fun handleStatusErrorBroadcast(context: Context) {
        SharedPrefsWidgetStorageHelper(context).apply {
            loading = false
            bitsData = bitsDataError
        }
    }
}
