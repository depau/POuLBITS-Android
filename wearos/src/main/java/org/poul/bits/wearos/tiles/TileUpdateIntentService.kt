package org.poul.bits.wearos.tiles

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.clockwork.tiles.ITileProvider
import eu.depau.kotlet.android.extensions.ui.context.getNotificationBuilder
import org.poul.bits.android.lib.services.CHANNEL_BITS_RETRIEVE_STATUS
import org.poul.bits.wearos.R

class TileUpdateIntentService : JobIntentService() {
    private val LOG_TAG = "TileUpdIntSvc"

    private val tileProviderConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, binder: IBinder) {
            val tileBinder = (binder as ITileProvider.Stub)
            val tileService = tileBinder.service as PoulBitsTileProviderService
            Log.d(LOG_TAG, "Bound to TileProviderService")
            tileService.updateTiles()
            unbindService(this)
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            stopForeground(true)
            stopSelf()
        }
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(LOG_TAG, "Updating tiles from TileUpdateIntentService")
        startForeground(
            99,
            getNotificationBuilder(CHANNEL_BITS_RETRIEVE_STATUS)
                .setContentTitle(getString(R.string.updating_tiles))
                .build()
        )

        bindService(
            Intent(
                "com.google.android.clockwork.ACTION_TILE_UPDATE_REQUEST",
                Uri.EMPTY,
                this,
                PoulBitsTileProviderService::class.java
            ),
            tileProviderConn,
            Context.BIND_AUTO_CREATE
        )

        // Work continues in tileProviderConn once the service is bound
    }

    companion object {
        const val JOB_ID = 393

        @JvmStatic
        public fun doRequestTileUpdate(context: Context) {
            enqueueWork(
                context,
                TileUpdateIntentService::class.java,
                JOB_ID,
                Intent(context, TileUpdateIntentService::class.java)
            )
        }
    }
}