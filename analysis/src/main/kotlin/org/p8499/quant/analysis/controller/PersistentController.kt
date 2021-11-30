package org.p8499.quant.analysis.controller

import org.p8499.quant.analysis.dto.GroupDto
import org.p8499.quant.analysis.dto.StockDto
import org.p8499.quant.analysis.entity.*
import org.p8499.quant.analysis.service.PersistentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/persistent"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
class PersistentController {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var persistentService: PersistentService

    @RequestMapping(method = [RequestMethod.GET], path = ["/find_stock"])
    fun findStock(@RequestParam region: String, @RequestParam groupId: String): List<StockDto> {
        val stockDtoList = persistentService.findStockByGroup(region, groupId).map {
            StockDto(it.region ?: "", it.id ?: "", it.name ?: "", it.message ?: "")
        }
        stockDtoList.forEach {
            persistentService.findStockIndexDaily(it.region, it.id, "open").run {
                it.date = mapNotNull(StockIndexDaily::date)
                it.open = map(StockIndexDaily::value)
            }
            it.close = persistentService.findStockIndexDaily(it.region, it.id, "close").map(StockIndexDaily::value)
            it.high = persistentService.findStockIndexDaily(it.region, it.id, "high").map(StockIndexDaily::value)
            it.low = persistentService.findStockIndexDaily(it.region, it.id, "low").map(StockIndexDaily::value)
            it.volume = persistentService.findStockIndexDaily(it.region, it.id, "volume").map(StockIndexDaily::value)
            it.amount = persistentService.findStockIndexDaily(it.region, it.id, "amount").map(StockIndexDaily::value)
            it.flowShare = persistentService.findStockIndexDaily(it.region, it.id, "flowShare").map(StockIndexDaily::value)
            it.totalShare = persistentService.findStockIndexDaily(it.region, it.id, "totalShare").map(StockIndexDaily::value)
            it.flowValue = persistentService.findStockIndexDaily(it.region, it.id, "flowValue").map(StockIndexDaily::value)
            it.totalValue = persistentService.findStockIndexDaily(it.region, it.id, "totalValue").map(StockIndexDaily::value)
            it.pb = persistentService.findStockIndexDaily(it.region, it.id, "pb").map(StockIndexDaily::value)
            it.pe = persistentService.findStockIndexDaily(it.region, it.id, "pe").map(StockIndexDaily::value)
            it.ps = persistentService.findStockIndexDaily(it.region, it.id, "ps").map(StockIndexDaily::value)
            it.pcf = persistentService.findStockIndexDaily(it.region, it.id, "pcf").map(StockIndexDaily::value)
        }
        return stockDtoList
    }

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/complete"])
    fun complete(@RequestParam region: String) = persistentService.complete(region)

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/save_stock"])
    fun saveStock(@RequestBody stockDto: StockDto) = persistentService.saveStock(
            stockDto.region, stockDto.id,
            Stock(stockDto.region, stockDto.id, stockDto.name, stockDto.message),
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "open", date, stockDto.open[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "close", date, stockDto.close[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "high", date, stockDto.high[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "low", date, stockDto.low[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "volume", date, stockDto.volume[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "amount", date, stockDto.amount[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "flowShare", date, stockDto.flowShare[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "totalShare", date, stockDto.totalShare[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "flowValue", date, stockDto.flowValue[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "totalValue", date, stockDto.totalValue[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "pb", date, stockDto.pb[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "pe", date, stockDto.pe[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "ps", date, stockDto.ps[index]) },
            stockDto.date.mapIndexed { index, date -> StockIndexDaily(stockDto.region, stockDto.id, "pcf", date, stockDto.pcf[index]) })

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/save_group"])
    fun saveGroup(@RequestBody groupDto: GroupDto) = persistentService.saveGroup(
            groupDto.region, groupDto.id,
            Group(groupDto.region, groupDto.id, groupDto.name, groupDto.message),
            groupDto.stockIdList.mapIndexed { index, stockId -> GroupStock(groupDto.region, groupDto.id, stockId, groupDto.percentList[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "open", date, groupDto.open[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "close", date, groupDto.close[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "high", date, groupDto.high[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "low", date, groupDto.low[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "volume", date, groupDto.volume[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "amount", date, groupDto.amount[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "flowShare", date, groupDto.flowShare[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "totalShare", date, groupDto.totalShare[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "flowValue", date, groupDto.flowValue[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "totalValue", date, groupDto.totalValue[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "pb", date, groupDto.pb[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "pe", date, groupDto.pe[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "ps", date, groupDto.ps[index]) },
            groupDto.date.mapIndexed { index, date -> GroupIndexDaily(groupDto.region, groupDto.id, "pcf", date, groupDto.pcf[index]) })
}