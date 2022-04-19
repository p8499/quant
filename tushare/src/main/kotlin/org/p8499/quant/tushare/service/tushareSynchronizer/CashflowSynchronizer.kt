package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.service.CashflowService
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.CashflowRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class CashflowSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var cashflowService: CashflowService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var cashflowRequest: CashflowRequest

    fun invoke() {
        logger.info("Start Synchronizing Cashflow")
        controllerService.begin("Cashflow", LocalDateTime.now())
        val periodList = (1990..LocalDate.now().year).flatMap {
            listOf(LocalDate.of(it, 3, 31), LocalDate.of(it, 6, 30), LocalDate.of(it, 9, 30), LocalDate.of(it, 12, 31))
        }
        val byReportType: (String) -> Iterable<Cashflow> = { reportType ->
            Flowable.fromIterable(periodList).zipWith(Flowable.interval(1000, TimeUnit.MILLISECONDS)) { period, _ -> period }
                    .flatMap { period ->
                        Flowable.fromIterable(cashflowRequest.invoke(CashflowRequest.InParams(period = period, reportType = reportType), CashflowRequest.OutParams::class.java, arrayOf("ts_code", "ann_date", "end_date", "n_cashflow_act", "update_flag"))
                                .groupBy(CashflowRequest.OutParams::tsCode)
                                .flatMap { entry -> entry.value.groupBy(CashflowRequest.OutParams::annDate).mapValues { it.value.sortedWith(compareBy(CashflowRequest.OutParams::updateFlag)).last() }.values }
                                .mapNotNull { org.p8499.quant.tushare.common.let(it.tsCode, it.annDate, it.endDate) { tsCode, annDate, endDate -> Cashflow(tsCode, annDate, endDate.year, (endDate.monthValue - 1) / 3 + 1, it.nCashflowAct) } })
                    }.blockingIterable()
        }
        cashflowService.saveAll(byReportType("1") + byReportType("4"))
        controllerService.end("Cashflow")
        logger.info("Finish Synchronizing Cashflow")
    }
}