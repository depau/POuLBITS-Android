package org.poul.bits.android.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import org.poul.bits.android.R
import org.poul.bits.android.lib.model.BitsData
import org.poul.bits.android.lib.model.enum.BitsStatus

class HeadquartersStatusIconAppWidget : HeadquartersStatusWidgetBase() {

    override val layoutId: Int
        get() = R.layout.icon_app_widget


    override fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData,
        loading: Boolean
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, layoutId)

        if (bitsData.status != null) {
            val bgDrawable = getBackgroundDrawable(bitsData)
            views.setInt(R.id.widget_icon_bg_layout, "setBackgroundResource", bgDrawable)
        }

        if (!loading) {
            drawRemoteDrawable(
                context,
                views,
                R.drawable.ic_poul_logo,
                R.id.widget_icon_imageview
            )
            views.setViewVisibility(R.id.widget_icon_imageview, View.VISIBLE)
            views.setViewVisibility(R.id.widget_icon_refresh_progressbar, View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_icon_imageview, View.GONE)
            views.setViewVisibility(R.id.widget_icon_refresh_progressbar, View.VISIBLE)
        }

        val pendingIntent = when (loading) {
            true -> getMainActivityPendingIntent(context)
            else -> getUpdateButtonPendingIntent(context)
        }
        views.setOnClickPendingIntent(R.id.widget_icon_bg_layout, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    private fun getBackgroundDrawable(bitsData: BitsData): Int {
        return when (bitsData.status!!) {
            BitsStatus.OPEN    -> R.drawable.widget_bg_shape_circle_open
            BitsStatus.CLOSED  -> R.drawable.widget_bg_shape_circle_closed
            BitsStatus.UNKNOWN -> R.drawable.widget_bg_shape_circle_gialla
        }
    }
}

