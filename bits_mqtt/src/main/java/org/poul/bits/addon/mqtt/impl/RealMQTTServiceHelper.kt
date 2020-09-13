package org.poul.bits.addon.mqtt.impl

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import eu.depau.kotlet.android.extensions.ui.context.startForegroundServiceCompat
import org.poul.bits.addon.mqtt.Constants
import org.poul.bits.addon.mqtt.services.MQTTService
import org.poul.bits.android.lib.mqtt_stub.IMQTTServiceHelper

class RealMQTTServiceHelper() : IMQTTServiceHelper {
    override val hasMQTTService: Boolean = true

    private fun getIntent(context: Context): Intent {
        return Intent(context, MQTTService::class.java).apply {
            action = Constants.ACTION_START
        }
    }

    override fun startService(context: Context) {
        MQTTService.ensureService(context)
    }

    override fun stopService(context: Context) {
        context.stopService(getIntent(context))
    }

    companion object {
        private const val JOB_ID = 3932
    }
}