package com.example.sftpeventlistener.extension

import org.springframework.messaging.Message
import java.io.ByteArrayInputStream
import java.io.InputStream

val Message<*>.inputStream: InputStream
    get() = ByteArrayInputStream(
        payload.toString().toByteArray()
    )