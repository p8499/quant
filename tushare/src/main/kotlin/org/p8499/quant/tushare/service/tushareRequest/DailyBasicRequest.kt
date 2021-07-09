package org.p8499.quant.tushare.service.tushareRequest

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.TushareRequestBodyFactory
import org.p8499.quant.tushare.feignClient.TushareFeignClient
import org.p8499.quant.tushare.service.TushareRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DailyBasicRequest : TushareRequest() {
    override val apiName = "daily_basic"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("trade_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var tradeDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var startDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var endDate: Date? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("ts_code")
            var tsCode: String? = null,

            @set:JsonProperty("trade_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var tradeDate: Date? = null,

            @set:JsonProperty("float_share")
            var floatShare: Double? = null,

            @set:JsonProperty("total_share")
            var totalShare: Double? = null)
}