package org.poul.bits.android.controllers.bitsclient.dto.v3

import org.poul.bits.android.model.BitsData

interface IBitsDataDTO {
    fun toBitsData(): BitsData
}