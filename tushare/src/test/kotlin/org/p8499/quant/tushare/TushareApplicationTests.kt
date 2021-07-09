package org.p8499.quant.tushare

import org.junit.jupiter.api.Test
import org.p8499.quant.tushare.service.tushareRequest.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class TushareApplicationTests {
    @Autowired
    lateinit var tradeCalRequest: TradeCalRequest

    @Autowired
    lateinit var stockBasicRequest: StockBasicRequest

    @Autowired
    lateinit var indexBasicRequest: IndexBasicRequest

    @Autowired
    lateinit var indexClassifyRequest: IndexClassifyRequest

    @Autowired
    lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    lateinit var balancesheetRequest: BalancesheetRequest

    @Autowired
    lateinit var incomeRequest: IncomeRequest

    @Test
    fun contextLoads() {
        assert(tradeCalRequest.invoke(TradeCalRequest.InParams(exchange = "SSE", endDate = Date(), isOpen = true), TradeCalRequest.OutParams::class.java).isNotEmpty())
        assert(stockBasicRequest.invoke(StockBasicRequest.InParams(listStatus = 'L'), StockBasicRequest.OutParams::class.java).isNotEmpty())
        assert(indexBasicRequest.invoke(IndexBasicRequest.InParams(market = "MSCI"), IndexBasicRequest.OutParams::class.java).isNotEmpty())
        assert(indexClassifyRequest.invoke(IndexClassifyRequest.InParams(), IndexBasicRequest.OutParams::class.java).isNotEmpty())
        assert(indexWeightRequest.invoke(IndexWeightRequest.InParams(indexCode = "399300.SZ", startDate = GregorianCalendar(2018, 9, 1).time, endDate = GregorianCalendar(2018, 9, 30).time), IndexWeightRequest.OutParams::class.java).isNotEmpty())
        assert(indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = "850531.SI"), IndexMemberRequest.OutParams::class.java).isNotEmpty())
        assert(balancesheetRequest.invoke(BalancesheetRequest.InParams(tsCode = "600000.SH"), BalancesheetRequest.OutParams::class.java).isNotEmpty())
        assert(incomeRequest.invoke(IncomeRequest.InParams(tsCode = "600000.SH"), IncomeRequest.OutParams::class.java).isNotEmpty())
    }
}
