package hse.personsearch.service.mapper

import hse.personsearch.domain.Report
import hse.personsearch.service.dto.ReportLinkDto
import hse.personsearch.service.dto.ReportDto
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ReportMapper {

    fun toDto(report: Report, linksDto: Page<ReportLinkDto>): ReportDto {
        return ReportDto().apply {
            this.updateDate = report.updateDate
            this.imageUrl = report.imageUrl
            this.links = linksDto
        }
    }
}