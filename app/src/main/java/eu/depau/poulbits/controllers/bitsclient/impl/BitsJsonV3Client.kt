package eu.depau.poulbits.controllers.bitsclient.impl

import eu.depau.poulbits.controllers.bitsclient.IBitsClient
import eu.depau.poulbits.model.BitsData
import eu.depau.poulbits.controllers.bitsclient.dto.v3.json.BitsJsonDataDTO
import org.springframework.web.client.RestTemplate

class BitsJsonV3Client : IBitsClient {
    override fun getData(): BitsData {
        val url = "https://bits.poul.org/data"
        val restTemplate = RestTemplate()
        return restTemplate.getForObject(url, BitsJsonDataDTO::class.java).toBitsData()
    }
}