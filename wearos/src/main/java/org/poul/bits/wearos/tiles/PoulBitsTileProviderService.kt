package org.poul.bits.wearos.tiles

import android.app.PendingIntent
import android.content.Intent
import android.text.Html
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import android.widget.RemoteViews
import com.google.android.clockwork.tiles.TileData
import com.google.android.clockwork.tiles.TileProviderService
import eu.depau.kotlet.android.extensions.intent.pendingIntentGetForegroundServiceCompat
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper
import org.poul.bits.android.lib.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.lib.misc.SimpleHtml
import org.poul.bits.android.lib.misc.getTextForStatus
import org.poul.bits.android.lib.model.BitsData
import org.poul.bits.android.lib.model.enum.BitsStatus
import org.poul.bits.android.lib.services.BitsRetrieveStatusService
import org.poul.bits.android.lib.widgets.drawRemoteDrawable
import org.poul.bits.wearos.MainActivity
import org.poul.bits.wearos.R

class PoulBitsTileProviderService : TileProviderService() {
    private val LOG_TAG = "BitsTileProvSvc"

    override fun onTileUpdate(tileId: Int) {
        if (isIdForDummyData(tileId)) return
        AppSettingsHelper(this).wearTileIDs += tileId
        onSystemTileUpdateRequested(tileId)
    }

    override fun onTileFocus(tileId: Int) {
        super.onTileFocus(tileId)
        if (isIdForDummyData(tileId)) return
        onSystemTileUpdateRequested(tileId)
    }

    private fun onSystemTileUpdateRequested(tileId: Int) {
        // Render tile with currently available data
        updateTiles(tileId)

        // Request an update if data is expired
        val widgetData = SharedPrefsWidgetStorageHelper(this)
        val appPrefs = AppSettingsHelper(this)
        if (widgetData.lastDataUpdate + 1000 * appPrefs.wearTileDataExpirationMins > System.currentTimeMillis())
            return

        BitsRetrieveStatusService.startActionRetrieveStatus(this)
    }

    internal fun updateTiles() {
        updateTiles(*AppSettingsHelper(this).wearTileIDs)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun updateTiles(vararg tileIds: Int) {
        val tileData = renderTile()
        tileIds.forEach { tileId -> sendData(tileId, tileData) }
    }

    private fun renderTile(): TileData {
        val widgetData = SharedPrefsWidgetStorageHelper(this)

        val views = RemoteViews(packageName, R.layout.tile)
        views.setInt(
            R.id.status_button,
            "setBackgroundColor",
            getBackgroundColor(widgetData.bitsData)
        )

        // Clockwork is too snob to read the layout
        views.setTextColor(R.id.status_button, getColor(R.color.transpWhite))
        views.setTextColor(R.id.status_card, getColor(R.color.transpWhite))
        views.setTextColor(R.id.message_card, getColor(R.color.transpWhite))

        views.setTextViewTextSize(R.id.status_button, COMPLEX_UNIT_DIP, 13F)
        views.setTextViewTextSize(R.id.message_card, COMPLEX_UNIT_DIP, 13F)
        views.setTextViewTextSize(
            R.id.status_card,
            COMPLEX_UNIT_DIP,
            when {
                widgetData.bitsData.message?.message?.isBlank() == true -> 17F
                widgetData.bitsData.status!! == BitsStatus.UNKNOWN      -> 17F
                else                                                    -> 13F
            }
        )

        if (widgetData.bitsData.status!! == BitsStatus.UNKNOWN || widgetData.bitsData.message?.message?.isBlank() == true) {
            views.setViewVisibility(R.id.message_card, View.GONE)
        } else {
            views.setViewVisibility(R.id.message_card, View.VISIBLE)
        }

        if (!widgetData.loading) {
            drawRemoteDrawable(
                this,
                views,
                R.drawable.ic_refresh_white_24dp,
                R.id.refresh_button
            )
            views.setViewVisibility(R.id.refresh_button, View.VISIBLE)
            views.setViewVisibility(R.id.refresh_progressbar, View.GONE)
        } else {
            views.setViewVisibility(R.id.refresh_button, View.GONE)
            views.setViewVisibility(R.id.refresh_progressbar, View.VISIBLE)
        }

        views.setTextViewText(
            R.id.status_button,
            getTextForStatus(widgetData.bitsData.status!!)
        )

        views.setTextViewText(
            R.id.status_card,
            when {
                widgetData.bitsData.status!! == BitsStatus.UNKNOWN      ->
                    Html.fromHtml(
                        SimpleHtml.italic(
                            "<br>" + getString(R.string.status_retrieve_failure_card) + "<br>"
                        )
                    )
                widgetData.bitsData.message?.message?.isBlank() == true ->
                    MainActivity.getStatusCardText(
                        this,
                        widgetData.bitsData,
                        "<br>",
                        "<br>"
                    )
                else                                                    ->
                    MainActivity.getStatusCardText(
                        this,
                        widgetData.bitsData
                    )
            }
        )

        views.setTextViewText(
            R.id.message_card,
            widgetData.bitsData.message
                .takeIf { it?.message?.isNotBlank() ?: false }
                ?.let { message ->
                    MainActivity.getMessageCardText(
                        this,
                        message,
                        maxMessageChars = 30
                    )
                }
                ?: ""
        )


        val pendingRefresh = getUpdateButtonPendingIntent()
        views.setOnClickPendingIntent(R.id.refresh_button, pendingRefresh)

        val pendingActivity = getMainActivityPendingIntent()
        views.setOnClickPendingIntent(R.id.open_app_button, pendingActivity)
        views.setOnClickPendingIntent(R.id.status_button, pendingActivity)

        return TileData.Builder().setRemoteViews(views).build()
    }

    private fun getUpdateButtonPendingIntent(): PendingIntent {
        val intent = BitsRetrieveStatusService.getIntent(this)
        return pendingIntentGetForegroundServiceCompat(this, 0, intent, 0)
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, 0)
    }


    private fun getBackgroundColor(bitsData: BitsData): Int {
        return when (bitsData.status!!) {
            BitsStatus.OPEN -> getColor(R.color.colorHQsOpen)
            BitsStatus.CLOSED -> getColor(R.color.colorHQsClosed)
            BitsStatus.UNKNOWN -> getColor(R.color.colorHQsGialla)
        }
    }
}