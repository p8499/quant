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
import java.time.LocalDate

@Service
class DailyBasicRequest : TushareRequest<DailyBasicRequest.InParams, DailyBasicRequest.OutParams>() {
    override val apiName = "daily_basic"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 1000))
    override fun invoke(inParams: InParams, outParamsClass: Class<OutParams>, fields: Array<String>): Array<OutParams> = super.invoke(inParams, outParamsClass, fields)

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("trade_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var tradeDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var startDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var endDate: LocalDate? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("ts_code")
            var tsCode: String? = null,

            @set:JsonProperty("trade_date")
            @set:JsonFormat(pattern = "yyyyMMdd")
            var tradeDate: LocalDate? = null,

            @set:JsonProperty("float_share")
            var floatShare: Double? = null/*wan stocks*/,

            @set:JsonProperty("total_share")
            var totalShare: Double? = null)/*wan stocks*/
}