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
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources
import eu.depau.commons.android.kotlin.ktexts.PendingIntentGetForegroundServiceCompat
import org.poul.bits.android.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.services.BitsRetrieveStatusService
import org.poul.bits.android.ui.activities.MainActivity


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val sharedPrefsHelper = SharedPrefsWidgetStorageHelper(context)

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

    abstract fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        bitsData: BitsData,
        loading: Boolean
    )

    internal fun getUpdateButtonPendingIntent(context: Context): PendingIntent {
        val intent = BitsRetrieveStatusService.getIntent(context)
        return PendingIntentGetForegroundServiceCompat(context, 0, intent, 0)
    }

    internal fun getMainActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
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
    }
}

