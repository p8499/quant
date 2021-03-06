package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.dto.StockDto
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DtoBuilderFactory {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var exchangeService: ExchangeService

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var level1CandlestickService: Level1CandlestickService

    @Autowired
    protected lateinit var level1BasicService: Level1BasicService

    @Autowired
    protected lateinit var level1AdjFactorService: Level1AdjFactorService

    @Autowired
    protected lateinit var level2Service: Level2Service

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var balanceSheetService: BalanceSheetService

    @Autowired
    protected lateinit var incomeService: IncomeService

    @Autowired
    protected lateinit var cashflowService: CashflowService

    @Autowired
    protected lateinit var expressService: ExpressService

    @Autowired
    protected lateinit var forecastService: ForecastService

    fun newSecurityDtoBuilder(stockId: String, from: LocalDate, to: LocalDate) = SecurityDtoBuilder(stockId, from, to, exchangeService, tradingDateService, stockService, groupService, level1CandlestickService, level1BasicService, level1AdjFactorService, level2Service, groupStockService, balanceSheetService, incomeService, cashflowService, expressService, forecastService)

    fun newSecurityDayDtoBuilder(stockId: String, from: LocalDate, to: LocalDate) = SecurityDayDtoBuilder(stockId, from, to, exchangeService, tradingDateService, stockService, groupService, level1CandlestickService, level1BasicService, level1AdjFactorService, level2Service, groupStockService, balanceSheetService, incomeService, cashflowService, expressService, forecastService)

    fun newSecurityQuarterDtoBuilder(stockId: String, from: LocalDate, to: LocalDate) = SecurityQuarterDtoBuilder(stockId, from, to, exchangeService, tradingDateService, stockService, groupService, level1CandlestickService, level1BasicService, level1AdjFactorService, level2Service, groupStockService, balanceSheetService, incomeService, cashflowService, expressService, forecastService)

    fun newStockBuilder(stockId: String, from: LocalDate, to: LocalDate) = StockDtoBuilder(stockId, from, to, exchangeService, tradingDateService, stockService, groupService, level1CandlestickService, level1BasicService, level1AdjFactorService, level2Service, groupStockService, balanceSheetService, incomeService, cashflowService, expressService, forecastService)

    fun newGroupBuilder(groupId: String, stockDtoList: List<StockDto>) = GroupDtoBuilder(groupId, stockDtoList, exchangeService, tradingDateService, stockService, groupService, level1CandlestickService, level1BasicService, level1AdjFactorService, level2Service, groupStockService, balanceSheetService, incomeService, cashflowService, expressService, forecastService)
}