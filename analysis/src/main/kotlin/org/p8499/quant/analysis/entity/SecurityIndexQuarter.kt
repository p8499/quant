package org.p8499.quant.analysis.entity

import org.p8499.quant.analysis.common.round
import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SIQ")
@IdClass(SecurityIndexQuarter.SecurityIndexQuarterId::class)
open class SecurityIndexQuarter(
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
        @Column(nullable = false)
        open var publish: LocalDate? = null,

        @Id
        @Column(nullable = false)
        open var quarter: Int? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(32, 8)", precision = 32, scale = 8)
        open var value: Double? = null) {
    open class SecurityIndexQuarterId(
            open var region: String? = null,
            open var id: String? = null,
            open var type: String? = null,
            open var publish: LocalDate? = null,
            open var quarter: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, type, publish, quarter)
        override fun equals(other: Any?): Boolean = other is SecurityIndexQuarterId && other.region == region && other.type == type && other.publish == publish && other.quarter == quarter
    }

    override fun hashCode(): Int = Objects.hash(region, id, type, publish, quarter, value)
    override fun equals(other: Any?): Boolean = other is SecurityIndexQuarter && other.region == region && other.type == type && other.publish == publish && other.quarter == quarter && other.value?.round(0.00000001) == value?.round(0.00000001)
}