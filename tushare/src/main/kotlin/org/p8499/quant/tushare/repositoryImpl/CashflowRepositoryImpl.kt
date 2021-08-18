package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.CashflowDao
import org.p8499.quant.tushare.entity.Cashflow
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class CashflowRepositoryImpl : CashflowDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): Cashflow? = em
            .createQuery("select t0 from Cashflow as t0 where t0.stockId = :stockId and t0.publish <= :date and not exists (select 1 from Cashflow as t1 where t1.stockId = :stockId and t1.publish = :date and t1.publish < t0.publish)", Cashflow::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun last(stockId: String): Cashflow? = em.createQuery("select t0 from Cashflow as t0 where t0.stockId = :stockId and not exists (select 1 from Cashflow as t1 where t1.stockId = :stockId and t1.publish > t0.publish)", Cashflow::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Cashflow> = em
            .createQuery("select t0 from Cashflow as t0 where t0.stockId = :stockId order by t0.publish asc", Cashflow::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Cashflow> = em
            .createQuery("select t0 from Cashflow as t0 where t0.stockId = :stockId and t0.publish between :from and :to order by t0.publish asc", Cashflow::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList
}