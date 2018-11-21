package org.poul.bits.android.controllers.bitsclient.impl

import org.poul.bits.android.controllers.bitsclient.IBitsClient
import org.poul.bits.android.controllers.bitsclient.dto.v3.json.BitsJsonDTO
import org.poul.bits.android.model.BitsData
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

class BitsJsonV3Client : IBitsClient {
    override fun downloadData(): BitsData {
        val url = "https://bits.poul.org/data"
        return restTemplate.getForObject(url, BitsJsonDTO::class.java).toBitsData()
    }
}