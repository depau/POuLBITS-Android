package org.poul.bits.wearos.complications

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.wearable.complications.ProviderUpdateRequester
import org.poul.bits.android.lib.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.lib.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.lib.model.BitsData

class ComplicationUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BitsStatusReceivedBroadcast.ACTION -> handleStatusReceivedBroadcast(
                context,
                intent
            )
            BitsStatusErrorBroadcast.ACTION -> handleStatusErrorBroadcast(context)
            else                               -> return
        }
        requestComplicationsUpdate(context)
    }

    private fun requestComplicationsUpdate(context: Context) {
        ProviderUpdateRequester(
            context,
            ComponentName(context, PoulBitsComplicationProviderService::class.java)
        ).requestUpdateAll()
    }

    private fun handleStatusReceivedBroadcast(context: Context, intent: Intent) {
        val bitsData = intent.getParcelableExtra<BitsData>(
            BitsStatusReceivedBroadcast.BITS_DATA
        )!!
        SharedPrefsWidgetStorageHelper(context).apply {
            this.bitsData = bitsData
            this.lastDataUpdate = System.currentTimeMillis()
        }
    }

    private fun handleStatusErrorBroadcast(context: Context) {
        SharedPrefsWidgetStorageHelper(context).apply { bitsData = bitsDataError }
    }
}