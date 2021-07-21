package org.p8499.quant.tushare.service.tushareRequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.feignClient.TushareFeignClient
import org.p8499.quant.tushare.service.TushareRequest
import org.p8499.quant.tushare.service.TushareRequestBodyFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class ConceptRequest : TushareRequest<ConceptRequest.InParams, ConceptRequest.OutParams>() {
    override val apiName = "concept"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    @Retryable(maxAttempts = 10, backoff = Backoff(delay = 5000))
    override fun invoke(inParams: InParams, outParamsClass: Class<OutParams>, fields: Array<String>): Array<OutParams> = super.invoke(inParams, outParamsClass, fields)

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var src: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            var code: String? = null,

            var name: String? = null)
}