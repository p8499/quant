package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.TradingDateDao
import org.p8499.quant.tushare.entity.TradingDate
import org.springframework.stereotype.Repository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class TradingDateRepositoryImpl : TradingDateDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun last(exchangeId: String): TradingDate? = em
            .createQuery("select t0 from TradingDate as t0 where t0.exchangeId = :exchangeId and not exists (select 1 from TradingDate as t1 where t1.exchangeId = :exchangeId and t1.date > t0.date)", TradingDate::class.java)
            .setParameter("exchangeId", exchangeId).resultList.singleOrNull()

    override fun unprocessedForLevel1Candlestick(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Candlestick t2 where t2.stockId = :stockId and t2.date >= t0.date) order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun firstUnprocessedForLevel1Candlestick(stockId: String): TradingDate? = em
            .createQuery("select min(t0) from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Candlestick t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun vacantForLevel1Candlestick(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Candlestick as t2 where t2.stockId = :stockId and t2.date = t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun unprocessedForLevel1Basic(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Basic t2 where t2.stockId = :stockId and t2.date >= t0.date) order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun firstUnprocessedForLevel1Basic(stockId: String): TradingDate? = em
            .createQuery("select min(t0) from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Basic t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun vacantForLevel1Basic(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1Basic as t2 where t2.stockId = :stockId and t2.date = t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun unprocessedForLevel1AdjFactor(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1AdjFactor t2 where t2.stockId = :stockId and t2.date >= t0.date) order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun firstUnprocessedForLevel1AdjFactor(stockId: String): TradingDate? = em
            .createQuery("select min(t0) from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1AdjFactor t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun vacantForLevel1AdjFactor(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level1AdjFactor as t2 where t2.stockId = :stockId and t2.date = t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun unprocessedForLevel2(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level2 t2 where t2.stockId = :stockId and t2.date >= t0.date) order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun firstUnprocessedForLevel2(stockId: String): TradingDate? = em
            .createQuery("select min(t0) from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level2 t2 where t2.stockId = :stockId and t2.date >= t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun vacantForLevel2(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 left join Stock as t1 on t1.exchangeId = t0.exchangeId where t1.id = :stockId and t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and not exists (select 1 from Level2 as t2 where t2.stockId = :stockId and t2.date = t0.date)", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockId(stockId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 inner join Stock as t1 on t1.exchangeId = t0.exchangeId and t1.id = :stockId where t1.listed <= t0.date and :from <= t0.date and t0.date <= coalesce(t1.delisted, t0.date) and t0.date <= :to order by t0.date asc", TradingDate::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList

    override fun findByExchangeId(exchangeId: String): List<TradingDate> = em
            .createQuery("select t0 from TradingDate as t0 where t0.exchangeId = :exchangeId order by t0.date asc", TradingDate::class.java)
            .setParameter("exchangeId", exchangeId).resultList
}