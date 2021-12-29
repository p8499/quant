package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.TradingDateService
import org.p8499.quant.tushare.service.tushareRequest.TradeCalRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TradingDateSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var tradeCalRequest: TradeCalRequest

    fun invoke() {
        logger.info("Start Synchronizing TradingDate")
        val unprocessedTradingDateList: (String, Boolean) -> List<TradingDate> = { exchange, todayIncluded ->
            val lastDate = tradingDateService.last(exchange)?.date
            val startDate = lastDate?.plusDays(1) ?: LocalDate.of(2007, 1, 1)
            val endDate = LocalDate.now().let { if (todayIncluded) it else it.minusDays(1) }
            tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = exchange, startDate = startDate, endDate = endDate, isOpen = 1), TradeCalRequest.OutParams::class.java).map { TradingDate(it.exchange, it.calDate) }
        }
        val todayIncluded = LocalDateTime.now().hour >= 16
        tradingDateService.saveAll(unprocessedTradingDateList("SSE", todayIncluded) + unprocessedTradingDateList("SZSE", todayIncluded))
        controllerService.complete("TradingDate")
        logger.info("Finish Synchronizing TradingDate")
    }
}