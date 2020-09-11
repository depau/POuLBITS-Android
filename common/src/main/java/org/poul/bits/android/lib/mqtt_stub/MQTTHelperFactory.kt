@file:Suppress("ConstantConditionIf")

package org.poul.bits.android.lib.mqtt_stub

import android.util.Log
import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper

object MQTTHelperFactory {
    fun getMqttHelper(appSettings: IAppSettingsHelper): IMQTTServiceHelper {
        try {
            val clazz = Class.forName("org.poul.bits.addon.mqtt.impl.RealMQTTServiceHelper")
            return clazz.newInstance() as IMQTTServiceHelper
        } catch (exc: Exception) {
            Log.e("MQTTHelperFacto", "Unable to create proper MQTT service helper", exc)
        }
        return StubMQTTServiceHelper()
    }
}