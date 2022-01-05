package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.StockMessageDayDao
import org.p8499.quant.analysis.entity.StockMessageDay
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

class StockMessageDayRepositoryImpl : StockMessageDayDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun find(region: String, id: String): List<StockMessageDay> = em
            .createQuery("select t0 from StockMessageDay as t0 where t0.region = :region and t0.id = :id order by t0.date asc", StockMessageDay::class.java)
            .setParameter("region", region).setParameter("id", id).resultList

    override fun messages(region: String, id: String, limit: Int): List<String> = em
            .createQuery("select t0.message from StockMessageDay as t0 where t0.region = :region and t0.id = :id order by t0.date desc", String::class.java)
            .setParameter("region", region).setParameter("id", id).setMaxResults(limit).resultList.reversed()

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from StockMessageDay as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}