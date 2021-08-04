package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.Level1CandlestickService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.DailyRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1CandlestickSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level1CandlestickService: Level1CandlestickService

    @Autowired
    protected lateinit var dailyRequest: DailyRequest

    fun invoke() {
        log.info("Start Synchronizing Level1Candlestick")
        val level1CandlestickList: (String) -> List<Level1Candlestick> = { tsCode ->
            tradingDateService.unprocessedForLevel1Candlestick(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { dailyRequest.invoke(DailyRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), DailyRequest.OutParams::class.java).asList() }
                    .map { Level1Candlestick(tsCode, it.tradeDate, it.open, it.close, it.high, it.low, it.vol, it.amount) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach {
            level1CandlestickService.saveAll(level1CandlestickList(it))
            level1CandlestickService.fillVacancies(it)
        }
        log.info("Finish Synchronizing Level1Candlestick")
    }
}