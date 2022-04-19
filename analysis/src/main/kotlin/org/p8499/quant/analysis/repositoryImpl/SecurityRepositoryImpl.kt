package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.SecurityDao
import org.p8499.quant.analysis.entity.Security
import org.springframework.stereotype.Repository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SecurityRepositoryImpl : SecurityDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String): List<Security> = em
            .createQuery("select t0 from Security as t0 where t0.region = :region order by t0.id asc", Security::class.java)
            .setParameter("region", region).resultList

    override fun tradingDates(region: String): List<LocalDate> = em
            .createQuery("select distinct(t0.date) from SecurityIndexDay as t0 where t0.region = :region and t0.type = 'close' order by t0.date asc", LocalDate::class.java)
            .setParameter("region", region).resultList

    override fun firstDay(region: String, id: String): LocalDate? = em
            .createQuery("select min(t0.date) from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = 'close'", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).singleResult

    override fun lastDay(region: String, id: String): LocalDate? = em
            .createQuery("select max(t0.date) from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = 'close'", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).singleResult

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from Security as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}