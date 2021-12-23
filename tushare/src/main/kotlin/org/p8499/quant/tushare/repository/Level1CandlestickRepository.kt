package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.Level1CandlestickDao
import org.p8499.quant.tushare.entity.Level1Candlestick
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface Level1CandlestickRepository :
        JpaRepository<Level1Candlestick, Level1Candlestick.Level1CandlestickId>,
        JpaSpecificationExecutor<Level1Candlestick>,
        PagingAndSortingRepository<Level1Candlestick, Level1Candlestick.Level1CandlestickId>,
        Level1CandlestickDao
