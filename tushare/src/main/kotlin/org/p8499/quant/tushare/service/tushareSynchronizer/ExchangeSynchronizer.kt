package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Exchange
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.ExchangeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangeSynchronizer {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var exchangeService: ExchangeService

    @Autowired
    protected lateinit var controllerService: ControllerService

    fun invoke() {
        logger.info("Start Synchronizing Exchange")
        exchangeService.save(Exchange("SSE", "上交所"))
        exchangeService.save(Exchange("SZSE", "深交所"))
        controllerService.complete("Exchange")
        logger.info("Finish Synchronizing Exchange")
    }
}