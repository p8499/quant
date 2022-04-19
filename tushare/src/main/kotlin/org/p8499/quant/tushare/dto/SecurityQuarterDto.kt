package org.p8499.quant.tushare.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class SecurityQuarterDto(
        @get:JsonProperty("r")
        @set:JsonProperty("r")
        var region: String = "",

        @get:JsonProperty("i")
        @set:JsonProperty("i")
        var id: String = "",


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

            @get:JsonProperty("i")
            @set:JsonProperty("i")
            var indexList: List<Index> = listOf())

    class Messages(
            @get:JsonProperty("t")
            @set:JsonProperty("t")
            var type: String = "",

            @get:JsonProperty("i")
            @set:JsonProperty("i")
            var messageList: List<Message> = listOf())

    class Index(
            @get:JsonProperty("p")
            @set:JsonProperty("p")
            var publish: LocalDate = LocalDate.EPOCH,

            @get:JsonProperty("q")
            @set:JsonProperty("q")
            var quarter: Int = 0,/*2021 * 4 + 0*/

            @get:JsonProperty("v")
            @set:JsonProperty("v")
            var value: Double? = null)

    class Message(
            @get:JsonProperty("p")
            @set:JsonProperty("p")
            var publish: LocalDate = LocalDate.EPOCH,

            @get:JsonProperty("q")
            @set:JsonProperty("q")
            var quarter: Int = 0,/*2021 * 4 + 0*/

            @get:JsonProperty("v")
            @set:JsonProperty("v")
            var value: String? = null)
}