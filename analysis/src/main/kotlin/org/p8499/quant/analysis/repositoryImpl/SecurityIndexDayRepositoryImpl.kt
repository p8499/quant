package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.SecurityIndexDayDao
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SecurityIndexDayRepositoryImpl : SecurityIndexDayDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, type: String): List<SecurityIndexDay> = em
            .createQuery("select t0 from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.date asc", SecurityIndexDay::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList

    override fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityIndexDay> = em
            .createQuery("select t0 from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type and t0.date >= :from and t0.date <= :to order by t0.date asc", SecurityIndexDay::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setParameter("from", from).setParameter("to", to).resultList

    override fun size(region: String, id: String, type: String): Int = em
            .createQuery("select count(t0) from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).singleResult.toInt()

    override fun firstOrNull(region: String, id: String, type: String): SecurityIndexDay? = em
            .createQuery("select t0 from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityIndexDay as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.date < t0.date)", SecurityIndexDay::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList.firstOrNull()

    override fun lastOrNull(region: String, id: String, type: String): SecurityIndexDay? = em
            .createQuery("select t0 from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type and not exists (select 1 from SecurityIndexDay as t1 where t1.region = :region and t1.id = :id and t1.type = :type and t1.date > t0.date)", SecurityIndexDay::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).resultList.firstOrNull()

    override fun dates(region: String, id: String, type: String, limit: Int): List<LocalDate> = em
            .createQuery("select t0.date from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.date desc", LocalDate::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed()

    override fun values(region: String, id: String, type: String, limit: Int): List<Double?> = em
            .createQuery("select t0.value from SecurityIndexDay as t0 where t0.region = :region and t0.id = :id and t0.type = :type order by t0.date desc", Number::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("type", type).setMaxResults(limit).resultList.reversed().map { it?.toDouble() }
}