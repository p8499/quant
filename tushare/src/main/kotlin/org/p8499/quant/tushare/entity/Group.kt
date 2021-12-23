package org.p8499.quant.tushare.entity

import javax.persistence.*

@Entity
@Table(name = "F04")
open class Group(
        @Id
        @Column(nullable = false, length = 32)
        open var id: String? = null,

        @Column(nullable = false, length = 64)
        open var name: String? = null,

        @Column(nullable = false)
        @Enumerated(EnumType.ORDINAL)
        open var type: Type? = null) {
    enum class Type { INDEX, INDUSTRY, CONCEPT }
}