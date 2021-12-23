package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F05")
@IdClass(GroupStock.GroupStockId::class)
open class GroupStock(
        @Id
        @Column(nullable = false, length = 32)
        open var groupId: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(8, 4)", precision = 8, scale = 4)
        open var weight: Double? = null) {
    open class GroupStockId(
            open var groupId: String? = null,
            open var stockId: String? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(groupId, stockId)
        override fun equals(other: Any?): Boolean = other is GroupStockId && other.groupId == groupId && other.stockId == stockId
    }
}
