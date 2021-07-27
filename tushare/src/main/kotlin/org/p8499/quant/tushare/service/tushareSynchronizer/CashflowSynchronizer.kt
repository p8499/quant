package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.CashflowService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.CashflowRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CashflowSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var cashflowService: CashflowService

    @Autowired
    lateinit var cashflowRequest: CashflowRequest

    fun invoke() {
        log.info("Start Synchronizing Cashflow")
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            cashflowService.saveAll(cashflowRequest.invoke(CashflowRequest.InParams(tsCode = stockId), CashflowRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "n_cashflow_act", "update_flag"))
                    .groupBy(CashflowRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(CashflowRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Cashflow(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1, it.value.annDate
                                    ?: it.value.endDate, it.value.nCashflowAct)
                        }
                    })
        log.info("Finish Synchronizing Cashflow")
    }
}