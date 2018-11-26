@file:Suppress("ConstantConditionIf")

package org.poul.bits.android.addons.mqtt

import org.poul.bits.android.BuildConfig
import org.poul.bits.android.addons.mqtt.impl.FakeMQTTServiceHelper
import org.poul.bits.android.addons.mqtt.impl.RealMQTTServiceHelper
import org.poul.bits.android.controllers.appsettings.IAppSettingsHelper

object MQTTHelperFactory {
    fun getMqttHelper(appSettings: IAppSettingsHelper): IMQTTServiceHelper {
        return when (BuildConfig.FLAVOR) {
            "internal" -> RealMQTTServiceHelper(
                appSettings.mqttHostname,
                appSettings.mqttPort,
                appSettings.mqttUseTls,
                appSettings.mqttSedeTopic,
                appSettings.mqttTempTopic,
                appSettings.mqttHumTopic
            )
            else       -> FakeMQTTServiceHelper()
        }
    }
}