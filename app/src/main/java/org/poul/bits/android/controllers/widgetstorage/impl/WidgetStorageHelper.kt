package org.poul.bits.android.controllers.widgetstorage.impl

import android.content.Context
import com.google.gson.Gson
import org.poul.bits.android.controllers.widgetstorage.IWidgetStorageHelper
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.BitsSensorData
import org.poul.bits.android.model.enum.BitsStatus
import java.util.*

const val WIDGET_PREFS_FILE = "hq_widget"
const val WIDGET_PREFS_MODE = 0

class WidgetStorageHelper(val context: Context) : IWidgetStorageHelper {
    private val sharedPrefs = context.getSharedPreferences(
        WIDGET_PREFS_FILE,
        WIDGET_PREFS_MODE
    )
    private val gson = Gson()

    override val bitsDataError: BitsData
        get() = BitsData(
            BitsStatus.UNKNOWN,
            "", Date(),
            listOf(BitsSensorData(0.0, 0L, "", Date(), null)),
            BitsMessage("App", "Could not retrieve status information", Date()),
            listOf()
        )

    override var loading: Boolean
        get() = sharedPrefs.getBoolean("loading", false)
        set(value) {
            sharedPrefs.edit().putBoolean("loading", value).apply()
        }

    override var bitsData: BitsData
        get() {
            val json = sharedPrefs.getString("bits_data", null)
                ?: return bitsDataError
            return try {
                gson.fromJson(json, BitsData::class.java)
            } catch (e: Exception) {
                bitsDataError
            }
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPrefs.edit().putString("bits_data", json).apply()
        }

    override fun getWidgetHeightCells(appWidgetId: Int): Int =
        sharedPrefs.getInt("app_widget_cells_height_$appWidgetId", 1)

    override fun setWidgetHeightCells(appWidgetId: Int, cells: Int) =
        sharedPrefs.edit().putInt("app_widget_cells_height_$appWidgetId", cells).apply()
}