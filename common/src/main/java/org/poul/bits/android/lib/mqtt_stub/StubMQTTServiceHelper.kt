package org.poul.bits.android.lib.mqtt_stub

import android.content.Context

class StubMQTTServiceHelper : IMQTTServiceHelper {
    override val hasMQTTService: Boolean = false

    override fun startService(context: Context) {}

    override fun stopService(context: Context) {}
}