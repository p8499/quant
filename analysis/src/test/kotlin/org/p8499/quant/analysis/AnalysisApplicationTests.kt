package org.p8499.quant.analysis

import org.junit.jupiter.api.Test
import org.p8499.quant.analysis.policy.CNPolicy
import org.p8499.quant.analysis.policy.Stage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class AnalysisApplicationTests {
    protected val logger by lazy { LoggerFactory.getLogger(Stage::class.java) }

    @Autowired
    protected lateinit var cnPolicy: CNPolicy

    @Test
    fun contextLoads() {
        val stage = Stage(50000.00, 100.00)
        stage.run(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), cnPolicy)
    }
}