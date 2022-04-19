package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.dto.SecurityDto
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityDtoBuilder(
        val stockId: String,
        from: LocalDate,
        to: LocalDate,
        protected val exchangeService: ExchangeService,
        protected val tradingDateService: TradingDateService,
        protected val stockService: StockService,
        protected val groupService: GroupService,
        protected val level1CandlestickService: Level1CandlestickService,
        protected val level1BasicService: Level1BasicService,
        protected val level1AdjFactorService: Level1AdjFactorService,
        protected val level2Service: Level2Service,
        protected val groupStockService: GroupStockService,
        protected val balanceSheetService: BalanceSheetService,
        protected val incomeService: IncomeService,
        protected val cashflowService: CashflowService,
        protected val expressService: ExpressService,
        protected val forecastService: ForecastService) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    val name by lazy { stockService[stockId]?.name ?: "" }

    fun build() = SecurityDto("CN", stockId, name)
}
