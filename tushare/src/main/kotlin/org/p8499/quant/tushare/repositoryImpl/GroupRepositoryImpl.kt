package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.GroupDao
import org.p8499.quant.tushare.entity.Group
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class GroupRepositoryImpl : GroupDao {
    @PersistenceContext
    lateinit var em: EntityManager

    override fun findByType(type: Group.Type): List<Group> = em
            .createQuery("select t0 from Group as t0 where t0.type = :type", Group::class.java)
            .setParameter("type", type).resultList

}