package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.TradeCalRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class TradingDateSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var tradeCalRequest: TradeCalRequest

    fun invoke() {
        log.info("Start Synchronizing TradingDate")
        val unprocessedTradingDateList: (String) -> List<TradingDate> = { exchange ->
            val lastDate = tradingDateService.last(exchange)?.date
            val startDate = lastDate?.let {
                Calendar.getInstance().apply {
                    time = it
                    add(Calendar.DATE, 1)
                }.time
            } ?: GregorianCalendar(2007, 0, 1).time
            tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = exchange, startDate = startDate, endDate = Date(), isOpen = 1), TradeCalRequest.OutParams::class.java).map { TradingDate(it.exchange, it.calDate) }
        }
        tradingDateService.saveAll(unprocessedTradingDateList("SSE") + unprocessedTradingDateList("SZSE"))
        log.info("Finish Synchronizing TradingDate")
    }
}