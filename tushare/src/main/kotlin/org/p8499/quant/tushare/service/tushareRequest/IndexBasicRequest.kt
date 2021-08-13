package org.p8499.quant.tushare.service.tushareRequest

import com.fasterxml.jackson.annotation.JsonFormat
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
import java.util.*

@Service
class IndexBasicRequest : TushareRequest<IndexBasicRequest.InParams, IndexBasicRequest.OutParams>() {
    override val apiName = "index_basic"

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
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var name: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var market: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var publisher: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var category: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("ts_code")
            var tsCode: String? = null,

            var name: String? = null,

            var fullname: String? = null,

            var market: String? = null,

            var publisher: String? = null,

            @set:JsonProperty("index_type")
            var indexType: String? = null,

            var category: String? = null,

            @set:JsonProperty("base_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var baseDate: Date? = null,

            @set:JsonProperty("base_point")
            var basePoint: Double? = null,

            @set:JsonProperty("list_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var listDate: Date? = null,

            @set:JsonProperty("weight_rule")
            var weightRule: String? = null,

            var desc: String? = null,

            @set:JsonProperty("exp_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var expDate: Date? = null)
}