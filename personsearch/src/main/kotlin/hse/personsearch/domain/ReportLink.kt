package hse.personsearch.domain

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "report_link")
class ReportLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_link_id_seq")
    @SequenceGenerator(name = "report_link_id_seq", allocationSize = 1)
    var id: Long = 0

    @ManyToOne
    @JoinColumn(name = "report_id")
    @NotNull
    var report: Report? = null

    @NotNull
    @Column(name = "url")
    var url: String? = null

    @NotNull
    @Column(name = "title")
    var title: String? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "image_url")
    var imageUrl: String? = null

    @NotNull
    @Column(name = "source_link_id")
    var sourceLinkId: Long? = null

    @NotNull
    @Column(name = "presence_rate")
    var presenceRate: Float? = null

    @Column(name = "publish_date")
    var publishDate: Instant? = null
}