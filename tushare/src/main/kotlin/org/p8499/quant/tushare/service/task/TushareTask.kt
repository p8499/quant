package org.p8499.quant.tushare.service.task

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.p8499.quant.tushare.dtoBuilder.DtoBuilderFactory
import org.p8499.quant.tushare.entity.Controller
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.persistentRequest.PersistentRequest
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


@Service
class TushareTask {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

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
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var dtoBuilderFactory: DtoBuilderFactory

    @Autowired
    protected lateinit var persistentRequest: PersistentRequest

    @Autowired
    protected lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    protected lateinit var stringRedisTemplate: StringRedisTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    /**
     *                                       group ┐
     * exchange -> tradingDate ┐                   ├ b -> groupStock ┐
     *                         ├ a ┬-> level1Basic ┘                 │
     *                   stock ┘   │                                 │
     *                             │             ┌ level1Candlestick │
     *                             │             │ level1AdjFactor   │
     *                             │             │ level2            ├ c -> Join
     *                             └------------>┤ balanceSheet      │
     *                                           │ income            │
     *                                           │ cashflow          │
     *                                           │ express           │
     *                                           └ forecast          ┘
     */
    @Scheduled(cron = "00 01 00 * * SUN-SAT")
    fun syncAndSend() {
        /**
         * Download from tushare.pro and save the data into database
         */
        val download = {
            logger.info("Download Start.")
            val executor = Executors.newCachedThreadPool()
            val syncExchange = CompletableFuture.runAsync(exchangeSynchronizer::invoke, executor)
            val syncTradingDate = syncExchange.thenRunAsync(tradingDateSynchronizer::invoke, executor)
            val syncStock = CompletableFuture.runAsync(stockSynchronizer::invoke, executor)
            val syncA = CompletableFuture.allOf(syncTradingDate, syncStock)
            val syncGroup = CompletableFuture.runAsync(groupSynchronizer::invoke, executor)
            val syncLevel1Basic = syncA.thenRunAsync(level1BasicSynchronizer::invoke, executor)
            val syncB = CompletableFuture.allOf(syncGroup, syncLevel1Basic)
            val syncGroupStock = syncB.thenRunAsync(groupStockSynchronizer::invoke, executor)
            val syncLevel1Candlestick = syncA.thenRunAsync(level1CandlestickSynchronizer::invoke, executor)
            val syncLevel1AdjFactor = syncA.thenRunAsync(level1AdjFactorSynchronizer::invoke, executor)
            val syncLevel2 = syncA.thenRunAsync(level2Synchronizer::invoke, executor)
            val syncBalanceSheet = syncA.thenRunAsync(balanceSheetSynchronizer::invoke, executor)
            val syncIncome = syncA.thenRunAsync(incomeSynchronizer::invoke, executor)
            val syncCashflow = syncA.thenRunAsync(cashflowSynchronizer::invoke, executor)
            val syncExpress = syncA.thenRunAsync(expressSynchronizer::invoke, executor)
            val syncForecast = syncA.thenRunAsync(forecastSynchronizer::invoke, executor)
            val syncC = CompletableFuture.allOf(syncGroupStock, syncLevel1Candlestick, syncLevel1AdjFactor, syncLevel2, syncBalanceSheet, syncIncome, syncCashflow, syncExpress, syncForecast)
            syncC.join()
            logger.info("Download Complete.")
        }

        /**
         * Calculate from database and call analysis persistent function
         */
        val persistent = {
            logger.info("Persistent Start.")
            val snapshot = controllerService.findAll().mapNotNull(Controller::snapshot).minOrNull()
            val startDate = LocalDate.of(2013, 1, 1)
            val endDate = tradingDateService.last("SSE")?.date
            if (snapshot != null && endDate != null) {
                persistentRequest.begin("CN", snapshot)
                Flowable.fromIterable(stockService.findAll().mapNotNull(Stock::id))
                        .parallel(3).runOn(Schedulers.io())
                        .doOnNext { stockId ->
                            dtoBuilderFactory.newSecurityDtoBuilder(stockId, startDate, endDate).build().also(persistentRequest::saveSecurity)
                            dtoBuilderFactory.newSecurityDayDtoBuilder(stockId, startDate, endDate).build().also(persistentRequest::saveSecurityDay)
                            dtoBuilderFactory.newSecurityQuarterDtoBuilder(stockId, startDate, endDate).build().also(persistentRequest::saveSecurityQuarter)
                        }
                        .sequential().blockingSubscribe()
                persistentRequest.end("CN")
            }
            logger.info("Persistent Complete.")
        }
        download()
        persistent()
    }
}