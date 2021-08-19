package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "GID")
@IdClass(GroupIndexDaily.GroupIndexDailyId::class)
data class GroupIndexDaily(
        @Id
        @Column(nullable = false, length = 2)
        var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        var id: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        var kpi: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = true, precision = 28, scale = 4)
        var value: Double? = null) {
    data class GroupIndexDailyId(
            var region: String? = null,
            var id: String? = null,
            var kpi: String? = null,
            var date: Date? = null) : Serializable
}