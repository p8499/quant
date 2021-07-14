package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F03", uniqueConstraints = [UniqueConstraint(columnNames = ["exchangeId", "code"])])
data class Stock(
        @Id
        @Column(nullable = false, length = 16)
        var id: String? = null,

        @Column(nullable = false, length = 4)
        var exchangeId: String? = null,

        @Column(nullable = false, length = 6)
        var code: String? = null,

        @Column(nullable = false, length = 16)
        var name: String? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var listed: Date? = null,

        @Temporal(TemporalType.DATE)
        var delisted: Date? = null)
