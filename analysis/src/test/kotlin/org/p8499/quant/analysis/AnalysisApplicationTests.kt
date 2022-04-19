package org.p8499.quant.analysis

import org.junit.jupiter.api.Test
import org.p8499.quant.analysis.service.AnalyzerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AnalysisApplicationTests {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var analyzerService: AnalyzerService

    @Test
    fun contextLoads() {
    }
}
