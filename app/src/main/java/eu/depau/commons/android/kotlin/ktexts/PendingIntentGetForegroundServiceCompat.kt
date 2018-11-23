package eu.depau.commons.android.kotlin.ktexts

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

fun PendingIntentGetForegroundServiceCompat(
    context: Context,
    requestCode: Int,
    intent: Intent,
    flags: Int
): PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    PendingIntent.getForegroundService(context, requestCode, intent, flags)
} else {
    PendingIntent.getService(context, requestCode, intent, flags)
}