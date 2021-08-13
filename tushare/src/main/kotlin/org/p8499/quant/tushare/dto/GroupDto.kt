package org.p8499.quant.tushare.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class GroupDto(
        var id: String = "",
        var name: String = "",
        @get:JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
        var date: List<Date> = listOf(),
        var open: List<Double?> = listOf(),
        var close: List<Double?> = listOf(),
        var high: List<Double?> = listOf(),
        var low: List<Double?> = listOf(),
        var volume: List<Double?> = listOf(),
        var amount: List<Double?> = listOf(),
        var pb: List<Double?> = listOf(),
        var pe: List<Double?> = listOf(),
        var ps: List<Double?> = listOf(),
        var pcf: List<Double?> = listOf(),
        @JsonProperty("stock_id_list")
        var stockIdList: List<String> = listOf()
)