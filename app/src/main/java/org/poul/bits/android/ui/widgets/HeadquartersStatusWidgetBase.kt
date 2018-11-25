package org.poul.bits.android.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import eu.depau.commons.android.kotlin.ktexts.PendingIntentGetForegroundServiceCompat
import org.poul.bits.android.R
import org.poul.bits.android.controllers.widgetstorage.impl.WidgetStorageHelper
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.services.BitsRetrieveStatusService
import org.poul.bits.android.ui.activities.MainActivity


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val sharedPrefsHelper = WidgetStorageHelper(context)

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                sharedPrefsHelper.bitsData,
                sharedPrefsHelper.loading
            )
        }
    }

    private fun getUpdateButtonPendingIntent(context: Context): PendingIntent {
        val intent = BitsRetrieveStatusService.getIntent(context)
        return PendingIntentGetForegroundServiceCompat(context, 0, intent, 0)
    }

    private fun getMainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun getBackgroundDrawable(bitsData: BitsData): Int {
        return when (bitsData.status) {
            BitsStatus.OPEN -> R.drawable.widget_fab_open
            BitsStatus.CLOSED -> R.drawable.widget_fab_closed
            BitsStatus.UNKNOWN -> R.drawable.widget_fab_gialla
        }
    }

    private fun drawRemoteRefreshButtonDrawable(context: Context, remoteViews: RemoteViews) {
        val layout = R.id.widget_fab_refresh_imageview

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(layout, R.drawable.ic_refresh_white_24dp)
        } else {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_refresh_white_24dp)
            val b = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(b)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            remoteViews.setImageViewBitmap(layout, b)
        }
    }


    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData,
        loading: Boolean
    ) {
        val sharedPrefsHelper = WidgetStorageHelper(context)
        val cells = sharedPrefsHelper.getWidgetHeightCells(appWidgetId)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)

        val hqStatus = context.getTextForStatus(bitsData.status)
        views.setTextViewText(R.id.widget_fab, hqStatus)

        if (!loading) {
            drawRemoteRefreshButtonDrawable(context, views)
            views.setViewVisibility(R.id.widget_fab_refresh_imageview, View.VISIBLE)
            views.setViewVisibility(R.id.widget_fab_refresh_progressbar, View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_fab_refresh_imageview, View.GONE)
            views.setViewVisibility(R.id.widget_fab_refresh_progressbar, View.VISIBLE)
        }

        val pendingRefresh = getUpdateButtonPendingIntent(context)
        views.setOnClickPendingIntent(R.id.widget_fab_refresh, pendingRefresh)

        val pendingActivity = getMainActivityPendingIntent(context)
        views.setOnClickPendingIntent(R.id.widget_fab, pendingActivity)

        val bgDrawable = getBackgroundDrawable(bitsData)
        views.setInt(R.id.widget_fab, "setBackgroundResource", bgDrawable)
        views.setInt(R.id.widget_fab_refresh, "setBackgroundResource", bgDrawable)

        views.setTextViewText(R.id.widget_status_card_textview, MainActivity.getStatusCardText(context, bitsData))

        if (bitsData.message.message.isNotBlank()) {
            views.setTextViewText(
                R.id.widget_message_card_textview,
                MainActivity.getMessageCardText(context, bitsData.message)
            )
        }

        val statusCardVisibility = if (cells > 1) View.VISIBLE else View.GONE
        val messageCardVisibility = if (cells > 2 && bitsData.message.message.isNotBlank()) View.VISIBLE else View.GONE

        views.setViewVisibility(R.id.status_card_textview, statusCardVisibility)
        views.setViewVisibility(R.id.message_card_textview, messageCardVisibility)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        val views = RemoteViews(context.packageName, layoutId)
        val (w, h) = getCellsForOptionsBundle(newOptions)

        views.setViewVisibility(R.id.widget_status_card_textview, if (h > 1) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.widget_message_card_textview, if (h > 2) View.VISIBLE else View.GONE)

        WidgetStorageHelper(context)
            .setWidgetHeightCells(appWidgetId, h)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        fun getCellsNumber(size: Int): Int = (size + 30) / 70

        fun getCellsForOptionsBundle(bundle: Bundle): Pair<Int, Int> {
            return Pair(
                getCellsNumber(bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)),
                getCellsNumber(bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT))
            )
        }

        fun <T> getUpdateIntent(
            context: Context,
            clazz: Class<T>,
            appWidgetIds: IntArray
        ): Intent where T : AppWidgetProvider = Intent(context, clazz).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }

        fun <T> getAppWidgetIds(context: Context, clazz: Class<T>): IntArray where T : AppWidgetProvider =
            AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, clazz))
    }
}

