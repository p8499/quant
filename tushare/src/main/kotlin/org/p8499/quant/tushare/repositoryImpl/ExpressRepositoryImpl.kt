package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.ExpressDao
import org.p8499.quant.tushare.entity.Express
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class ExpressRepositoryImpl : ExpressDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun last(stockId: String): Express? = em.createQuery("select t0 from Express as t0 where t0.stockId = :stockId and not exists (select 1 from Express as t1 where t1.stockId = :stockId and t1.publish > t0.publish)", Express::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Express> = em
            .createQuery("select t0 from Express as t0 where t0.stockId = :stockId order by t0.publish asc", Express::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Express> = em
            .createQuery("select t0 from Express as t0 where t0.stockId = :stockId and t0.publish between :from and :to order by t0.publish asc", Express::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList
}