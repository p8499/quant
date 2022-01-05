package org.p8499.quant.analysis

import org.junit.jupiter.api.Test
import org.p8499.quant.analysis.policy.CNPolicy
import org.p8499.quant.analysis.policy.Security
import org.p8499.quant.analysis.policy.Stage
import org.p8499.quant.analysis.service.PolicyService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class AnalysisApplicationTests {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var policyService: PolicyService

    @Test
    fun contextLoads() {
        val security = policyService.security("CN", "002719.SZ", 2000)
        Stage(1000000.00, 100.00).apply {
            run(LocalDate.of(2018, 1, 1), LocalDate.now(), CNPolicy(policyService))
            logger.info(log())
        }
    }
}

class TestPolicy(policyService: PolicyService) : CNPolicy(policyService) {
    override fun find(securities: List<Security>, barDate: LocalDate): List<Security> {
        return super.find(securities.filter { it.id == "002719.SZ" }, barDate)
    }
}