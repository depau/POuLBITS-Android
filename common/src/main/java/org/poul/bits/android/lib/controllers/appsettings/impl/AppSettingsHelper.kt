package org.poul.bits.android.lib.controllers.appsettings.impl

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.lib.controllers.appsettings.enum.TemperatureUnit

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

    override var mqttEnabled: Boolean
        get() = sharedPrefs.getBoolean("enable_mqtt", false)
        set(value) = sharedPrefs.edit { putBoolean("enable_mqtt", value) }

    override var mqttHostname: String
        get() = sharedPrefs.getString("mqtt_hostname", "192.168.0.4")!!
        set(value) = sharedPrefs.edit { putString("mqtt_hostname", value) }

    override var mqttPort: Int
        get() = sharedPrefs.getString("mqtt_port", "1883")!!.toInt()
        set(value) = sharedPrefs.edit { putString("mqtt_port", value.toString()) }

    override var mqttUseTls: Boolean
        get() = sharedPrefs.getBoolean("mqtt_tls", false)
        set(value) = sharedPrefs.edit { putBoolean("mqtt_tls", value) }

    override var mqttSedeTopic: String
        get() = sharedPrefs.getString("mqtt_sede_topic", "sede/status")!!
        set(value) = sharedPrefs.edit { putString("mqtt_sede_topic", value) }

    override var mqttTempTopic: String
        get() = sharedPrefs.getString("mqtt_temperature_topic", "sede/sensors/si7020/temperature")!!
        set(value) = sharedPrefs.edit { putString("mqtt_temperature_topic", value) }

    override var mqttHumTopic: String
        get() = sharedPrefs.getString("mqtt_humidity_topic", "sede/sensors/si7020/humidity")!!
        set(value) = sharedPrefs.edit { putString("mqtt_humidity_topic", value) }

    override var jsonStatusUrl: String
        get() = sharedPrefs.getString("http_json_status_url", "https://bits.poul.org/data")!!
        set(value) = sharedPrefs.edit { putString("http_json_status_url", value) }

    override var presenceVectorUri: String
        get() = sharedPrefs.getString(
            "http_presence_svg_uri",
            "https://bits.poul.org/presence.svg"
        )!!
        set(value) = sharedPrefs.edit { putString("http_presence_svg_uri", value) }

    override var wearTileIDs: IntArray
        get() {
            return try {
                sharedPrefs
                    .getString("wear_tile_ids", null)
                    ?.split(",")
                    ?.map { it.toInt() }
                    ?.distinct()
                    ?.toIntArray()
                    ?: intArrayOf()
            } catch (_: Exception) {
                intArrayOf()
            }
        }
        set(value) = sharedPrefs.edit { putString("wear_tile_ids", value.joinToString(",")) }

    override var wearTileDataExpirationMins: Int
        get() = sharedPrefs.getString("wear_tile_data_expire_timeout", "15")!!.toInt()
        set(value) = sharedPrefs.edit { putString("wear_tile_data_expire_timeout", value.toString()) }
}