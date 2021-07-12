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
}