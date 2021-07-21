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
class StockBasicRequest : TushareRequest<StockBasicRequest.InParams, StockBasicRequest.OutParams>() {
    override val apiName = "stock_basic"

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
            @get:JsonProperty("is_hs")
            var isHs: Char? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("list_status")
            var listStatus: Char? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var exchange: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var market: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var limit: Int? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var offset: Int? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var name: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("ts_code")
            var tsCode: String? = null,

            var symbol: String? = null,

            var name: String? = null,

            var area: String? = null,

            var industry: String? = null,

            var fullname: String? = null,

            var enname: String? = null,

            var cnspell: String? = null,

            var market: String? = null,

            var exchange: String? = null,

            @set:JsonProperty("curr_type")
            var currType: String? = null,

            @set:JsonProperty("list_status")
            var listStatus: Char? = null,

            @set:JsonProperty("list_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var listDate: Date? = null,

            @set:JsonProperty("delist_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var delistDate: Date? = null,

            @set:JsonProperty("is_hs")
            var isHs: Char? = null)
}