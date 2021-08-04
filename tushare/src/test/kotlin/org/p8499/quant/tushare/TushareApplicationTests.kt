package org.p8499.quant.tushare

import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.task.TushareTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TushareApplicationTests {
    @Autowired
    lateinit var tushareTask: TushareTask

    @Test
    fun contextLoads() {
            tushareTask.syncAndNotify()
    }
}
