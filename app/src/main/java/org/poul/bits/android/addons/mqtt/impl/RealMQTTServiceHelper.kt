package org.poul.bits.android.addons.mqtt.impl

import android.content.Context
import android.content.Intent
import eu.depau.kotlet.android.extensions.ui.context.startForegroundServiceCompat
import org.poul.bits.android.addons.mqtt.Constants
import org.poul.bits.android.addons.mqtt.IMQTTServiceHelper

class RealMQTTServiceHelper(
    private val hostname: String,
    private val port: Int,
    private val useTls: Boolean,
    private val sedeTopic: String,
    private val tempTopic: String,
    private val humTopic: String

) : IMQTTServiceHelper {
    override val hasMQTTService: Boolean = true

    private fun getIntent(context: Context): Intent {
        val mqttClass = Class.forName(Constants.SERVICE_CLASS)

        return Intent(context, mqttClass).apply {
            action = Constants.ACTION_START
        }
    }

    override fun startService(context: Context) {
        context.startForegroundServiceCompat(getIntent(context))
    }

    override fun stopService(context: Context) {
        context.stopService(getIntent(context))
    }

    data class Builder(
        var hostname: String?,
        var port: Int?,
        var useTls: Boolean?,
        var sedeTopic: String?,
        var tempTopic: String?,
        var humTopic: String?
    ) {
        constructor() : this(null, null, null, null, null, null)

        fun build() = RealMQTTServiceHelper(
            hostname!!,
            port!!,
            useTls!!,
            sedeTopic!!,
            tempTopic!!,
            humTopic!!
        )
    }
}