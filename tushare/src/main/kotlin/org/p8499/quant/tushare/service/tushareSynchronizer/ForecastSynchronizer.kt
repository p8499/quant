package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.ForecastService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ForecastRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

@Service
class ForecastSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var forecastService: ForecastService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    lateinit var forecastRequest: ForecastRequest

    fun invoke() {
        logger.info("Start Synchronizing Forecast")
        val format = DecimalFormat("0.00")
        val stockIdList = stockService.findAll().map(Stock::id)
        Flowable.fromIterable(stockIdList).zipWith(Flowable.interval(1200, TimeUnit.MILLISECONDS)) { stockId, _ -> stockId }
                .blockingSubscribe { stockId ->
                    forecastService.saveAll(forecastRequest.invoke(ForecastRequest.InParams(tsCode = stockId), ForecastRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "type", "p_change_min", "p_change_max", "change_reason"))
                            .groupBy(ForecastRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ForecastRequest.OutParams::annDate)).last() }.mapNotNull {
                                it.value.endDate?.run {
                                    Forecast(stockId, year, (monthValue - 1) / 3 + 1,
                                            it.value.annDate ?: this,
                                            "${it.value.type} (${it.value.pChangeMin?.run(format::format) ?: "null"}% ~ ${it.value.pChangeMax?.run(format::format) ?: "null"}%)",
                                            it.value.changeReason)
                                }
                            })
                }
        controllerService.complete("Forecast")
        logger.info("Finish Synchronizing Forecast")
    }
}