package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.GroupService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.quantAnalysis.GroupAnalysis
import org.p8499.quant.tushare.service.quantAnalysis.QuantAnalysisFactory
import org.p8499.quant.tushare.service.quantAnalysis.StockAnalysis
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

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
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var quantAnalysisFactory: QuantAnalysisFactory

    @Autowired
    protected lateinit var amqpTemplate: AmqpTemplate

    /**
     *                                    group ┐
     * exchange -> tradingDate ┐                ├ -> groupStock
     *                         ├┬-> level1Basic ┘
     *                   stock ┘│
     *                          │  ┌ level1Candlestick
     *                          │  │ level1AdjFactor
     *                          │  │ level2
     *                          └->┤ balanceSheet
     *                             │ income
     *                             │ cashflow
     *                             │ express
     *                             └ forecast
     */
    @Scheduled(cron = "0 0 16 * * MON-FRI", zone = "Asia/Shanghai")
    fun syncAndSend() {
        val executor = Executors.newCachedThreadPool()
        val a = CompletableFuture.allOf(
                CompletableFuture.runAsync(exchangeSynchronizer::invoke, executor).thenRunAsync(tradingDateSynchronizer::invoke, executor),
                CompletableFuture.runAsync(stockSynchronizer::invoke, executor))
        val b = a.thenRunAsync(level1BasicSynchronizer::invoke, executor)
        val c = a.thenComposeAsync({
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(level1CandlestickSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(level1AdjFactorSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(level2Synchronizer::invoke, executor),
                    CompletableFuture.runAsync(balanceSheetSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(incomeSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(cashflowSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(expressSynchronizer::invoke, executor),
                    CompletableFuture.runAsync(forecastSynchronizer::invoke, executor))
        }, executor)
        val d = CompletableFuture.runAsync(groupSynchronizer::invoke, executor)
        val e = CompletableFuture.allOf(b, d).thenRunAsync(groupStockSynchronizer::invoke, executor)
        CompletableFuture.allOf(c, e).join()
        val stockAnalysisList = stockService.findAll().mapNotNull(Stock::id).map(quantAnalysisFactory::stockAnalysis)
        stockAnalysisList.map(StockAnalysis::dto).forEach { amqpTemplate.convertAndSend("stock", it) }
        val groupAnalysisList = groupService.findAll().mapNotNull(Group::id).map { quantAnalysisFactory.groupAnalysis(it, stockAnalysisList) }
        groupAnalysisList.map(GroupAnalysis::dto).forEach { amqpTemplate.convertAndSend("group", it) }
    }
}