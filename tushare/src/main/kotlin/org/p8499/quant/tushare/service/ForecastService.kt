package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Forecast
import org.p8499.quant.tushare.repository.ForecastRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ForecastService {
    @Autowired
    lateinit var forecastRepository: ForecastRepository

    fun saveAll(entityList: List<Forecast>) = forecastRepository.saveAllAndFlush(entityList)
}