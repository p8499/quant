package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.le
import org.p8499.quant.analysis.common.lt
import org.p8499.quant.analysis.common.times
import org.p8499.quant.analysis.service.PolicyService
import java.text.NumberFormat
import java.time.LocalDate

class CNPolicy(policyService: PolicyService) : ReplacingPolicy(policyService) {
    protected var amountFormat = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 2 }

    override fun dates(): List<LocalDate> = policyService.tradingDates("CN")

    override fun select(): List<Security> = policyService.securitiesByRegion("CN", 2000)

    override fun extend(security: Security) {
        with(security) {
//            register("mcst") { dma(amount / volume, volume / flowShare) }
            register("history") { barscount(close) }
            register("llvprofit") { llv(profit, 750) }
            register("lodps") { lod(ps, 750) }
        }
    }

    override fun find(securities: List<Security>, barDate: LocalDate): List<Security> {
        return securities.filter {
            it.message(barDate).orEmpty().run { contains("续盈") || contains("预增") || contains("略增") }
                    && 750.0 lt it["history", barDate] == 1.0
                    && 0.0 lt it["llvprofit", barDate] == 1.0
        }.sortedBy { it["lodps", barDate] ?: Double.MAX_VALUE }
    }

    override val slots = 1

    override fun pre(stage: Stage, date: LocalDate) {
        super.pre(stage, date)
        stage.log("初始价值 ${amountFormat.format(stage.value())}")
    }

    override fun proceed(stage: Stage, date: LocalDate) {
        super.proceed(stage, date)
    }

    override fun post(stage: Stage, date: LocalDate) {
        super.post(stage, date)
        stage.log("终末价值 ${amountFormat.format(stage.value())}")
        val securitiesBuy = targets(stage, date, slots)
        stage.log("明日建议 ${securitiesBuy.joinToString(separator = ", ") { "${it.region}-${it.id}" }}")
    }
}