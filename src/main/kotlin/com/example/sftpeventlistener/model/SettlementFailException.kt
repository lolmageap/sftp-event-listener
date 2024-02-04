package com.example.sftpeventlistener.model

import java.lang.RuntimeException

data class SettlementFailException(
    override val message: String = "정산 처리 실패",
) : RuntimeException(message)