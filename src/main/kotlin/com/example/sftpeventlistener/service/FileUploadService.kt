package com.example.sftpeventlistener.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileUploadService(
    @Qualifier("fileUploadChannel") private val fileUploadChannel: MessageChannel,
) {

    fun uploadFile(file: File) {
        val message = MessageBuilder
            .withPayload(file)
            .build()

        fileUploadChannel.send(message)
    }

}
