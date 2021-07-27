package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.BalanceSheet
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.BalanceSheetService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.BalancesheetRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BalanceSheetSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var balanceSheetService: BalanceSheetService

    @Autowired
    lateinit var balancesheetRequest: BalancesheetRequest

    fun invoke() {
        log.info("Start Synchronizing BalanceSheet")
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList) {
            balanceSheetService.saveAll(balancesheetRequest.invoke(BalancesheetRequest.InParams(tsCode = stockId), BalancesheetRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "total_hldr_eqy_exc_min_int", "update_flag"))
                    .groupBy(BalancesheetRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(BalancesheetRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            BalanceSheet(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1, it.value.annDate
                                    ?: it.value.endDate, it.value.totalHldrEqyExcMinInt)
                        }
                    })
        }
        log.info("Finish Synchronizing BalanceSheet")
    }
}