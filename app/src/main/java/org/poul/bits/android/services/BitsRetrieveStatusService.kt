package org.poul.bits.android.services

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import eu.depau.commons.android.kotlin.ktexts.buildCompat
import eu.depau.commons.android.kotlin.ktexts.getNotificationBuilder
import eu.depau.commons.android.kotlin.ktexts.registerNotificationChannel
import org.poul.bits.R
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.controllers.bitsclient.IBitsClient
import org.poul.bits.android.controllers.bitsclient.impl.BitsJsonV3Client

private const val ACTION_RETRIEVE_STATUS = "org.poul.bits.android.services.action.ACTION_RETRIEVE_STATUS"

private const val FOREGROUND_RETRIEVE_STATUS_ID = 4389

private const val CHANNEL_BITS_RETRIEVE_STATUS = "org.poul.bits.android.notification.CHANNEL_BITS_RETRIEVING_STATUS"

class BitsRetrieveStatusService : IntentService("BitsRetrieveStatusService") {

    private val bitsClient: IBitsClient = BitsJsonV3Client()

    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(
            CHANNEL_BITS_RETRIEVE_STATUS,
            getString(R.string.channel_bits_retrieving_status_name),
            getString(R.string.channel_bits_retrieving_status_description),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotificationManager.IMPORTANCE_LOW else null
        )
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_RETRIEVE_STATUS -> {
                handleActionRetrieveStatus()
            }
        }
    }

    private fun getForegroundNotification(): Notification {
        return getNotificationBuilder(CHANNEL_BITS_RETRIEVE_STATUS)
            .setContentTitle(getString(R.string.updating_bits_status))
            .setContentText(getString(R.string.updating_bits_desc))
            .buildCompat()
    }


    private fun handleActionRetrieveStatus() {
        startForeground(FOREGROUND_RETRIEVE_STATUS_ID, getForegroundNotification())
        val data = bitsClient.downloadData()
        BitsStatusReceivedBroadcast.broadcast(this, data)
        stopForeground(true)
    }

    companion object {
        @JvmStatic
        fun startActionRetrieveStatus(context: Context) {
            val intent = Intent(context, BitsRetrieveStatusService::class.java).apply {
                action = ACTION_RETRIEVE_STATUS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
