package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupStockDao
import org.p8499.quant.analysis.entity.GroupStock
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupStockRepositoryImpl : GroupStockDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun findByGroup(region: String, groupId: String): List<GroupStock> = em
            .createQuery("select t0 from GroupStock as t0 where t0.region = :region and t0.groupId = :groupId", GroupStock::class.java)
            .setParameter("region", region).setParameter("groupId", groupId).resultList

    override fun deleteByGroup(region: String, groupId: String): Int = em
            .createQuery("delete from GroupStock as t0 where t0.region = :region and t0.groupId = :groupId")
            .setParameter("region", region).setParameter("groupId", groupId).executeUpdate()
}