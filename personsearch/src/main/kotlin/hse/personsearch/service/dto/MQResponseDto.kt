package hse.personsearch.service.dto

import java.util.UUID

class MQResponseDto {

    var uuid: UUID? = null

    var errorCode: String? = null

    var reportId: Long? = null

    override fun toString(): String {
        return "{ uuid: $uuid, errorCode: $errorCode, reportId: $reportId }"
    }
}