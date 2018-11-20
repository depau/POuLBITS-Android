package eu.depau.poulbits.controllers.bitsclient.dto.v3

import eu.depau.poulbits.model.BitsTemperatureData

interface IBitsTemperatureDTO {
    fun toBitsTemperature(): BitsTemperatureData
}