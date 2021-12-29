package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.common.finiteOrNull
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.dto.StockDto
import org.p8499.quant.tushare.entity.*
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class StockDtoBuilder(
        val stockId: String,
        from: LocalDate,
        to: LocalDate,
        val exchangeService: ExchangeService,
        val tradingDateService: TradingDateService,
        val stockService: StockService,
        val groupService: GroupService,
        val level1CandlestickService: Level1CandlestickService,
        val level1BasicService: Level1BasicService,
        val level1AdjFactorService: Level1AdjFactorService,
        val level2Service: Level2Service,
        val groupStockService: GroupStockService,
        val balanceSheetService: BalanceSheetService,
        val incomeService: IncomeService,
        val cashflowService: CashflowService,
        val expressService: ExpressService,
        val forecastService: ForecastService) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    val name by lazy { stockService[stockId]?.name ?: "" }

    /*val message by lazy {
        val forecast = forecastService.last(stockId)
        val required = forecast?.publish?.let {
            val balanceSheetForecast = balanceSheetService.last(stockId)?.publish
            val incomeForecast = incomeService.last(stockId)?.publish
            val cashflowForecast = cashflowService.last(stockId)?.publish
            val expressForecast = expressService.last(stockId)?.publish
            balanceSheetForecast == null || balanceSheetForecast < it
            incomeForecast == null || incomeForecast < it
            cashflowForecast == null || cashflowForecast < it
            expressForecast == null || expressForecast < it
        } ?: false
        forecast.takeIf { required }?.let { "${it.subject}\n${it.content}" } ?: ""
    }*/

    private fun <T, V> mapOf(items: Iterable<T>, keyTransform: (T) -> LocalDate?, valueTransform: (T) -> V?): Map<LocalDate, V?> {
        val entryMap = mutableMapOf<LocalDate, V?>()
        for (item in items)
            keyTransform(item)?.let { entryMap[it] = valueTransform(item) }
        return entryMap
    }

    private fun <V> flatten(entryMap: Map<LocalDate, V?>): Map<LocalDate, V?> {
        val map = TreeMap<LocalDate, V?>()
        for (date in dateList)
            map[date] = null
        for (entry in entryMap)
            map[entry.key] = entry.value
        for (e in map)
            if (e.value === null)
                map[e.key] = map.lowerEntry(e.key)?.value
        map.keys.iterator().apply {
            while (hasNext())
                if (!dateList.contains(next())) remove()
        }
        return map
    }

    private fun <T, V> flatMapOf(items: Iterable<T>, keyTransform: (T) -> LocalDate?, valueTransform: (T) -> V?): Map<LocalDate, V?> = flatten(mapOf(items, keyTransform, valueTransform))

    val dateList by lazy { tradingDateService.findByStockIdBetween(stockId, from, to).mapNotNull(TradingDate::date) }

    val factorList by lazy { flatMapOf(level1AdjFactorService.findByStockIdBetween(stockId, from, to), Level1AdjFactor::date, Level1AdjFactor::factor).values.toList() }

    val maxFactor by lazy { factorList.mapNotNull { it }.maxOrNull() }

    val openList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::open).values.toList() }

    val openPreList by lazy { openList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    val closeList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::close).values.toList() }

    val closePreList by lazy { closeList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    val highList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::high).values.toList() }

    val highPreList by lazy { highList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    val lowList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::low).values.toList() }

    val lowPreList by lazy { lowList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    val volumeList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::volume).values.toList() }

    val volumePreList by lazy { volumeList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    val amountList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::amount).values.toList() }

    val flowShareList by lazy { flatMapOf(level1BasicService.findByStockIdBetween(stockId, from, to), Level1Basic::date, Level1Basic::flowShare).values.toList() }

    val flowSharePreList by lazy { flowShareList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    val totalShareList by lazy { flatMapOf(level1BasicService.findByStockIdBetween(stockId, from, to), Level1Basic::date, Level1Basic::totalShare).values.toList() }

    val totalSharePreList by lazy { totalShareList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    val flowValueList by lazy { flowSharePreList.mapIndexed { index, d -> let(d, closePreList[index]) { a, b -> a * b } } }

    val totalValueList by lazy { totalSharePreList.mapIndexed { index, d -> let(d, closePreList[index]) { a, b -> a * b } } }

    private val balanceSheetList by lazy { balanceSheetService.findByStockIdBetween(stockId, from, to) }

    private val incomeList by lazy { incomeService.findByStockIdBetween(stockId, from, to) }

    private val cashflowList by lazy { cashflowService.findByStockIdBetween(stockId, from, to) }

    private val expressList by lazy { expressService.findByStockIdBetween(stockId, from, to) }

    private fun <T> multiplierOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, yearTransform: (T) -> Int?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, date: LocalDate, period: Int): Double? {
        val periodItemList = items.filter { item -> dateTransform(item)?.let { it < date } ?: false && periodTransform(item) == period }
        val yearItemList = periodItemList.map { periodItem -> items.find { yearItem -> yearTransform(yearItem) == yearTransform(periodItem) && periodTransform(yearItem) == 4 } }
        val multiplierList = periodItemList.mapIndexedNotNull { index, periodItem -> valueTransform(periodItem)?.let { yearItemList[index]?.let(valueTransform)?.div(it)?.finiteOrNull() } }
        return multiplierList.takeIf(List<*>::isNotEmpty)?.sorted()?.let { it[it.size - 1] }
    }

    private fun <T> multipliedMapOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (LocalDate, Int) -> Double?): Map<LocalDate, Double?> = mapOf(items.mapNotNull {
        let(dateTransform(it), periodTransform(it), valueTransform(it)) { a, b, c -> a to multiplier(a, b)?.times(c) }
    }, Pair<LocalDate, Double?>::first, Pair<LocalDate, Double?>::second)

    private fun <T> multipliedFlatMapOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (LocalDate, Int) -> Double?): Map<LocalDate, Double?> = flatten(multipliedMapOf(items, dateTransform, periodTransform, valueTransform, multiplier))

    private fun netProfitMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::nIncomeAttrP, publish, period)

    private fun revenueMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::revenue, publish, period)

    private fun opCashflowMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(cashflowList, Cashflow::publish, Cashflow::year, Cashflow::period, Cashflow::nCashflowAct, publish, period)

    val netAssetList by lazy {
        flatten(mapOf(balanceSheetList, BalanceSheet::publish, BalanceSheet::totalHldrEqyExcMinInt)
                + mapOf(expressList, Express::publish, Express::totalHldrEqyExcMinInt)).values.toList()
    }

    val netProfitList by lazy { multipliedFlatMapOf(incomeList, Income::publish, Income::period, Income::nIncomeAttrP, this::netProfitMultiplier).values.toList() }

    val revenueList by lazy {
        flatten(multipliedMapOf(incomeList, Income::publish, Income::period, Income::revenue, this::revenueMultiplier)
                + multipliedMapOf(expressList, Express::publish, Express::period, Express::revenue, this::revenueMultiplier)).values.toList()
    }

    val opCashflowList by lazy { multipliedFlatMapOf(cashflowList, Cashflow::publish, Cashflow::period, Cashflow::nCashflowAct, this::opCashflowMultiplier).values.toList() }

    val netAssetPerStockList by lazy { netAssetList.mapIndexed { index, d -> let(d, totalSharePreList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val netProfitPerStockList by lazy { netProfitList.mapIndexed { index, d -> let(d, totalSharePreList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val revenuePerStockList by lazy { revenueList.mapIndexed { index, d -> let(d, totalSharePreList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val opCashflowPerStockList by lazy { opCashflowList.mapIndexed { index, d -> let(d, totalSharePreList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val pbList by lazy { closePreList.mapIndexed { index, d -> let(d, netAssetPerStockList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val peList by lazy { closePreList.mapIndexed { index, d -> let(d, netProfitPerStockList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val psList by lazy { closePreList.mapIndexed { index, d -> let(d, revenuePerStockList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val pcfList by lazy { closePreList.mapIndexed { index, d -> let(d, opCashflowPerStockList[index]) { a, b -> (a / b).finiteOrNull() } } }

    val messageList by lazy {
        val forecasts = forecastService.findByStockId(stockId)
        val blankForecasts = forecasts.mapNotNull { let(it.stockId, it.year, it.period) { a, b, c -> forecastService.expires(a, b, c) } }
                .map { Forecast(stockId, 0, 0, it, "", "") }
        flatMapOf(forecasts + blankForecasts, Forecast::publish, Forecast::subject).values.toList()
    }

    fun build(): StockDto {
        logger.info("Constructing $stockId DTO")
        return StockDto("CN", stockId, name, dateList, openPreList, closePreList, highPreList, lowPreList, volumePreList, amountList, flowSharePreList, totalSharePreList, flowValueList, totalValueList, netAssetList, netProfitList, revenueList, opCashflowList, pbList, peList, psList, pcfList, messageList)
    }
}