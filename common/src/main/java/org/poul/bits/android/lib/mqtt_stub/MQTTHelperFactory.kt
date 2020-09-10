@file:Suppress("ConstantConditionIf")

package org.poul.bits.android.lib.mqtt_stub

import org.poul.bits.android.lib.controllers.appsettings.IAppSettingsHelper

object MQTTHelperFactory {
    fun getMqttHelper(appSettings: IAppSettingsHelper): IMQTTServiceHelper {
        try {
            val clazz = Class.forName("org.poul.bits.android.lib.mqtt.impl.RealMQTTServiceHelper")
            return clazz
                .getConstructor(
                    String::class.java,
                    Int::class.java,
                    Boolean::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java
                )
                .newInstance(
                    appSettings.mqttHostname,
                    appSettings.mqttPort,
                    appSettings.mqttUseTls,
                    appSettings.mqttSedeTopic,
                    appSettings.mqttTempTopic,
                    appSettings.mqttHumTopic
                ) as IMQTTServiceHelper

        } catch (_: Exception) {
        }
        return StubMQTTServiceHelper()
    }
}