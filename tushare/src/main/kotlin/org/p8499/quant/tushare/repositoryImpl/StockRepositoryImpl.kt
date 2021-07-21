package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.StockDao
import org.p8499.quant.tushare.entity.Stock
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class StockRepositoryImpl : StockDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun findByStockIdList(stockIdList: List<String>): List<Stock> = em
            .createQuery("select t0 from Stock as t0 where ${if (stockIdList.isNotEmpty()) stockIdList.joinToString(separator = " or ", transform = { "t0.stockId = :$it" }) else "1 = 2"}", Stock::class.java)
            .resultList


    override fun findByExchangeId(exchangeId: String): List<Stock> = em
            .createQuery("select t0 from Stock as t0 where t0.exchangeId = :exchangeId", Stock::class.java)
            .setParameter("exchangeId", exchangeId).resultList
}