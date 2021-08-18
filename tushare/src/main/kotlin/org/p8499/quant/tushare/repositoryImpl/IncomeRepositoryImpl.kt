package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.IncomeDao
import org.p8499.quant.tushare.entity.Income
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class IncomeRepositoryImpl : IncomeDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): Income? = em
            .createQuery("select t0 from Income as t0 where t0.stockId = :stockId and t0.publish <= :date and not exists (select 1 from Income as t1 where t1.stockId = :stockId and t1.publish = :date and t1.publish < t0.publish)", Income::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun last(stockId: String): Income? = em.createQuery("select t0 from Income as t0 where t0.stockId = :stockId and not exists (select 1 from Income as t1 where t1.stockId = :stockId and t1.publish > t0.publish)", Income::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Income> = em
            .createQuery("select t0 from Income as t0 where t0.stockId = :stockId order by t0.publish asc", Income::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Income> = em
            .createQuery("select t0 from Income as t0 where t0.stockId = :stockId and t0.publish between :from and :to order by t0.publish asc", Income::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList
}