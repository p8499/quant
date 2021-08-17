package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.Stock
import org.p8499.quant.analysis.repository.StockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockService {
    @Autowired
    protected lateinit var stockRepository: StockRepository

    fun save(entity: Stock): Stock = stockRepository.save(entity)

    fun deleteById(id: String): Int = stockRepository.deleteById(id)
}