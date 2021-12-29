package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.IncomeService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.IncomeRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
        val stockIdList = stockService.findAll().map(Stock::id)
        Flowable.fromIterable(stockIdList).zipWith(Flowable.interval(1200, TimeUnit.MILLISECONDS)) { stockId, _ -> stockId }
                .blockingSubscribe { stockId ->
                    incomeService.saveAll(incomeRequest.invoke(IncomeRequest.InParams(tsCode = stockId), IncomeRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "revenue", "n_income_attr_p", "update_flag"))
                            .groupBy(IncomeRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(IncomeRequest.OutParams::updateFlag)).last() }.mapNotNull {
                                it.value.endDate?.run {
                                    Income(stockId, year, (monthValue - 1) / 3 + 1, it.value.annDate
                                            ?: this, it.value.revenue, it.value.nIncomeAttrP)
                                }
                            })
                }
        controllerService.complete("Income")
        logger.info("Finish Synchronizing Income")
    }
}