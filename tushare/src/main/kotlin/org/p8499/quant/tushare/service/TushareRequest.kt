package org.p8499.quant.tushare.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.TushareRequestBodyFactory
import org.p8499.quant.tushare.feignClient.TushareFeignClient

abstract class TushareRequest {
    abstract val apiName: String
    abstract val objectMapper: ObjectMapper
    abstract val tushareRequestBodyFactory: TushareRequestBodyFactory
    abstract val tushareFeignClient: TushareFeignClient
    fun <I, O> invoke(inParams: I, outParamsClass: Class<O>, fields: Array<String> = arrayOf()): Array<O> {
        val responseBody = tushareRequestBodyFactory.tushareRequestBody(apiName, inParams, fields).let(tushareFeignClient::request)
        if (responseBody.code == 0)
            return objectMapper.convertValue(responseBody.data, objectMapper.typeFactory.constructArrayType(outParamsClass))
        else
            throw RuntimeException("Error invoking $apiName, Code = ${responseBody.code}, Message = ${responseBody.msg}")
    }
}