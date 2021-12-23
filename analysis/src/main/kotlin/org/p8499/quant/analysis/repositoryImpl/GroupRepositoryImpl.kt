package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupDao
import org.p8499.quant.analysis.entity.Group
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupRepositoryImpl : GroupDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String): List<Group> = em
            .createQuery("select t0 from Group as t0 where t0.region = :region order by t0.id asc", Group::class.java)
            .setParameter("region", region).resultList

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from Group as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}