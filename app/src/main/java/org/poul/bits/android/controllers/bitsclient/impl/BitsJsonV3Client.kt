package org.poul.bits.android.controllers.bitsclient.impl

import org.poul.bits.android.controllers.bitsclient.IBitsClient
import org.poul.bits.android.model.BitsData
import org.poul.bits.android.controllers.bitsclient.dto.v3.json.BitsJsonDataDTO
import org.springframework.web.client.RestTemplate

class BitsJsonV3Client : IBitsClient {
    override fun downloadData(): BitsData {
        val url = "https://bits.poul.org/data"
        val restTemplate = RestTemplate()
        return restTemplate.getForObject(url, BitsJsonDataDTO::class.java).toBitsData()
    }
}