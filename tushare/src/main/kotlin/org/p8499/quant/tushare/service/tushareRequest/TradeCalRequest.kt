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
class TradeCalRequest : TushareRequest() {
    override val apiName = "trade_cal"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var exchange: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var startDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var endDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("is_open")
            @get:JsonFormat(shape = JsonFormat.Shape.NUMBER)
            var isOpen: Boolean? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            var exchange: String? = null,

            @set:JsonProperty("cal_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var calDate: Date? = null,

            @set:JsonProperty("is_open")
            @set:JsonFormat(shape = JsonFormat.Shape.NUMBER)
            var isOpen: Boolean? = null,

            @set:JsonProperty("pretrade_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var pretradeDate: Date? = null)
}