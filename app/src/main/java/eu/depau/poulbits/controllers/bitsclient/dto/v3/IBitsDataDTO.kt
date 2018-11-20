package eu.depau.poulbits.controllers.bitsclient.dto.v3

import eu.depau.poulbits.model.BitsData

interface IBitsDataDTO {
    fun toBitsData(): BitsData
}