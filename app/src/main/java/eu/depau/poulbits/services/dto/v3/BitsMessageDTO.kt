package eu.depau.poulbits.services.dto.v3

import java.util.*

data class BitsMessageDTO(
    val user: String,
    val message: String,
    val lastModified: Date
)