package org.p8499.quant.tushare.feignClient

import org.p8499.quant.tushare.dto.GroupDto
import org.p8499.quant.tushare.dto.StockDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "persistent", url = "\${analysis.persistent.url}")
interface PersistentFeignClient {
    @PostMapping(path = ["/stock"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun stock(@RequestBody stockDto: StockDto)

    @PostMapping(path = ["/group"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun group(@RequestBody groupDto: GroupDto)
}