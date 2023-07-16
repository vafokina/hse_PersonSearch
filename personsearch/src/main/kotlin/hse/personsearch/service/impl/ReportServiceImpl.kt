package hse.personsearch.service.impl

import hse.personsearch.domain.Report
import hse.personsearch.domain.ReportLink
import hse.personsearch.domain.ReportSorting
import hse.personsearch.repository.ReportLinkRepository
import hse.personsearch.repository.ReportRepository
import hse.personsearch.service.ReportService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ReportServiceImpl(
    private val reportRepository: ReportRepository,
    private val reportLinkRepository: ReportLinkRepository
) : ReportService {

    override fun getReport(id: Long): Report {
        return reportRepository.getById(id)
    }

    override fun getReportLinks(id: Long, sorting: ReportSorting, pageable: Pageable): Page<ReportLink> {
        return when (sorting) {
            ReportSorting.BY_PRESENCE_RATE -> reportLinkRepository.findAllByReportIdOrderByPresenceRateDesc(id, pageable)
            ReportSorting.BY_PUBLISH_DATE -> reportLinkRepository.findAllByReportIdOrderByPublishDateDesc(id, pageable)
        }
    }

    override fun findReportImageUrl(id: Long): String? {
        return reportRepository.getById(id).imageUrl
    }

}