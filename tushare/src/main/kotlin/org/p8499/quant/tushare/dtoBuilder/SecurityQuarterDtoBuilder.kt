package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.dto.SecurityQuarterDto
import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityQuarterDtoBuilder(
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
        protected val forecastService: ForecastService
) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    companion object {
        private fun quarter(year: Int, period: Int): Int = year * 4 + period - 1
        private fun median(values: List<Double>): Double? = values.takeIf(List<*>::isNotEmpty)?.sorted()?.let {
            if (values.size % 2 == 0) (it[it.size / 2 - 1] + it[it.size / 2]) / 2
            else it[(it.size - 1) / 2]
        }

        private fun <T> multiplierOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, yearTransform: (T) -> Int?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, date: LocalDate, year: Int, period: Int): Double? {
            val periodItemList = items.filter { item ->
                dateTransform(item)?.let { it < date } == true && periodTransform(item) == period
            }
            val yearItemList = periodItemList.map { periodItem ->
                items.find { yearItem -> dateTransform(yearItem)?.let { it < date } == true && yearTransform(yearItem) == yearTransform(periodItem) && periodTransform(yearItem) == 4 }
            }
            val multiplierList = periodItemList.mapIndexedNotNull { index, periodItem ->
                let(valueTransform(periodItem)?.takeIf { it > 0 }, yearItemList[index]?.let(valueTransform)?.takeIf { it > 0 }) { a, b -> b / a }
            }
            return median(multiplierList)
        }
    }

    private val balanceSheetList by lazy { balanceSheetService.findByStockIdBetween(stockId, from, to) }

    private val incomeList by lazy { incomeService.findByStockIdBetween(stockId, from, to) }

    private val cashflowList by lazy { cashflowService.findByStockIdBetween(stockId, from, to) }

    private val expressList by lazy { expressService.findByStockIdBetween(stockId, from, to) }

    private val forecastList by lazy { forecastService.findByStockIdBetween(stockId, from, to) }

    private val Forecast.profit: Double?
        get() {
            val profitLastYear = let(year, period) { a, b -> incomeList.find { it.year == a - 1 && it.period == b } }?.nIncomeAttrP
            val multiplier = subject?.substringAfter('(')?.substringBefore('%')?.toDoubleOrNull()?.div(100)?.plus(1)
            return let(profitLastYear, multiplier) { a, b -> a * b }
        }

    private fun profitMultiplier(publish: LocalDate, year: Int, period: Int): Double? = multiplierOf(
            incomeList, Income::publish, Income::year, Income::period, Income::nIncomeAttrP, publish, year, period
    )

    private fun revenueMultiplier(publish: LocalDate, year: Int, period: Int): Double? = multiplierOf(
            incomeList, Income::publish, Income::year, Income::period, Income::revenue, publish, year, period
    )

    private fun cashflowMultiplier(publish: LocalDate, year: Int, period: Int): Double? = multiplierOf(
            cashflowList, Cashflow::publish, Cashflow::year, Cashflow::period, Cashflow::nCashflowAct, publish, year, period
    )

    private val assetList by lazy {
        expressList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.totalHldrEqyExcMinInt) } } +
                balanceSheetList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.totalHldrEqyExcMinInt) } }
    }
    private val profitList by lazy {
        forecastList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.profit) } } +
                expressList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nIncome) } } +
                incomeList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nIncomeAttrP) } }
    }
    private val profitForecastList by lazy {
        forecastList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.profit?.let { profit -> profitMultiplier(publish, year, period)?.times(profit) }) } } +
                expressList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nIncome?.let { profit -> profitMultiplier(publish, year, period)?.times(profit) }) } } +
                incomeList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nIncomeAttrP?.let { profit -> profitMultiplier(publish, year, period)?.times(profit) }) } }
    }
    private val revenueList by lazy {
        expressList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.revenue) } } +
                incomeList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.revenue) } }
    }
    private val revenueForecastList by lazy {
        expressList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.revenue?.let { revenue -> revenueMultiplier(publish, year, period)?.times(revenue) }) } } +
                incomeList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.revenue?.let { revenue -> revenueMultiplier(publish, year, period)?.times(revenue) }) } }
    }
    private val cashList by lazy {
        cashflowList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nCashflowAct) } }
    }
    private val cashForecastList by lazy {
        cashflowList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Index(publish, quarter(year, period), it.nCashflowAct?.let { cash -> cashflowMultiplier(publish, year, period)?.times(cash) }) } }
    }
    private val messageList by lazy {
        forecastList.mapNotNull { let(it.publish, it.year, it.period) { publish, year, period -> SecurityQuarterDto.Message(publish, quarter(year, period), it.subject) } }
    }

    fun build(): SecurityQuarterDto = SecurityQuarterDto("CN", stockId, listOf(
            SecurityQuarterDto.Indices("asset", assetList),
            SecurityQuarterDto.Indices("profit", profitList),
            SecurityQuarterDto.Indices("profitForecast", profitForecastList),
            SecurityQuarterDto.Indices("revenue", revenueList),
            SecurityQuarterDto.Indices("revenueForecast", revenueForecastList),
            SecurityQuarterDto.Indices("cash", cashList),
            SecurityQuarterDto.Indices("cashForecast", cashForecastList)), listOf(
            SecurityQuarterDto.Messages("message", messageList))
    )
}