package org.p8499.quant.tushare.service.tushareRequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.TushareRequestBodyFactory
import org.p8499.quant.tushare.feignClient.TushareFeignClient
import org.p8499.quant.tushare.service.TushareRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConceptRequest : TushareRequest() {
    override val apiName = "concept"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var src: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            var code: String? = null,

            var name: String? = null)
}