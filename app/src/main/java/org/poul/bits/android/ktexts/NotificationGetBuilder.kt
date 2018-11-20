package org.poul.bits.android.ktexts

import android.app.Notification
import android.content.Context
import android.os.Build

@Suppress("DEPRECATION")
fun getNotificationBuilder(context: Context, channel: String): Notification.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        Notification.Builder(context, channel)
    else
        Notification.Builder(context)
}