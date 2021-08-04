package org.p8499.quant.tushare.dto

import java.util.*

data class GroupDto(
        val id: String,
        val date: List<Date>,
        val open: List<Double?>,
        val close: List<Double?>,
        val high: List<Double?>,
        val low: List<Double?>,
        val volume: List<Double?>,
        val amount: List<Double?>,
        val pb: List<Double?>,
        val pe: List<Double?>,
        val ps: List<Double?>,
        val pcf: List<Double?>)