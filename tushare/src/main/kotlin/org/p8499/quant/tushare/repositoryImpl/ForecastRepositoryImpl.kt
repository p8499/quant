package org.p8499.quant.tushare.repositoryImpl

import org.p8499.quant.tushare.dao.ForecastDao
import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.entity.Forecast
import org.springframework.stereotype.Repository
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class ForecastRepositoryImpl : ForecastDao {
    @PersistenceContext
    protected lateinit var em: EntityManager

    override fun last(stockId: String): Forecast? = em
            .createQuery("select t0 from Forecast as t0 where t0.stockId = :stockId and not exists (select 1 from Forecast as t1 where t1.stockId = :stockId and t1.publish > t0.publish)", Forecast::class.java)
            .setParameter("stockId", stockId).resultList.firstOrNull()

    override fun findByStockId(stockId: String): List<Forecast> = em
            .createQuery("select t0 from Forecast as t0 where t0.stockId = :stockId order by t0.publish asc", Forecast::class.java)
            .setParameter("stockId", stockId).resultList

    override fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Forecast> = em
            .createQuery("select t0 from Forecast as t0 where t0.stockId = :stockId and t0.publish between :from and :to order by t0.publish asc", Forecast::class.java)
            .setParameter("stockId", stockId).setParameter("from", from).setParameter("to", to).resultList

    override fun expires(stockId: String, year: Int, period: Int): LocalDate? {
        val publishBS = em.createQuery("select t1.publish" +
                " from Forecast as t0 left join BalanceSheet as t1 on t1.stockId = t0.stockId and t1.publish > t0.publish and not exists (select 1 from BalanceSheet t11 where t11.stockId = t1.stockId and t11.publish > t0.publish and t11.publish < t1.publish)" +
                " where t0.stockId = :stockId and t0.year = :year and t0.period = :period and t1.publish is not null",
                LocalDate::class.java).setParameter("stockId", stockId).setParameter("year", year).setParameter("period", period).resultList
        val publishPL = em.createQuery("select t2.publish" +
                " from Forecast as t0 left join Income as t2 on t2.stockId = t0.stockId and t2.publish > t0.publish and not exists (select 1 from Income t21 where t21.stockId = t2.stockId and t21.publish > t0.publish and t21.publish < t2.publish)" +
                " where t0.stockId = :stockId and t0.year = :year and t0.period = :period and t2.publish is not null",
                LocalDate::class.java).setParameter("stockId", stockId).setParameter("year", year).setParameter("period", period).resultList
        val publishCf = em.createQuery("select t3.publish" +
                " from Forecast as t0 left join Cashflow as t3 on t3.stockId = t0.stockId and t3.publish > t0.publish and not exists (select 1 from Cashflow t31 where t31.stockId = t3.stockId and t31.publish > t0.publish and t31.publish < t3.publish)" +
                " where t0.stockId = :stockId and t0.year = :year and t0.period = :period and t3.publish is not null",
                LocalDate::class.java).setParameter("stockId", stockId).setParameter("year", year).setParameter("period", period).resultList
        val publishEx = em.createQuery("select te.publish" +
                " from Forecast as t0 left join Express as te on te.stockId = t0.stockId and te.publish > t0.publish and not exists (select 1 from Express te1 where te1.stockId = te.stockId and te1.publish > t0.publish and te1.publish < te.publish)" +
                " where t0.stockId = :stockId and t0.year = :year and t0.period = :period and te.publish is not null",
                LocalDate::class.java).setParameter("stockId", stockId).setParameter("year", year).setParameter("period", period).resultList
        return (publishBS + publishPL + publishCf + publishEx).minOrNull()
    }
}