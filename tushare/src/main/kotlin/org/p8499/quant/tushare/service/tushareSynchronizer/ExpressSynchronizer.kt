package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ExpressService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.ExpressRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ExpressSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var expressService: ExpressService

    @Autowired
    lateinit var expressRequest: ExpressRequest

    fun invoke() {
        log.info("Start Synchronizing Express")
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            expressService.saveAll(expressRequest.invoke(ExpressRequest.InParams(tsCode = stockId), ExpressRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "revenue", "total_hldr_eqy_exc_min_int", "is_audit"))
                    .groupBy(ExpressRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(ExpressRequest.OutParams::isAudit)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Express(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1, it.value.annDate
                                    ?: it.value.endDate, it.value.totalHldrEqyExcMinInt, it.value.revenue)
                        }
                    })
        log.info("Finish Synchronizing Express")
    }
}