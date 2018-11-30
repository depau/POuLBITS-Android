package org.poul.bits.android.controllers.appsettings

import org.poul.bits.android.controllers.appsettings.enum.TemperatureUnit

interface IAppSettingsHelper {
    var temperatureUnit: TemperatureUnit
    var fullscreen: Boolean

    var jsonStatusUrl: String
    var presenceImageUrl: String

    var mqttEnabled: Boolean
    var mqttHostname: String
    var mqttPort: Int
    var mqttUseTls: Boolean
    var mqttSedeTopic: String
    var mqttTempTopic: String
    var mqttHumTopic: String
}