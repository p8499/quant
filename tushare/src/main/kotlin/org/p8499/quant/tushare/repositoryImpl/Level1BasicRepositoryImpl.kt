package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.Level1BasicDao
import org.p8499.quant.tushare.entity.Level1Basic
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class Level1BasicRepositoryImpl : Level1BasicDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): Level1Basic? = em
            .createQuery("select t0 from Level1Basic as t0 where t0.stockId = :stockId and t0.date <= :date and not exists (select 1 from Level1Basic as t1 where t1.stockId = :stockId and t1.date > t0.date)", Level1Basic::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Level1Basic> = em
            .createQuery("select t0 from Level1Basic as t0 where t0.stockId = :stockId order by t0.date asc", Level1Basic::class.java)
            .setParameter("stockId", stockId).resultList
}