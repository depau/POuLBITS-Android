package org.poul.bits.android.lib.controllers.appsettings

import org.poul.bits.android.lib.controllers.appsettings.enum.TemperatureUnit

interface IAppSettingsHelper {
    var version: Int

    var temperatureUnit: TemperatureUnit
    var fullscreen: Boolean

    var jsonStatusUrl: String
    var presenceVectorUri: String

    var mqttEnabled: Boolean
    var mqttProto: String
    var mqttServer: String
    var mqttSedeTopic: String
    var mqttTempTopic: String
    var mqttHumTopic: String

    var wearTileIDs: IntArray
    var wearTileDataExpirationMins: Int

    fun popOldString(key: String, default: String?): String?
    fun popOldInt(key: String, default: Int): Int?
    fun popOldBool(key: String, default: Boolean): Boolean?

    fun migrate()
}