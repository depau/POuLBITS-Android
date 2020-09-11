package org.poul.bits.android.lib.services

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import eu.depau.kotlet.android.extensions.notification.NotificationImportanceCompat
import eu.depau.kotlet.android.extensions.notification.buildCompat
import eu.depau.kotlet.android.extensions.notification.registerNotificationChannel
import eu.depau.kotlet.android.extensions.ui.context.getNotificationBuilder
import eu.depau.kotlet.android.extensions.ui.context.startForegroundServiceCompat
import org.poul.bits.android.lib.R
import org.poul.bits.android.lib.broadcasts.BitsStatusErrorBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.lib.broadcasts.BitsStatusRetrieveStartBroadcast
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.lib.controllers.bitsclient.IBitsClient
import org.poul.bits.android.lib.controllers.bitsclient.impl.BitsJsonV3Client


internal const val ACTION_RETRIEVE_STATUS = "org.poul.bits.android.lib.services.action.ACTION_RETRIEVE_STATUS"

private const val FOREGROUND_RETRIEVE_STATUS_ID = 4389

const val CHANNEL_BITS_RETRIEVE_STATUS = "org.poul.bits.android.notification.CHANNEL_BITS_RETRIEVING_STATUS"

class BitsRetrieveStatusService : IntentService("BitsRetrieveStatusService") {

    private val LOG_TAG = "BitsRetrieveStatusSvc"

    private val bitsClient: IBitsClient = BitsJsonV3Client()

    private lateinit var appSettings: IAppSettingsHelper

    override fun onCreate() {
        super.onCreate()

        appSettings = AppSettingsHelper(this)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.registerNotificationChannel(
            CHANNEL_BITS_RETRIEVE_STATUS,
            getString(R.string.channel_bits_retrieving_status_name),
            getString(R.string.channel_bits_retrieving_status_description),
            NotificationImportanceCompat.IMPORTANCE_LOW
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
        try {
            startForeground(FOREGROUND_RETRIEVE_STATUS_ID, getForegroundNotification())

            BitsStatusRetrieveStartBroadcast.broadcast(this)
            val data = bitsClient.downloadData(appSettings.jsonStatusUrl)
            val svg: String? = try {
                bitsClient.downloadPresenceSVG(appSettings.presenceVectorUri)
            } catch (e: java.lang.Exception) {
                Log.e(LOG_TAG, "Error downloading presence image", e)
                null
            }
            BitsStatusReceivedBroadcast.broadcast(this, data, svg)

            stopForeground(true)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to retrieve status JSON", e)
            BitsStatusErrorBroadcast.broadcast(this)
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context) = Intent(context, BitsRetrieveStatusService::class.java).apply {
            action = ACTION_RETRIEVE_STATUS
        }

        @JvmStatic
        fun startActionRetrieveStatus(context: Context) {
            context.startForegroundServiceCompat(getIntent(context))
        }
    }
}
