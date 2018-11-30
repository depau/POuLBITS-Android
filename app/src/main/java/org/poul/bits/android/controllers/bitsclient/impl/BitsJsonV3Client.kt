package org.poul.bits.android.controllers.bitsclient.impl

import org.poul.bits.android.controllers.bitsclient.IBitsClient
import org.poul.bits.android.controllers.bitsclient.dto.v3.json.BitsJsonDTO
import org.poul.bits.android.model.BitsData
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

    override fun downloadData(url: String): BitsData {
        return restTemplate.getForObject(url, BitsJsonDTO::class.java).toBitsData()
    }
}