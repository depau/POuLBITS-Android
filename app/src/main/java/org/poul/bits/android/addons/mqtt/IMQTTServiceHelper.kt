package org.poul.bits.android.addons.mqtt

import android.content.Context

interface IMQTTServiceHelper {
    val hasMQTTService: Boolean

    fun startService(context: Context)
    fun stopService(context: Context)
}