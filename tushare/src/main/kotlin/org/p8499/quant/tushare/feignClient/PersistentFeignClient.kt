package org.p8499.quant.tushare.feignClient

import org.p8499.quant.tushare.dto.SecurityDayDto
import org.p8499.quant.tushare.dto.SecurityDto
import org.p8499.quant.tushare.dto.SecurityQuarterDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@FeignClient(name = "persistent", url = "\${analysis.persistent.url}")
interface PersistentFeignClient {
    @PostMapping(path = ["/begin"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun begin(@RequestParam region: String, @RequestParam snapshot: LocalDateTime)

    @PostMapping(path = ["/security"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveSecurity(@RequestBody securityDto: SecurityDto)

    @PostMapping(path = ["/security-day"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveSecurityDay(@RequestBody securityDayDto: SecurityDayDto)

    @PostMapping(path = ["/security-quarter"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveSecurityQuarter(@RequestBody securityQuarterDto: SecurityQuarterDto)

    @PostMapping(path = ["/end"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun end(@RequestParam region: String)
}