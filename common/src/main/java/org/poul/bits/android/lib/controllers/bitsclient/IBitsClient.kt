package org.poul.bits.android.lib.controllers.bitsclient

import org.poul.bits.android.lib.model.BitsData

interface IBitsClient {
    fun downloadData(url: String = "https://bits.poul.org/data"): BitsData
    fun downloadPresenceSVG(url: String = "https://bits.poul.org/presence.svg"): String
}