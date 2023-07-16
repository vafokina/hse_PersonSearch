package hse.personsearch.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import org.hibernate.annotations.Type

@Entity
@Table(name = "mail_notice")
class MailNotice {

    @Id
    @Column(name = "request_uuid")
    var requestUuid: UUID? = null

    @NotEmpty
    @Column(name = "email")
    var email: String? = null
}