package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SMD")
@IdClass(SecurityMessageDay.SecurityMessageDayId::class)
open class SecurityMessageDay(
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

        @Lob
        @Column(nullable = true, columnDefinition = "CLOB")
        open var value: String? = null) {
    open class SecurityMessageDayId(
            open var region: String? = null,
            open var id: String? = null,
            open var type: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, type, date)
        override fun equals(other: Any?): Boolean = other is SecurityMessageDayId && other.region == region && other.type == type && other.date == date
    }

    override fun hashCode(): Int = Objects.hash(region, id, type, date, value)
    override fun equals(other: Any?): Boolean = other is SecurityMessageDay && other.region == region && other.type == type && other.date == date && other.value == value
}