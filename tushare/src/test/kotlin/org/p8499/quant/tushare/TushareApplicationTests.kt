package org.p8499.quant.tushare

import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.dtoBuilder.DtoBuilderFactory
import org.p8499.quant.tushare.feignClient.PersistentFeignClient
import org.p8499.quant.tushare.service.task.TushareTask
import org.p8499.quant.tushare.service.tushareRequest.*
import org.p8499.quant.tushare.service.tushareSynchronizer.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TushareApplicationTests {
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

    @Test
    fun contextLoads() {
        persistentFeignClient.complete()
    }
}
