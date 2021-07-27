package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.BalanceSheetDao
import org.p8499.quant.tushare.entity.BalanceSheet
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class BalanceSheetRepositoryImpl : BalanceSheetDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun get(stockId: String, date: Date): BalanceSheet? = em
            .createQuery("select t0 from BalanceSheet as t0 where t0.stockId = :stockId and t0.publish <= :date and not exists (select 1 from BalanceSheet as t1 where t1.stockId = :stockId and t1.publish = :date and t1.publish < t0.publish)", BalanceSheet::class.java)
            .setParameter("stockId", stockId).setParameter("date", date).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<BalanceSheet> = em
            .createQuery("select t0 from BalanceSheet as t0 where t0.stockId = :stockId order by t0.publish asc", BalanceSheet::class.java)
            .setParameter("stockId", stockId).resultList
}