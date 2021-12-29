package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.service.ControllerService
import org.p8499.quant.analysis.service.PolicyService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
class PolicyPool {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var policyService: PolicyService

    protected val recordMap: MutableMap<String, Record> = ConcurrentHashMap()

    operator fun get(name: String): Record? = recordMap[name]

    protected operator fun set(name: String, value: Record) {
        recordMap[name] = value
    }

    @Scheduled(cron = "00 * * * * SUN-SAT")
    protected fun build() {
        val buildRegion: (String, () -> String) -> Unit = { region, policyProvider ->
            controllerService[region]?.date?.let { controllerTime ->
                this[region]?.takeIf { it.updated == controllerTime } ?: run {
                    logger.info("开始重建策略")
                    this[region] = Record(policyProvider(), controllerTime)
                    logger.info("结束重建策略")
                }
            }
        }
        buildRegion("CN") {
            Stage(1000000.00, 100.00).apply { run(LocalDate.now().minusDays(100), LocalDate.now(), CNPolicy(policyService)) }.log()
        }
    }

    class Record(val log: String, val updated: LocalDateTime)
}

