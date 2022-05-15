package org.p8499.quant.analysis.controller

import org.p8499.quant.analysis.dayPolicy.Action
import org.p8499.quant.analysis.dayPolicy.Policy
import org.p8499.quant.analysis.dayPolicy.cn.CNStage
import org.p8499.quant.analysis.dayPolicy.cn.CNStatus
import org.p8499.quant.analysis.dayPolicy.cn.convert
import org.p8499.quant.analysis.dayPolicy.cn.policy21.Policy21
import org.p8499.quant.analysis.dayPolicy.common.positionRate
import org.p8499.quant.analysis.dayPolicy.common.price
import org.p8499.quant.analysis.dayPolicy.common.value
import org.p8499.quant.analysis.service.AnalyzerService
import org.p8499.quant.analysis.service.ControllerService
import org.p8499.quant.analysis.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping(path = ["/policy"], produces = [MediaType.TEXT_PLAIN_VALUE])
class PolicyController {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    protected val percentFormat = NumberFormat.getNumberInstance().apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }
    protected val amountFormat = NumberFormat.getNumberInstance().apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }
    protected val volumeFormat = NumberFormat.getNumberInstance().apply { minimumFractionDigits = 0; maximumFractionDigits = 0 }
    protected val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    protected val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var analyzerService: AnalyzerService

    @Autowired
    protected lateinit var mailService: MailService

    private var updated: LocalDateTime? = null

    @Scheduled(cron = "* * * * * SUN-SAT")
    protected fun build() {
        controllerService["CN"]?.takeIf { it.end != null }?.snapshot?.let { snapshot ->
            updated?.takeIf { it == snapshot } ?: run {
                logger.info("策略更新，时间戳从${updated?.let(dateTimeFormat::format)}至${dateTimeFormat.format(snapshot)}")
                val stage = CNStage().apply {
                    date = LocalDate.of(2022, 5, 16)
//                    date = LocalDate.of(2017, 12, 31)
                    status = CNStatus.AFTER
                    cash = 1000000.0
                }
                val policy = Policy21(analyzerService.region("CN", LocalDate.of(2015, 1, 4), LocalDate.now()))
                stage.run(LocalDate.now(), CNStatus.BEFORE, policy)
                mailService.send(
                        to = arrayOf("p8499@126.com"),
                        subject = "Policy as of ${dateFormat.format(snapshot)}",
                        text = msg(stage, policy))
                updated = snapshot
                logger.info("策略更新完成")
            }
        }
    }

    fun msg(stage: CNStage, policy: Policy<CNStatus>): String {
        stage.lock.lock()
        val date = stage.date
        val status = stage.status
        val value = stage.value()
        val cash = stage.cash
        val positions = stage.positions.joinToString("\n") { "${it.security.id}\t${amountFormat.format(convert(date, status).let { p -> it.price(p.first, p.second) })}\t${volumeFormat.format(it.volume)}\t=> ${amountFormat.format(stage.positionValue(it.security))}" }
        val commissions = stage.commissions.joinToString("\n") { "${it.action}\t${it.security.id}\t${amountFormat.format(it.price)}\t${volumeFormat.format(it.volume)}" }
        val successRate = "${percentFormat.format(stage.transactions.filter { it.action == Action.SELL }.mapNotNull { it.pl }.let { if (it.count() > 0) it.count { pl -> pl > 0 }.toDouble() / it.count() else 0.0 } * 100)}%"
        val positionRate = "${percentFormat.format(stage.snapshots.positionRate * 100)}%"
        val yearEnds = stage.snapshots.groupBy { it.date.year }.mapValues { it.value.last().value }.toList().let {
            it.mapIndexed { index, pair -> pair.first to (if (index == 0) pair.second / stage.snapshots.first().value else pair.second / it[index - 1].second) * 100 - 100 }
        }.joinToString("\n") { "${it.first}\t${amountFormat.format(it.second)}%" }
        val transactions = stage.transactions.reversed().joinToString("\n") { "${dateTimeFormat.format(it.time)}\t${it.action}\t${it.security.id}\t${amountFormat.format(it.price)}\t${volumeFormat.format(it.volume)}\t${it.pl?.let(amountFormat::format).orEmpty()}\t${it.plPercent?.let { pp -> pp * 100 }?.let(percentFormat::format)?.let { f -> "$f%" }.orEmpty()}" }
        val callingCommissions = policy.callingCommissions.joinToString("\n") { "${it.action}\t${it.security.id}\t\t${amountFormat.format(it.price)}\t${volumeFormat.format(it.volume)}" }
        val openingCommissions = policy.openingCommissions.joinToString("\n") { "${it.action}\t${it.security.id}\t\t${amountFormat.format(it.price)}\t${volumeFormat.format(it.volume)}" }
        val hint = policy.hint(stage)
        stage.lock.unlock()
        return "Policy: ${policy::class.java.simpleName}\n\nDate: ${dateFormat.format(date)}\nStatus: $status\n\nCalling Commissions: \n$callingCommissions\nOpening Commissions: \n$openingCommissions\nOpening Hint: $hint\n\nValue: ${amountFormat.format(value)}\nCash: ${amountFormat.format(cash)}\nPositions: \n$positions\nCommissions: \n$commissions\n\nSuccess Rate: \n$successRate\n\nPosition Rate: \n$positionRate\n\nYear End Values:\n$yearEnds\n\nTransactions: \n$transactions"
    }
}