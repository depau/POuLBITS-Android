package org.poul.bits.android.misc

import android.annotation.SuppressLint
import android.content.Context
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.BitsMessage
import org.poul.bits.android.model.BitsTemperatureData
import org.poul.bits.android.model.enum.BitsStatus
import java.util.*

const val WIDGET_PREFS_FILE = "hq_widget"
const val WIDGET_PREFS_MODE = 0

@SuppressLint("ApplySharedPref")
class WidgetSharedPrefsHelper(val context: Context) {
    private val sharedPrefs = context.getSharedPreferences(WIDGET_PREFS_FILE, WIDGET_PREFS_MODE)
    private val jackson = jacksonObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, false)

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
            sharedPrefs.edit().putBoolean("loading", value).commit()
        }

    var bitsData: BitsData
        get() {
            val json = sharedPrefs.getString("bits_data", null)
                ?: return getErrorBitsData()
            return try {
                jackson.readValue(json)
            } catch (e: Exception) {
                getErrorBitsData()
            }
        }
        set(value) {
            val json = jackson.writeValueAsString(value)
            sharedPrefs.edit().putString("bits_data", json).commit()
        }
}