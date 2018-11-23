package org.poul.bits.android.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.poul.bits.android.R
import org.poul.bits.android.misc.WidgetSharedPrefsHelper
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.ui.activities.MainActivity


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val sharedPrefsHelper = WidgetSharedPrefsHelper(context)

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                sharedPrefsHelper.bitsData
            )
        }
    }

    private fun getUpdateIntent(context: Context) = Intent(context, this.javaClass).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }

    fun getUpdateButtonPendingIntent(context: Context): PendingIntent {
        val intent = getUpdateIntent(context)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getMainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    fun getBackgroundDrawable(bitsData: BitsData): Int {
        return when (bitsData.status) {
            BitsStatus.OPEN -> R.drawable.widget_fab_open
            BitsStatus.CLOSED -> R.drawable.widget_fab_closed
            BitsStatus.UNKNOWN -> R.drawable.widget_fab_gialla
        }
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData
    ) {
        val hqStatus = context.getTextForStatus(bitsData.status)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)
        views.setTextViewText(R.id.widget_fab, hqStatus)

        val pendingRefresh = getUpdateButtonPendingIntent(context)
        views.setOnClickPendingIntent(R.id.widget_fab_refresh, pendingRefresh)

        val pendingActivity = getMainActivityPendingIntent(context)
        views.setOnClickPendingIntent(R.id.widget_fab, pendingActivity)

        val bgDrawable = getBackgroundDrawable(bitsData)
        views.setInt(R.id.widget_fab, "setBackgroundResource", bgDrawable)
        views.setInt(R.id.widget_fab_refresh, "setBackgroundResource", bgDrawable)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

