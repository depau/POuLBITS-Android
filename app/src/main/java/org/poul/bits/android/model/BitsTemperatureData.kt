package org.poul.bits.android.model

import java.util.*

data class BitsTemperatureData(
    val value: Double,
    val sensorId: Long,
    val modifiedBy: String,
    val lastModified: Date
)