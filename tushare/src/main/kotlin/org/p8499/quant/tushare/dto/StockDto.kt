package org.p8499.quant.tushare.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class StockDto(
        var id: String = "",
        @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
        var date: List<Date> = listOf(),
        var open: List<Double?> = listOf(),
        var close: List<Double?> = listOf(),
        var high: List<Double?> = listOf(),
        var low: List<Double?> = listOf(),
        var volume: List<Double?> = listOf(),
        var amount: List<Double?> = listOf(),
        var flowShare: List<Double?> = listOf(),
        var totalShare: List<Double?> = listOf(),
        var flowValue: List<Double?> = listOf(),
        var totalValue: List<Double?> = listOf(),
        var pb: List<Double?> = listOf(),
        var pe: List<Double?> = listOf(),
        var ps: List<Double?> = listOf(),
        var pcf: List<Double?> = listOf())