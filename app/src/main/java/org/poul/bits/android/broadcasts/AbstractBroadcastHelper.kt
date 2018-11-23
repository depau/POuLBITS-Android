package org.poul.bits.android.broadcasts

import android.content.Context
import android.content.Intent

abstract class AbstractBroadcastHelper {
    abstract val ACTION: String
    internal abstract fun getIntent(): Intent

    fun localBroadcast(context: Context) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager
            .getInstance(context)
            .sendBroadcast(getIntent())
    }

    fun broadcast(context: Context) {
        context.sendBroadcast(getIntent())
    }
}