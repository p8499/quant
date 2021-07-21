package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.ExpressDao
import org.p8499.quant.tushare.entity.Express
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class ExpressRepositoryImpl : ExpressDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun findByStockId(stockId: String): List<Express> = em
            .createQuery("select t0 from Express as t0 where t0.stockId = :stockId order by t0.publish asc", Express::class.java)
            .setParameter("stockId", stockId).resultList
}