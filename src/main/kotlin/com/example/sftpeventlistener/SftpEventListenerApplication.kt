package com.example.sftpeventlistener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SftpEventListenerApplication

fun main(args: Array<String>) {
    runApplication<SftpEventListenerApplication>(*args)
}
