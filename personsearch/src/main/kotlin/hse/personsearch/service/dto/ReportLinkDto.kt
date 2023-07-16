package hse.personsearch.service.dto

import java.time.Instant
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class ReportLinkDto {

    @NotEmpty
    var url: String? = null

    @NotEmpty
    var title: String? = null

    @NotEmpty
    var description: String? = null

    var imageUrl: String? = null

    @NotNull
    var presenceRate: Float? = null

    var publishDate: Instant? = null
}