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
class CashflowRequest : TushareRequest<CashflowRequest.InParams, CashflowRequest.OutParams>() {
    override val apiName = "cashflow"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    @Retryable(maxAttempts = 200, backoff = Backoff(delay = 12000))
    override fun invoke(inParams: InParams, outParamsClass: Class<OutParams>, fields: Array<String>): Array<OutParams> = super.invoke(inParams, outParamsClass, fields)

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
            @get:JsonProperty("report_type")
            var reportType: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("comp_type")
            var compType: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @set:JsonProperty("ann_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var annDate: Date? = null,

            @set:JsonProperty("f_ann_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var fAnnDate: Date? = null,

            @set:JsonProperty("end_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var endDate: Date? = null,

            @set:JsonProperty("report_type")
            var reportType: String? = null,

            @set:JsonProperty("comp_type")
            var compType: String? = null,

            @set:JsonProperty("n_cashflow_act")
            var nCashflowAct: Double? = null,

            @set:JsonProperty("update_flag")
            var updateFlag: Int? = null)
}