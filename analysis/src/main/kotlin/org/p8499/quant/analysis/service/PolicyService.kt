package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockIndexDaily
import org.p8499.quant.analysis.policy.AnalysisObject
import org.p8499.quant.analysis.repository.GroupIndexDailyRepository
import org.p8499.quant.analysis.repository.StockIndexDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PolicyService {
    @Autowired
    protected lateinit var stockIndexDailyRepository: StockIndexDailyRepository

    @Autowired
    protected lateinit var groupIndexDailyRepository: GroupIndexDailyRepository

    fun getStockObject(region: String, id: String) = AnalysisObject(region, id,
            stockIndexDailyRepository.find(region, id, "open").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "close").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "high").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "low").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "volume").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "amount").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "totalShare").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "flowShare").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "totalValue").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "flowValue").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "pb").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "pe").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "ps").map(StockIndexDaily::value),
            stockIndexDailyRepository.find(region, id, "pcf").map(StockIndexDaily::value))
}