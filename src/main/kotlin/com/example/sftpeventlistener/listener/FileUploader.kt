package com.example.sftpeventlistener.listener

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory
import org.springframework.messaging.MessageChannel

@Configuration
@EnableIntegration
class FileUploader(
    private val sessionFactory: DefaultSftpSessionFactory,
) {

    @Bean
    fun fileUploadChannel(): MessageChannel {
        return DirectChannel()
    }

}