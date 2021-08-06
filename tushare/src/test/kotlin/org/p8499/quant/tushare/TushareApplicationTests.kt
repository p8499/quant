package org.p8499.quant.tushare

import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.service.tushareSynchronizer.Level1AdjFactorSynchronizer
import org.p8499.quant.tushare.service.tushareSynchronizer.Level1BasicSynchronizer
import org.p8499.quant.tushare.service.tushareSynchronizer.Level1CandlestickSynchronizer
import org.p8499.quant.tushare.task.TushareTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TushareApplicationTests {
    @Autowired
    lateinit var tushareTask: TushareTask

    @Autowired
    protected lateinit var level1CandlestickSynchronizer: Level1CandlestickSynchronizer

    @Autowired
    protected lateinit var level1BasicSynchronizer: Level1BasicSynchronizer

    @Autowired
    protected lateinit var level1AdjFactorSynchronizer: Level1AdjFactorSynchronizer

    @Test
    fun contextLoads() {
        tushareTask.syncAndSend()
    }
}
