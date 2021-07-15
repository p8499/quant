package org.p8499.quant.tushare

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("tushare")
data class TushareProperties(
        var token: String = "")