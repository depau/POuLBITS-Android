package org.poul.bits.android.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import org.poul.bits.android.R
import org.poul.bits.android.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.misc.getTextForStatus
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.enum.BitsStatus
import org.poul.bits.android.ui.activities.MainActivity

class HeadquartersStatusHorizontalWidget : HeadquartersStatusWidgetBase() {

    override val layoutId: Int
        get() = R.layout.headquarters_status_horizontal_widget

    override fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData,
        loading: Boolean
    ) {
        val sharedPrefsHelper = SharedPrefsWidgetStorageHelper(context)
        val cells = sharedPrefsHelper.getWidgetHeightCells(appWidgetId)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)

        if (bitsData.status != null) {
            val hqStatus = context.getTextForStatus(bitsData.status)
            views.setTextViewText(R.id.widget_fab, hqStatus)

            val bgDrawable = getBackgroundDrawable(bitsData)
            views.setInt(R.id.widget_fab, "setBackgroundResource", bgDrawable)
            views.setInt(R.id.widget_fab_refresh, "setBackgroundResource", bgDrawable)
        }

        if (!loading) {
            drawRemoteDrawable(
                context,
                views,
                R.drawable.ic_refresh_white_24dp,
                R.id.widget_fab_refresh_imageview
            )
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

        views.setTextViewText(R.id.widget_status_card_textview, MainActivity.getStatusCardText(context, bitsData))

        if (bitsData.message != null && bitsData.message.message.isNotBlank()) {
            views.setTextViewText(
                R.id.widget_message_card_textview,
                MainActivity.getMessageCardText(context, bitsData.message)
            )
        }

        val statusCardVisibility = if (cells > 1) View.VISIBLE else View.GONE
        val messageCardVisibility = if (cells > 2 && bitsData.message?.empty == false) View.VISIBLE else View.GONE

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

        SharedPrefsWidgetStorageHelper(context)
            .setWidgetHeightCells(appWidgetId, h)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getBackgroundDrawable(bitsData: BitsData): Int {
        return when (bitsData.status!!) {
            BitsStatus.OPEN    -> R.drawable.widget_fab_open
            BitsStatus.CLOSED  -> R.drawable.widget_fab_closed
            BitsStatus.UNKNOWN -> R.drawable.widget_fab_gialla
        }
    }


}