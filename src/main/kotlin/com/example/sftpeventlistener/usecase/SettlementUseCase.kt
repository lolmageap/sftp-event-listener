package com.example.sftpeventlistener.usecase

import com.example.sftpeventlistener.model.SettlementFailException
import com.example.sftpeventlistener.service.FileService
import com.example.sftpeventlistener.service.SettlementCalculator
import com.example.sftpeventlistener.service.SettlementService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File

private val logger = KotlinLogging.logger {}

@Service
class SettlementUseCase(
    private val fileService: FileService,
    private val settlementService: SettlementService,
    private val settlementCalculator: SettlementCalculator,
) {

    /**
     * finally 블록을 사용 하여 파일을 삭제 하게 되면 errorHandling 실패 시에도 파일이 삭제 됨
     */
    fun settlement(file: File) {
        try {
            val amount = settlementCalculator.calculateSettlement(file)
            settlementService.save(file.name, amount)

            fileService.copyToSettlementSuccess(file)
            fileService.deleteLocalFile(file)

        } catch (e: SettlementFailException) {
            logger.error { "정산 처리 실패: ${e.message}" }
            fileService.copyToError(file)
            fileService.deleteLocalFile(file)
        }
    }

}