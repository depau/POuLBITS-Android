package eu.depau.poulbits.services.impl

import eu.depau.poulbits.services.IBitsClient
import eu.depau.poulbits.services.dto.v3.BitsDataDTO
import eu.depau.poulbits.services.dto.v3.json.BitsJsonDataDTO
import org.springframework.web.client.RestTemplate

class BitsJsonClient : IBitsClient {
    override fun getData(): BitsDataDTO {
        val url = "https://bits.poul.org/data"
        val restTemplate = RestTemplate()
        return restTemplate.getForObject(url, BitsJsonDataDTO::class.java).toStandardDTO()
    }
}