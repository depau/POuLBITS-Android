package org.poul.bits.android.lib.controllers.appsettings

import org.poul.bits.android.lib.controllers.appsettings.enum.TemperatureUnit

interface IAppSettingsHelper {
    var temperatureUnit: TemperatureUnit
    var fullscreen: Boolean

    var jsonStatusUrl: String
    var presenceVectorUri: String

    var mqttEnabled: Boolean
    var mqttHostname: String
    var mqttPort: Int
    var mqttUseTls: Boolean
    var mqttSedeTopic: String
    var mqttTempTopic: String
    var mqttHumTopic: String

    var wearTileIDs: IntArray
    var wearTileDataExpirationMins: Int
}