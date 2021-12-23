package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupIndexDailyDao
import org.p8499.quant.analysis.entity.GroupIndexDaily
import org.springframework.stereotype.Repository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupIndexDailyRepositoryImpl : GroupIndexDailyDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, kpi: String): List<GroupIndexDaily> = em
            .createQuery("select t0 from GroupIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi order by t0.date asc", GroupIndexDaily::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).resultList

    override fun size(region: String, id: String): Int = em
            .createQuery("select count(distinct t0.date) from GroupIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = 'close'", Number::class.java)
            .setParameter("region", region).setParameter("id", id).singleResult.toInt()

    override fun dates(region: String, id: String, limit: Int): List<LocalDate> = em
            .createQuery("select t0.date from GroupIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = 'close' order by t0.date desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setMaxResults(limit).resultList.reversed()

    override fun values(region: String, id: String, kpi: String, limit: Int): List<Double?> = em
            .createQuery("select t0.value from GroupIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi order by t0.date desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).setMaxResults(limit).resultList.reversed().map { it?.toDouble() }

    override fun tradingDates(region: String): List<LocalDate> = em
            .createQuery("select distinct(t0.date) from GroupIndexDaily as t0 where t0.region = :region and t0.kpi = 'close' order by t0.date asc", LocalDate::class.java)
            .setParameter("region", region).resultList

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from GroupIndexDaily as t0 where t0.region = :region and  t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}