package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Exchange
import org.p8499.quant.tushare.repository.ExchangeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ExchangeService {
    @Autowired
    lateinit var exchangeRepository: ExchangeRepository

    fun findAll(): List<Exchange> = exchangeRepository.findAll(Sort.by("id"))

    fun save(entity: Exchange) = exchangeRepository.saveAndFlush(entity)
}