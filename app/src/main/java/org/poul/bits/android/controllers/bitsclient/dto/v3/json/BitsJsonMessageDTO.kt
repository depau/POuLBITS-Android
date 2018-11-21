package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.poul.bits.android.controllers.bitsclient.dto.v3.IBitsMessageDTO
import org.poul.bits.android.model.BitsMessage
import java.util.*

data class BitsJsonMessageDTO(
    @JsonProperty("user") val user: String,
    @JsonProperty("value") val value: String,

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: Date

) : IBitsMessageDTO {

    override fun toBitsMessage(): BitsMessage {
        return BitsMessage(
            user,
            value,
            timestamp
        )
    }
}