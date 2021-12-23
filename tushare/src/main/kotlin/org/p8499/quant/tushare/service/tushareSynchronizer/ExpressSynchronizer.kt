package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.ExpressService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ExpressRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ExpressSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

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
        val stockIdList = stockService.findAll().map(Stock::id)
        Flowable.fromIterable(stockIdList).zipWith(Flowable.interval(1200, TimeUnit.MILLISECONDS)) { stockId, _ -> stockId }
                .blockingSubscribe { stockId ->
                    expressService.saveAll(expressRequest.invoke(ExpressRequest.InParams(tsCode = stockId), ExpressRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "revenue", "total_hldr_eqy_exc_min_int", "is_audit"))
                            .groupBy(ExpressRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ExpressRequest.OutParams::isAudit)).last() }.mapNotNull {
                                it.value.endDate?.run {
                                    Express(stockId, year, (monthValue - 1) / 3 + 1, it.value.annDate
                                            ?: this, it.value.totalHldrEqyExcMinInt, it.value.revenue)
                                }
                            })
                }
        controllerService.complete("Express")
        logger.info("Finish Synchronizing Express")
    }
}