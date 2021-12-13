package org.p8499.quant.tushare.dtoBuilder

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.finiteOrNull
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.dto.GroupDto
import org.p8499.quant.tushare.dto.StockDto
import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.*
import org.slf4j.LoggerFactory
import java.util.*

class GroupDtoBuilder(
        val groupId: String,
        stockDtoList: List<StockDto>,
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

    private val name by lazy { groupService[groupId]?.name ?: "" }

    private val message by lazy { "" }

    private fun stockIdListByExchange(exchangeId: String): List<String> = stockService.findByStockIdList(groupStockService.findByGroupId(groupId).mapNotNull(GroupStock::stockId)).filter { it.exchangeId == exchangeId }.mapNotNull(Stock::id)

    private val stockIdListBySh by lazy { stockIdListByExchange("SSE") }

    private val stockIdListBySz by lazy { stockIdListByExchange("SZSE") }

    private fun stockDtoMapByExchange(stockDtoList: List<StockDto>, stockIdListByExchange: List<String>): Map<String, StockDto> = mutableMapOf<String, StockDto>().apply { stockDtoList.filter { stockIdListByExchange.contains(it.id) }.forEach { put(it.id, it) } }

    private val stockDtoMapBySh by lazy { stockDtoMapByExchange(stockDtoList, stockIdListBySh) }

    private val stockDtoMapBySz by lazy { stockDtoMapByExchange(stockDtoList, stockIdListBySz) }

    private fun dateListByExchange(stockDtoMapByExchange: Map<String, StockDto>) = stockDtoMapByExchange.flatMap { it.value.date }.distinct().sorted()

    private val dateListBySh by lazy { dateListByExchange(stockDtoMapBySh) }

    private val dateListBySz by lazy { dateListByExchange(stockDtoMapBySz) }

    val dateList by lazy { (dateListBySh + dateListBySz).distinct().sorted() }

    private fun stockIdsListByExchange(dateListByExchange: List<Date>, stockDtoMapByExchange: Map<String, StockDto>) = dateListByExchange.map { date -> stockDtoMapByExchange.filter { it.value.date.isNotEmpty() && it.value.date.first() <= date && it.value.date.last() >= date }.mapNotNull { it.value.id } }

    private val stockIdsListBySh by lazy { stockIdsListByExchange(dateListBySh, stockDtoMapBySh) }

    private val stockIdsListBySz by lazy { stockIdsListByExchange(dateListBySz, stockDtoMapBySz) }

    private fun effStockIdsListByExchange(stockIdsListByExchange: List<List<String>>) = stockIdsListByExchange.mapIndexed { index, stockIds -> if (index > 0) stockIds.filter(stockIdsListByExchange[index - 1]::contains) else listOf() }

    private val effStockIdsListBySh by lazy { effStockIdsListByExchange(stockIdsListBySh) }

    private val effStockIdsListBySz by lazy { effStockIdsListByExchange(stockIdsListBySz) }

    private fun offsetMapByExchange(dateListByExchange: List<Date>, stockDtoMapByExchange: Map<String, StockDto>) = stockDtoMapByExchange.mapValues { it.value.date.firstOrNull()?.let(dateListByExchange::indexOf) }

    private val offsetMapBySh by lazy { offsetMapByExchange(dateListBySh, stockDtoMapBySh) }

    private val offsetMapBySz by lazy { offsetMapByExchange(dateListBySz, stockDtoMapBySz) }

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

    private fun weightedRateListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>, valueListTransform: (StockDto) -> List<Double?>, weightListTransform: (StockDto) -> List<Double?>): List<Double> {
        val weightedRateList = mutableListOf<Double>()
        dateListByExchange.indices.forEach { index ->
            if (index > 0) {
                var numerator = 0.0
                var denominator = 0.0
                effStockIdsListByExchange[index].forEach { effStockId ->
                    val offset = offsetMapByExchange[effStockId]
                    if (offset !== null)
                        stockDtoMapByExchange[effStockId]?.let {
                            val valueList = valueListTransform(it)
                            val weightList = weightListTransform(it)
                            let(valueList[index - offset], valueList[index - offset - 1], weightList[index - offset]) { a, b, c ->
                                if (b != 0.0) {
                                    numerator += a / b * c
                                    denominator += c
                                }
                            }
                        }
                }
                weightedRateList.add((numerator / denominator).finiteOrNull() ?: weightedRateList[index - 1])
            } else
                weightedRateList.add(1.0)
        }
        return weightedRateList
    }

    private fun cumprod(eleList: List<Double?>): List<Double?> {
        val cumList = mutableListOf<Double?>()
        eleList.forEachIndexed { index, d ->
            cumList += if (index == 0) d
            else let(cumList[index - 1], d) { a, b -> a * b }
        }
        return cumList
    }

    private fun sumListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>, valueListTransform: (StockDto) -> List<Double?>) =
            dateListByExchange.indices.map { index ->
                stockIdsListByExchange[index].sumOf { stockId ->
                    val offset = offsetMapByExchange[stockId]
                    if (offset !== null)
                        stockDtoMapByExchange[stockId]?.let(valueListTransform)?.get(index - offset) ?: 0.0
                    else
                        0.0
                }
            }

    private fun openPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::open, StockDto::totalShare))

    private val openPreListBySh by lazy { openPreListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatOpenPreListBySh by lazy { flatMapOf(dateListBySh, openPreListBySh).values.toList() }

    private val openPreListBySz by lazy { openPreListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatOpenPreListBySz by lazy { flatMapOf(dateListBySz, openPreListBySz).values.toList() }

    private fun closePreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::close, StockDto::totalShare))

    private val closePreListBySh by lazy { closePreListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatClosePreListBySh by lazy { flatMapOf(dateListBySh, closePreListBySh).values.toList() }

    private val closePreListBySz by lazy { closePreListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatClosePreListBySz by lazy { flatMapOf(dateListBySz, closePreListBySz).values.toList() }

    private fun highPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::high, StockDto::totalShare))

    private val highPreListBySh by lazy { highPreListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatHighPreListBySh by lazy { flatMapOf(dateListBySh, highPreListBySh).values.toList() }

    private val highPreListBySz by lazy { highPreListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatHighPreListBySz by lazy { flatMapOf(dateListBySz, highPreListBySz).values.toList() }

    private fun lowPreListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::low, StockDto::totalShare))

    private val lowPreListBySh by lazy { lowPreListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatLowPreListBySh by lazy { flatMapOf(dateListBySh, lowPreListBySh).values.toList() }

    private val lowPreListBySz by lazy { lowPreListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatLowPreListBySz by lazy { flatMapOf(dateListBySz, lowPreListBySz).values.toList() }

    private fun volumePreListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::volume)

    private val volumePreListBySh by lazy { volumePreListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatVolumePreListBySh by lazy { flatMapOf(dateListBySh, volumePreListBySh).values.toList() }

    private val volumePreListBySz by lazy { volumePreListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatVolumePreListBySz by lazy { flatMapOf(dateListBySz, volumePreListBySz).values.toList() }

    private fun amountListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::amount)

    private val amountListBySh by lazy { amountListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatAmountListBySh by lazy { flatMapOf(dateListBySh, amountListBySh).values.toList() }

    private val amountListBySz by lazy { amountListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatAmountListBySz by lazy { flatMapOf(dateListBySz, amountListBySz).values.toList() }

    private fun flowShareListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::flowShare)

    private val flowShareListBySh by lazy { flowShareListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatFlowShareListBySh by lazy { flatMapOf(dateListBySh, flowShareListBySh).values.toList() }

    private val flowShareListBySz by lazy { flowShareListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatFlowShareListBySz by lazy { flatMapOf(dateListBySz, flowShareListBySz).values.toList() }

    private fun totalShareListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::totalShare)

    private val totalShareListBySh by lazy { totalShareListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatTotalShareListBySh by lazy { flatMapOf(dateListBySh, totalShareListBySh).values.toList() }

    private val totalShareListBySz by lazy { totalShareListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatTotalShareListBySz by lazy { flatMapOf(dateListBySz, totalShareListBySz).values.toList() }

    private fun flowValueListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::flowValue)

    private val flowValueListBySh by lazy { flowValueListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatFlowValueListBySh by lazy { flatMapOf(dateListBySh, flowValueListBySh).values.toList() }

    private val flowValueListBySz by lazy { flowValueListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatFlowValueListBySz by lazy { flatMapOf(dateListBySz, flowValueListBySz).values.toList() }

    private fun totalValueListByExchange(dateListByExchange: List<Date>, stockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = sumListByExchange(dateListByExchange, stockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::totalValue)

    private val totalValueListBySh by lazy { totalValueListByExchange(dateListBySh, stockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatTotalValueListBySh by lazy { flatMapOf(dateListBySh, totalValueListBySh).values.toList() }

    private val totalValueListBySz by lazy { totalValueListByExchange(dateListBySz, stockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatTotalValueListBySz by lazy { flatMapOf(dateListBySz, totalValueListBySz).values.toList() }

    private fun pbListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::pb, StockDto::totalShare))

    private val pbListBySh by lazy { pbListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatPbListBySh by lazy { flatMapOf(dateListBySh, pbListBySh).values.toList() }

    private val pbListBySz by lazy { pbListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatPbListBySz by lazy { flatMapOf(dateListBySz, pbListBySz).values.toList() }

    private fun peListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::pe, StockDto::totalShare))

    private val peListBySh by lazy { peListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatPeListBySh by lazy { flatMapOf(dateListBySh, peListBySh).values.toList() }

    private val peListBySz by lazy { peListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatPeListBySz by lazy { flatMapOf(dateListBySz, peListBySz).values.toList() }

    private fun psListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::ps, StockDto::totalShare))

    private val psListBySh by lazy { psListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatPsListBySh by lazy { flatMapOf(dateListBySh, psListBySh).values.toList() }

    private val psListBySz by lazy { psListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatPsListBySz by lazy { flatMapOf(dateListBySz, psListBySz).values.toList() }

    private fun pcfListByExchange(dateListByExchange: List<Date>, effStockIdsListByExchange: List<List<String>>, stockDtoMapByExchange: Map<String, StockDto>, offsetMapByExchange: Map<String, Int?>) = cumprod(weightedRateListByExchange(dateListByExchange, effStockIdsListByExchange, stockDtoMapByExchange, offsetMapByExchange, StockDto::pcf, StockDto::totalShare))

    private val pcfListBySh by lazy { pcfListByExchange(dateListBySh, effStockIdsListBySh, stockDtoMapBySh, offsetMapBySh) }

    private val flatPcfListBySh by lazy { flatMapOf(dateListBySh, pcfListBySh).values.toList() }

    private val pcfListBySz by lazy { pcfListByExchange(dateListBySz, effStockIdsListBySz, stockDtoMapBySz, offsetMapBySz) }

    private val flatPcfListBySz by lazy { flatMapOf(dateListBySz, pcfListBySz).values.toList() }

    private fun weightedCombine(valueListBySh: List<Double?>, weightListBySh: List<Double?>, valueListBySz: List<Double?>, weightListBySz: List<Double?>) =
            dateList.indices.map {
                var numerator = 0.0
                var denominator = 0.0
                let(valueListBySh[it], weightListBySh[it]) { a, b ->
                    numerator += a * b
                    denominator += b
                }
                let(valueListBySz[it], weightListBySz[it]) { a, b ->
                    numerator += a * b
                    denominator += b
                }
                (numerator / denominator).finiteOrNull()
            }

    private fun sumCombine(valueListBySh: List<Double?>, valueListBySz: List<Double?>): List<Double?> = dateList.indices.map { let(valueListBySh[it], valueListBySz[it]) { a, b -> a + b } }

    val openPreList by lazy { weightedCombine(flatOpenPreListBySh, flatFlowValueListBySh, flatOpenPreListBySz, flatFlowValueListBySz) }

    val closePreList by lazy { weightedCombine(flatClosePreListBySh, flatFlowValueListBySh, flatClosePreListBySz, flatFlowValueListBySz) }

    val highPreList by lazy { weightedCombine(flatHighPreListBySh, flatFlowValueListBySh, flatHighPreListBySz, flatFlowValueListBySz) }

    val lowPreList by lazy { weightedCombine(flatLowPreListBySh, flatFlowValueListBySh, flatLowPreListBySz, flatFlowValueListBySz) }

    val volumePreList by lazy { sumCombine(flatVolumePreListBySh, flatVolumePreListBySz) }

    val amountList by lazy { sumCombine(flatAmountListBySh, flatAmountListBySz) }

    val flowShareList by lazy { sumCombine(flatFlowShareListBySh, flatFlowShareListBySz) }

    val totalShareList by lazy { sumCombine(flatTotalShareListBySh, flatTotalShareListBySz) }

    val flowValueList by lazy { sumCombine(flatFlowValueListBySh, flatFlowValueListBySz) }

    val totalValueList by lazy { sumCombine(flatTotalValueListBySh, flatTotalValueListBySz) }

    val pbList by lazy { weightedCombine(flatPbListBySh, flatFlowValueListBySh, flatPbListBySz, flatFlowValueListBySz) }

    val peList by lazy { weightedCombine(flatPeListBySh, flatFlowValueListBySh, flatPeListBySz, flatFlowValueListBySz) }

    val psList by lazy { weightedCombine(flatPsListBySh, flatFlowValueListBySh, flatPsListBySz, flatFlowValueListBySz) }

    val pcfList by lazy { weightedCombine(flatPcfListBySh, flatFlowValueListBySh, flatPcfListBySz, flatFlowValueListBySz) }

    val stockIdList by lazy { stockIdListBySh + stockIdListBySz }

    val percentList by lazy {
        var weightListBySh = stockIdListBySh.map { stockDtoMapBySh[it]?.flowValue?.last() ?: 0.0 }
        var weightListBySz = stockIdListBySz.map { stockDtoMapBySz[it]?.flowValue?.last() ?: 0.0 }
        val sum = weightListBySh.sum() + weightListBySz.sum()
        weightListBySh = weightListBySh.map { it / sum }
        weightListBySz = weightListBySz.map { it / sum }
        weightListBySh + weightListBySz
    }

    fun build(): GroupDto {
        logger.info("Constructing $groupId DTO")
        /*
           TODO cumprod(weightedRateListByExchange) is not applicable to pcf
        */
        return GroupDto("CN", groupId, name, message, stockIdList, percentList, dateList, openPreList, closePreList, highPreList, lowPreList, volumePreList, amountList, flowShareList, totalShareList, flowValueList, totalValueList, pbList, peList, psList, dateList.map { 0.0 }/*pcfList*/)
    }
}