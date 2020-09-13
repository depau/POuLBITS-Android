package org.poul.bits.addon.mqtt.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.poul.bits.addon.mqtt.Constants
import org.poul.bits.addon.mqtt.services.MQTTService
import org.poul.bits.android.lib.controllers.appsettings.impl.AppSettingsHelper

class MQTTBootstrapBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Constants.ACTION_RESTART)
            return

        val appSetting = AppSettingsHelper(context)
        if (!appSetting.mqttEnabled || !appSetting.mqttStartOnBoot)
            return

        MQTTService.ensureService(context)
    }
}