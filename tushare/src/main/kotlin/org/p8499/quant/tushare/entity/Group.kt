package org.p8499.quant.tushare.entity

import javax.persistence.*

@Entity
@Table(name = "F04")
data class Group(
        @Id
        @Column(nullable = false, length = 32)
        var id: String? = null,

        @Column(nullable = false, length = 64)
        var name: String? = null,

        @Column(nullable = false)
        @Enumerated(EnumType.ORDINAL)
        var type: Type? = null
) {
    enum class Type { INDEX, INDUSTRY, CONCEPT }
}