package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Exchange
import org.p8499.quant.tushare.service.ExchangeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangeSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var exchangeService: ExchangeService

    fun invoke() {
        log.info("Start Synchronizing Exchange")
        exchangeService.save(Exchange("SSE", "上交所"))
        exchangeService.save(Exchange("SZSE", "深交所"))
        log.info("Finish Synchronizing Exchange")
    }
}