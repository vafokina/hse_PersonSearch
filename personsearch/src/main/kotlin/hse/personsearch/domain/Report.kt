package hse.personsearch.domain

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "report")
//@NamedEntityGraph(
//    name = "report.joinLinks",
//    attributeNodes = [NamedAttributeNode("links")]
//)
class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_id_seq")
    @SequenceGenerator(name = "report_id_seq", allocationSize = 1)
    var id: Long = 0

    @NotNull
    @Column(name = "update_date")
    var updateDate: Instant? = null

    @Column(name = "image_url")
    var imageUrl: String? = null

    @NotNull
    @Column(name = "image_code")
    var imageCode: String? = null

//    @OneToMany(mappedBy = "report")
//    val links: List<ReportLink>? = null
}