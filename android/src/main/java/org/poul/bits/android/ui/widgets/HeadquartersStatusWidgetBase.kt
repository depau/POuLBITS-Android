package org.poul.bits.android.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import eu.depau.kotlet.android.extensions.intent.pendingIntentGetForegroundServiceCompat
import org.poul.bits.android.lib.controllers.widgetstorage.impl.SharedPrefsWidgetStorageHelper
import org.poul.bits.android.lib.model.BitsData
import org.poul.bits.android.lib.services.BitsRetrieveStatusService
import org.poul.bits.android.ui.activities.MainActivity


abstract class HeadquartersStatusWidgetBase : AppWidgetProvider() {
    abstract val layoutId: Int

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
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
        return pendingIntentGetForegroundServiceCompat(context, 0, intent, 0)
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
    }
}

