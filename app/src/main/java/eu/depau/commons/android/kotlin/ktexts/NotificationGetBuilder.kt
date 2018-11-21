package eu.depau.commons.android.kotlin.ktexts

import android.app.Notification
import android.content.Context
import android.os.Build

@Suppress("DEPRECATION")
fun Context.getNotificationBuilder(channel: String): Notification.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        Notification.Builder(this, channel)
    else
        Notification.Builder(this)
}