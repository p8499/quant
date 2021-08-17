package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupIndexDailyDao
import org.p8499.quant.analysis.entity.GroupIndexDaily
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupIndexDailyRepositoryImpl : GroupIndexDailyDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String, kpi: String): List<GroupIndexDaily> = em
            .createQuery("select t0 from GroupIndexDaily as t0 where t0.region = :region and t0.id = :id and t0.kpi = :kpi", GroupIndexDaily::class.java)
            .setParameter("region", region).setParameter("id", id).setParameter("kpi", kpi).resultList

    override fun deleteById(id: String): Int = em
            .createQuery("delete from GroupIndexDaily as t0 where t0.id = :id")
            .setParameter("id", id).executeUpdate()
}