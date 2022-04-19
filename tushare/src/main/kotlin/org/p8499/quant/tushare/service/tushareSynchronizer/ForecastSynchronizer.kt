package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.ForecastService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ForecastRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class ForecastSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

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
        controllerService.begin("Forecast", LocalDateTime.now())
        val periodList = (1990..LocalDate.now().year).flatMap {
            listOf(LocalDate.of(it, 3, 31), LocalDate.of(it, 6, 30), LocalDate.of(it, 9, 30), LocalDate.of(it, 12, 31))
        }
        val format = DecimalFormat("0.00")
        forecastService.saveAll(Flowable.fromIterable(periodList).zipWith(Flowable.interval(1000, TimeUnit.MILLISECONDS)) { period, _ -> period }
                .flatMap { period ->
                    Flowable.fromIterable(forecastRequest.invoke(ForecastRequest.InParams(period = period), ForecastRequest.OutParams::class.java, arrayOf("ts_code", "ann_date", "end_date", "type", "p_change_min", "p_change_max", "change_reason"))
                            .mapNotNull { let(it.tsCode, it.annDate, it.endDate) { tsCode, annDate, endDate -> Forecast(tsCode, annDate, endDate.year, (endDate.monthValue - 1) / 3 + 1, "${it.type} (${it.pChangeMin?.run(format::format) ?: "null"}% ~ ${it.pChangeMax?.run(format::format) ?: "null"}%)", it.changeReason) } })
                }.blockingIterable())
        controllerService.end("Forecast")
        logger.info("Finish Synchronizing Forecast")
    }
}