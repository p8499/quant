package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.StockIndexDailyDao
import org.p8499.quant.analysis.entity.StockIndexDaily
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class StockIndexDailyRepositoryImpl : StockIndexDailyDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, kpi: String): List<StockIndexDaily> = em
            .createQuery("select t0 from StockIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi", StockIndexDaily::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).resultList

    override fun deleteById(id: String): Int = em
            .createQuery("delete from StockIndexDaily as t0 where t0.id = :id")
            .setParameter("id", id).executeUpdate()
}