package eu.depau.poulbits.controllers.bitsclient.dto.v3.json

import eu.depau.poulbits.controllers.bitsclient.dto.v3.IBitsDataDTO
import eu.depau.poulbits.model.BitsData
import eu.depau.poulbits.model.enum.BitsStatus
import java.util.*

data class BitsJsonDataDTO(
    val value: String,
    val modifiedBy: String,
    val timestamp: Date,
    val version: Int,
    val tempint: BitsJsonTemperatureDataDTO,
    val message: BitsJsonMessageDTO,
    val tempinthist: List<BitsJsonTemperatureDataDTO>
) : IBitsDataDTO {
    init {
        assert(version == 3) { "BITS version $version is not supported" }
    }

    override fun toBitsData(): BitsData {
        return BitsData(
            status = when (value) {
                "open" -> BitsStatus.OPEN
                "closed" -> BitsStatus.CLOSED
                else -> throw IllegalArgumentException("Invalid status string: $value")
            },
            modifiedBy = modifiedBy,
            lastModified = timestamp,
            temperature = tempint.toBitsTemperature(),
            message = message.toBitsMessage(),
            temperatureHistory = tempinthist.map { it.toBitsTemperature() }
        )
    }
}