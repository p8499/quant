package org.p8499.quant.analysis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AnalysisConfiguration {
    @get:Bean
    val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }
}