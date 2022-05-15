package org.p8499.quant.analysis.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class MailService {
    @Value("\${spring.mail.username}")
    protected lateinit var username: String

    @Resource
    protected lateinit var mailSender: JavaMailSender

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
        mailSender.send(message)
    }
}
