package com.example.sftpeventlistener.service

import com.example.sftpeventlistener.model.SettlementFailException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.RuntimeException
import java.math.BigDecimal
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

// 파일을 읽어서 정산 처리를 하는 로직을 구현 해야함
@Component
class SettlementCalculator {

    fun calculateSettlement(file: File): BigDecimal {
        try {
            BufferedReader(
                FileReader(file)
            ).use {
                val readText = file.readText(StandardCharsets.UTF_8)
                logger.info { "file content: $readText" }
            }
            return BigDecimal(1000)
        } catch (e: RuntimeException) {
            throw SettlementFailException("정산 처리 실패")
        }
    }

}