package org.poul.bits.addon.mqtt.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import eu.depau.kotlet.android.extensions.ui.context.startForegroundServiceCompat
import org.poul.bits.addon.mqtt.Constants
import org.poul.bits.addon.mqtt.Constants.ACTION_START
import org.poul.bits.addon.mqtt.receivers.MQTTBootstrapBroadcastReceiver
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper


fun Handler.postDelayed(delayMillis: Long, block: () -> Unit) {
    this.postDelayed(block, delayMillis)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MQTTShimJobService : JobService() {

    private fun scheduleSelfRestart() {
        if (AppSettingsHelper(this).mqttEnabled) {
            // Try scheduling restart
            Handler().postDelayed(3000) {
                sendBroadcast(
                    Intent(
                        Constants.ACTION_RESTART,
                        Uri.EMPTY,
                        this,
                        MQTTBootstrapBroadcastReceiver::class.java
                    )
                )
            }
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // Start the service
        startForegroundServiceCompat(
            Intent(this, MQTTService::class.java).apply { action = ACTION_START }
        )
        // Also schedule a restart of the shim service to keep the main service up
        scheduleSelfRestart()
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        scheduleSelfRestart()
        return false
    }
}