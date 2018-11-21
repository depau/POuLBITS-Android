package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsDataDTO
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.model.enum.BitsStatus

class BitsJsonDTO(
    var status: BitsJsonStatusDTO?,
    var message: BitsJsonMessageDTO?,
    var tempint: BitsJsonTemperatureDataDTO?,
    var tempinthist: List<BitsJsonTemperatureDataDTO>?,
    var version: Int?
) : IBitsDataDTO {
    init {
        if (version != null)
            assert(version == 3) { "BITS version $version is not supported" }
    }

    override fun toBitsData(): BitsData {
        return BitsData(
            status = when (status!!.value) {
                "open" -> BitsStatus.OPEN
                "closed" -> BitsStatus.CLOSED
                else -> throw IllegalArgumentException("Invalid status string: ${status!!.value}")
            },
            modifiedBy = status!!.modifiedBy!!,
            lastModified = status!!.timestamp!!,
            temperature = tempint!!.toBitsTemperature(),
            message = message!!.toBitsMessage(),
            temperatureHistory = tempinthist!!.map { it.toBitsTemperature() }
        )
    }
}