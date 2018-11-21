package eu.depau.commons.android.kotlin.ktexts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build

fun Context.registerNotificationChannel(
    channelId: String,
    name: String,
    description: String?,
    importance: Int? = null
): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(
                channelId,
                name,
                importance ?: NotificationManager.IMPORTANCE_DEFAULT
            ).also {
                if (description != null)
                    it.description = description
            }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    return channelId
}
