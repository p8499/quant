package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.TradingDateDao
import org.p8499.quant.tushare.entity.TradingDate
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class TradingDateRepositoryImpl : TradingDateDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun last(exchangeId: String): TradingDate? = em
            .createQuery("select t0 from TradingDate as t0 where t0.exchangeId = :exchangeId and not exists (select 1 from TradingDate as t1 where t1.exchangeId = :exchangeId and t1.date > t0.date)", TradingDate::class.java)
            .setParameter("exchangeId", exchangeId).resultList.singleOrNull()

    override fun unprocessedForLevel1(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1 t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun unprocessedForLevel2(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level2 t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList
}