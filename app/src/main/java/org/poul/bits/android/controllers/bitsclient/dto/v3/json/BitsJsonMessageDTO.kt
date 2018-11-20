package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsMessageDTO
import org.poul.bits.android.model.BitsMessage
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