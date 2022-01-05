package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupMessageDayDao
import org.p8499.quant.analysis.entity.GroupMessageDay
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class GroupMessageDayRepositoryImpl : GroupMessageDayDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String): List<GroupMessageDay> = em
            .createQuery("select t0 from GroupMessageDay as t0 where t0.region = :region and t0.id = :id order by t0.date asc", GroupMessageDay::class.java)
            .setParameter("region", region).setParameter("id", id).resultList

    override fun messages(region: String, id: String, limit: Int): List<String> = em
            .createQuery("select t0.message from GroupMessageDay as t0 where t0.region = :region and t0.id = :id order by t0.date desc", String::class.java)
            .setParameter("region", region).setParameter("id", id).setMaxResults(limit).resultList.reversed()

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from GroupMessageDay as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}