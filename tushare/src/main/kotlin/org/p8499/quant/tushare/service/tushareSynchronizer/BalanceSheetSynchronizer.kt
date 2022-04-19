package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.entity.BalanceSheet
import org.p8499.quant.tushare.service.BalanceSheetService
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.BalancesheetRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class BalanceSheetSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var balanceSheetService: BalanceSheetService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var balancesheetRequest: BalancesheetRequest

    fun invoke() {
        logger.info("Start Synchronizing BalanceSheet")
        controllerService.begin("BalanceSheet", LocalDateTime.now())
        val periodList = (1990..LocalDate.now().year).flatMap {
            listOf(LocalDate.of(it, 3, 31), LocalDate.of(it, 6, 30), LocalDate.of(it, 9, 30), LocalDate.of(it, 12, 31))
        }
        val byReportType: (String) -> Iterable<BalanceSheet> = { reportType ->
            Flowable.fromIterable(periodList).zipWith(Flowable.interval(1000, TimeUnit.MILLISECONDS)) { period, _ -> period }
                    .flatMap { period ->
                        Flowable.fromIterable(balancesheetRequest.invoke(BalancesheetRequest.InParams(period = period, reportType = reportType), BalancesheetRequest.OutParams::class.java, arrayOf("ts_code", "ann_date", "end_date", "total_hldr_eqy_exc_min_int", "update_flag"))
                                .groupBy(BalancesheetRequest.OutParams::tsCode)
                                .flatMap { entry -> entry.value.groupBy(BalancesheetRequest.OutParams::annDate).mapValues { it.value.sortedWith(compareBy(BalancesheetRequest.OutParams::updateFlag)).last() }.values }
                                .mapNotNull { org.p8499.quant.tushare.common.let(it.tsCode, it.annDate, it.endDate) { tsCode, annDate, endDate -> BalanceSheet(tsCode, annDate, endDate.year, (endDate.monthValue - 1) / 3 + 1, it.totalHldrEqyExcMinInt) } })
                    }.blockingIterable()
        }
        balanceSheetService.saveAll(byReportType("1") + byReportType("4"))
        controllerService.end("BalanceSheet")
        logger.info("Finish Synchronizing BalanceSheet")
    }
}