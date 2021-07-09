package org.p8499.quant.tushare.entity

import javax.persistence.*

@Entity
@Table(name = "F05", uniqueConstraints = [UniqueConstraint(columnNames = ["groupId", "stockId"])])
data class GroupStock(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GroupStock_id")
        @SequenceGenerator(name = "GroupStock_id", allocationSize = 1)
        var id: Int? = null,

        @Column(nullable = false, length = 32)
        var groupId: String? = null,

        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false, precision = 8, scale = 4)
        var weight: Double? = null
)
