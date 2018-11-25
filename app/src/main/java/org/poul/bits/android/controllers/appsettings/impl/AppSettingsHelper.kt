package org.poul.bits.android.controllers.appsettings.impl

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import org.poul.bits.android.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.controllers.appsettings.enum.TemperatureUnit

const val APP_PREFS_FILE = "app"
const val APP_PREFS_MODE = 0

class AppSettingsHelper(val context: Context) : IAppSettingsHelper {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override var fullscreen: Boolean
        get() = sharedPrefs.getBoolean("fullscreen", false)
        set(value) = sharedPrefs.edit { putBoolean("fullscreen", value) }

    override var temperatureUnit: TemperatureUnit
        get() = TemperatureUnit.valueOf(
            sharedPrefs.getString("temp_unit", TemperatureUnit.CELSIUS.name)!!
        )
        set(value) = sharedPrefs.edit { putString("temp_unit", value.name) }
}