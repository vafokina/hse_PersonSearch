package hse.personsearch.service.mapper

import hse.personsearch.domain.ReportLink
import hse.personsearch.service.dto.ReportLinkDto
import org.springframework.stereotype.Component

@Component
class ReportLinkMapper {

    fun toDto(reportLink: ReportLink): ReportLinkDto {
        return ReportLinkDto().apply {
            this.url = reportLink.url
            this.imageUrl = reportLink.imageUrl
            this.title = reportLink.title
            this.description = reportLink.description
            this.presenceRate = reportLink.presenceRate
            this.publishDate = reportLink.publishDate
        }
    }
}