package hse.personsearch.service.mapper

import hse.personsearch.domain.MailNotice
import hse.personsearch.service.dto.EmailDto
import org.springframework.stereotype.Component

@Component
class MailNoticeMapper {

    fun toEntity(emailDto: EmailDto): MailNotice {
        return MailNotice().apply {
            this.requestUuid = emailDto.requestUuid
            this.email = emailDto.email
        }
    }
}