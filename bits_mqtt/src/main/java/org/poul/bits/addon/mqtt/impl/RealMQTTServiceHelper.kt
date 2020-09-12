package org.poul.bits.addon.mqtt.impl

import android.content.Context
import android.content.Intent
import eu.depau.kotlet.android.extensions.ui.context.startForegroundServiceCompat
import org.poul.bits.addon.mqtt.Constants
import org.poul.bits.android.lib.mqtt_stub.IMQTTServiceHelper

class RealMQTTServiceHelper() : IMQTTServiceHelper {
    override val hasMQTTService: Boolean = true

    private fun getIntent(context: Context): Intent {
        val mqttClass = Class.forName(Constants.SERVICE_CLASS)

        return Intent(context, mqttClass).apply {
            action = Constants.ACTION_START
        }
    }

    override fun startService(context: Context) {
        context.startForegroundServiceCompat(getIntent(context))
    }

    override fun stopService(context: Context) {
        context.stopService(getIntent(context))
    }
}