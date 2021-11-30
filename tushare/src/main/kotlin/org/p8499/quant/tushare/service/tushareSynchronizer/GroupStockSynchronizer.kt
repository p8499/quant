package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.tushareRequest.ConceptDetailRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexMemberRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexWeightRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class GroupStockSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var level1BasicService: Level1BasicService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    protected lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    protected lateinit var conceptDetailRequest: ConceptDetailRequest

    fun invoke() {
        logger.info("Start Synchronizing GroupStock")
        val groupStockListOfIndex: (Date) -> List<GroupStock> = { tradeDate ->
            val groupStockList = indexWeightRequest.invoke(IndexWeightRequest.InParams(tradeDate = tradeDate), IndexWeightRequest.OutParams::class.java).map { GroupStock(it.indexCode, it.conCode, it.weight) }
            val stockIdList = groupStockList.mapNotNull(GroupStock::stockId).let(stockService::findByStockIdList).map(Stock::id)
            groupStockList.filter { stockIdList.contains(it.groupId) }
        }
        val flowShareMap: (Date) -> Map<String, Double> = { tradeDate ->
            val map = mutableMapOf<String, Double>()
            for (stockId in stockService.findAll().mapNotNull(Stock::id))
                level1BasicService[stockId, tradeDate]?.flowShare?.also { map[stockId] = it }
            map
        }
        val weightList: (Map<String, Double>, List<String>) -> List<Double> = { rsMap, stockIdList ->
            val flowShareList = stockIdList.map { stockId -> rsMap[stockId] }
            val maxFlowShare = flowShareList.mapNotNull { it }.maxOrNull()
            flowShareList.map { let(it, maxFlowShare) { a, b -> a * 1000 / b } ?: 0.0 }
        }
        val groupStockListOfTransform: (Map<String, Double>, Int, (String) -> List<String>) -> List<GroupStock> = { rsMap, times, transform ->
            val groupIdList = groupService.findByType(Group.Type.INDUSTRY).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            val groupIdFlowable = Flowable.fromIterable(groupIdList)
            val stockIdListFlowable = groupIdFlowable.map(transform).zipWith(Flowable.interval((60 * 1000 / times).toLong(), TimeUnit.MILLISECONDS)) { stockIdList, _ -> stockIdList }
            val wListFlowable = stockIdListFlowable.map { weightList(rsMap, it) }
            Flowable.zip(groupIdFlowable, stockIdListFlowable, wListFlowable) { groupId, stockIdList, wList -> Triple(groupId, stockIdList, wList) }
                    .blockingSubscribe(
                            { it.second.mapIndexedTo(groupStockList) { index, s -> GroupStock(it.first, s, it.third[index]) } },
                            { logger.error(it.message) })
            groupStockList
        }
        val groupStockListOfIndustry: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            groupStockListOfTransform(rsMap, 200) { indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = it), IndexMemberRequest.OutParams::class.java).mapNotNull(IndexMemberRequest.OutParams::conCode) }
        }
        val groupStockListOfConcept: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            groupStockListOfTransform(rsMap, 200) { conceptDetailRequest.invoke(ConceptDetailRequest.InParams(id = it), ConceptDetailRequest.OutParams::class.java).mapNotNull(ConceptDetailRequest.OutParams::tsCode) }
        }
        val date = tradingDateService.last("SSE")?.date ?: return
        val fsMap = flowShareMap(date)
        groupStockService.deleteAndSaveAll(groupStockListOfIndex(date) + groupStockListOfIndustry(fsMap) + groupStockListOfConcept(fsMap))
        controllerService.complete("GroupStock")
        logger.info("Finish Synchronizing GroupStock")
    }
}