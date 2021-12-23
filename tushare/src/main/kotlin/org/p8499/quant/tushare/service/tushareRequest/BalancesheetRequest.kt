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
class BalancesheetRequest : TushareRequest<BalancesheetRequest.InParams, BalancesheetRequest.OutParams>() {
    override val apiName = "balancesheet"

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
            @get:JsonProperty("ann_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var annDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("start_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var startDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("end_date")
            @get:JsonFormat(pattern = "yyyyMMdd")
            var endDate: LocalDate? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonFormat(pattern = "yyyyMMdd")
            var period: LocalDate? = null,

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
            @set:JsonFormat(pattern = "yyyyMMdd")
            var annDate: LocalDate? = null,

            @set:JsonProperty("f_ann_date")
            @set:JsonFormat(pattern = "yyyyMMdd")
            var fAnnDate: LocalDate? = null,

            @set:JsonProperty("end_date")
            @set:JsonFormat(pattern = "yyyyMMdd")
            var endDate: LocalDate? = null,

            @set:JsonProperty("report_type")
            var reportType: String? = null,

            @set:JsonProperty("comp_type")
            var compType: String? = null,

            @set:JsonProperty("total_hldr_eqy_exc_min_int")
            var totalHldrEqyExcMinInt: Double? = null,

            @set:JsonProperty("update_flag")
            var updateFlag: Int? = null)
}