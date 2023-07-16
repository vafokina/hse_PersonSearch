package hse.personsearch.service.dto

import hse.personsearch.domain.SearchError

class ResponseDto {

    var errorCode: SearchError? = null

    var reportId: Long? = null
}