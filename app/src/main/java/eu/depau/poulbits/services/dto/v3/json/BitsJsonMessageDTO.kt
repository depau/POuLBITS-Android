package eu.depau.poulbits.services.dto.v3

import java.util.*

data class BitsJsonMessageDTO(
    val user: String,
    val message: String,
    val timestamp: Date
) {

    fun toStandardDTO(): BitsMessageDTO {
        return BitsMessageDTO(
            user,
            message,
            timestamp
        )
    }
}