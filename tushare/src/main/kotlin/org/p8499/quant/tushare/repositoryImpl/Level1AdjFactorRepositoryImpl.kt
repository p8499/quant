package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.Level1AdjFactorDao
import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class Level1AdjFactorRepositoryImpl : Level1AdjFactorDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): Level1AdjFactor? = em
            .createQuery("select t0 from Level1AdjFactor as t0 where t0.stockId = :stockId and t0.date <= :date and not exists (select 1 from Level1AdjFactor t1 where t1.stockId = :stockId and t1.date > t0.date)", Level1AdjFactor::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Level1AdjFactor> = em
            .createQuery("select t0 from Level1AdjFactor as t0 where t0.stockId = :stockId order by t0.date asc", Level1AdjFactor::class.java)
            .setParameter("stockId", stockId).resultList

    override fun previous(stockId: String, date: Date): Level1AdjFactor? = em.createQuery("select t0 from Level1AdjFactor as t0 where t0.stockId = :stockId and t0.date < :date and not exists (select 1 from Level1AdjFactor as t1 where t1.stockId = :stockId and t1.date < :date and t1.date > t0.date)", Level1AdjFactor::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Level1AdjFactor> = em
            .createQuery("select t0 from Level1AdjFactor as t0 where t0.stockId = :stockId and t0.date between :from and :to order by t0.date asc", Level1AdjFactor::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList
}