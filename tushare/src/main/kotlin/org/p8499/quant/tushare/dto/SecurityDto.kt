package org.p8499.quant.tushare.dto

import com.fasterxml.jackson.annotation.JsonProperty

class SecurityDto(
        @get:JsonProperty("r")
        @set:JsonProperty("r")
        var region: String = "",

        @get:JsonProperty("i")
        @set:JsonProperty("i")
        var id: String = "",

        @get:JsonProperty("n")
        @set:JsonProperty("n")
        var name: String = "")