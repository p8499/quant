package org.p8499.quant.tushare.controller

import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FormulaController {
    @Autowired
    lateinit var exchangeService: ExchangeService

    @Autowired
    lateinit var tradingDateService: TradingDateService

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var level1CandlestickService: Level1CandlestickService

    @Autowired
    lateinit var level1BasicService: Level1BasicService

    @Autowired
    lateinit var level1AdjFactorService: Level1AdjFactorService

    @Autowired
    lateinit var level2Service: Level2Service

    @Autowired
    lateinit var groupStockService: GroupStockService

    @Autowired
    lateinit var balanceSheetService: BalanceSheetService

    @Autowired
    lateinit var incomeService: IncomeService

    @Autowired
    lateinit var cashflowService: CashflowService

    @Autowired
    lateinit var expressService: ExpressService

    @Autowired
    lateinit var forecastService: ForecastService

    @GetMapping
    fun level1Candlestick(@RequestParam("stock-id") stockId: String): List<Level1Candlestick> = tradingDateService.findByStockId(stockId).mapNotNull(TradingDate::date).mapNotNull { level1CandlestickService[stockId, it] }
}