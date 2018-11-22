package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsTemperatureDTO
import org.poul.bits.android.model.BitsTemperatureData
import java.util.*

data class BitsJsonTemperatureDataDTO(
    @JsonProperty("value") val value: Double,
    @JsonProperty("sensor") val sensor: Long,
    @JsonProperty("modifiedby") val modifiedBy: String,

    @JsonProperty("timestamp")
    @JsonFormat(pattern = JSON_TIME_FORMAT, timezone = JSON_TIMEZONE)
    val timestamp: Date

) : IBitsTemperatureDTO {

    override fun toBitsTemperature(): BitsTemperatureData {
        return BitsTemperatureData(
            value,
            sensor,
            modifiedBy,
            timestamp
        )
    }
}