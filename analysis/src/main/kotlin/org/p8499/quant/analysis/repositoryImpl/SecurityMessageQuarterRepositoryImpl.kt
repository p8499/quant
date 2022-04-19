package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.SecurityMessageQuarterDao
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SecurityMessageQuarterRepositoryImpl : SecurityMessageQuarterDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, type: String): List<SecurityMessageQuarter> = em
            .createQuery("select t0 from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter asc", SecurityMessageQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityMessageQuarter> = em
            .createQuery("select t0 from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and t0.publish >= :from and t0.publish <= :to order by t0.publish asc, t0.quarter asc", SecurityMessageQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setParameter("from", from).setParameter("to", to).resultList

    override fun size(region: String, id: String, type: String): Int = em
            .createQuery("select count(t0) from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).singleResult.toInt()

    override fun firstOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter> = em
            .createQuery("select t0 from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityMessageQuarter as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.publish < t0.publish)", SecurityMessageQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun lastOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter> = em
            .createQuery("select t0 from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityMessageQuarter as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.publish > t0.publish)", SecurityMessageQuarter::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun quarters(region: String, id: String, type: String, limit: Int): List<Int> = em
            .createQuery("select t0.quarter from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed().map(Number::toInt)

    override fun values(region: String, id: String, type: String, limit: Int): List<String?> = em
            .createQuery("select t0.value from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", String::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed()

    override fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?> = em
            .createQuery("select t0.publish from SecurityMessageQuarter as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.quarter desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed()
}