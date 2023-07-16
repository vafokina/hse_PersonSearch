package hse.personsearch.service.dto

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class EmailDto {

    @Email
    @NotEmpty
    var email: String? = null

    @NotNull
    var requestUuid: UUID? = null
}