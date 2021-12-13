package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F91")
class Controller(
        @Id
        @Column(nullable = false, length = 32)
        var objectId: String? = null,

        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.TIMESTAMP)
        var date: Date? = null)
