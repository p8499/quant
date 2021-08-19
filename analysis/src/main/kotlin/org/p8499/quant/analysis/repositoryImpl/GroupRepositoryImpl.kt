package org.p8499.quant.analysis.repositoryImpl

import org.p8499.quant.analysis.dao.GroupDao
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupRepositoryImpl : GroupDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun delete(region: String, id: String): Int = em
            .createQuery("delete from Group as t0 where t0.region = :region and t0.id = :id")
            .setParameter("region", region).setParameter("id", id).executeUpdate()
}