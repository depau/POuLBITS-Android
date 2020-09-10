package org.poul.bits.android.lib.controllers.bitsclient.impl

import org.poul.bits.android.lib.controllers.bitsclient.IBitsClient
import org.poul.bits.android.lib.controllers.bitsclient.dto.v3.json.BitsJsonDTO
import org.poul.bits.android.lib.model.BitsData
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

class BitsJsonV3Client : IBitsClient {
    private val restTemplate = RestTemplate().also {
        it.messageConverters = listOf(
            MappingJackson2HttpMessageConverter().also { mapping ->
                mapping.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON)
            }
        )
    }
    private val plainRestTemplate = RestTemplate()

    override fun downloadData(url: String): BitsData {
        return restTemplate.getForObject(url, BitsJsonDTO::class.java).toBitsData()
    }

    override fun downloadPresenceSVG(url: String): String {
        return plainRestTemplate.getForObject(url, String::class.java)
    }
}