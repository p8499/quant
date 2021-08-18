package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.ForecastDao
import org.p8499.quant.tushare.entity.Forecast
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class ForecastRepositoryImpl : ForecastDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun last(stockId: String): Forecast? = em
            .createQuery("select t0 from Forecast as t0 where t0.stockId = :stockId and not exists (select 1 from Forecast as t1 where t1.stockId = :stockId and t1.publish > t0.publish)", Forecast::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Forecast> = em
            .createQuery("select t0 from Forecast as t0 where t0.stockId = :stockId order by t0.publish asc", Forecast::class.java)
            .setParameter("stockId", stockId).resultList
}