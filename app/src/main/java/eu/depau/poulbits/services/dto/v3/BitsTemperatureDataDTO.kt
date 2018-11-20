package eu.depau.poulbits.services.dto.v3

import java.util.*

data class BitsTemperatureDataDTO(
    val value: Double,
    val sensorId: Long,
    val modifiedBy: String,
    val lastModified: Date
)