package org.poul.bits.android.lib.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class BitsJsonStatusDTO(
    @JsonProperty("value") val value: String,
    @JsonProperty("modifiedby") val modifiedBy: String,

    @JsonProperty("timestamp")
    @JsonFormat(pattern = JSON_TIME_FORMAT, timezone = JSON_TIMEZONE)
    val timestamp: Date
)