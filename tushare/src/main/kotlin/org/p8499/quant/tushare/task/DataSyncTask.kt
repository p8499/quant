package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.entity.Exchange
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ExchangeService
import org.p8499.quant.tushare.service.GroupService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.TradingDateService
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
            } ?: GregorianCalendar(1990, 0, 1).time
            tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = exchange, startDate = startDate, isOpen = true), TradeCalRequest.OutParams::class.java).map { TradingDate(null, it.exchange, it.calDate) }
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
}