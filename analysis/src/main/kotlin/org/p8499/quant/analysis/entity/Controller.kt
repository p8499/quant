package org.p8499.quant.analysis.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "C")
class Controller(
        @Id
        @Column(nullable = false, length = 2)
        var region: String? = null,

        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null
)