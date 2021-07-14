package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.Level2Dao
import org.p8499.quant.tushare.entity.Level2
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class Level2RepositoryImpl : Level2Dao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): Level2? = em
            .createQuery("select t0 from Level2 as t0 where t0.stockId = :stockId and t0.date <= :date and not exists (select 1 from Level2 t1 where t1.stockId = :stockId and t1.date > t0.date)", Level2::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()
}