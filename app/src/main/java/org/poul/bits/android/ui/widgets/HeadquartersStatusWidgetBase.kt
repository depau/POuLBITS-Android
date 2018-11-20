package org.poul.bits.android.ui.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.poul.bits.R
import org.poul.bits.android.broadcasts.BitsStatusReceivedBroadcast
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.services.BitsRetrieveStatusService


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Fuck everything, we're doing it on our own
        BitsRetrieveStatusService.startActionRetrieveStatus(context)
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

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData
    ) {
        val widgetText = context.getString(R.string.appwidget_text)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)
        views.setTextViewText(R.id.appwidget_text, widgetText)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

