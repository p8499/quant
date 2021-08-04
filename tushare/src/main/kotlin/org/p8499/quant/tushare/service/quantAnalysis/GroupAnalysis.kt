package org.p8499.quant.tushare.service.quantAnalysis

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.let2
import org.p8499.quant.tushare.common.let3
import org.p8499.quant.tushare.dto.GroupDto
import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.util.*

class GroupAnalysis(
        val groupId: String,
        stockAnalysisList: List<StockAnalysis>,
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

    private fun stockIdListByExchange(exchangeId: String): List<String> = stockService.findByStockIdList(groupStockService.findByGroupId(groupId).mapNotNull(GroupStock::stockId)).filter { it.exchangeId == exchangeId }.mapNotNull(Stock::id)

    private val stockIdListBySh by lazy { stockIdListByExchange("SSE") }

    private val stockIdListBySz by lazy { stockIdListByExchange("SZSE") }

    private fun stockAnalysisMapByExchange(stockAnalysisList: List<StockAnalysis>, stockIdListByExchange: List<String>): Map<String, StockAnalysis> = mutableMapOf<String, StockAnalysis>().apply { stockAnalysisList.filter { stockIdListByExchange.contains(it.stockId) }.forEach { put(it.stockId, it) } }

    private val stockAnalysisMapBySh by lazy { stockAnalysisMapByExchange(stockAnalysisList, stockIdListBySh) }

    private val stockAnalysisMapBySz by lazy { stockAnalysisMapByExchange(stockAnalysisList, stockIdListBySz) }

    private fun dateListByExchange(stockAnalysisMapExchange: Map<String, StockAnalysis>) = stockAnalysisMapExchange.flatMap { it.value.dateList }.distinct().sorted()

    private val dateListBySh by lazy { dateListByExchange(stockAnalysisMapBySh) }

    private val dateListBySz by lazy { dateListByExchange(stockAnalysisMapBySz) }

    val dateList by lazy { (dateListBySh + dateListBySz).distinct().sorted() }

    private fun stockIdsListByExchange(dateListByExchange: List<Date>, stockAnalysisMapByExchange: Map<String, StockAnalysis>) = dateListByExchange.map { date -> stockAnalysisMapByExchange.filter { it.value.dateList.isNotEmpty() && it.value.dateList.first() <= date && it.value.dateList.last() >= date }.mapNotNull { it.value.stockId } }

    private val stockIdsListBySh by lazy { stockIdsListByExchange(dateListBySh, stockAnalysisMapBySh) }

    private val stockIdsListBySz by lazy { stockIdsListByExchange(dateListBySz, stockAnalysisMapBySz) }

    private fun effStockIdsListByExchange(stockIdsListByExchange: List<List<String>>) = stockIdsListByExchange.mapIndexed { index, stockIds -> if (index > 0) stockIds.filter(stockIdsListByExchange[index - 1]::contains) else listOf() }

    private val effStockIdsListBySh by lazy { effStockIdsListByExchange(stockIdsListBySh) }

    private val effStockIdsListBySz by lazy { effStockIdsListByExchange(stockIdsListBySz) }

    private fun offsetMapByExchange(dateListByExchange: List<Date>, stockAnalysisMapByExchange: Map<String, StockAnalysis>) = stockAnalysisMapByExchange.mapValues { it.value.dateList.firstOrNull()?.let(dateListByExchange::indexOf) }

    private val offsetMapBySh by lazy { offsetMapByExchange(dateListBySh, stockAnalysisMapBySh) }

    private val offsetMapBySz by lazy { offsetMapByExchange(dateListBySz, stockAnalysisMapBySz) }

    private fun mapOf(dateListByExchange: List<Date>, valueListByExchange: List<Double?>): Map<Date, Double?> {
        val map = mutableMapOf<Date, Double?>()
        dateListByExchange.forEachIndexed { index, date -> map[date] = valueListByExchange[index] }
        return map
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

    private fun flatMapOf(dateListByExchange: List<Date>, valueListByExchange: List<Double?>): Map<Date, Double?> = flatten(mapOf(dateListByExchange, valueListByExchange))

    private fun weightedRateListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>, valueListTransform: (StockAnalysis) -> List<Double?>, weightListTransform: (StockAnalysis) -> List<Double?>) =
            dateListByExchange.indices.map { index ->
                if (index > 0) {
                    var numerator = 0.0
                    var denominator = 0.0
                    effStockIdsListByExchange[index].forEach { effStockId ->
                        val offset = offsetMapByExchange[effStockId]
                        if (offset !== null)
                            stockAnalysisMapByExchange[effStockId]?.let {
                                let3(valueListTransform(it)[index - offset], valueListTransform(it)[index - offset - 1], weightListTransform(it)[index - offset]) { a, b, c ->
                                    numerator += a / b * c
                                    denominator += c
                                }
                            }
                    }
                    numerator / denominator
                } else
                    1.0
            }

    private fun cumprod(eleList: List<Double?>): List<Double?> {
        val cumList = mutableListOf<Double?>()
        eleList.forEachIndexed { index, d ->
            cumList += if (index == 0) d
            else let2(cumList[index - 1], d) { a, b -> a * b }
        }
        return cumList
    }

    private fun sumListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>, valueListTransform: (StockAnalysis) -> List<Double?>) =
            dateListByExchange.indices.map { index ->
                stockIdsListByExchange[index].sumOf { stockId ->
                    val offset = offsetMapByExchange[stockId]
                    if (offset !== null)
                        stockAnalysisMapByExchange[stockId]?.let(valueListTransform)?.get(index - offset) ?: 0.0
                    else
                        0.0
                }
            }

    private fun openPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::openPreList, StockAnalysis::totalShareList))

    private val openPreListBySh by lazy { openPreListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatOpenPreListBySh by lazy { flatMapOf(dateListBySh, openPreListBySh).values.toList() }

    private val openPreListBySz by lazy { openPreListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatOpenPreListBySz by lazy { flatMapOf(dateListBySz, openPreListBySz).values.toList() }

    private fun closePreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::closePreList, StockAnalysis::totalShareList))

    private val closePreListBySh by lazy { closePreListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatClosePreListBySh by lazy { flatMapOf(dateListBySh, closePreListBySh).values.toList() }

    private val closePreListBySz by lazy { closePreListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatClosePreListBySz by lazy { flatMapOf(dateListBySz, closePreListBySz).values.toList() }

    private fun highPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::highPreList, StockAnalysis::totalShareList))

    private val highPreListBySh by lazy { highPreListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatHighPreListBySh by lazy { flatMapOf(dateListBySh, highPreListBySh).values.toList() }

    private val highPreListBySz by lazy { highPreListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatHighPreListBySz by lazy { flatMapOf(dateListBySz, highPreListBySz).values.toList() }

    private fun lowPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::lowPreList, StockAnalysis::totalShareList))

    private val lowPreListBySh by lazy { lowPreListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatLowPreListBySh by lazy { flatMapOf(dateListBySh, lowPreListBySh).values.toList() }

    private val lowPreListBySz by lazy { lowPreListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatLowPreListBySz by lazy { flatMapOf(dateListBySz, lowPreListBySz).values.toList() }

    private fun volumePreListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::volumePreList)

    private val volumePreListBySh by lazy { volumePreListByExchange(dateListBySh, stockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatVolumePreListBySh by lazy { flatMapOf(dateListBySh, volumePreListBySh).values.toList() }

    private val volumePreListBySz by lazy { volumePreListByExchange(dateListBySz, stockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatVolumePreListBySz by lazy { flatMapOf(dateListBySz, volumePreListBySz).values.toList() }

    private fun amountListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::amountList)

    private val amountListBySh by lazy { amountListByExchange(dateListBySh, stockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatAmountListBySh by lazy { flatMapOf(dateListBySh, amountListBySh).values.toList() }

    private val amountListBySz by lazy { amountListByExchange(dateListBySz, stockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatAmountListBySz by lazy { flatMapOf(dateListBySz, amountListBySz).values.toList() }

    private fun flowValueListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::flowValueList)

    private val flowValueListBySh by lazy { flowValueListByExchange(dateListBySh, stockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatFlowValueListBySh by lazy { flatMapOf(dateListBySh, flowValueListBySh).values.toList() }

    private val flowValueListBySz by lazy { flowValueListByExchange(dateListBySz, stockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatFlowValueListBySz by lazy { flatMapOf(dateListBySz, flowValueListBySz).values.toList() }

    private fun totalValueListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::totalValueList)

    private val totalValueListBySh by lazy { totalValueListByExchange(dateListBySh, stockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatTotalValueListBySh by lazy { flatMapOf(dateListBySh, totalValueListBySh).values.toList() }

    private val totalValueListBySz by lazy { totalValueListByExchange(dateListBySz, stockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatTotalValueListBySz by lazy { flatMapOf(dateListBySz, totalValueListBySz).values.toList() }

    private fun pbListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::pbList, StockAnalysis::totalShareList))

    private val pbListBySh by lazy { pbListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatpbListBySh by lazy { flatMapOf(dateListBySh, pbListBySh).values.toList() }

    private val pbListBySz by lazy { pbListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatpbListBySz by lazy { flatMapOf(dateListBySz, pbListBySz).values.toList() }

    private fun peListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::peList, StockAnalysis::totalShareList))

    private val peListBySh by lazy { peListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatpeListBySh by lazy { flatMapOf(dateListBySh, peListBySh).values.toList() }

    private val peListBySz by lazy { peListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatpeListBySz by lazy { flatMapOf(dateListBySz, peListBySz).values.toList() }

    private fun psListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::psList, StockAnalysis::totalShareList))

    private val psListBySh by lazy { psListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatpsListBySh by lazy { flatMapOf(dateListBySh, psListBySh).values.toList() }

    private val psListBySz by lazy { psListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatpsListBySz by lazy { flatMapOf(dateListBySz, psListBySz).values.toList() }

    private fun pcfListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockAnalysisMapByExchange: Map<String, StockAnalysis>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockAnalysisMapByExchange, offsetMapByExchange, StockAnalysis::pcfList, StockAnalysis::totalShareList))

    private val pcfListBySh by lazy { pcfListByExchange(dateListBySh, effStockIdsListBySh, stockAnalysisMapBySh, offsetMapBySh) }

    private val flatpcfListBySh by lazy { flatMapOf(dateListBySh, pcfListBySh).values.toList() }

    private val pcfListBySz by lazy { pcfListByExchange(dateListBySz, effStockIdsListBySz, stockAnalysisMapBySz, offsetMapBySz) }

    private val flatpcfListBySz by lazy { flatMapOf(dateListBySz, pcfListBySz).values.toList() }

    private fun weightedCombine(valueListBySh: List<Double?>, weightListBySh: List<Double?>, valueListBySz: List<Double?>, weightListBySz: List<Double?>) =
            dateList.indices.map {
                var numerator = 0.0
                var denominator = 0.0
                let2(valueListBySh[it], weightListBySh[it]) { a, b ->
                    numerator += a * b
                    denominator += b
                }
                let2(valueListBySz[it], weightListBySz[it]) { a, b ->
                    numerator += a * b
                    denominator += b
                }
                if (denominator > 0)
                    numerator / denominator
                else
                    null
            }

    private fun sumCombine(valueListBySh: List<Double?>, valueListBySz: List<Double?>): List<Double?> = dateList.indices.map { let2(valueListBySh[it], valueListBySz[it]) { a, b -> a + b } }

    val openPreList by lazy { weightedCombine(flatOpenPreListBySh, flatFlowValueListBySh, flatOpenPreListBySz, flatFlowValueListBySz) }

    val closePreList by lazy { weightedCombine(flatClosePreListBySh, flatFlowValueListBySh, flatClosePreListBySz, flatFlowValueListBySz) }

    val highPreList by lazy { weightedCombine(flatHighPreListBySh, flatFlowValueListBySh, flatHighPreListBySz, flatFlowValueListBySz) }

    val lowPreList by lazy { weightedCombine(flatLowPreListBySh, flatFlowValueListBySh, flatLowPreListBySz, flatFlowValueListBySz) }

    val volumePreList by lazy { sumCombine(flatVolumePreListBySh, flatVolumePreListBySz) }

    val amountList by lazy { sumCombine(flatAmountListBySh, flatAmountListBySz) }

    val flowValueList by lazy { sumCombine(flatFlowValueListBySh, flatFlowValueListBySz) }

    val totalValueList by lazy { sumCombine(flatTotalValueListBySh, flatTotalValueListBySz) }

    val pbList by lazy { weightedCombine(pbListBySh, flatFlowValueListBySh, pbListBySz, flatFlowValueListBySz) }

    val peList by lazy { weightedCombine(peListBySh, flatFlowValueListBySh, peListBySz, flatFlowValueListBySz) }

    val psList by lazy { weightedCombine(psListBySh, flatFlowValueListBySh, psListBySz, flatFlowValueListBySz) }

    val pcfList by lazy { weightedCombine(pcfListBySh, flatFlowValueListBySh, pcfListBySz, flatFlowValueListBySz) }

    val dto by lazy { GroupDto(groupId, dateList, openPreList, closePreList, highPreList, lowPreList, volumePreList, amountList, pbList, peList, psList, pcfList) }
}