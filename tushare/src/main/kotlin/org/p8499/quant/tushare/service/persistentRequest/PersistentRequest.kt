package org.p8499.quant.tushare.service.persistentRequest

import org.p8499.quant.tushare.dto.SecurityDayDto
import org.p8499.quant.tushare.dto.SecurityDto
import org.p8499.quant.tushare.dto.SecurityQuarterDto
import org.p8499.quant.tushare.feignClient.PersistentFeignClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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
    fun begin(region: String, snapshot: LocalDateTime) = invoke { it.begin(region, snapshot) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurity(securityDto: SecurityDto) = invoke { it.saveSecurity(securityDto) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurityDay(securityDayDto: SecurityDayDto) = invoke { it.saveSecurityDay(securityDayDto) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurityQuarter(securityQuarterDto: SecurityQuarterDto) = invoke { it.saveSecurityQuarter(securityQuarterDto) }

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun end(region: String) = invoke { it.end(region) }
}