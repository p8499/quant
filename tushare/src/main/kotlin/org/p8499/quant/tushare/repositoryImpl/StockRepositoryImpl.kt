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
            .createQuery("select t0 from Stock as t0 where ${stockIdList.joinToString(separator = " or ", transform = { "t0.stockId = :$it" })}", Stock::class.java)
            .resultList
}