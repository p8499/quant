package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.GroupStockDao
import org.p8499.quant.tushare.entity.GroupStock
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupStockRepositoryImpl : GroupStockDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun findByGroupId(groupId: String): List<GroupStock> = em
            .createQuery("select t0 from GroupStock as t0 where t0.groupId = :groupId order by t0.stockId asc", GroupStock::class.java)
            .setParameter("groupId", groupId).resultList
}