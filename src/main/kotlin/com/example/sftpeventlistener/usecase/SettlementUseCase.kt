package com.example.sftpeventlistener.usecase

import com.example.sftpeventlistener.model.SettlementFailException
import com.example.sftpeventlistener.service.ErrorFileService
import com.example.sftpeventlistener.service.SettlementCalculator
import com.example.sftpeventlistener.service.SettlementService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File

private val logger = KotlinLogging.logger {}

@Service
class SettlementUseCase(
    private val errorFileService: ErrorFileService,
    private val settlementService: SettlementService,
    private val settlementCalculator: SettlementCalculator,
) {

    fun settlement(file: File) {
        try {
            val amount = settlementCalculator.calculateSettlement(file)
            settlementService.save(file.name, amount)
        } catch (e: SettlementFailException) {
            logger.error { "정산 처리 실패: ${e.message}" }
            errorFileService.copyToError(file)
        }
    }

}