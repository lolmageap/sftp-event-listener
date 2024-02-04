package com.example.sftpeventlistener.service

import com.example.sftpeventlistener.model.Settlement
import com.example.sftpeventlistener.repository.SettlementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class SettlementService(
    private val settlementRepository: SettlementRepository,
) {

    fun save(name: String, amount: BigDecimal) {
        val settlement = Settlement(
            name = name,
            amount = amount,
        )
        settlementRepository.save(settlement)
    }

}
