package org.poul.bits.android.controllers.appsettings.impl

import android.content.Context
import org.poul.bits.android.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.controllers.appsettings.enum.TemperatureUnit

const val APP_PREFS_FILE = "app"
const val APP_PREFS_MODE = 0

class AppSettingsHelper(val context: Context) : IAppSettingsHelper {
    private val sharedPrefs = context.getSharedPreferences(
        APP_PREFS_FILE,
        APP_PREFS_MODE
    )

    override var temperatureUnit: TemperatureUnit
        get() = TemperatureUnit.valueOf(
            sharedPrefs.getString("temp_unit", TemperatureUnit.CELSIUS.name)!!
        )
        set(value) = sharedPrefs.edit().putString("temp_unit", value.name).apply()
}