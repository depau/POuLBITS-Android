package eu.depau.poulbits.services.dto.v3

import java.util.*

data class BitsJsonTemperatureDataDTO(
    val value: Double,
    val sensor: Long,
    val modifiedBy: String,
    val timestamp: Date
) {

    fun toStandardDTO(): BitsTemperatureDataDTO {
        return BitsTemperatureDataDTO(
            value,
            sensor,
            modifiedBy,
            timestamp
        )
    }
}