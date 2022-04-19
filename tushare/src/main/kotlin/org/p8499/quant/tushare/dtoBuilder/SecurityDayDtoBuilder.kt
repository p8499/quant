package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.common.finiteOrNull
import org.p8499.quant.tushare.dto.SecurityDayDto
import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.p8499.quant.tushare.entity.Level1Basic
import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class SecurityDayDtoBuilder(
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

    companion object {
        private fun <T, K, V> mapOf(items: Iterable<T>, keyTransform: (T) -> K?, valueTransform: (T) -> V?): Map<K, V?> {
            val entryMap = mutableMapOf<K, V?>()
            for (item in items)
                keyTransform(item)?.let { entryMap[it] = valueTransform(item) }
            return entryMap
        }
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

    private val dateList by lazy { tradingDateService.findByStockIdBetween(stockId, from, to).mapNotNull(TradingDate::date) }

    private val factorList by lazy { flatMapOf(level1AdjFactorService.findByStockIdBetween(stockId, from, to), Level1AdjFactor::date, Level1AdjFactor::factor).values.toList() }

    private val maxFactor by lazy { factorList.mapNotNull { it }.maxOrNull() }

    private val openList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::open).values.toList() }

    private val openPreList by lazy { openList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    private val closeList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::close).values.toList() }

    private val closePreList by lazy { closeList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    private val highList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::high).values.toList() }

    private val highPreList by lazy { highList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    private val lowList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::low).values.toList() }

    private val lowPreList by lazy { lowList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a * b / c).finiteOrNull() } } }

    private val volumeList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::volume).values.toList() }

    private val volumePreList by lazy { volumeList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    private val amountList by lazy { flatMapOf(level1CandlestickService.findByStockIdBetween(stockId, from, to), Level1Candlestick::date, Level1Candlestick::amount).values.toList() }

    private val flowShareList by lazy { flatMapOf(level1BasicService.findByStockIdBetween(stockId, from, to), Level1Basic::date, Level1Basic::flowShare).values.toList() }

    private val flowSharePreList by lazy { flowShareList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    private val totalShareList by lazy { flatMapOf(level1BasicService.findByStockIdBetween(stockId, from, to), Level1Basic::date, Level1Basic::totalShare).values.toList() }

    private val totalSharePreList by lazy { totalShareList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, factorList[index], maxFactor) { a, b, c -> (a / b * c).finiteOrNull() } } }

    private val flowValueList by lazy { flowSharePreList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, closePreList[index]) { a, b -> a * b } } }

    private val totalValueList by lazy { totalSharePreList.mapIndexed { index, d -> org.p8499.quant.tushare.common.let(d, closePreList[index]) { a, b -> a * b } } }

    fun build() = SecurityDayDto("CN", stockId, dateList, listOf(
            SecurityDayDto.Indices("open", openPreList),
            SecurityDayDto.Indices("close", closePreList),
            SecurityDayDto.Indices("high", highPreList),
            SecurityDayDto.Indices("low", lowPreList),
            SecurityDayDto.Indices("volume", volumePreList),
            SecurityDayDto.Indices("amount", amountList),
            SecurityDayDto.Indices("flowShare", flowSharePreList),
            SecurityDayDto.Indices("totalShare", totalSharePreList),
            SecurityDayDto.Indices("flowValue", flowValueList),
            SecurityDayDto.Indices("totalValue", totalValueList)))
}