package org.p8499.quant.tushare.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.text.StringEscapeUtils
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.feignClient.TushareFeignClient
import org.p8499.quant.tushare.feignClient.TushareRequestBody
import org.p8499.quant.tushare.feignClient.TushareResponseBody
import org.slf4j.LoggerFactory

class TushareRequestBodyFactory(val token: String) {
    fun <T> tushareRequestBody(apiName: String, params: T, fields: Array<String>) = TushareRequestBody(apiName, token, params, fields)
}

abstract class TushareRequest<I, O> {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    abstract val apiName: String

    abstract val objectMapper: ObjectMapper

    abstract val tushareRequestBodyFactory: TushareRequestBodyFactory

    abstract val tushareFeignClient: TushareFeignClient

    open fun invoke(inParams: I, outParamsClass: Class<O>, fields: Array<String> = arrayOf()): Array<O> {
        val requestBody = tushareRequestBodyFactory.tushareRequestBody(apiName, inParams, fields)
        val responseStr = tushareFeignClient.request(requestBody)
        try {
            val responseBody = objectMapper.readValue(responseStr, TushareResponseBody::class.java)
            return objectMapper.convertValue(responseBody.data, objectMapper.typeFactory.constructArrayType(outParamsClass))
        } catch (e: Throwable) {
            logger.error("requestBody: ${objectMapper.writeValueAsString(requestBody)}")
            logger.error("responseStr: ${StringEscapeUtils.unescapeJava(responseStr)}")
            throw e
        }
    }
}