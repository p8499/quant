package org.p8499.quant.tushare

import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.service.TushareRequestBodyFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TushareConfiguration {
    @Autowired
    lateinit var tushareProperties: TushareProperties

    @get:Bean
    val objectMapper = ObjectMapper()

    @Bean
    fun tushareRequestBodyFactory() = TushareRequestBodyFactory(tushareProperties.token)
}