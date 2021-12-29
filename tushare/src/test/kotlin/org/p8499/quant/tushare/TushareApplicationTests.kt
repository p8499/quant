package org.p8499.quant.tushare

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.dtoBuilder.DtoBuilderFactory
import org.p8499.quant.tushare.dtoBuilder.StockDtoBuilder
import org.p8499.quant.tushare.feignClient.PersistentFeignClient
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.task.TushareTask
import org.p8499.quant.tushare.service.tushareRequest.*
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import java.io.File
import java.nio.file.Files
import java.time.LocalDate

@SpringBootTest
class TushareApplicationTests {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    lateinit var balanceSheetSynchronizer: BalanceSheetSynchronizer

    @Autowired
    lateinit var cashflowSynchronizer: CashflowSynchronizer

    @Autowired
    lateinit var exchangeSynchronizer: ExchangeSynchronizer

    @Autowired
    lateinit var expressSynchronizer: ExpressSynchronizer

    @Autowired
    lateinit var forecastSynchronizer: ForecastSynchronizer

    @Autowired
    lateinit var groupStockSynchronizer: GroupStockSynchronizer

    @Autowired
    lateinit var groupSynchronizer: GroupSynchronizer

    @Autowired
    lateinit var incomeSynchronizer: IncomeSynchronizer

    @Autowired
    lateinit var level1AdjFactorSynchronizer: Level1AdjFactorSynchronizer

    @Autowired
    lateinit var level1BasicSynchronizer: Level1BasicSynchronizer

    @Autowired
    lateinit var level1CandlestickSynchronizer: Level1CandlestickSynchronizer

    @Autowired
    lateinit var level2Synchronizer: Level2Synchronizer

    @Autowired
    lateinit var stockSynchronizer: StockSynchronizer

    @Autowired
    lateinit var tradingDateSynchronizer: TradingDateSynchronizer

    @Autowired
    lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    lateinit var conceptDetailRequest: ConceptDetailRequest

    @Autowired
    lateinit var incomeRequest: IncomeRequest

    @Autowired
    protected lateinit var adjFactorRequest: AdjFactorRequest

    @Autowired
    protected lateinit var dailyBasicRequest: DailyBasicRequest

    @Autowired
    protected lateinit var dailyRequest: DailyRequest

    @Autowired
    protected lateinit var moneyflowRequest: MoneyflowRequest

    @Autowired
    protected lateinit var tushareTask: TushareTask

    @Autowired
    protected lateinit var dtoBuilderFactory: DtoBuilderFactory

    @Autowired
    protected lateinit var balancesheetRequest: BalancesheetRequest

    @Autowired
    protected lateinit var persistentFeignClient: PersistentFeignClient

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var incomeService: IncomeService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var stringRedisTemplate: StringRedisTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Test
    fun contextLoads() {
        val startDate = LocalDate.of(2015, 1, 4)
        val endDate = tradingDateService.last("SSE")?.date
        if (endDate != null) {
            val directory = Files.createTempDirectory(null).toFile()
            logger.info("Directory: ${directory.absolutePath}")
            Flowable.fromIterable(listOf("603718.SH"))
                    .parallel(3).runOn(Schedulers.io())
                    .map { dtoBuilderFactory.newStockBuilder(it, startDate, endDate) }
                    .map(StockDtoBuilder::build)
                    .doOnNext { File(directory, it.id).writeText(objectMapper.writeValueAsString(it)) }
                    .doOnNext { persistentFeignClient.saveStock(it) }
                    .sequential().subscribe()
            persistentFeignClient.complete("CN")
        }
    }
}