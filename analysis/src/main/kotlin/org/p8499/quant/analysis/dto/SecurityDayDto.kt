package org.p8499.quant.analysis.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class SecurityDayDto(
        @get:JsonProperty("r")
        @set:JsonProperty("r")
        var region: String = "",

        @get:JsonProperty("i")
        @set:JsonProperty("i")
        var id: String = "",

        @get:JsonProperty("d")
        @set:JsonProperty("d")
        @get:JsonFormat(pattern = "yyyyMMdd")
        @set:JsonFormat(pattern = "yyyyMMdd")
        var dateList: List<LocalDate> = listOf(),

        @get:JsonProperty("ie")
        @set:JsonProperty("ie")
        var indicesList: List<Indices> = listOf(),

        @get:JsonProperty("me")
        @set:JsonProperty("me")
        var messagesList: List<Messages> = listOf()) {
    class Indices(
            @get:JsonProperty("t")
            @set:JsonProperty("t")
            var type: String = "",

            @get:JsonProperty("v")
            @set:JsonProperty("v")
            var values: List<Double?> = listOf())

    class Messages(
            @get:JsonProperty("t")
            @set:JsonProperty("t")
            var type: String = "",

            @get:JsonProperty("v")
            @set:JsonProperty("v")
            var values: List<String?> = listOf())
}