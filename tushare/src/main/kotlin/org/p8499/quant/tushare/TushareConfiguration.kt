package org.p8499.quant.tushare

import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.feignClient.TushareRequestBody
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tushare")
class TushareRequestBodyFactory {
    lateinit var token: String
    fun <T> tushareRequestBody(apiName: String, params: T, fields: Array<String>) = TushareRequestBody(apiName, token, params, fields)
}

@Configuration
class TushareConfiguration {
    @get:Bean
    val objectMapper = ObjectMapper()
}