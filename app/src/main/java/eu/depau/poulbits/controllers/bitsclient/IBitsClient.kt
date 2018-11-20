package eu.depau.poulbits.controllers.bitsclient

import eu.depau.poulbits.model.BitsData

interface IBitsClient {
    fun getData(): BitsData
}