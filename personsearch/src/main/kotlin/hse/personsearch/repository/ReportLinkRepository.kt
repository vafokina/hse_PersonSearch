package hse.personsearch.repository

import hse.personsearch.domain.ReportLink
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportLinkRepository : JpaRepository<ReportLink, Long> {

    fun findAllByReportIdOrderByPresenceRateDesc(id: Long, pageable: Pageable): Page<ReportLink>

    fun findAllByReportIdOrderByPublishDateDesc(id: Long, pageable: Pageable): Page<ReportLink>
}