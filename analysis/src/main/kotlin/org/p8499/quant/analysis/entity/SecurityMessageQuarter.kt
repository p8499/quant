package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SMQ")
@IdClass(SecurityMessageQuarter.SecurityMessageQuarterId::class)
open class SecurityMessageQuarter(
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

        @Lob
        @Column(nullable = true, columnDefinition = "CLOB")
        open var value: String? = null) {
    open class SecurityMessageQuarterId(
            open var region: String? = null,
            open var id: String? = null,
            open var type: String? = null,
            open var publish: LocalDate? = null,
            open var quarter: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, type, publish, quarter)
        override fun equals(other: Any?): Boolean = other is SecurityMessageQuarterId && other.region == region && other.type == type && other.publish == publish && other.quarter == quarter
    }

    override fun hashCode(): Int = Objects.hash(region, id, type, publish, quarter, value)
    override fun equals(other: Any?): Boolean = other is SecurityMessageQuarter && other.region == region && other.type == type && other.publish == publish && other.quarter == quarter && other.value == value
}