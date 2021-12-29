package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.CashflowService
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.CashflowRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
        val stockIdList = stockService.findAll().map(Stock::id)
        Flowable.fromIterable(stockIdList).zipWith(Flowable.interval(1200, TimeUnit.MILLISECONDS)) { stockId, _ -> stockId }
                .blockingSubscribe { stockId ->
                    cashflowService.saveAll(cashflowRequest.invoke(CashflowRequest.InParams(tsCode = stockId), CashflowRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "n_cashflow_act", "update_flag"))
                            .groupBy(CashflowRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(CashflowRequest.OutParams::updateFlag)).last() }.mapNotNull {
                                it.value.endDate?.run {
                                    Cashflow(stockId, year, (monthValue - 1) / 3 + 1, it.value.annDate
                                            ?: this, it.value.nCashflowAct)
                                }
                            })
                }
        controllerService.complete("Cashflow")
        logger.info("Finish Synchronizing Cashflow")
    }
}