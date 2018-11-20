package eu.depau.poulbits.services.dto.v3

import eu.depau.poulbits.services.enum.BitsStatus
import java.util.*

data class BitsDataDTO(
    val status: BitsStatus,
    val modifiedBy: String,
    val lastModified: Date,
    val version: Int,
    val temperature: BitsTemperatureDataDTO,
    val message: BitsMessageDTO,
    val temperatureHistory: List<BitsTemperatureDataDTO>
)