package org.poul.bits.android.misc

import android.content.Context
import com.google.gson.Gson
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.BitsTemperatureData
import org.poul.bits.android.model.enum.BitsStatus
import java.util.*

const val WIDGET_PREFS_FILE = "hq_widget"
const val WIDGET_PREFS_MODE = 0

class WidgetSharedPrefsHelper(val context: Context) {
    private val sharedPrefs = context.getSharedPreferences(WIDGET_PREFS_FILE, WIDGET_PREFS_MODE)
    private val gson = Gson()

    fun getErrorBitsData() = BitsData(
        BitsStatus.UNKNOWN,
        "", Date(),
        BitsTemperatureData(0.0, 0L, "", Date()),
        BitsMessage("App", "Could not retrieve status information", Date()),
        listOf()
    )

    var loading: Boolean
        get() = sharedPrefs.getBoolean("loading", false)
        set(value) {
            sharedPrefs.edit().putBoolean("loading", value).apply()
        }

    var bitsData: BitsData
        get() {
            val json = sharedPrefs.getString("bits_data", null)
                ?: return getErrorBitsData()
            return try {
                gson.fromJson(json, BitsData::class.java)
            } catch (e: Exception) {
                getErrorBitsData()
            }
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPrefs.edit().putString("bits_data", json).apply()
        }

    fun getWidgetHeightCells(appWidgetId: Int): Int =
        sharedPrefs.getInt("app_widget_cells_height_$appWidgetId", 1)

    fun setWidgetHeightCells(appWidgetId: Int, cells: Int) =
        sharedPrefs.edit().putInt("app_widget_cells_height_$appWidgetId", cells).apply()
}