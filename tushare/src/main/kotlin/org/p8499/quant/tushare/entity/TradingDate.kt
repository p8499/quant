package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F02", uniqueConstraints = [UniqueConstraint(columnNames = ["exchangeId", "dte"])])
data class TradingDate(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TradingDate_id")
        @SequenceGenerator(name = "TradingDate_id", allocationSize = 1)
        var id: Int? = null,

        @Column(nullable = false, length = 4)
        var exchangeId: String? = null,

        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null
)
