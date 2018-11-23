package org.poul.bits.android.broadcasts

import android.content.Intent

object BitsStatusRetrieveStartBroadcast : AbstractBroadcastHelper() {
    override val ACTION = "org.poul.bits.android.broadcasts.action.BITS_STATUS_RETRIEVE_START"

    override fun getIntent() = Intent().apply {
        action = ACTION
    }
}