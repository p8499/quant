package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.let2
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

@Service
class GroupStockSynchronizer {
    val log by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    lateinit var tradingDateService: TradingDateService

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var groupStockService: GroupStockService

    @Autowired
    lateinit var level1BasicService: Level1BasicService

    @Autowired
    lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    lateinit var conceptDetailRequest: ConceptDetailRequest

    fun invoke() {
        log.info("Start Synchronizing GroupStock")
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
            flowShareList.map { let2(it, maxFlowShare) { a, b -> a * 1000 / b } ?: 0.0 }
        }
        val groupStockListOfIndustry: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            val groupIdList = groupService.findByType(Group.Type.INDUSTRY).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = groupId), IndexMemberRequest.OutParams::class.java).map(IndexMemberRequest.OutParams::conCode).mapNotNull { it }
                val wList = weightList(rsMap, stockIdList)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val groupStockListOfConcept: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            val groupIdList = groupService.findByType(Group.Type.CONCEPT).map(Group::id)
            val groupStockList = mutableListOf<GroupStock>()
            for (groupId in groupIdList) {
                val stockIdList = conceptDetailRequest.invoke(ConceptDetailRequest.InParams(id = groupId), ConceptDetailRequest.OutParams::class.java).map(ConceptDetailRequest.OutParams::tsCode).mapNotNull { it }
                val wList = weightList(rsMap, stockIdList)
                stockIdList.mapIndexedTo(groupStockList) { index, s -> GroupStock(groupId, s, wList[index]) }
            }
            groupStockList
        }
        val date = tradingDateService.last("SSE")?.date ?: return
        val fsMap = flowShareMap(date)
        groupStockService.deleteAndSaveAll(groupStockListOfIndex(date) + groupStockListOfIndustry(fsMap) + groupStockListOfConcept(fsMap))
        log.info("Finish Synchronizing GroupStock")
    }
}