package org.poul.bits.android.controllers.bitsclient

import org.poul.bits.android.model.BitsData

interface IBitsClient {
    fun downloadData(): BitsData
}