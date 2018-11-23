package org.poul.bits.android.broadcasts

import android.content.Context
import android.content.Intent

object BitsStatusErrorBroadcast {
    const val ACTION = "org.poul.bits.android.broadcasts.action.BITS_STATUS_ERROR"

    private fun getIntent() = Intent().apply {
        action = ACTION
    }

    fun localBroadcast(context: Context) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager
            .getInstance(context)
            .sendBroadcast(getIntent())
    }

    fun broadcast(context: Context) {
        context.sendBroadcast(getIntent())
    }
}