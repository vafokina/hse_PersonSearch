package hse.personsearch.web.rest

import hse.personsearch.domain.Constants.AUTO_EXPIRE_QUEUE_MILLIS
import hse.personsearch.domain.Constants.REQUEST_QUEUE_NAME
import hse.personsearch.domain.SearchError
import hse.personsearch.service.ImageUploadService
import hse.personsearch.service.MailService
import hse.personsearch.service.RabbitQueueService
import hse.personsearch.service.ReportService
import hse.personsearch.service.dto.EmailDto
import hse.personsearch.service.dto.MQRequestDto
import hse.personsearch.service.dto.MQResponseDto
import hse.personsearch.service.dto.ResponseDto
import hse.personsearch.service.mapper.MailNoticeMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Api(description = "Endpoints for finding web pages related to the person in the photo")
@RestController
@RequestMapping("/api/search")
class SearchResource {
    private val log = LoggerFactory.getLogger(SearchResource::class.java)

    @Autowired
    private lateinit var rabbitQueueService: RabbitQueueService

    @Autowired
    private lateinit var imageUploadService: ImageUploadService

    @Autowired
    private lateinit var reportService: ReportService

    @Autowired
    private lateinit var mailService: MailService

    @Autowired
    private lateinit var mailNoticeMapper: MailNoticeMapper

    //@Async
    @ApiOperation(
        value = "Send a request to search for web pages related to the person in the photo",
        response = String::class
    )
    @PostMapping()
    fun search(@NotNull @RequestBody file: MultipartFile): UUID {
        log.debug("POST /api/search Send a request to search for web pages related to the person in the photo")
        val uuid = UUID.randomUUID()

        val imageUrl = imageUploadService.upload(file, null, null)

        val request = MQRequestDto().apply {
            this.imageUrl = imageUrl
            this.uuid = uuid
        }

        rabbitQueueService.createQueue(uuid.toString(), AUTO_EXPIRE_QUEUE_MILLIS)
        rabbitQueueService.send(REQUEST_QUEUE_NAME, request)

        return uuid
    }

    @ApiOperation(
        value = "Get an id of a report with search results by the request uuid (waiting for a response from the queue)",
        response = String::class
    )
    @GetMapping("/{uuid}")
    fun getSearchResult(
        @ApiParam("the request uuid", example = "d54ffc8f-a3cd-4078-a68c-6f5df63e167a", required = true)
        @NotNull @PathVariable uuid: UUID
    ): ResponseDto {
        log.debug("GET /api/search/$uuid Get an id of a report with search results by the request uuid (waiting for a response from the queue)")
        val response = rabbitQueueService.receive<MQResponseDto>(uuid.toString(), ParameterizedTypeReference.forType(MQResponseDto::class.java))

        return if (!response.errorCode.isNullOrEmpty()) {
            val error = SearchError(response.errorCode!!)
            ResponseDto().apply { this.errorCode = error }
        } else {
            val reportId = response.reportId!!

            // notify by email
            val mailNotice = mailService.findMailNotice(uuid)
            if (mailNotice != null) {
                mailService.sendMailNotice(mailNotice.email!!, reportId, reportService.findReportImageUrl(reportId))
                mailService.deleteMailNotice(uuid)
            }

            ResponseDto().apply { this.reportId = reportId }
        }
    }

    @ApiOperation(
        value = "Get the number of requests in the queue",
        response = Int::class
    )
    @GetMapping("/request-count")
    fun getRequestCount(): Int {
        log.debug("GET /api/search/request-count Get the number of requests in the queue")
        // returns a count of all messages in both queues, not a count of messages before a specific request
        return rabbitQueueService.messageCount
    }

    @ApiOperation(
        value = "Send a user email to notify the user of search results when the results are ready"
    )
    @PostMapping("/email")
    fun notifyByEmail(@Valid @RequestBody emailDto: EmailDto) {
        log.debug("POST /api/search/email Send a user email to notify the user of search results when the results are ready")
        mailService.saveMailNotice(mailNoticeMapper.toEntity(emailDto))
    }

//    @PostMapping("/test-send/{uuid}")
//    fun test1(@NotNull @PathVariable uuid: UUID, response: MQResponseDto) {
//        rabbitQueueService.send(uuid.toString(), response)
//    }
//
    @PostMapping("/test-send-mail")
    fun test1(@NotNull @RequestParam email: String, @NotNull @RequestParam reportId: Long) {
        mailService.sendMailNotice(email, reportId, reportService.findReportImageUrl(reportId))
    }
}