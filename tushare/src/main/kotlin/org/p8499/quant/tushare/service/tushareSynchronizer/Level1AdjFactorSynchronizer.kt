package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.Level1AdjFactorService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.AdjFactorRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1AdjFactorSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var level1AdjFactorService: Level1AdjFactorService

    @Autowired
    protected lateinit var adjFactorRequest: AdjFactorRequest

    fun invoke() {
        log.info("Start Synchronizing Level1AdjFactor")
        val level1AdjFactorList: (String) -> List<Level1AdjFactor> = { tsCode ->
            tradingDateService.unprocessedForLevel1AdjFactor(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { adjFactorRequest.invoke(AdjFactorRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), AdjFactorRequest.OutParams::class.java).asList() }
                    .map { Level1AdjFactor(tsCode, it.tradeDate, it.adjFactor) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach {
            level1AdjFactorService.saveAll(level1AdjFactorList(it))
            level1AdjFactorService.fillVacancies(it)
        }
        log.info("Finish Synchronizing Level1AdjFactor")
    }
}