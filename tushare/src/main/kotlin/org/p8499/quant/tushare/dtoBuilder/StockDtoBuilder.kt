package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.common.finiteOrNull
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.dto.SecurityDayDto
import org.p8499.quant.tushare.dto.SecurityDto
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

    val name by lazy {
        stockService[stockId]?.name
                ?: ""
    }

    private fun <T, K, V> mapOf(items: Iterable<T>, keyTransform: (T) -> K?, valueTransform: (T) -> V?): Map<K, V?> {
        val entryMap = mutableMapOf<K, V?>()
        for (item in items)
            keyTransform(item)?.let { entryMap[it] = valueTransform(item) }
        return entryMap
    }

    private fun <T, K, V> mapIndexedOf(items: List<T>, keyTransform: (Int, T) -> K?, valueTransform: (Int, T) -> V?): Map<K, V?> {
        val entryMap = mutableMapOf<K, V?>()
        for (index in items.indices)
            keyTransform(index, items[index])?.let { entryMap[it] = valueTransform(index, items[index]) }
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

    private fun <V> quarterFlatten(entryMap: Map<LocalDate, V?>): Map<LocalDate, V?> {
        val map = TreeMap<LocalDate, V?>()
        for (quarterDate in quarterDateList)
            map[quarterDate] = null
        for (entry in entryMap)
            map[entry.key] = entry.value
        map.keys.iterator().apply {
            while (hasNext())
                if (!quarterDateList.contains(next())) remove()
        }
        return map
    }

    private fun <T, V> flatMapOf(items: Iterable<T>, keyTransform: (T) -> LocalDate?, valueTransform: (T) -> V?): Map<LocalDate, V?> = flatten(mapOf(items, keyTransform, valueTransform))

    private fun <T, V> flatMapIndexedOf(items: List<T>, keyTransform: (Int, T) -> LocalDate?, valueTransform: (Int, T) -> V?): Map<LocalDate, V?> = flatten(mapIndexedOf(items, keyTransform, valueTransform))

    private fun <T, V> quarterFlatMapOf(items: Iterable<T>, keyTransform: (T) -> LocalDate?, valueTransform: (T) -> V?): Map<LocalDate, V?> = quarterFlatten(mapOf(items, keyTransform, valueTransform))

    private fun <T, V> quarterFlatMapIndexedOf(items: List<T>, keyTransform: (Int, T) -> LocalDate?, valueTransform: (Int, T) -> V?): Map<LocalDate, V?> = quarterFlatten(mapIndexedOf(items, keyTransform, valueTransform))

    val dateList by lazy { tradingDateService.findByStockIdBetween(stockId, from, to).mapNotNull(TradingDate::date) }

    private fun q2d(quarter: Pair<Int, Int>) = LocalDate.of(quarter.first, quarter.second * 3, if (quarter.second == 2 || quarter.second == 3) 30 else 31)

    private fun d2q(date: LocalDate) = date.year to (date.monthValue - 1) / 3 + 1

    private fun nextQuarter(quarter: Pair<Int, Int>) = (if (quarter.second < 4) quarter.first else quarter.first + 1) to (if (quarter.second < 4) quarter.second + 1 else 1)

    private fun previousQuarter(quarter: Pair<Int, Int>) = (if (quarter.second > 1) quarter.first else quarter.first - 1) to (if (quarter.second > 1) quarter.second - 1 else 4)

    val quarterDateList by lazy {
        val fromQuarter = previousQuarter(d2q(from))
        val toQuarter = previousQuarter(d2q(to))
        val quarterList = mutableListOf<LocalDate>()
        var quarter = fromQuarter
        while (quarter.first < toQuarter.first || quarter.first == toQuarter.first && quarter.second <= toQuarter.second) {
            quarterList.add(q2d(quarter))
            quarter = nextQuarter(quarter)
        }
        quarterList
    }

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

    private val forecastList by lazy { forecastService.findByStockIdBetween(stockId, from, to) }

    private fun forecastedProfit(forecast: Forecast): Double? {
        val profitLastYear = let(forecast.year, forecast.period) { a, b -> incomeList.find { it.year == a - 1 && it.period == b } }?.nIncomeAttrP
        val multiplier = forecast.subject?.substringAfter('(')?.substringBefore('%')?.toDoubleOrNull()?.div(100)?.plus(1)
        return let(profitLastYear, multiplier) { a, b -> a * b }
    }

    private fun <T> multiplierOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, yearTransform: (T) -> Int?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, date: LocalDate, period: Int): Double? {
        val periodItemList = items.filter { item -> dateTransform(item)?.let { it < date } ?: false && periodTransform(item) == period }
        val yearItemList = periodItemList.map { periodItem -> items.find { yearItem -> yearTransform(yearItem) == yearTransform(periodItem) && periodTransform(yearItem) == 4 } }
        val multiplierList = periodItemList.mapIndexedNotNull { index, periodItem -> valueTransform(periodItem)?.let { yearItemList[index]?.let(valueTransform)?.div(it)?.finiteOrNull() } }
        return multiplierList.takeIf(List<*>::isNotEmpty)?.sorted()?.let {
            if (multiplierList.size % 2 == 0) (it[it.size / 2 - 1] + it[it.size / 2]) / 2
            else it[(it.size - 1) / 2]
        }
    }

    private fun <T> multipliedMapOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (LocalDate, Int) -> Double?): Map<LocalDate, Double?> = mapOf(items.mapNotNull {
        let(dateTransform(it), periodTransform(it), valueTransform(it)) { a, b, c -> a to multiplier(a, b)?.times(c) }
    }, Pair<LocalDate, Double?>::first, Pair<LocalDate, Double?>::second)

    private fun <T> multipliedFlatMapOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (LocalDate, Int) -> Double?): Map<LocalDate, Double?> = flatten(multipliedMapOf(items, dateTransform, periodTransform, valueTransform, multiplier))

    private fun <T> quarterMultipliedFlatMapOf(items: Iterable<T>, dateTransform: (T) -> LocalDate?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (LocalDate, Int) -> Double?): Map<LocalDate, Double?> = quarterFlatten(multipliedMapOf(items, dateTransform, periodTransform, valueTransform, multiplier))

    private fun netProfitMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::nIncomeAttrP, publish, period)

    private fun revenueMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::revenue, publish, period)

    private fun opCashflowMultiplier(publish: LocalDate, period: Int): Double? = multiplierOf(cashflowList, Cashflow::publish, Cashflow::year, Cashflow::period, Cashflow::nCashflowAct, publish, period)

    private fun <T> sliceIndexed(items: List<T>, yearTransform: (Int, T) -> Int?, periodTransform: (Int, T) -> Int?, valueTransform: (Int, T) -> Double?): List<Double?> {
        val indices = items.indices
        return items.mapIndexed { index, item ->
            let(yearTransform(index, item), periodTransform(index, item)) { year, period ->
                if (period > 1) {
                    val pq = previousQuarter(year to period)
                    let(valueTransform(index, item), indices.find { yearTransform(it, items[it]) == pq.first && periodTransform(it, items[it]) == pq.second }?.let { it to items[it] }?.let { valueTransform(it.first, it.second) }) { a, b -> a - b }
                } else
                    valueTransform(index, item)
            }
        }
    }

    val netAssetList by lazy {
        flatten(mapOf(expressList, Express::publish, Express::totalHldrEqyExcMinInt)
                + mapOf(balanceSheetList, BalanceSheet::publish, BalanceSheet::totalHldrEqyExcMinInt)).values.toList()
    }

    val netProfitList by lazy {
        flatten(multipliedMapOf(forecastList, Forecast::publish, Forecast::period, this::forecastedProfit, this::netProfitMultiplier)
                + multipliedMapOf(incomeList, Income::publish, Income::period, Income::nIncomeAttrP, this::netProfitMultiplier)).values.toList()
    }

    val revenueList by lazy {
        flatten(multipliedMapOf(expressList, Express::publish, Express::period, Express::revenue, this::revenueMultiplier)
                + multipliedMapOf(incomeList, Income::publish, Income::period, Income::revenue, this::revenueMultiplier)).values.toList()
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
                .map { Forecast(stockId, it, 0, 0, "", "") }
        flatMapOf(forecasts + blankForecasts, Forecast::publish, Forecast::subject).values.toList()
    }

    //asset
    val quarterNetAssetList by lazy {
        quarterFlatten(mapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::totalHldrEqyExcMinInt)
                + mapOf(balanceSheetList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, BalanceSheet::totalHldrEqyExcMinInt)).values.toList()
    }

    val quarterNetAssetPublishList by lazy {
        quarterFlatten(mapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::publish)
                + mapOf(balanceSheetList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, BalanceSheet::publish)).values.toList()
    }

    //profit
    val quarterNetProfitList by lazy {
        quarterFlatten(multipliedMapOf(forecastList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Forecast::period, this::forecastedProfit, this::netProfitMultiplier)
                + multipliedMapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::period, Income::nIncomeAttrP, this::netProfitMultiplier)).values.toList()
    }

    val quarterNetProfitPublishList by lazy {
        quarterFlatten(mapOf(forecastList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Forecast::publish)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::publish)).values.toList()
    }

    val quarterNetProfitQuarterList by lazy {
        quarterFlatten(mapOf(forecastList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Forecast::publish)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::publish)).keys.toList()
    }

    val quarterAccumulatedNetProfitList by lazy {
        quarterFlatten(mapOf(forecastList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, this::forecastedProfit)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::nIncomeAttrP)).values.toList()
    }

    val quarterCurrentNetProfitList by lazy {
        quarterFlatMapIndexedOf(
                sliceIndexed(quarterAccumulatedNetProfitList, { i, item -> d2q(quarterNetProfitQuarterList[i]).first }, { i, item -> d2q(quarterNetProfitQuarterList[i]).second }, { i, item -> quarterAccumulatedNetProfitList[i] }),
                { i, item -> quarterNetProfitQuarterList[i] },
                { i, item -> item }).values.toList()
    }

    //revenue
    val quarterRevenueList by lazy {
        quarterFlatten(multipliedMapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::period, Express::revenue, this::revenueMultiplier)
                + multipliedMapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::period, Income::revenue, this::revenueMultiplier)).values.toList()
    }

    val quarterRevenuePublishList by lazy {
        quarterFlatten(mapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::publish)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::publish)).values.toList()
    }

    val quarterRevenueQuarterList by lazy {
        quarterFlatten(mapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::publish)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::publish)).keys.toList()
    }

    val quarterAccumulatedRevenueList by lazy {
        quarterFlatten(mapOf(expressList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Express::revenue)
                + mapOf(incomeList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Income::revenue)).values.toList()
    }

    val quarterCurrentRevenueList by lazy {
        quarterFlatMapIndexedOf(
                sliceIndexed(quarterAccumulatedRevenueList, { i, item -> d2q(quarterRevenueQuarterList[i]).first }, { i, item -> d2q(quarterRevenueQuarterList[i]).second }, { i, item -> quarterAccumulatedRevenueList[i] }),
                { i, item -> quarterRevenueQuarterList[i] },
                { i, item -> item }).values.toList()
    }

    //cashflow
    val quarterOpCashflowList by lazy { quarterMultipliedFlatMapOf(cashflowList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Cashflow::period, Cashflow::nCashflowAct, this::opCashflowMultiplier).values.toList() }

    val quarterOpCashflowPublishList by lazy { quarterFlatMapOf(cashflowList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Cashflow::publish).values.toList() }

    val quarterOpCashflowQuarterList by lazy { quarterFlatMapOf(cashflowList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Cashflow::publish).keys.toList() }

    val quarterAccumulatedOpCashflowList by lazy { quarterFlatMapOf(cashflowList, { let(it.year, it.period) { a, b -> q2d(a to b) } }, Cashflow::nCashflowAct).values.toList() }

    val quarterCurrentOpCashflowList by lazy {
        quarterFlatMapIndexedOf(
                sliceIndexed(quarterAccumulatedOpCashflowList, { i, item -> d2q(quarterOpCashflowQuarterList[i]).first }, { i, item -> d2q(quarterOpCashflowQuarterList[i]).second }, { i, item -> quarterAccumulatedOpCashflowList[i] }),
                { i, item -> quarterOpCashflowQuarterList[i] },
                { i, item -> item }).values.toList()
    }

    fun securityDto(): SecurityDto {
        return SecurityDto("CN", stockId, name)
    }

    fun securityDayDto(): SecurityDayDto {
        return SecurityDayDto("CN", stockId, dateList, listOf(
                SecurityDayDto.Indices("open", openPreList),
                SecurityDayDto.Indices("close", closePreList),
                SecurityDayDto.Indices("high", highPreList),
                SecurityDayDto.Indices("low", lowPreList),
                SecurityDayDto.Indices("volume", volumePreList),
                SecurityDayDto.Indices("amount", amountList),
                SecurityDayDto.Indices("flowShare", flowSharePreList),
                SecurityDayDto.Indices("flowValue", flowValueList),
                SecurityDayDto.Indices("totalShare", totalSharePreList),
                SecurityDayDto.Indices("totalValue", totalValueList),
                SecurityDayDto.Indices("pb", pbList),
                SecurityDayDto.Indices("pe", peList),
                SecurityDayDto.Indices("ps", psList),
                SecurityDayDto.Indices("pcf", pcfList)), listOf(
                SecurityDayDto.Messages("forecast", messageList)))
    }

//    fun securityQuarterDto(): SecurityQuarterDto {
//        return SecurityQuarterDto("CN", stockId, quarterDateList.map(this::d2q).map { it.first * 4 + it.second - 1 }, listOf(
//                SecurityQuarterDto.Indices("asset", quarterNetAssetList, quarterNetAssetPublishList),
//                SecurityQuarterDto.Indices("profit", quarterNetProfitList, quarterNetProfitPublishList),
//                SecurityQuarterDto.Indices("currentProfit", quarterCurrentNetProfitList, quarterNetProfitPublishList),
//                SecurityQuarterDto.Indices("revenue", quarterRevenueList, quarterRevenuePublishList),
//                SecurityQuarterDto.Indices("currentRevenue", quarterCurrentRevenueList, quarterRevenuePublishList),
//                SecurityQuarterDto.Indices("cashflow", quarterOpCashflowList, quarterOpCashflowPublishList),
//                SecurityQuarterDto.Indices("currentCashflow", quarterCurrentOpCashflowList, quarterOpCashflowPublishList)), listOf())
//    }
}