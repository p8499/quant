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
class IndexMemberRequest : TushareRequest() {
    override val apiName = "index_member"

    @Autowired
    override lateinit var objectMapper: ObjectMapper

    @Autowired
    override lateinit var tushareRequestBodyFactory: TushareRequestBodyFactory

    @Autowired
    override lateinit var tushareFeignClient: TushareFeignClient

    class InParams(
            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("index_code")
            var indexCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("ts_code")
            var tsCode: String? = null,

            @get:JsonInclude(JsonInclude.Include.NON_NULL)
            @get:JsonProperty("is_new")
            var isNew: Char? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    class OutParams(
            @set:JsonProperty("index_code")
            var indexCode: String? = null,

            @set:JsonProperty("index_name")
            var indexName: String? = null,

            @set:JsonProperty("con_code")
            var conCode: String? = null,

            @set:JsonProperty("con_name")
            var conName: String? = null,

            @set:JsonProperty("in_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var inDate: Date? = null,

            @set:JsonProperty("out_date")
            @set:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
            var outDate: Date? = null,

            @set:JsonProperty("is_new")
            var isNew: Char? = null)
}