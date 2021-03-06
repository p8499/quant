package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.StockService
import org.p8499.quant.tushare.service.tushareRequest.StockBasicRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StockSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var stockBasicRequest: StockBasicRequest

    fun invoke() {
        logger.info("Start Synchronizing Stock")
        controllerService.begin("Stock", LocalDateTime.now())
        val stockListOfStatus: (Char) -> List<Stock> = { listStatus ->
            stockBasicRequest.invoke(StockBasicRequest.InParams(listStatus = listStatus), StockBasicRequest.OutParams::class.java, arrayOf("ts_code", "exchange", "symbol", "name", "list_date", "delist_date")).map { Stock(it.tsCode, it.exchange, it.symbol, it.name, it.listDate, it.delistDate) }
        }
        stockService.saveAll(stockListOfStatus('L') + stockListOfStatus('P') + stockListOfStatus('D'))
        controllerService.end("Stock")
        logger.info("Finish Synchronizing Stock")
    }
}