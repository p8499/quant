package org.p8499.quant.analysis.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "C")
open class Controller(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.TIMESTAMP)
        open var date: LocalDateTime? = null
)