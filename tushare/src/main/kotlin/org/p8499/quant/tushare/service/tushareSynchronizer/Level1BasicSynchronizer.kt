package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level1Basic
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.Level1BasicService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.DailyBasicRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class Level1BasicSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level1BasicService: Level1BasicService

    @Autowired
    protected lateinit var dailyBasicRequest: DailyBasicRequest

    fun invoke() {
        log.info("Start Synchronizing Level1Basic")
        val level1BasicIterable: (String) -> Iterable<Level1Basic> = { tsCode ->
            val datesCollection = tradingDateService.unprocessedForLevel1Basic(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.values
            Flowable.fromIterable(datesCollection).zipWith(Flowable.interval(150, TimeUnit.MILLISECONDS)) { dateList, _ -> dateList }
                    .flatMap { Flowable.fromArray(*dailyBasicRequest.invoke(DailyBasicRequest.InParams(tsCode = tsCode, startDate = it.minOrNull(), endDate = it.maxOrNull()), DailyBasicRequest.OutParams::class.java)) }
                    .map { Level1Basic(tsCode, it.tradeDate, it.totalShare, it.floatShare) }
                    .blockingIterable()
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach {
            level1BasicService.saveAll(level1BasicIterable(it))
            level1BasicService.fillVacancies(it)
        }
        log.info("Finish Synchronizing Level1Basic")
    }
}