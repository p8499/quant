package org.p8499.quant.analysis.controller

import org.p8499.quant.analysis.policy.PolicyPool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping(path = ["/policy"], produces = [MediaType.TEXT_PLAIN_VALUE])
class PolicyController {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    protected val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")

    @Autowired
    protected lateinit var policyPool: PolicyPool

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    fun index(): String = policyPool["CN"]?.let { "更新时间: ${dateFormat.format(it.updated)}\n\n${it.log}" } ?: ""
}