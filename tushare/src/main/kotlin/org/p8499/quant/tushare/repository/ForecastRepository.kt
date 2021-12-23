package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.ForecastDao
import org.p8499.quant.tushare.entity.Forecast
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ForecastRepository :
        JpaRepository<Forecast, Forecast.ForecastId>,
        JpaSpecificationExecutor<Forecast>,
        PagingAndSortingRepository<Forecast, Forecast.ForecastId>,
        ForecastDao
