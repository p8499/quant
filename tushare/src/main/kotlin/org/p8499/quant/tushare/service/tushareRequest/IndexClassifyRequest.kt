package org.p8499.quant.tushare.service.tushareRequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.feignClient.TushareFeignClient
import org.p8499.quant.tushare.service.TushareRequest
import org.p8499.quant.tushare.service.TushareRequestBodyFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class IndexClassifyRequest : TushareRequest<IndexClassifyRequest.InParams, IndexClassifyRequest.OutParams>() {
    override val apiName = "index_classify"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    @Retryable(maxAttempts = 200, backoff = Backoff(delay = 5000))
    override fun invoke(inParams: InParams, outParamsClass: Class<OutParams>, fields: Array<String>): Array<OutParams> = super.invoke(inParams, outParamsClass, fields)

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("index_code")
            var indexCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var level: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var src: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("index_code")
            var indexCode: String? = null,

            @set:JsonProperty("industry_name")
            var industryName: String? = null,

            var level: String? = null,

            @set:JsonProperty("industry_code")
            var industryCode: String? = null,

            var src: String? = null)
}