package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonProperty
import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsDataDTO
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.enum.BitsDataSource
import org.poul.bits.android.model.enum.BitsStatus

data class BitsJsonDTO(
    @JsonProperty("status") val status: BitsJsonStatusDTO,
    @JsonProperty("message") val message: BitsJsonMessageDTO,
    @JsonProperty("tempint") val tempint: BitsJsonTemperatureDataDTO,
    @JsonProperty("tempinthist") val tempinthist: List<BitsJsonTemperatureDataDTO>,
    @JsonProperty("version") val version: Int
) : IBitsDataDTO {
    init {
        assert(version == 3) { "BITS version $version is not supported" }
    }

    override fun toBitsData(): BitsData {
        return BitsData(
            status = when (status.value) {
                "open" -> BitsStatus.OPEN
                "closed" -> BitsStatus.CLOSED
                else -> throw IllegalArgumentException("Invalid status string: ${status.value}")
            },
            modifiedBy = status.modifiedBy,
            lastModified = status.timestamp,
            sensors = listOf(tempint.toBitsTemperature()),
            message = message.toBitsMessage(),
            sensorsHistory = tempinthist.map { it.toBitsTemperature() },
            source = BitsDataSource.JSON
        )
    }
}