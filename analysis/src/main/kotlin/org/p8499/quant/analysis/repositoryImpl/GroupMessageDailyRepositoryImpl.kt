package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupMessageDailyDao
import org.p8499.quant.analysis.entity.GroupMessageDaily
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class GroupMessageDailyRepositoryImpl : GroupMessageDailyDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String): List<GroupMessageDaily> = em
            .createQuery("select t0 from GroupMessageDaily as t0 where t0.region = :region and t0.id = :id order by t0.date asc", GroupMessageDaily::class.java)
            .setParameter("region", region).setParameter("id", id).resultList

    override fun messages(region: String, id: String, limit: Int): List<String> = em
            .createQuery("select t0.message from GroupMessageDaily as t0 where t0.region = :region and t0.id = :id order by t0.date desc", String::class.java)
            .setParameter("region", region).setParameter("id", id).setMaxResults(limit).resultList.reversed()

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from GroupMessageDaily as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}