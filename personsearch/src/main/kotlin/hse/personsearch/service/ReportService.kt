package hse.personsearch.service

import hse.personsearch.domain.Report
import hse.personsearch.domain.ReportLink
import hse.personsearch.domain.ReportSorting
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReportService {

    fun getReport(id: Long): Report

    fun getReportLinks(id: Long, sorting: ReportSorting, pageable: Pageable): Page<ReportLink>

    fun findReportImageUrl(id: Long): String?
}