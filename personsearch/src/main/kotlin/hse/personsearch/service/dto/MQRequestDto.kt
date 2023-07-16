package hse.personsearch.service.dto

import java.util.UUID

class MQRequestDto {

    var uuid: UUID? = null

    var imageUrl: String? = null

    override fun toString(): String {
        return "{ uuid: $uuid, imageUrl: $imageUrl }"
    }
}