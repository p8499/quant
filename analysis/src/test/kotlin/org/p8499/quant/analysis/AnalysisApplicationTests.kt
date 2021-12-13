package org.p8499.quant.analysis

import org.junit.jupiter.api.Test
import org.p8499.quant.analysis.service.PolicyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AnalysisApplicationTests {

    @Autowired
    protected lateinit var policyService: PolicyService

    @Test
    fun contextLoads() {
        val x = policyService.getStockObject("CN", "601318.SH")
        println(x)
    }
}
