package org.poul.bits.android.broadcasts

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import org.poul.bits.android.model.BitsData

object BitsStatusReceivedBroadcast {
    const val ACTION = "org.poul.bits.android.broadcasts.action.BITS_STATUS_RECEIVED"
    const val BITS_DATA = "data"

    private fun getIntent(data: BitsData) = Intent().also { intent ->
        intent.action = ACTION
        intent.putExtra(BITS_DATA, data)
    }

    fun localBroadcast(context: Context, data: BitsData) {
        LocalBroadcastManager
            .getInstance(context)
            .sendBroadcast(getIntent(data))
    }

    fun broadcast(context: Context, data: BitsData) {
        context.sendBroadcast(getIntent(data))
    }
}