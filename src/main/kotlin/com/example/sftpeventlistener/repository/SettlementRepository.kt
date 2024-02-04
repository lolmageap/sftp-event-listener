package com.example.sftpeventlistener.repository

import com.example.sftpeventlistener.model.Settlement
import org.springframework.data.jpa.repository.JpaRepository

interface SettlementRepository: JpaRepository<Settlement, Long>