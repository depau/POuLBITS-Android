package org.poul.bits.android.controllers.appsettings

import org.poul.bits.android.controllers.appsettings.enum.TemperatureUnit

interface IAppSettingsHelper {
    var temperatureUnit: TemperatureUnit
    var fullscreen: Boolean
}