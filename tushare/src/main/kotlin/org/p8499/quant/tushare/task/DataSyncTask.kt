package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.*
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.tushareRequest.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.util.*

@Component
class DataSyncTask {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

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

    @Autowired
    lateinit var tradeCalRequest: TradeCalRequest

    @Autowired
    lateinit var stockBasicRequest: StockBasicRequest

    @Autowired
    lateinit var indexBasicRequest: IndexBasicRequest

    @Autowired
    lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    lateinit var indexClassifyRequest: IndexClassifyRequest

    @Autowired
    lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    lateinit var conceptRequest: ConceptRequest

    @Autowired
    lateinit var conceptDetailRequest: ConceptDetailRequest

    @Autowired
    lateinit var balancesheetRequest: BalancesheetRequest

    @Autowired
    lateinit var incomeRequest: IncomeRequest

    @Autowired
    lateinit var cashflowRequest: CashflowRequest

    @Autowired
    lateinit var expressRequest: ExpressRequest

    @Autowired
    lateinit var forecastRequest: ForecastRequest

    @Autowired
    lateinit var dailyRequest: DailyRequest

    @Autowired
    lateinit var adjFactorRequest: AdjFactorRequest

    @Autowired
    lateinit var dailyBasicRequest: DailyBasicRequest

    @Autowired
    lateinit var moneyflowRequest: MoneyflowRequest

    @Autowired
    lateinit var amqpTemplate: AmqpTemplate;

    private fun <A, B, R> let2(a: A?, b: B?, block: (A, B) -> R): R? = if (a !== null && b !== null) block(a, b) else null

    private fun <A, B, C, R> let3(a: A?, b: B?, c: C?, block: (A, B, C) -> R): R? = if (a !== null && b !== null && c !== null) block(a, b, c) else null

    @Scheduled(cron = "0 0 18 * * MON-FRI")
    fun sync() {
        pullExchange()
        pullTradingDate()
        pullStock()
        pullGroup()
        pullLevel1Candlestick()
        pullLevel1Basic()
        pullLevel1AdjFactor()
        pullLevel2()
        pullGroupStock()
        pullBalanceSheet()
        pullIncome()
        pullCashflow()
        pullExpress()
        pullForecast()
        push()
    }

    private fun pullExchange() {
        exchangeService.save(Exchange("SSE", "上交所"))
        exchangeService.save(Exchange("SZSE", "深交所"))
    }

    private fun pullTradingDate() {
        val unprocessedTradingDateList: (String) -> List<TradingDate> = { exchange ->
            val lastDate = tradingDateService.last(exchange)?.date
            val startDate = lastDate?.let {
                Calendar.getInstance().apply {
                    time = it
                    add(Calendar.DATE, 1)
                }.time
            } ?: GregorianCalendar(2007, 0, 1).time
            tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = exchange, startDate = startDate, endDate = Date(), isOpen = true), TradeCalRequest.OutParams::class.java).map { TradingDate(it.exchange, it.calDate) }
        }
        tradingDateService.saveAll(unprocessedTradingDateList("SSE") + unprocessedTradingDateList("SZSE"))
    }

    private fun pullStock() {
        val stockListOfStatus: (Char) -> List<Stock> = { listStatus ->
            stockBasicRequest.invoke(StockBasicRequest.InParams(listStatus = listStatus), StockBasicRequest.OutParams::class.java, arrayOf("ts_code", "exchange", "symbol", "name", "list_date", "delist_date")).map { Stock(it.tsCode, it.exchange, it.symbol, it.name, it.listDate, it.delistDate) }
        }
        stockService.saveAll(stockListOfStatus('L') + stockListOfStatus('P') + stockListOfStatus('D'))
    }

    private fun pullGroup() {
        val indexListOfMarket: (String) -> List<Group> = { market ->
            indexBasicRequest.invoke(IndexBasicRequest.InParams(market = market), IndexBasicRequest.OutParams::class.java).map { Group(it.tsCode, it.name, Group.Type.INDEX) }
        }
        val industryList: () -> List<Group> = {
            indexClassifyRequest.invoke(IndexClassifyRequest.InParams(), IndexClassifyRequest.OutParams::class.java).map { Group(it.indexCode, it.industryName, Group.Type.INDUSTRY) }
        }
        val conceptList: () -> List<Group> = {
            conceptRequest.invoke(ConceptRequest.InParams(), ConceptRequest.OutParams::class.java).map { Group(it.code, it.name, Group.Type.CONCEPT) }
        }
        groupService.saveAll(indexListOfMarket("MSCI") + indexListOfMarket("CSI") + indexListOfMarket("SSE") + indexListOfMarket("SZSE") + indexListOfMarket("CICC") + indexListOfMarket("SW") + indexListOfMarket("OTH") + industryList() + conceptList())
    }

    private fun pullLevel1Candlestick() {
        val level1CandlestickList: (String) -> List<Level1Candlestick> = { tsCode ->
            tradingDateService.unprocessedForLevel1Candlestick(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { dailyRequest.invoke(DailyRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), DailyRequest.OutParams::class.java).asList() }
                    .map { Level1Candlestick(tsCode, it.tradeDate, it.open, it.close, it.high, it.low, it.vol, it.amount) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level1CandlestickService.saveAll(level1CandlestickList(it)) }
    }

    private fun pullLevel1Basic() {
        val level1BasicList: (String) -> List<Level1Basic> = { tsCode ->
            tradingDateService.unprocessedForLevel1Basic(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { dailyBasicRequest.invoke(DailyBasicRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), DailyBasicRequest.OutParams::class.java).asList() }
                    .map { Level1Basic(tsCode, it.tradeDate, it.totalShare, it.floatShare) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level1BasicService.saveAll(level1BasicList(it)) }
    }

    private fun pullLevel1AdjFactor() {
        val level1AdjFactorList: (String) -> List<Level1AdjFactor> = { tsCode ->
            tradingDateService.unprocessedForLevel1AdjFactor(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { adjFactorRequest.invoke(AdjFactorRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), AdjFactorRequest.OutParams::class.java).asList() }
                    .map { Level1AdjFactor(tsCode, it.tradeDate, it.adjFactor) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level1AdjFactorService.saveAll(level1AdjFactorList(it)) }
    }

    private fun pullLevel2() {
        val level2List: (String) -> List<Level2> = { tsCode ->
            tradingDateService.unprocessedForLevel2(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { moneyflowRequest.invoke(MoneyflowRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), MoneyflowRequest.OutParams::class.java).asList() }
                    .map { Level2(tsCode, it.tradeDate, it.buySmVol, it.sellSmVol, it.buyMdVol, it.sellMdVol, it.buyLgVol, it.sellLgVol, it.buyElgVol, it.sellElgVol) }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        stockIdList.forEach { level2Service.saveAll(level2List(it)) }
    }

    private fun pullGroupStock() {
        val groupStockListOfIndex: (Date) -> List<GroupStock> = { tradeDate ->
            val groupStockList = indexWeightRequest.invoke(IndexWeightRequest.InParams(tradeDate = tradeDate), IndexWeightRequest.OutParams::class.java).map { GroupStock(it.indexCode, it.conCode, it.weight) }
            val stockIdList = groupStockList.mapNotNull(GroupStock::stockId).let(stockService::findByStockIdList).map(Stock::id)
            groupStockList.filter { stockIdList.contains(it.groupId) }
        }
        val flowShareMap: (Date) -> Map<String, Double> = { tradeDate ->
            val map = mutableMapOf<String, Double>()
            for (stockId in stockService.findAll().mapNotNull(Stock::id))
                level1BasicService[stockId, tradeDate]?.flowShare?.also { map[stockId] = it }
            map
        }
        val weightList: (Map<String, Double>, List<String>) -> List<Double> = { rsMap, stockIdList ->
            val flowShareList = stockIdList.map { stockId -> rsMap[stockId] }
            val maxFlowShare = flowShareList.mapNotNull { it }.maxOrNull()
            flowShareList.map { let2(it, maxFlowShare) { a, b -> a * 1000 / b } ?: 0.0 }
        }
        val groupStockListOfIndustry: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            val groupIdList = groupService.findByType(Group.Type.INDUSTRY).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = groupId), IndexMemberRequest.OutParams::class.java).map(IndexMemberRequest.OutParams::conCode).mapNotNull { it }
                val wList = weightList(rsMap, stockIdList)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val groupStockListOfConcept: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            val groupIdList = groupService.findByType(Group.Type.CONCEPT).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = conceptDetailRequest.invoke(ConceptDetailRequest.InParams(id = groupId), ConceptDetailRequest.OutParams::class.java).map(ConceptDetailRequest.OutParams::tsCode).mapNotNull { it }
                val wList = weightList(rsMap, stockIdList)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val date = tradingDateService.last("SSE")?.date ?: return
        val fsMap = flowShareMap(date)
        groupStockService.deleteAndSaveAll(groupStockListOfIndex(date) + groupStockListOfIndustry(fsMap) + groupStockListOfConcept(fsMap))
    }

    private fun pullBalanceSheet() {
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            balanceSheetService.saveAll(balancesheetRequest.invoke(BalancesheetRequest.InParams(tsCode = stockId), BalancesheetRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "total_hldr_eqy_exc_min_int", "update_flag"))
                    .groupBy(BalancesheetRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(BalancesheetRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            BalanceSheet(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, it.value.totalHldrEqyExcMinInt)
                        }
                    })
    }

    private fun pullIncome() {
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            incomeService.saveAll(incomeRequest.invoke(IncomeRequest.InParams(tsCode = stockId), IncomeRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "n_income_attr_p", "update_flag"))
                    .groupBy(IncomeRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(IncomeRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Income(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, it.value.nIncomeAttrP)
                        }
                    })
    }

    private fun pullCashflow() {
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            cashflowService.saveAll(cashflowRequest.invoke(CashflowRequest.InParams(tsCode = stockId), CashflowRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "n_cashflow_act", "update_flag"))
                    .groupBy(CashflowRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(CashflowRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Cashflow(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, it.value.nCashflowAct)
                        }
                    })
    }

    private fun pullExpress() {
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            expressService.saveAll(expressRequest.invoke(ExpressRequest.InParams(tsCode = stockId), ExpressRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "revenue", "total_hldr_eqy_exc_min_int", "is_audit"))
                    .groupBy(ExpressRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ExpressRequest.OutParams::isAudit)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Express(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, it.value.totalHldrEqyExcMinInt, it.value.revenue)
                        }
                    })
    }

    private fun pullForecast() {
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            forecastService.saveAll(forecastRequest.invoke(ForecastRequest.InParams(tsCode = stockId), ForecastRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "type", "p_change_min", "p_change_max", "change_reason"))
                    .groupBy(ForecastRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ForecastRequest.OutParams::annDate)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            val format = DecimalFormat("0.00")
                            Forecast(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, "${it.value.type} (${format.format(it.value.pChangeMin)} - ${format.format(it.value.pChangeMax)})", it.value.changeReason)
                        }
                    })
    }

    private fun push() {
        val message: (String, Date, String, Double?) -> Map<String, Any?> = { code, date, name, value ->
            mutableMapOf<String, Any?>().also {
                it["code"] = code
                it["date"] = date
                it["name"] = name
                it["value"] = value
            }
        }
        val stockIdList = stockService.findAll().mapNotNull(Stock::id)
        for (stockId in stockIdList) {
            val data = StockData(stockId)
            data.openPreList.mapIndexed { index, d -> message(stockId, data.dateList[index], "open", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.closePreList.mapIndexed { index, d -> message(stockId, data.dateList[index], "close", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.highPreList.mapIndexed { index, d -> message(stockId, data.dateList[index], "high", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.lowPreList.mapIndexed { index, d -> message(stockId, data.dateList[index], "low", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.volumePreList.mapIndexed { index, d -> message(stockId, data.dateList[index], "volume", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.amountList.mapIndexed { index, d -> message(stockId, data.dateList[index], "amount", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.pbList.mapIndexed { index, d -> message(stockId, data.dateList[index], "pb", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.peList.mapIndexed { index, d -> message(stockId, data.dateList[index], "pe", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.psList.mapIndexed { index, d -> message(stockId, data.dateList[index], "ps", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
            data.pcfList.mapIndexed { index, d -> message(stockId, data.dateList[index], "pcf", d) }.forEach { amqpTemplate.convertAndSend("data", it) }
        }
    }


    internal inner class StockData(
            val stockId: String) {
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
            for (entry in entryMap)
                if (entry.value === null)
                    map[entry.key] = map.lowerEntry(entry.key)?.value
            return map
        }

        private fun <T> flatMapOf(items: Iterable<T>, keyTransform: (T) -> Date?, valueTransform: (T) -> Double?): Map<Date, Double?> = flatten(mapOf(items, keyTransform, valueTransform))

        val dateList by lazy { tradingDateService.findByStockId(stockId).mapNotNull(TradingDate::date) }

        val factorList by lazy { flatMapOf(level1AdjFactorService.findByStockId(stockId), Level1AdjFactor::date, Level1AdjFactor::factor).values.toList() }
        val maxFactor by lazy { factorList.mapNotNull { it }.maxOrNull() }

        val openList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::open).values.toList() }

        val openPreList by lazy { openList.mapIndexed { index, d -> let3(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

        val closeList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::close).values.toList() }

        val closePreList by lazy { closeList.mapIndexed { index, d -> let3(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

        val highList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::high).values.toList() }

        val highPreList by lazy { highList.mapIndexed { index, d -> let3(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

        val lowList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::low).values.toList() }

        val lowPreList by lazy { lowList.mapIndexed { index, d -> let3(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

        val volumeList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::volume).values.toList() }

        val volumePreList by lazy { volumeList.mapIndexed { index, d -> let3(d, factorList[index], maxFactor) { a, b, c -> a * b / c } } }

        val amountList by lazy { flatMapOf(level1CandlestickService.findByStockId(stockId), Level1Candlestick::date, Level1Candlestick::amount).values.toList() }

        val flowShareList by lazy { flatMapOf(level1BasicService.findByStockId(stockId), Level1Basic::date, Level1Basic::flowShare).values.toList() }

        val totalShareList by lazy { flatMapOf(level1BasicService.findByStockId(stockId), Level1Basic::date, Level1Basic::totalShare).values.toList() }

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
            let3(dateTransform(it), periodTransform(it), valueTransform(it)) { a, b, c -> a to multiplier(a, b)?.times(c) }
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

        val netAssetPerStockList by lazy { netAssetList.mapIndexed { index, d -> let2(d, totalShareList[index]) { a, b -> a / b } } }

        val netProfitPerStockList by lazy { netProfitList.mapIndexed { index, d -> let2(d, totalShareList[index]) { a, b -> a / b } } }

        val revenuePerStockList by lazy { revenueList.mapIndexed { index, d -> let2(d, totalShareList[index]) { a, b -> a / b } } }

        val opCashflowPerStockList by lazy { opCashflowList.mapIndexed { index, d -> let2(d, totalShareList[index]) { a, b -> a / b } } }

        val pbList by lazy { closePreList.mapIndexed { index, d -> let2(d, netAssetPerStockList[index]) { a, b -> a / b } } }

        val peList by lazy { closePreList.mapIndexed { index, d -> let2(d, netProfitPerStockList[index]) { a, b -> a / b } } }

        val psList by lazy { closePreList.mapIndexed { index, d -> let2(d, revenuePerStockList[index]) { a, b -> a / b } } }

        val pcfList by lazy { closePreList.mapIndexed { index, d -> let2(d, opCashflowPerStockList[index]) { a, b -> a / b } } }
    }
}