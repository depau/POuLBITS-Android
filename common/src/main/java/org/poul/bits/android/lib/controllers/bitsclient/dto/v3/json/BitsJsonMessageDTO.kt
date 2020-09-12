package org.poul.bits.android.lib.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.poul.bits.android.lib.controllers.bitsclient.dto.v3.IBitsMessageDTO
import org.poul.bits.android.lib.model.BitsMessage
import java.util.*

data class BitsJsonMessageDTO(
    @JsonProperty("user") val user: String,
    @JsonProperty("value") val value: String,

    @JsonProperty("timestamp")
    @JsonFormat(pattern = JSON_TIME_FORMAT, timezone = JSON_TIMEZONE)
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