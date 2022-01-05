package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.StockIndexQuarterDao
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class StockIndexQuarterRepositoryImpl : StockIndexQuarterDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun dates(region: String, id: String, limit: Int): List<LocalDate> = em
            .createQuery("select t0.date from StockIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.kpi = 'asset' order by t0.date desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setMaxResults(limit).resultList.reversed()

    override fun values(region: String, id: String, kpi: String, limit: Int): List<Double?> = em
            .createQuery("select t0.value from StockIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi order by t0.date desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).setMaxResults(limit).resultList.reversed().map { it?.toDouble() }

    override fun publishes(region: String, id: String, kpi: String, limit: Int): List<LocalDate?> = em
            .createQuery("select t0.publish from StockIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi order by t0.date desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).setMaxResults(limit).resultList.reversed()

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from StockIndexQuarter as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}