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
import java.util.*

@Service
class TradeCalRequest : TushareRequest<TradeCalRequest.InParams, TradeCalRequest.OutParams>() {
    override val apiName = "trade_cal"

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
            var exchange: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var startDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var endDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("is_open")
            var isOpen: Int? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            var exchange: String? = null,

            @set:JsonProperty("cal_date")
            @set:JsonFormat(pattern = "yyyyMMdd")
            var calDate: LocalDate? = null,

            @set:JsonProperty("is_open")
            var isOpen: Int? = null,

            @set:JsonProperty("pretrade_date")
            @set:JsonFormat(pattern = "yyyyMMdd")
            var pretradeDate: LocalDate? = null)
}