package org.p8499.quant.tushare.service.persistentRequest

import org.p8499.quant.tushare.dto.GroupDto
import org.p8499.quant.tushare.dto.StockDto
import org.p8499.quant.tushare.feignClient.PersistentFeignClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class PersistentRequest {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var persistentFeignClient: PersistentFeignClient

    protected fun <T> invoke(method: (PersistentFeignClient) -> T): T {
        try {
            return method(persistentFeignClient)
        } catch (e: Throwable) {
            logger.info("PersistentRequest Failed And Retry.")
            throw e
        }
    }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveStock(stockDto: StockDto) = invoke { it.saveStock(stockDto) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveGroup(groupDto: GroupDto) = invoke { it.saveGroup(groupDto) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun complete(region: String) = invoke { it.complete(region) }
}