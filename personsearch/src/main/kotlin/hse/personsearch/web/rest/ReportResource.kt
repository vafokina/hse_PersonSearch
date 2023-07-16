package hse.personsearch.web.rest

import hse.personsearch.domain.Constants.DEFAULT_SORTING
import hse.personsearch.domain.ReportSorting
import hse.personsearch.service.ExcelExportService
import hse.personsearch.service.ReportService
import hse.personsearch.service.dto.ReportDto
import hse.personsearch.service.mapper.ReportLinkMapper
import hse.personsearch.service.mapper.ReportMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import java.io.File
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Api(description = "Endpoints for getting search results")
@RestController
@RequestMapping("/api/report")
class ReportResource {
    private val log = LoggerFactory.getLogger(ReportResource::class.java)

    @Autowired
    private lateinit var reportService: ReportService

    @Autowired
    private lateinit var reportMapper: ReportMapper

    @Autowired
    private lateinit var reportLinkMapper: ReportLinkMapper

    @Autowired
    private lateinit var excelExportService: ExcelExportService

    @ApiOperation(
        value = "Get search results by the report id",
        response = ReportDto::class
    )
    @GetMapping("/{id}")
    fun getReport(
        @ApiParam("the report id", example = "1", required = true)
        @NotNull @PathVariable id: Long,
        @ApiParam("sorting method")
        @RequestParam(name = "sorting", defaultValue = DEFAULT_SORTING) sorting: ReportSorting,
        pageable: Pageable
    ): ReportDto {
        log.debug("GET /api/report/$id Get search results by the report id")
        val reportLinks = reportService.getReportLinks(id, sorting, pageable).map(reportLinkMapper::toDto)
        return reportMapper.toDto(reportService.getReport(id), reportLinks)
    }

    @ApiOperation(
        value = "Download search results as Excel file by the report id",
        response = File::class
    )
    @GetMapping("/{id}/download")
    fun downloadReport(
        @ApiParam("the report id", example = "1", required = true)
        @NotNull @PathVariable id: Long,
        @ApiParam("sorting method")
        @RequestParam(name = "sorting", defaultValue = DEFAULT_SORTING) sorting: ReportSorting,
        response: HttpServletResponse
    ) {
        log.debug("GET /api/report/$id/download Download search results as Excel file by the report id")
        val report = reportService.getReport(id)
        val reportLinks = reportService.getReportLinks(id, sorting, Pageable.unpaged()).toList()

        response.contentType = "application/octet-stream"

        val headerKey = "Content-Disposition"
        val headerValue = "attachment; filename=Report_${report.id}.xlsx"
        response.setHeader(headerKey, headerValue)

        excelExportService.export(response, report, reportLinks)
    }
}