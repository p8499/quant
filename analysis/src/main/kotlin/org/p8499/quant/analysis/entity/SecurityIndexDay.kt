package org.p8499.quant.analysis.entity

import org.p8499.quant.analysis.common.round
import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SID")
@IdClass(SecurityIndexDay.SecurityIndexDayId::class)
open class SecurityIndexDay(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var id: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        open var type: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        open var date: LocalDate? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(32, 8)", precision = 32, scale = 8)
        open var value: Double? = null) {
    open class SecurityIndexDayId(
            open var region: String? = null,
            open var id: String? = null,
            open var type: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, type, date)
        override fun equals(other: Any?): Boolean = other is SecurityIndexDayId && other.region == region && other.type == type && other.date == date
    }

    override fun hashCode(): Int = Objects.hash(region, id, type, date, value)
    override fun equals(other: Any?): Boolean = other is SecurityIndexDay && other.region == region && other.type == type && other.date == date && other.value?.round(0.00000001) == value?.round(0.00000001)
}