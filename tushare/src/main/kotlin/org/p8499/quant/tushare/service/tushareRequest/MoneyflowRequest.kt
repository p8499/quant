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
class MoneyflowRequest : TushareRequest<MoneyflowRequest.InParams, MoneyflowRequest.OutParams>() {
    override val apiName = "moneyflow"

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

            @set:JsonProperty("buy_sm_vol")
            var buySmVol: Double? = null,

            @set:JsonProperty("buy_sm_amount")
            var buySmAmount: Double? = null,

            @set:JsonProperty("sell_sm_vol")
            var sellSmVol: Double? = null,

            @set:JsonProperty("sell_sm_amount")
            var sellSmAmount: Double? = null,

            @set:JsonProperty("buy_md_vol")
            var buyMdVol: Double? = null,

            @set:JsonProperty("buy_md_amount")
            var buyMdAmount: Double? = null,

            @set:JsonProperty("sell_md_vol")
            var sellMdVol: Double? = null,

            @set:JsonProperty("sell_md_amount")
            var sellMdAmount: Double? = null,

            @set:JsonProperty("buy_lg_vol")
            var buyLgVol: Double? = null,

            @set:JsonProperty("buy_lg_amount")
            var buyLgAmount: Double? = null,

            @set:JsonProperty("sell_lg_vol")
            var sellLgVol: Double? = null,

            @set:JsonProperty("sell_lg_amount")
            var sellLgAmount: Double? = null,

            @set:JsonProperty("buy_elg_vol")
            var buyElgVol: Double? = null,

            @set:JsonProperty("buy_elg_amount")
            var buyElgAmount: Double? = null,

            @set:JsonProperty("sell_elg_vol")
            var sellElgVol: Double? = null,

            @set:JsonProperty("sell_elg_amount")
            var sellElgAmount: Double? = null)
}