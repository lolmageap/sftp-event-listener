package com.example.sftpeventlistener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory

/**
 *  보안이 있는 SFTP 연결 설정을 할 때 사용 하는 클래스
 */

@Configuration
class SftpConfig {

    @Bean
    fun sftpSessionFactory(): DefaultSftpSessionFactory =
        DefaultSftpSessionFactory(true).apply {
            setHost("localhost")
            setPort(2222)
            setUser("user")
            setPassword("password")
            setAllowUnknownKeys(true)
        }
}
