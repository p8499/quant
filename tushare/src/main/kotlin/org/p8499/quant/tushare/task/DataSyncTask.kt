package org.p8499.quant.tushare.task

import org.p8499.quant.tushare.entity.Exchange
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ExchangeService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.TradeCalRequest
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
    lateinit var tradeCalRequest: TradeCalRequest

    @Scheduled(cron = "0 0 18 * * MON-FRI")
    fun sync() {
        syncExchange()
        syncTradingDate()
    }

    private fun syncExchange() {
        exchangeService.save(Exchange("SSE", "上交所"))
        exchangeService.save(Exchange("SZSE", "深交所"))
    }

    private fun unprocessedDates(exchangeId: String): List<Date> {
        val lastDate = tradingDateService.last(exchangeId)?.date
        val startDate = lastDate?.let {
            Calendar.getInstance().apply {
                time = it
                add(Calendar.DATE, 1)
            }.time
        } ?: GregorianCalendar(1990, 0, 1).time
        return tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = exchangeId, startDate = startDate, isOpen = true), TradeCalRequest.OutParams::class.java).map { it.calDate as Date }
    }

    private fun syncTradingDate() {
        tradingDateService.saveAll(unprocessedDates("SSE").map { TradingDate(null, "SSE", it) })
        tradingDateService.saveAll(unprocessedDates("SZSE").map { TradingDate(null, "SZSE", it) })
    }
}