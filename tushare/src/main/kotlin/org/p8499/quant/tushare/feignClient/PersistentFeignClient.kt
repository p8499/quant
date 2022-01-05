package org.p8499.quant.tushare.feignClient

import org.p8499.quant.tushare.dto.GroupDto
import org.p8499.quant.tushare.dto.StockDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "persistent", url = "\${analysis.persistent.url}")
interface PersistentFeignClient {
    @PostMapping(path = ["/save_stock"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveStock(@RequestBody stockDto: StockDto)

    @PostMapping(path = ["/save_group"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveGroup(@RequestBody groupDto: GroupDto)

    @PostMapping(path = ["/complete"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun complete(@RequestParam region: String = "CN")
}