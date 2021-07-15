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
class ConceptDetailRequest : TushareRequest() {
    override val apiName = "concept_detail"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            var id: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("ts_code")
            var tsCode: String? = null)

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            var id: String? = null,

            @set:JsonProperty("concept_name")
            var conceptName: String? = null,

            @set:JsonProperty("ts_code")
            var tsCode: String? = null,

            var name: String? = null,

            @set:JsonProperty("in_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var inDate: Date? = null,

            @set:JsonProperty("out_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var outDate: Date? = null)
}