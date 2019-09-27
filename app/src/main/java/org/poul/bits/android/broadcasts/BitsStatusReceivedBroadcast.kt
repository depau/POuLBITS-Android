package org.poul.bits.android.broadcasts

import android.content.Context
import android.content.Intent
import org.poul.bits.android.model.BitsData

object BitsStatusReceivedBroadcast {
    const val ACTION = "org.poul.bits.android.broadcasts.action.BITS_STATUS_RECEIVED"
    const val BITS_DATA = "data"
    const val BITS_PRESENCE_SVG = "presence_svg"

    private fun getIntent(data: BitsData, presenceSvg: String? = null) = Intent().also { intent ->
        intent.action = ACTION
        intent.putExtra(BITS_DATA, data)
        intent.putExtra(BITS_PRESENCE_SVG, presenceSvg)
    }

    fun localBroadcast(context: Context, data: BitsData) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager
            .getInstance(context)
            .sendBroadcast(getIntent(data))
    }

    fun broadcast(context: Context, data: BitsData, presenceSvg: String? = null) {
        context.sendBroadcast(getIntent(data, presenceSvg).apply {
            `package` = context.packageName
        })
    }
}