package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.IncomeService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.IncomeRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class IncomeSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var incomeService: IncomeService

    @Autowired
    lateinit var incomeRequest: IncomeRequest

    fun invoke() {
        log.info("Start Synchronizing Income")
        val stockIdList = stockService.findAll().map(Stock::id)
        for (stockId in stockIdList)
            incomeService.saveAll(incomeRequest.invoke(IncomeRequest.InParams(tsCode = stockId), IncomeRequest.OutParams::class.java, arrayOf("ann_date", "end_date", "n_income_attr_p", "update_flag"))
                    .groupBy(IncomeRequest.OutParams::endDate).mapValues { it.value.sortedWith(compareBy(IncomeRequest.OutParams::updateFlag)).last() }.map {
                        Calendar.getInstance().run {
                            time = it.value.endDate
                            Income(stockId, get(Calendar.YEAR), get(Calendar.MONTH) / 3 + 1, it.value.annDate
                                    ?: it.value.endDate, it.value.nIncomeAttrP)
                        }
                    })
        log.info("Finish Synchronizing Income")
    }
}