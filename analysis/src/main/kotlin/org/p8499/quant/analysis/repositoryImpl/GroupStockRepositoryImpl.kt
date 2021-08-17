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

    override fun findByGroupId(groupId: String): List<GroupStock> = em
            .createQuery("select t0 from GroupStock as t0 where t0.groupId = :groupId", GroupStock::class.java)
            .setParameter("groupId", groupId).resultList

    override fun deleteByGroupId(groupId: String): Int = em
            .createQuery("delete from GroupStock as t0 where t0.groupId = :groupId")
            .setParameter("groupId", groupId).executeUpdate()
}