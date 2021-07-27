package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level2
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.Level2Service
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.MoneyflowRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level2Synchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level2Service: Level2Service

    @Autowired
    protected lateinit var moneyflowRequest: MoneyflowRequest

    fun invoke() {
        log.info("Start Synchronizing Level2")
        val level2List: (String) -> List<Level2> = { tsCode ->
            tradingDateService.unprocessedForLevel2(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { moneyflowRequest.invoke(MoneyflowRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), MoneyflowRequest.OutParams::class.java).asList() }
                    .map { Level2(tsCode, it.tradeDate, it.buySmVol, it.sellSmVol, it.buyMdVol, it.sellMdVol, it.buyLgVol, it.sellLgVol, it.buyElgVol, it.sellElgVol) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level2Service.saveAll(level2List(it)) }
        log.info("Finish Synchronizing Level2")
    }
}