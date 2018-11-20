package eu.depau.poulbits.controllers.bitsclient.dto.v3.json

import eu.depau.poulbits.controllers.bitsclient.dto.v3.IBitsMessageDTO
import eu.depau.poulbits.model.BitsMessage
import java.util.*

data class BitsJsonMessageDTO(
    val user: String,
    val message: String,
    val timestamp: Date
): IBitsMessageDTO {

    override fun toBitsMessage(): BitsMessage {
        return BitsMessage(
            user,
            message,
            timestamp
        )
    }
}