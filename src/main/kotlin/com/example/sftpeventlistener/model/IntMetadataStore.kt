package com.example.sftpeventlistener.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

/**
 * Entity 복합키 설정
 */
@Entity
@IdClass(IntMetadataStorePK::class)
class IntMetadataStore(
    @Id
    private val metadataKey: String,

    @Id
    private val region: String,

    private val metadataValue: String,
)

class IntMetadataStorePK(
    private val metadataKey: String,
    private val region: String,
) : Serializable