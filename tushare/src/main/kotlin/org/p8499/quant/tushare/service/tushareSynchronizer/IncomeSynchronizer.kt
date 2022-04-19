package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.IncomeService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.IncomeRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class IncomeSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var incomeService: IncomeService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var incomeRequest: IncomeRequest

    fun invoke() {
        logger.info("Start Synchronizing Income")
        controllerService.begin("Income", LocalDateTime.now())
        val periodList = (1990..LocalDate.now().year).flatMap {
            listOf(LocalDate.of(it, 3, 31), LocalDate.of(it, 6, 30), LocalDate.of(it, 9, 30), LocalDate.of(it, 12, 31))
        }
        val byReportType: (String) -> Iterable<Income> = { reportType ->
            Flowable.fromIterable(periodList).zipWith(Flowable.interval(1000, TimeUnit.MILLISECONDS)) { period, _ -> period }
                    .flatMap { period ->
                        Flowable.fromIterable(incomeRequest.invoke(IncomeRequest.InParams(period = period, reportType = reportType), IncomeRequest.OutParams::class.java, arrayOf("ts_code", "ann_date", "end_date", "revenue", "n_income_attr_p", "update_flag"))
                                .groupBy(IncomeRequest.OutParams::tsCode)
                                .flatMap { entry -> entry.value.groupBy(IncomeRequest.OutParams::annDate).mapValues { it.value.sortedWith(compareBy(IncomeRequest.OutParams::updateFlag)).last() }.values }
                                .mapNotNull { let(it.tsCode, it.annDate, it.endDate) { tsCode, annDate, endDate -> Income(tsCode, annDate, endDate.year, (endDate.monthValue - 1) / 3 + 1, it.revenue, it.nIncomeAttrP) } })
                    }.blockingIterable()
        }
        incomeService.saveAll(byReportType("1") + byReportType("4"))
        controllerService.end("Income")
        logger.info("Finish Synchronizing Income")
    }
}