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
import org.springframework.stereotype.Service
import java.util.*

@Service
class ForecastRequest : TushareRequest() {
    override val apiName = "forecast"

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
            @get:JsonProperty("ann_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var annDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var startDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var endDate: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var period: Date? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var type: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @set:JsonProperty("ann_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var annDate: Date? = null,

            @set:JsonProperty("end_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var endDate: Date? = null,

            var type: String? = null,

            @set:JsonProperty("p_change_min")
            var pChangeMin: Double? = null,

            @set:JsonProperty("p_change_max")
            var pChangeMax: Double? = null,

            @set:JsonProperty("change_reason")
            var changeReason: String? = null)
}