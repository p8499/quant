package org.p8499.quant.tushare

import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.service.tushareRequest.*
import org.p8499.quant.tushare.task.DataSyncTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TushareApplicationTests {
    @Autowired
    lateinit var tradeCalRequest: TradeCalRequest

    @Autowired
    lateinit var stockBasicRequest: StockBasicRequest

    @Autowired
    lateinit var indexBasicRequest: IndexBasicRequest

    @Autowired
    lateinit var indexClassifyRequest: IndexClassifyRequest

    @Autowired
    lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    lateinit var balancesheetRequest: BalancesheetRequest

    @Autowired
    lateinit var incomeRequest: IncomeRequest

    @Autowired
    lateinit var expressRequest: ExpressRequest

    @Autowired
    lateinit var forecastRequest: ForecastRequest

    @Autowired
    lateinit var dailyRequest: DailyRequest

    @Autowired
    lateinit var adjFactorRequest: AdjFactorRequest

    @Autowired
    lateinit var dailyBasicRequest: DailyBasicRequest

    @Autowired
    lateinit var moneyflowRequest: MoneyflowRequest

    @Autowired
    lateinit var dataSyncTask: DataSyncTask

    @Test
    fun contextLoads() {
        dataSyncTask.sync()
    }
}
