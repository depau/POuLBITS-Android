package eu.depau.commons.android.kotlin.ktexts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build

fun Context.startForegroundServiceCompat(intent: Intent): ComponentName? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}