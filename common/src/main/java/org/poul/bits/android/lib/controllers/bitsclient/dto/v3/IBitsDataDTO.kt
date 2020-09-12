package org.poul.bits.android.lib.controllers.bitsclient.dto.v3

import org.poul.bits.android.lib.model.BitsData

interface IBitsDataDTO {
    fun toBitsData(): BitsData
}