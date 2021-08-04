package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ForecastService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ForecastRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.util.*

@Service
class ForecastSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var forecastService: ForecastService

    @Autowired
    lateinit var forecastRequest: ForecastRequest

    fun invoke() {
        log.info("Start Synchronizing Forecast")
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            forecastService.saveAll(forecastRequest.invoke(ForecastRequest.InParams(tsCode = stockId), ForecastRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "type", "p_change_min", "p_change_max", "change_reason"))
                    .groupBy(ForecastRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ForecastRequest.OutParams::annDate)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            val format = DecimalFormat("0.00")
                            Forecast(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1,
                                    it.value.annDate ?: it.value.endDate,
                                    "${it.value.type} (${it.value.pChangeMin?.run(format::format) ?: "null"}% ~ ${it.value.pChangeMax?.run(format::format) ?: "null"}%)",
                                    it.value.changeReason)
                        }
                    })
        log.info("Finish Synchronizing Forecast")
    }
}