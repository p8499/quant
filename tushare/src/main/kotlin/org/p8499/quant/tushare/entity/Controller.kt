package org.p8499.quant.tushare.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "F91")
open class Controller(
        @Id
        @Column(nullable = false, length = 32)
        open var objectId: String? = null,

        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.TIMESTAMP)
        open var date: LocalDateTime? = null)
