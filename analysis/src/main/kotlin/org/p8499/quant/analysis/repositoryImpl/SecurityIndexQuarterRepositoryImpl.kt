package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.SecurityIndexQuarterDao
import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SecurityIndexQuarterRepositoryImpl : SecurityIndexQuarterDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, type: String): List<SecurityIndexQuarter> = em
            .createQuery("select t0 from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter asc", SecurityIndexQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityIndexQuarter> = em
            .createQuery("select t0 from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and t0.publish >= :from and t0.publish <= :to order by t0.publish asc, t0.quarter asc", SecurityIndexQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setParameter("from", from).setParameter("to", to).resultList

    override fun size(region: String, id: String, type: String): Int = em
            .createQuery("select count(t0) from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).singleResult.toInt()

    override fun firstOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter> = em
            .createQuery("select t0 from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityIndexQuarter as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.publish < t0.publish)", SecurityIndexQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun lastOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter> = em
            .createQuery("select t0 from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityIndexQuarter as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.publish > t0.publish)", SecurityIndexQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun quarters(region: String, id: String, type: String, limit: Int): List<Int> = em
            .createQuery("select t0.quarter from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed().map(Number::toInt)

    override fun values(region: String, id: String, type: String, limit: Int): List<Double?> = em
            .createQuery("select t0.value from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed().map { it?.toDouble() }

    override fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?> = em
            .createQuery("select t0.publish from SecurityIndexQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed()
}