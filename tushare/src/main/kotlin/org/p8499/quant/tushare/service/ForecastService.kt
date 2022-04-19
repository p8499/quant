package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.repository.ForecastRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ForecastService {
    @Autowired
    lateinit var forecastRepository: ForecastRepository

    fun last(stockId: String) = forecastRepository.last(stockId)

    fun findByStockId(stockId: String) = forecastRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = forecastRepository.findByStockIdBetween(stockId, from, to)

    fun expires(stockId: String, year: Int, period: Int) = forecastRepository.expires(stockId, year, period)

    fun saveAll(entityIterable: Iterable<Forecast>): List<Forecast> = forecastRepository.saveAllAndFlush(entityIterable)
}