package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.quantNotifier.QuantNotifierFactory
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TushareTask {
    protected val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var exchangeSynchronizer: ExchangeSynchronizer

    @Autowired
    protected lateinit var tradingDateSynchronizer: TradingDateSynchronizer

    @Autowired
    protected lateinit var stockSynchronizer: StockSynchronizer

    @Autowired
    protected lateinit var groupSynchronizer: GroupSynchronizer

    @Autowired
    protected lateinit var groupStockSynchronizer: GroupStockSynchronizer

    @Autowired
    protected lateinit var level1CandlestickSynchronizer: Level1CandlestickSynchronizer

    @Autowired
    protected lateinit var level1BasicSynchronizer: Level1BasicSynchronizer

    @Autowired
    protected lateinit var level1AdjFactorSynchronizer: Level1AdjFactorSynchronizer

    @Autowired
    protected lateinit var level2Synchronizer: Level2Synchronizer

    @Autowired
    protected lateinit var balanceSheetSynchronizer: BalanceSheetSynchronizer

    @Autowired
    protected lateinit var incomeSynchronizer: IncomeSynchronizer

    @Autowired
    protected lateinit var cashflowSynchronizer: CashflowSynchronizer

    @Autowired
    protected lateinit var expressSynchronizer: ExpressSynchronizer

    @Autowired
    protected lateinit var forecastSynchronizer: ForecastSynchronizer

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var tushareNotifierFactory: QuantNotifierFactory

    @Scheduled(cron = "0 0 18 * * MON-FRI")
    fun syncAndNotify() {
        expressSynchronizer.invoke()
        forecastSynchronizer.invoke()
//        val a = CompletableFuture.allOf(
//                CompletableFuture.runAsync(exchangeSynchronizer::invoke).thenRunAsync(tradingDateSynchronizer::invoke),
//                CompletableFuture.runAsync(stockSynchronizer::invoke))
//        val b = a.thenRunAsync(level1BasicSynchronizer::invoke)
//        val c = a.thenComposeAsync {
//            CompletableFuture.allOf(
//                    CompletableFuture.runAsync(level1CandlestickSynchronizer::invoke),
//                    CompletableFuture.runAsync(level1AdjFactorSynchronizer::invoke),
//                    CompletableFuture.runAsync(level2Synchronizer::invoke),
//                    CompletableFuture.runAsync(balanceSheetSynchronizer::invoke),
//                    CompletableFuture.runAsync(incomeSynchronizer::invoke),
//                    CompletableFuture.runAsync(cashflowSynchronizer::invoke),
//                    CompletableFuture.runAsync(expressSynchronizer::invoke),
//                    CompletableFuture.runAsync(forecastSynchronizer::invoke))
//        }
//        val d = CompletableFuture.runAsync(groupSynchronizer::invoke)
//        val e = CompletableFuture.allOf(b, d).thenRunAsync(groupStockSynchronizer::invoke)
//        CompletableFuture.allOf(c, e).join()
    }
}