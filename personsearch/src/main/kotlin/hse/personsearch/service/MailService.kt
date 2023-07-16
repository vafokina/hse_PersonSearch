package hse.personsearch.service

import hse.personsearch.domain.MailNotice
import java.util.UUID

interface MailService {

    fun findMailNotice(uuid: UUID): MailNotice?

    fun saveMailNotice(mailNotice: MailNotice)

    fun deleteMailNotice(uuid: UUID)

    fun sendMailNotice(email: String, reportId: Long, reportImageUrl: String?)
}