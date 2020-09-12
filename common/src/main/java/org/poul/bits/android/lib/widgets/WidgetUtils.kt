package org.poul.bits.android.lib.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources

fun <T> getAppWidgetIds(context: Context, clazz: Class<T>): IntArray where T : AppWidgetProvider =
    AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, clazz))

fun drawRemoteDrawable(context: Context, remoteViews: RemoteViews, drawableId: Int, layout: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        remoteViews.setImageViewResource(layout, drawableId)
    } else {
        val drawable = AppCompatResources.getDrawable(context, drawableId)!!
        val b = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(b)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        remoteViews.setImageViewBitmap(layout, b)
    }
}