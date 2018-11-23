package org.poul.bits.android.broadcasts

import android.content.Intent

object BitsStatusErrorBroadcast: AbstractBroadcastHelper() {
    override val ACTION = "org.poul.bits.android.broadcasts.action.BITS_STATUS_ERROR"

    override fun getIntent() = Intent().apply {
        action = ACTION
    }
}