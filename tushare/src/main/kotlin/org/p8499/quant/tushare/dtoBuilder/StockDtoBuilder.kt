package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.dto.StockDto
import org.p8499.quant.tushare.entity.*
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.util.*

class StockDtoBuilder(
        val stockId: String,
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
    protected val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    private val name by lazy { stockService[stockId]?.name ?: "" }

    private val message by lazy {
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
    }

    private fun <T> mapOf(items: Iterable<T>, keyTransform: (T) -> Date?, valueTransform: (T) -> Double?): Map<Date, Double?> {
        val entryMap = mutableMapOf<Date, Double?>()
        for (item in items)
            keyTransform(item)?.let { entryMap[it] = valueTransform(item) }
        return entryMap
    }

    private fun flatten(entryMap: Map<Date, Double?>): Map<Date, Double?> {
        val map = TreeMap<Date, Double?>()
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

    private fun <T> flatMapOf(items: Iterable<T>, keyTransform: (T) -> Date?, valueTransform: (T) -> Double?): Map<Date, Double?> = flatten(mapOf(items, keyTransform, valueTransform))

    val dateList by lazy { tradingDateService.findByStockId(stockId).mapNotNull(TradingDate::date) }

    val factorList by lazy { flatMapOf(level1AdjFactorService.findByStockId(stockId), Level1AdjFactor::date, Level1AdjFactor::factor).values.toList() }

    val maxFactor by lazy { factorList.mapNotNull { it }.maxOrNull() }

    val openList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::open).values.toList() }

    val openPreList by lazy { openList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

    val closeList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::close).values.toList() }

    val closePreList by lazy { closeList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

    val highList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::high).values.toList() }

    val highPreList by lazy { highList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

    val lowList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::low).values.toList() }

    val lowPreList by lazy { lowList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

    val volumeList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::volume).values.toList() }

    val volumePreList by lazy { volumeList.mapIndexed { index, d -> let(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

    val amountList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::amount).values.toList() }

    val flowShareList by lazy { flatMapOf(level1BasicService.findByStockId(stockId), Level1Basic::date, Level1Basic::flowShare).values.toList() }

    val totalShareList by lazy { flatMapOf(level1BasicService.findByStockId(stockId), Level1Basic::date, Level1Basic::totalShare).values.toList() }

    val flowValueList by lazy { flowShareList.mapIndexed { index, d -> let(d, closeList[index]) { a, b -> a * b } } }

    val totalValueList by lazy { totalShareList.mapIndexed { index, d -> let(d, closeList[index]) { a, b -> a * b } } }

    private val balanceSheetList by lazy { balanceSheetService.findByStockId(stockId) }

    private val incomeList by lazy { incomeService.findByStockId(stockId) }

    private val cashflowList by lazy { cashflowService.findByStockId(stockId) }

    private val expressList by lazy { expressService.findByStockId(stockId) }

    private fun <T> multiplierOf(items: Iterable<T>, dateTransform: (T) -> Date?, yearTransform: (T) -> Int?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, date: Date, period: Int): Double? {
        val periodItemList = items.filter { item -> dateTransform(item)?.let { it < date } ?: false && periodTransform(item) == period }
        val yearItemList = periodItemList.map { periodItem -> items.find { yearItem -> yearTransform(yearItem) == yearTransform(periodItem) && periodTransform(yearItem) == 4 } }
        val multiplierList = periodItemList.mapIndexedNotNull { index, periodItem -> valueTransform(periodItem)?.let { yearItemList[index]?.let(valueTransform)?.div(it) } }
        return multiplierList.takeIf(List<*>::isNotEmpty)?.sorted()?.let { it[it.size - 1] }
    }

    private fun <T> multipliedMapOf(items: Iterable<T>, dateTransform: (T) -> Date?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (Date, Int) -> Double?): Map<Date, Double?> = mapOf(items.mapNotNull {
        let(dateTransform(it), periodTransform(it), valueTransform(it)) { a, b, c -> a to multiplier(a, b)?.times(c) }
    }, Pair<Date, Double?>::first, Pair<Date, Double?>::second)

    private fun <T> multipliedFlatMapOf(items: Iterable<T>, dateTransform: (T) -> Date?, periodTransform: (T) -> Int?, valueTransform: (T) -> Double?, multiplier: (Date, Int) -> Double?): Map<Date, Double?> = flatten(multipliedMapOf(items, dateTransform, periodTransform, valueTransform, multiplier))

    private fun netProfitMultiplier(publish: Date, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::nIncomeAttrP, publish, period)

    private fun revenueMultiplier(publish: Date, period: Int): Double? = multiplierOf(incomeList, Income::publish, Income::year, Income::period, Income::revenue, publish, period)

    private fun opCashflowMultiplier(publish: Date, period: Int): Double? = multiplierOf(cashflowList, Cashflow::publish, Cashflow::year, Cashflow::period, Cashflow::nCashflowAct, publish, period)

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

    val netAssetPerStockList by lazy {
        netAssetList.mapIndexed { index, d ->
            let(d, totalShareList[index]) { a, b ->
                a / b
            }
        }
    }

    val netProfitPerStockList by lazy { netProfitList.mapIndexed { index, d -> let(d, totalShareList[index]) { a, b -> a / b } } }

    val revenuePerStockList by lazy { revenueList.mapIndexed { index, d -> let(d, totalShareList[index]) { a, b -> a / b } } }

    val opCashflowPerStockList by lazy { opCashflowList.mapIndexed { index, d -> let(d, totalShareList[index]) { a, b -> a / b } } }

    val pbList by lazy { closePreList.mapIndexed { index, d -> let(d, netAssetPerStockList[index]) { a, b -> a / b } } }

    val peList by lazy { closePreList.mapIndexed { index, d -> let(d, netProfitPerStockList[index]) { a, b -> a / b } } }

    val psList by lazy { closePreList.mapIndexed { index, d -> let(d, revenuePerStockList[index]) { a, b -> a / b } } }

    val pcfList by lazy { closePreList.mapIndexed { index, d -> let(d, opCashflowPerStockList[index]) { a, b -> a / b } } }

    fun build(): StockDto {
        logger.info("Constructing $stockId DTO")
        return StockDto("CN", stockId, name, message, dateList, openPreList, closePreList, highPreList, lowPreList, volumePreList, amountList, flowShareList, totalShareList, flowValueList, totalValueList, pbList, peList, psList, pcfList)
    }
}