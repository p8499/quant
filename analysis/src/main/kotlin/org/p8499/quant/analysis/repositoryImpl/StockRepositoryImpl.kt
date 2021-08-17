package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.StockDao
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class StockRepositoryImpl : StockDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun deleteById(id: String): Int = em
            .createQuery("delete from Stock as t0 where t0.id = :id")
            .setParameter("id", id).executeUpdate()
}