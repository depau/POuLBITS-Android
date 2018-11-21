package org.poul.bits.android.controllers.bitsclient.dto.v3.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class BitsJsonStatusDTO(
    @JsonProperty("value") val value: String,
    @JsonProperty("modifiedby") val modifiedBy: String,

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: Date
)