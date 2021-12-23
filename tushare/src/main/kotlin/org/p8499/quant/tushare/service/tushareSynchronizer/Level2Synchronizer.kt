package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level2
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.Level2Service
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.MoneyflowRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Service
class Level2Synchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level2Service: Level2Service

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var moneyflowRequest: MoneyflowRequest

    fun invoke() {
        logger.info("Start Synchronizing Level2")
        val level2Iterable: (String) -> Iterable<Level2> = { tsCode ->
            val datesCollection = tradingDateService.unprocessedForLevel2(tsCode).mapNotNull(TradingDate::date).groupBy(LocalDate::getYear).values
            Flowable.fromIterable(datesCollection).zipWith(Flowable.interval(200, TimeUnit.MILLISECONDS)) { dateList, _ -> dateList }
                    .flatMap { Flowable.fromArray(*moneyflowRequest.invoke(MoneyflowRequest.InParams(tsCode = tsCode, startDate = it.minOrNull(), endDate = it.maxOrNull()), MoneyflowRequest.OutParams::class.java)) }
                    .map { Level2(tsCode, it.tradeDate, it.buySmVol, it.sellSmVol, it.buyMdVol, it.sellMdVol, it.buyLgVol, it.sellLgVol, it.buyElgVol, it.sellElgVol) }
                    .blockingIterable()
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level2Service.saveAll(level2Iterable(it)) }
        controllerService.complete("Level2")
        logger.info("Finish Synchronizing Level2")
    }
}