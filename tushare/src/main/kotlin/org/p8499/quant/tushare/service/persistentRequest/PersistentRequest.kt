package org.p8499.quant.tushare.service.persistentRequest

import org.p8499.quant.tushare.common.tryInvoke
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

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun begin(region: String, snapshot: LocalDateTime) = tryInvoke({ persistentFeignClient.begin(region, snapshot) }, { logger.info("PersistentRequest Failed And Retry.") })

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurity(securityDto: SecurityDto) = tryInvoke({ persistentFeignClient.saveSecurity(securityDto) }, { logger.info("PersistentRequest Failed And Retry.") })

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurityDay(securityDayDto: SecurityDayDto) = tryInvoke({ persistentFeignClient.saveSecurityDay(securityDayDto) }, { logger.info("PersistentRequest Failed And Retry.") })

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun saveSecurityQuarter(securityQuarterDto: SecurityQuarterDto) = tryInvoke({ persistentFeignClient.saveSecurityQuarter(securityQuarterDto) }, { logger.info("PersistentRequest Failed And Retry.") })

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun end(region: String) = tryInvoke({ persistentFeignClient.end(region) }, { logger.info("PersistentRequest Failed And Retry.") })
}