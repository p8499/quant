package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.div
import org.p8499.quant.analysis.common.gt
import org.p8499.quant.analysis.common.lt
import org.p8499.quant.analysis.service.PolicyService
import java.text.NumberFormat
import java.time.LocalDate

open class CNPolicy(policyService: PolicyService) : FixedPolicy(policyService) {
    protected var amountFormat = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 2 }

    override fun dates(): List<LocalDate> = policyService.tradingDates("CN")

    override fun select(): List<Security> = policyService.securitiesByRegion("CN", 2000)

    override fun extend(security: Security) {
        with(security) {
//            register("mcst") { dma(amount / volume, volume / flowShare) }
            register("history") { barscount(close) }
//            register("llvprofit") { llv(profit, 750) }
            register("lodps") { lod(ps, 750) }
            register("rateps") { ps / ma(ps, 750) }
        }
    }

    override fun find(securities: List<Security>, barDate: LocalDate): List<Security> {
        return securities.filter {
            it.message(barDate).orEmpty().run { contains("续盈") || contains("预增") || contains("略增") }
                    && 750.0 lt it["history", barDate] == 1.0
                    && every(it.getQuarter("profit", barDate) gt 0.0, 12).last() == 1.0
        }.sortedWith(compareBy(
                { it["lodps", barDate] ?: Double.MAX_VALUE },
                { it["rateps", barDate] ?: Double.MAX_VALUE }))
    }

    override val slots = 1

    override fun pre(stage: Stage, date: LocalDate) {
        super.pre(stage, date)
        stage.log("初始价值 ${amountFormat.format(stage.value("open"))}")
    }

    override fun post(stage: Stage, date: LocalDate) {
        super.post(stage, date)
        stage.log("终末价值 ${amountFormat.format(stage.value("close"))}")
        val securitiesBuy = find(securities, date)
        stage.log("明日建议\n${securitiesBuy.joinToString(separator = "\n  ") { "${it.region}-${it.id} lodps = ${it["lodps", date]}" }}")
    }
}