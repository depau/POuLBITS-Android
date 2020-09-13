package org.poul.bits.android.lib.controllers.appsettings.impl

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.edit
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper
import org.poul.bits.android.lib.controllers.appsettings.enum.TemperatureUnit

const val APP_PREFS_FILE = "app"
const val APP_PREFS_MODE = 0

class AppSettingsHelper(val context: Context) : IAppSettingsHelper {
    val LATEST_VERSION = 2
    private val LOG_TAG = "AppSettingsHelp"
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override var version: Int
        get() = sharedPrefs.getInt("version", LATEST_VERSION)
        set(value) = sharedPrefs.edit { putInt("version", value) }

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

    override var mqttStartOnBoot: Boolean
        get() = sharedPrefs.getBoolean("bootup_mqtt", false)
        set(value) = sharedPrefs.edit { putBoolean("bootup_mqtt", value) }

    override var mqttProto: String
        get() = sharedPrefs.getString("mqtt_proto", "wss")!!
        set(value) = sharedPrefs.edit { putString("mqtt_proto", value) }

    override var mqttServer: String
        get() = sharedPrefs.getString("mqtt_hostname", "bits.poul.org/mqtt")!!
        set(value) = sharedPrefs.edit { putString("mqtt_server", value) }

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
        set(value) = sharedPrefs.edit {
            putString(
                "wear_tile_data_expire_timeout",
                value.toString()
            )
        }

    override fun popOldString(key: String, default: String?): String? {
        val retval = sharedPrefs.getString(key, default)
        try {
            sharedPrefs.edit { remove(key) }
        } catch (_: Exception) {
        }
        return retval
    }

    override fun popOldInt(key: String, default: Int): Int? {
        val retval = sharedPrefs.getInt(key, default)
        try {
            sharedPrefs.edit { remove(key) }
        } catch (_: Exception) {
        }
        return retval
    }

    override fun popOldBool(key: String, default: Boolean): Boolean? {
        val retval = sharedPrefs.getBoolean(key, default)
        try {
            sharedPrefs.edit { remove(key) }
        } catch (_: Exception) {
        }
        return retval
    }

    override fun migrate() {
        outer@ do {
            val ver = sharedPrefs.getInt("version", -1)
            when (ver) {
                LATEST_VERSION -> break@outer
                -1 -> {
                    if (!sharedPrefs.contains("mqtt_hostname")
                        && !sharedPrefs.contains("mqtt_port")
                        && !sharedPrefs.contains("mqtt_tls")
                    ) {
                        version = 1
                        continue@outer
                    }

                    val host = popOldString("mqtt_hostname", "192.168.0.4")
                    val port = popOldString("mqtt_port", "1883")
                    val useTls = popOldBool("mqtt_tls", false)!!
                    mqttProto = if (useTls) "ssl" else "tcp"
                    mqttServer = "$host:$port"
                    version = 1
                }
                1 -> {
                    if (mqttServer == "bits.poul.org/ws")
                        mqttServer = "bits.poul.org/mqtt"
                    version = 2
                }
                else           -> {
                    Log.w(LOG_TAG, "Deleting all settings since version is greater than latest")
                    sharedPrefs.edit { clear() }
                }
            }
        } while (ver != LATEST_VERSION)
    }
}