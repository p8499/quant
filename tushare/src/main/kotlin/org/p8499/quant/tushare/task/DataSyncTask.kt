package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.entity.*
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.tushareRequest.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
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
    lateinit var level1Service: Level1Service

    @Autowired
    lateinit var level2Service: Level2Service

    @Autowired
    lateinit var groupStockService: GroupStockService

    @Autowired
    lateinit var balanceSheetService: BalanceSheetService

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
        syncLevel1()
        syncLevel2()
        syncGroupStock()
        syncBalanceSheet()
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

    private fun syncLevel1() {
        val level1: (String, Date) -> Level1? = { tsCode, tradeDate ->
            val daily = dailyRequest.invoke(DailyRequest.InParams(tsCode = tsCode, tradeDate = tradeDate), DailyRequest.OutParams::class.java).firstOrNull()
            val dailyBasic = dailyBasicRequest.invoke(DailyBasicRequest.InParams(tsCode = tsCode, tradeDate = tradeDate), DailyBasicRequest.OutParams::class.java).firstOrNull()
            val adjFactor = adjFactorRequest.invoke(AdjFactorRequest.InParams(tsCode = tsCode, tradeDate = tradeDate), AdjFactorRequest.OutParams::class.java).firstOrNull()
            if (daily != null && dailyBasic != null && adjFactor != null)
                Level1(tsCode, tradeDate, daily.open, daily.close, daily.high, daily.low, daily.vol, daily.amount, dailyBasic.totalShare, dailyBasic.floatShare, adjFactor.adjFactor)
            else
                null
        }
        for (stock in stockService.findAll())
            for (tradingDate in stock.id?.let(tradingDateService::unprocessedForLevel1) ?: continue) {
                val stockId = stock.id ?: continue
                val date = tradingDate.date ?: continue
                level1(stockId, date)?.run(level1Service::save)
            }
    }

    private fun syncLevel2() {
        val level2: (String, Date) -> Level2? = { tsCode, tradeDate ->
            val moneyflow = moneyflowRequest.invoke(MoneyflowRequest.InParams(tsCode = tsCode, tradeDate = tradeDate), MoneyflowRequest.OutParams::class.java).firstOrNull()
            if (moneyflow != null)
                Level2(tsCode, tradeDate, moneyflow.buySmVol, moneyflow.sellSmVol, moneyflow.buyMdVol, moneyflow.sellMdVol, moneyflow.buyLgVol, moneyflow.sellLgVol, moneyflow.buyElgVol, moneyflow.sellElgVol)
            else
                null
        }
        for (stock in stockService.findAll())
            for (tradingDate in stock.id?.let(tradingDateService::unprocessedForLevel2) ?: continue) {
                val stockId = stock.id ?: continue
                val date = tradingDate.date ?: continue
                level2(stockId, date)?.run(level2Service::save)
            }
    }

    private fun syncGroupStock() {
        val groupStockListOfIndex: (Date) -> List<GroupStock> = { tradeDate ->
            val groupStockList = indexWeightRequest.invoke(IndexWeightRequest.InParams(tradeDate = tradeDate), IndexWeightRequest.OutParams::class.java).map { GroupStock(it.indexCode, it.conCode, it.weight) }
            val stockIdList = groupStockList.mapNotNull(GroupStock::stockId).let(stockService::findByStockIdList).map(Stock::id)
            groupStockList.filter { stockIdList.contains(it.groupId) }
        }
        val weightList: (List<String>, Date) -> List<Double> = { stockIdList, tradeDate ->
            val flowShareList = stockIdList.map { level1Service[it, tradeDate] }.map { it?.flowShare ?: 0.0 }
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
        for (stockId in stockIdList) {
            balanceSheetService.saveAll(balancesheetRequest.invoke(BalancesheetRequest.InParams(tsCode = stockId), BalancesheetRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "total_hldr_eqy_exc_min_int", "update_flag"))
                    .groupBy(BalancesheetRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(BalancesheetRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            BalanceSheet(stockId, get(Calendar.YEAR), get(Calendar.MONTH / 4) + 1, it.value.annDate, it.value.totalHldrEqyExcMinInt)
                        }
                    })
        }
    }
}