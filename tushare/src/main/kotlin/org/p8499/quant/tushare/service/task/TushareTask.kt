package org.p8499.quant.tushare.service.task

import com.fasterxml.jackson.databind.ObjectMapper
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.dtoBuilder.DtoBuilderFactory
import org.p8499.quant.tushare.dtoBuilder.GroupDtoBuilder
import org.p8499.quant.tushare.dtoBuilder.StockDtoBuilder
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.feignClient.PersistentFeignClient
import org.p8499.quant.tushare.service.GroupService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


@Service
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
    protected lateinit var dtoBuilderFactory: DtoBuilderFactory

    @Autowired
    protected lateinit var persistentFeignClient: PersistentFeignClient

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
    @Scheduled(cron = "00 30 16 * * MON-FRI")
    fun syncAndSend() {
        val startDate = GregorianCalendar(2015, 0, 1).time
        val today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())

        /**
         * Download from tushare.pro and save the data into database
         */
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
        /**
         * Calculate from database and call analysis persistent function
         */
        stockService.findAll().mapNotNull(Stock::id).parallelStream().map { dtoBuilderFactory.newStockBuilder(it, startDate, today) }.map(StockDtoBuilder::build).forEach(persistentFeignClient::saveStock)
        groupService.findAll().mapNotNull(Group::id).parallelStream().map { dtoBuilderFactory.newGroupBuilder(it, persistentFeignClient.findStock(groupId = it)) }.map(GroupDtoBuilder::build).forEach(persistentFeignClient::saveGroup)
        persistentFeignClient.complete()
//        stringRedisTemplate.keys("*").forEach { stringRedisTemplate.delete(it) }
//        stockService.findAll().mapNotNull(Stock::id).parallelStream().forEach { stringRedisTemplate.opsForValue().set("CNS-$it", objectMapper.writeValueAsString(dtoBuilderFactory.newStockBuilder(it).build())) }
//        val stockDtoList = stringRedisTemplate.keys("CNG-*").map { objectMapper.readValue(stringRedisTemplate.opsForValue()[it], StockDto::class.java) }
//        groupService.findAll().mapNotNull(Group::id).parallelStream().forEach { stringRedisTemplate.opsForValue().set("CNG-$it", objectMapper.writeValueAsString(dtoBuilderFactory.newGroupBuilder(it, stockDtoList).build())) }
        /**
         * Notify ampq
         */
//        amqpTemplate.convertAndSend("quant", "CN")
    }
}