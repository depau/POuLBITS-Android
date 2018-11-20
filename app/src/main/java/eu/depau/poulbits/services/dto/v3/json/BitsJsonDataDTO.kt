package eu.depau.poulbits.services.dto.v3.json

import eu.depau.poulbits.services.dto.v3.BitsDataDTO
import eu.depau.poulbits.services.dto.v3.BitsJsonMessageDTO
import eu.depau.poulbits.services.dto.v3.BitsJsonTemperatureDataDTO
import eu.depau.poulbits.services.enum.BitsStatus
import java.util.*

data class BitsJsonDataDTO(
    val value: String,
    val modifiedBy: String,
    val timestamp: Date,
    val version: Int,
    val tempint: BitsJsonTemperatureDataDTO,
    val message: BitsJsonMessageDTO,
    val tempinthist: List<BitsJsonTemperatureDataDTO>
) {
    init {
        assert(version == 3) { "BITS version $version is not supported" }
    }

    fun toStandardDTO(): BitsDataDTO {
        return BitsDataDTO(
            status = when (value) {
                "open" -> BitsStatus.OPEN
                "closed" -> BitsStatus.CLOSED
                else -> throw IllegalArgumentException("Invalid status string: $value")
            },
            modifiedBy = modifiedBy,
            lastModified = timestamp,
            version = version,
            temperature = tempint.toStandardDTO(),
            message = message.toStandardDTO(),
            temperatureHistory = tempinthist.map { it.toStandardDTO() }
        )
    }
}