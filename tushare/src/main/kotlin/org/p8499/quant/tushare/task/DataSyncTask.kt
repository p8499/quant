package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.entity.*
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.tushareRequest.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.util.*

@Component
class DataSyncTask {
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

    @Scheduled(cron = "0 0 18 * * MON-FRI")
    fun sync() {
        syncExchange()
        syncTradingDate()
        syncStock()
        syncGroup()
        syncLevel1Candlestick()
        syncLevel1Basic()
        syncLevel1AdjFactor()
        syncLevel2()
        syncGroupStock()
        syncBalanceSheet()
        syncIncome()
        syncCashflow()
        syncExpress()
        syncForecast()
    }

    private fun syncExchange() {
        exchangeService.save(Exchange("SSE", "上交所"))
        exchangeService.save(Exchange("SZSE", "深交所"))
    }

    private fun syncTradingDate() {
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

    private fun syncStock() {
        val stockListOfStatus: (Char) -> List<Stock> = { listStatus ->
            stockBasicRequest.invoke(StockBasicRequest.InParams(listStatus = listStatus), StockBasicRequest.OutParams::class.java, arrayOf("ts_code", "exchange", "symbol", "name", "list_date", "delist_date")).map { Stock(it.tsCode, it.exchange, it.symbol, it.name, it.listDate, it.delistDate) }
        }
        stockService.saveAll(stockListOfStatus('L') + stockListOfStatus('P') + stockListOfStatus('D'))
    }

    private fun syncGroup() {
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

    private fun syncLevel1Candlestick() {
        val level1CandlestickList: (String) -> List<Level1Candlestick> = { tsCode ->
            tradingDateService.unprocessedForLevel1Candlestick(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { dailyRequest.invoke(DailyRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), DailyRequest.OutParams::class.java).asList() }
                    .map { Level1Candlestick(tsCode, it.tradeDate, it.open, it.close, it.high, it.low, it.vol, it.amount) }
        }
        for (stockId in stockService.findAll().mapNotNull(Stock::id))
            level1CandlestickService.saveAll(level1CandlestickList(stockId))
    }

    private fun syncLevel1Basic() {
        val level1BasicList: (String) -> List<Level1Basic> = { tsCode ->
            tradingDateService.unprocessedForLevel1Basic(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { dailyBasicRequest.invoke(DailyBasicRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), DailyBasicRequest.OutParams::class.java).asList() }
                    .map { Level1Basic(tsCode, it.tradeDate, it.totalShare, it.floatShare) }
        }
        for (stockId in stockService.findAll().mapNotNull(Stock::id))
            level1BasicService.saveAll(level1BasicList(stockId))
    }

    private fun syncLevel1AdjFactor() {
        val level1AdjFactorList: (String) -> List<Level1AdjFactor> = { tsCode ->
            tradingDateService.unprocessedForLevel1AdjFactor(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { adjFactorRequest.invoke(AdjFactorRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), AdjFactorRequest.OutParams::class.java).asList() }
                    .map { Level1AdjFactor(tsCode, it.tradeDate, it.adjFactor) }
        }
        for (stockId in stockService.findAll().mapNotNull(Stock::id))
            level1AdjFactorService.saveAll(level1AdjFactorList(stockId))
    }

    private fun syncLevel2() {
        val level2List: (String) -> List<Level2> = { tsCode ->
            tradingDateService.unprocessedForLevel2(tsCode).mapNotNull(TradingDate::date).groupBy {
                Calendar.getInstance().run {
                    time = it
                    get(Calendar.YEAR)
                }
            }.flatMap { moneyflowRequest.invoke(MoneyflowRequest.InParams(tsCode = tsCode, startDate = it.value.minOrNull(), endDate = it.value.maxOrNull()), MoneyflowRequest.OutParams::class.java).asList() }
                    .map { Level2(tsCode, it.tradeDate, it.buySmVol, it.sellSmVol, it.buyMdVol, it.sellMdVol, it.buyLgVol, it.sellLgVol, it.buyElgVol, it.sellElgVol) }
        }
        for (stockId in stockService.findAll().mapNotNull(Stock::id))
            level2Service.saveAll(level2List(stockId))
    }

    private fun syncGroupStock() {
        val groupStockListOfIndex: (Date) -> List<GroupStock> = { tradeDate ->
            val groupStockList = indexWeightRequest.invoke(IndexWeightRequest.InParams(tradeDate = tradeDate), IndexWeightRequest.OutParams::class.java).map { GroupStock(it.indexCode, it.conCode, it.weight) }
            val stockIdList = groupStockList.mapNotNull(GroupStock::stockId).let(stockService::findByStockIdList).map(Stock::id)
            groupStockList.filter { stockIdList.contains(it.groupId) }
        }
        val weightList: (List<String>, Date) -> List<Double> = { stockIdList, tradeDate ->
            val flowShareList = stockIdList.map { level1BasicService[it, tradeDate] }.map { it?.flowShare ?: 0.0 }
            val maxFlowShare = flowShareList.maxOf { it }
            flowShareList.map { it * 1000 / maxFlowShare }
        }
        val groupStockListOfIndustry: (Date) -> List<GroupStock> = { tradeDate ->
            val groupIdList = groupService.findByType(Group.Type.INDUSTRY).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = groupId), IndexMemberRequest.OutParams::class.java).map(IndexMemberRequest.OutParams::conCode).mapNotNull { it }
                val wList = weightList(stockIdList, tradeDate)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val groupStockListOfConcept: (Date) -> List<GroupStock> = { tradeDate ->
            val groupIdList = groupService.findByType(Group.Type.CONCEPT).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = conceptDetailRequest.invoke(ConceptDetailRequest.InParams(id = groupId), ConceptDetailRequest.OutParams::class.java).map(ConceptDetailRequest.OutParams::tsCode).mapNotNull { it }
                val wList = weightList(stockIdList, tradeDate)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val date = tradingDateService.last("SSE")?.date ?: return
        groupStockService.saveAll(groupStockListOfIndex(date) + groupStockListOfIndustry(date) + groupStockListOfConcept(date))
    }

    private fun syncBalanceSheet() {
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

    private fun syncIncome() {
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

    private fun syncCashflow() {
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

    private fun syncExpress() {
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

    private fun syncForecast() {
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
}