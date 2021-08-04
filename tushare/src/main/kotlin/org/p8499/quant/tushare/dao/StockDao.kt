package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Stock

interface StockDao {
    fun findByStockIdList(stockIdList: List<String>): List<Stock>

    fun findByExchangeId(exchangeId: String): List<Stock>

    fun findByGroupId(groupId: String): List<Stock>
}