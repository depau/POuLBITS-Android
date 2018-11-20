package eu.depau.poulbits.controllers.bitsclient.dto.v3

import eu.depau.poulbits.model.BitsMessage

interface IBitsMessageDTO {
    fun toBitsMessage(): BitsMessage
}