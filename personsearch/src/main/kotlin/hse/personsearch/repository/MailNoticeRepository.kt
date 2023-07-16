package hse.personsearch.repository

import hse.personsearch.domain.MailNotice
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MailNoticeRepository : JpaRepository<MailNotice, Long> {

    fun findByRequestUuid(uuid: UUID): MailNotice?

    fun deleteByRequestUuid(uuid: UUID)
}