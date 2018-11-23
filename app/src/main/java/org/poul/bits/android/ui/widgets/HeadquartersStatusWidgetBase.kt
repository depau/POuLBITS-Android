package org.poul.bits.android.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.poul.bits.R
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.BitsTemperatureData
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.ui.activities.MainActivity
import java.util.*


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Fuck everything, we're doing it on our own
//        BitsRetrieveStatusService.startActionRetrieveStatus(context)
        updateWidgets(context, BitsData(BitsStatus.CLOSED, "a", Date(), BitsTemperatureData(0.toDouble(), 0L, "cc", Date()), BitsMessage("user", "msg", Date()), listOf()))
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BitsStatusReceivedBroadcast.ACTION ->
                handleStatusReceivedBroadcast(context, intent)
            else ->
                super.onReceive(context, intent)
        }
    }

    fun handleStatusReceivedBroadcast(context: Context, intent: Intent) {
        val bitsData = intent.getParcelableExtra<BitsData>(BitsStatusReceivedBroadcast.BITS_DATA)
        updateWidgets(context, bitsData)
    }

    fun updateWidgets(context: Context, bitsData: BitsData) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, this::class.java))

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                bitsData
            )
        }
    }

    fun getUpdateButtonPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, this.javaClass).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getMainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    fun getBackgroundDrawable(bitsData: BitsData): Int {
        return when(bitsData.status) {
            BitsStatus.OPEN -> R.drawable.widget_fab_open
            BitsStatus.CLOSED -> R.drawable.widget_fab_closed
        }
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData
    ) {
        val widgetText = context.getTextForStatus(bitsData.status)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)
        views.setTextViewText(R.id.widget_fab, widgetText)

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

