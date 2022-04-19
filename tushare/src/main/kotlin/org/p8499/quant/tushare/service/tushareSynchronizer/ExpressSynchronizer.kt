package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.ExpressService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ExpressRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class ExpressSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var expressService: ExpressService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var expressRequest: ExpressRequest

    fun invoke() {
        logger.info("Start Synchronizing Express")
        controllerService.begin("Express", LocalDateTime.now())
        val periodList = (1990..LocalDate.now().year).flatMap {
            listOf(LocalDate.of(it, 3, 31), LocalDate.of(it, 6, 30), LocalDate.of(it, 9, 30), LocalDate.of(it, 12, 31))
        }
        expressService.saveAll(Flowable.fromIterable(periodList).zipWith(Flowable.interval(1000, TimeUnit.MILLISECONDS)) { period, _ -> period }
                .flatMap { period ->
                    Flowable.fromIterable(expressRequest.invoke(ExpressRequest.InParams(period = period), ExpressRequest.OutParams::class.java, arrayOf("ts_code", "ann_date", "end_date", "revenue", "n_income", "total_hldr_eqy_exc_min_int", "is_audit"))
                            .groupBy(ExpressRequest.OutParams::tsCode)
                            .flatMap { entry -> entry.value.groupBy(ExpressRequest.OutParams::annDate).mapValues { it.value.sortedWith(compareBy(ExpressRequest.OutParams::isAudit)).last() }.values }
                            .mapNotNull { let(it.tsCode, it.annDate, it.endDate) { tsCode, annDate, endDate -> Express(tsCode, annDate, endDate.year, (endDate.monthValue - 1) / 3 + 1, it.totalHldrEqyExcMinInt, it.nIncome, it.revenue) } })
                }.blockingIterable())
        controllerService.end("Express")
        logger.info("Finish Synchronizing Express")
    }
}