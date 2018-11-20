package eu.depau.poulbits.services

import eu.depau.poulbits.services.dto.v3.BitsDataDTO

interface IBitsClient {
    fun getData(): BitsDataDTO
}