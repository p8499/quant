package org.p8499.quant.analysis.policy

import java.text.NumberFormat
import java.time.format.DateTimeFormatter

class Audit(val stage: Stage) {
    protected var dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    protected var amountFormat = NumberFormat.getNumberInstance().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    protected var volumeFormat = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 0 }

    protected val stringBuilder = StringBuilder()

    fun snapshot() {
        stringBuilder.append("${dateFormat.format(stage.date())}: 总价值 ${amountFormat.format(stage.value("close"))}\n")
        stage.positions().forEach {
            val close = it.security["close", stage.date()] ?: 0.0
            stringBuilder.append("    ${it.security.id}: ${amountFormat.format(close * it.volume)}, 成本: ${amountFormat.format(it.cost)}, 现价: ${amountFormat.format(close)}, 数量: ${volumeFormat.format(it.volume)}, 浮盈: ${amountFormat.format((close - it.cost) * it.volume)}\n")
        }
        stringBuilder.append("    现金持有: ${amountFormat.format(stage.cash())}\n")
    }

    fun buy(security: Security, price: Double, volume: Double) {
        stringBuilder.append("${dateFormat.format(stage.date())}: 买入 ${security.id}, 成本: ${amountFormat.format(price)}, 数量: ${volumeFormat.format(volume)}\n")
    }

    fun sell(position: Stage.Position, price: Double, volume: Double) {
        val close = position.security["close", stage.date()] ?: 0.0
        stringBuilder.append("${dateFormat.format(stage.date())}: 卖出 ${position.security.id}, 成本: ${amountFormat.format(position.cost)}, 现价: ${amountFormat.format(close)}, 数量: ${volumeFormat.format(volume)}, 盈利: ${amountFormat.format((close - position.cost) * volume)}\n")
    }

    fun log(content: String) {
        stringBuilder.append("${dateFormat.format(stage.date())} $content\n")
    }

    fun wrap() {
        stringBuilder.append("\n")
    }

    override fun toString(): String = stringBuilder.toString()

    fun clear() = stringBuilder.clear()
}