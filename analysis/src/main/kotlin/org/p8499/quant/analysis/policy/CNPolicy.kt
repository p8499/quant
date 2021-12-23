package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.div
import org.p8499.quant.analysis.common.gt
import org.p8499.quant.analysis.common.lt
import org.p8499.quant.analysis.service.PolicyService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CNPolicy : Policy {
    protected val logger by lazy { LoggerFactory.getLogger(CNPolicy::class.java) }

    @Autowired
    protected lateinit var policyService: PolicyService

    override fun dates(): List<LocalDate> = policyService.tradingDates("CN")

    override fun select(): List<Security> = policyService.securitiesByRegion("CN", 2000)

    override fun extend(security: Security) {
        with(security) {
            register("mcst") { dma(amount / volume, volume / flowShare) }
            register("history") { barscount(close) }
            /**
             * lowest pe in 750 days
             */
            register("lowpe") { llv(pe, 750) }
            /**
             * bottom rank of ps in 750 days
             */
            register("lowps") { lod(ps, 750) }
            /**
             * previous profit
             */
            register("profit1") { ref(profit, 1) }
            /**
             * previous revenue
             */
            register("revenue1") { ref(revenue, 1) }
        }
    }

    override fun proceed(stage: Stage, date: LocalDate) {
        val barDate = date.minusDays(1)
        val slots = 1
        var securitiesTarget = targets(stage, barDate, slots)
        stage.positions()
                .filter { it.first !in securitiesTarget }
                .onEach { stage.sell(it.first.region, it.first.id, it.second) }
        val freeSlots = { slots - stage.positionMap.size }
        val securitiesPos = stage.positions().map(Pair<Security, *>::first)
        securitiesTarget = securitiesTarget
                .filter { it !in securitiesPos }
                .onEach { stage.buySlot(it.region, it.id, freeSlots()) }
    }

    fun targets(stage: Stage, date: LocalDate, size: Int): List<Security> {
        return stage.securities
                /**
                 * 750 < history
                 */
                .filter { (750.0 lt it["history", date]) == 1.0 }
                /**
                 * 无亏损 in 750 days
                 */
                .filter { (it["lowpe", date] gt 0.0) == 1.0 }
                /**
                 * 增长 in message
                 */
                .filter { it.message(date).orEmpty().run { contains("续盈") || contains("预增") || contains("略增") } }
                /**
                 * PS低位排名
                 */
                .sortedBy { it["lowps", date] ?: Double.MAX_VALUE }
                /**
                 * first n
                 */
                .let { if (it.size > size) it.subList(0, size) else it }
    }
}