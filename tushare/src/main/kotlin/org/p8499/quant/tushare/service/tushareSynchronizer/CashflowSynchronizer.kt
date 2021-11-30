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
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class CashflowSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

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
                            .groupBy(CashflowRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(CashflowRequest.OutParams::updateFlag)).last() }.map {
                                Calendar.getInstance().run {
                                    time = it.value.endDate
                                    Cashflow(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1, it.value.annDate
                                            ?: it.value.endDate, it.value.nCashflowAct)
                                }
                            })
                }
        controllerService.complete("Cashflow")
        logger.info("Finish Synchronizing Cashflow")
    }
}