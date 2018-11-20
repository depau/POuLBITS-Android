package org.poul.bits.android.controllers.bitsclient.dto.v3

import org.poul.bits.android.model.BitsMessage

interface IBitsMessageDTO {
    fun toBitsMessage(): BitsMessage
}