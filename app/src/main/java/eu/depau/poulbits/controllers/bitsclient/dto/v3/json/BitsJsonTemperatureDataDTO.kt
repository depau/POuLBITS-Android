package eu.depau.poulbits.controllers.bitsclient.dto.v3.json

import eu.depau.poulbits.controllers.bitsclient.dto.v3.IBitsTemperatureDTO
import eu.depau.poulbits.model.BitsTemperatureData
import java.util.*

data class BitsJsonTemperatureDataDTO(
    val value: Double,
    val sensor: Long,
    val modifiedBy: String,
    val timestamp: Date
): IBitsTemperatureDTO {

    override fun toBitsTemperature(): BitsTemperatureData {
        return BitsTemperatureData(
            value,
            sensor,
            modifiedBy,
            timestamp
        )
    }
}