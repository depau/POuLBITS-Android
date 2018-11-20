package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsTemperatureDTO
import org.poul.bits.android.model.BitsTemperatureData
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