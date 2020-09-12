package org.poul.bits.android.lib.mqtt_stub

import android.content.Context

interface IMQTTServiceHelper {
    val hasMQTTService: Boolean

    fun startService(context: Context)
    fun stopService(context: Context)
}