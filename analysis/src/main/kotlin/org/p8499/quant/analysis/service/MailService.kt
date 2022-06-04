package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.common.tryInvoke
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class MailService {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Value("\${spring.mail.username}")
    protected lateinit var username: String

    @Resource
    protected lateinit var mailSender: JavaMailSender

    @Retryable(maxAttempts = Int.MAX_VALUE, backoff = Backoff(delay = 60000))
    fun send(
            to: Array<String>,
            cc: Array<String> = emptyArray(),
            bcc: Array<String> = emptyArray(),
            subject: String,
            text: String) {
        val message = mailSender.createMimeMessage()
        with(MimeMessageHelper(message, true)) {
            setFrom(username)
            setTo(to)
            setCc(cc)
            setBcc(bcc)
            setSubject(subject)
            setText(text)
        }
        tryInvoke({ mailSender.send(message) }, { logger.info("SendingMail Failed And Retry.") })
    }
}
