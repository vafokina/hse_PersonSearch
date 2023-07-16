package hse.personsearch.service.dto

import java.time.Instant
import org.springframework.data.domain.Page

class ReportDto {

    var updateDate: Instant? = null

    var imageUrl: String? = null

    var links: Page<ReportLinkDto>? = null
}