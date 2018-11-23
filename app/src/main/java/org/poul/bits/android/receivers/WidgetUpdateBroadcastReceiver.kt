package org.poul.bits.android.receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.broadcasts.BitsStatusRetrieveStartBroadcast
import org.poul.bits.android.misc.WidgetSharedPrefsHelper
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.ui.widgets.HeadquartersStatusHorizontalWidget

class WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BitsStatusRetrieveStartBroadcast.ACTION -> handleStatusRetrieveStartBroadcast(context)
            BitsStatusReceivedBroadcast.ACTION -> handleStatusReceivedBroadcast(context, intent)
        }
    }

    private fun getWidgetUpdateIntent(context: Context): Intent {
        return Intent(context, HeadquartersStatusHorizontalWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
    }

    private fun requestWidgetUpdate(context: Context) {
        context.sendBroadcast(getWidgetUpdateIntent(context))
    }

    private fun handleStatusRetrieveStartBroadcast(context: Context) {
        WidgetSharedPrefsHelper(context).apply {
            loading = true
        }
        requestWidgetUpdate(context)
    }

    private fun handleStatusReceivedBroadcast(context: Context, intent: Intent) {
        val bitsData = intent.getParcelableExtra<BitsData>(
            BitsStatusReceivedBroadcast.BITS_DATA
        )
        WidgetSharedPrefsHelper(context).apply {
            this.loading = false
            this.bitsData = bitsData
        }
        requestWidgetUpdate(context)
    }
}
