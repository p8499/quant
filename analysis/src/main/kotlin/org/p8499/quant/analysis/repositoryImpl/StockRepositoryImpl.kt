package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.StockDao
import org.p8499.quant.analysis.entity.Stock
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class StockRepositoryImpl : StockDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String): List<Stock> = em
            .createQuery("select t0 from Stock as t0 where t0.region = :region order by t0.id asc", Stock::class.java)
            .setParameter("region", region).resultList

    override fun findByGroup(region: String, groupId: String): List<Stock> = em.createQuery("select t0 from Stock as t0 where exists (select 1 from GroupStock as t1 where t1.region = t0.region and t1.stockId = t0.id and t1.region = :region and t1.groupId = :groupId)", Stock::class.java)
            .setParameter("region", region).setParameter("groupId", groupId).resultList

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from Stock as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}