package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.Level1AdjFactorService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.AdjFactorRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class Level1AdjFactorSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level1AdjFactorService: Level1AdjFactorService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var adjFactorRequest: AdjFactorRequest

    fun invoke() {
        logger.info("Start Synchronizing Level1AdjFactor")
        controllerService.begin("Level1AdjFactor", LocalDateTime.now())
        val level1AdjFactorIterable: (String) -> Iterable<Level1AdjFactor> = { tsCode ->
            val datesCollection = tradingDateService.unprocessedForLevel1AdjFactor(tsCode).mapNotNull(TradingDate::date).groupBy(LocalDate::getYear).values
            Flowable.fromIterable(datesCollection)
                    .flatMap { Flowable.fromArray(*adjFactorRequest.invoke(AdjFactorRequest.InParams(tsCode = tsCode, startDate = it.minOrNull(), endDate = it.maxOrNull()), AdjFactorRequest.OutParams::class.java)) }
                    .map { Level1AdjFactor(tsCode, it.tradeDate, it.adjFactor) }
                    .blockingIterable()
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach {
            level1AdjFactorService.saveAll(level1AdjFactorIterable(it))
            level1AdjFactorService.fillVacancies(it)
        }
        controllerService.end("Level1AdjFactor")
        logger.info("Finish Synchronizing Level1AdjFactor")
    }
}