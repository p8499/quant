package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ControllerService
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
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level1CandlestickService: Level1CandlestickService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var dailyRequest: DailyRequest

    fun invoke() {
        logger.info("Start Synchronizing Level1Candlestick")
        val level1CandlestickIterable: (String) -> Iterable<Level1Candlestick> = { tsCode ->
            val datesCollection = tradingDateService.unprocessedForLevel1Candlestick(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.values
            Flowable.fromIterable(datesCollection)
                    .flatMap { Flowable.fromArray(*dailyRequest.invoke(DailyRequest.InParams(tsCode = tsCode, startDate = it.minOrNull(), endDate = it.maxOrNull()), DailyRequest.OutParams::class.java)) }
                    .map { Level1Candlestick(tsCode, it.tradeDate, it.open, it.close, it.high, it.low, it.vol?.times(100), it.amount?.times(1000)) }
                    .blockingIterable()
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach {
            level1CandlestickService.saveAll(level1CandlestickIterable(it))
            level1CandlestickService.fillVacancies(it)
        }
        controllerService.complete("Level1Candlestick")
        logger.info("Finish Synchronizing Level1Candlestick")
    }
}