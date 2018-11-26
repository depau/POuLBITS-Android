package org.poul.bits.android.addons.mqtt.impl

import android.content.Context
import org.poul.bits.android.addons.mqtt.IMQTTServiceHelper

class FakeMQTTServiceHelper : IMQTTServiceHelper {
    override val hasMQTTService: Boolean = false

    override fun startService(context: Context) {}

    override fun stopService(context: Context) {}
}