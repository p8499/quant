package org.p8499.quant.tushare.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class StockDto(
        var region: String = "",

        var id: String = "",

        var name: String = "",

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var date: List<LocalDate> = listOf(),

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

        var pcf: List<Double?> = listOf(),

        var message: List<String?> = listOf(),

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var quarterDate: List<LocalDate> = listOf(),

        var asset: List<Double?> = listOf(),

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var assetPublish: List<LocalDate?> = listOf(),

        var profit: List<Double?> = listOf(),

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var profitPublish: List<LocalDate?> = listOf(),

        var revenue: List<Double?> = listOf(),

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var revenuePublish: List<LocalDate?> = listOf(),

        var cashflow: List<Double?> = listOf(),

        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var cashflowPublish: List<LocalDate?> = listOf())