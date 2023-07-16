package hse.personsearch.service.impl

import hse.personsearch.config.ApplicationProperties
import hse.personsearch.domain.MailNotice
import hse.personsearch.repository.MailNoticeRepository
import hse.personsearch.service.MailService
import java.util.Locale
import java.util.UUID
import javax.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context


@Service
open class MailServiceImpl(
    private val mailNoticeRepository: MailNoticeRepository,
    private val javaMailSender: JavaMailSender,
    private val templateEngine: ITemplateEngine,
    private val applicationProperties: ApplicationProperties,
    private val environment: Environment
) : MailService {
    private val log = LoggerFactory.getLogger(MailService::class.java)

    override fun findMailNotice(uuid: UUID): MailNotice? {
        return mailNoticeRepository.findByRequestUuid(uuid)
    }

    @Transactional
    override fun saveMailNotice(mailNotice: MailNotice) {
        mailNoticeRepository.save(mailNotice)
    }

    @Transactional
    override fun deleteMailNotice(uuid: UUID) {
        mailNoticeRepository.deleteByRequestUuid(uuid)
    }

    override fun sendMailNotice(email: String, reportId: Long, reportImageUrl: String?) {
        val msg = javaMailSender.createMimeMessage()

        val helper = MimeMessageHelper(msg, true)
        helper.setTo(email)
        helper.setFrom(environment.getProperty("spring.mail.username", "personsearch.service@gmail.com"),
            applicationProperties.name)
        helper.setSubject("Отчет №$reportId готов")

        val params = mapOf(
            Pair("url", "${applicationProperties.address}/report/$reportId"),
            Pair("imageUrl", reportImageUrl),
        )
        val context = Context(Locale("RU"), params)
        helper.setText(templateEngine.process("mailNotice.html", context), true)

        javaMailSender.send(msg)
        log.info("Send message to $email with reportId=$reportId")
    }

}