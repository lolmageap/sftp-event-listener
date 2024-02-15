package com.example.sftpeventlistener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory

/**
 *  보안이 있는 SFTP 연결 설정을 할 때 사용 하는 클래스
 */
@Configuration
class SftpConfig {

    @Bean
    fun defaultSessionFactory() =
        DefaultSftpSessionFactory(true).apply {
            setHost("localhost")
            setPort(2222)
            setUser("user")
            setPassword("password")
            setAllowUnknownKeys(true)
        }

    /**
     * CachingSessionFactory 를 사용 하여 세션을 캐싱 하여 사용
     * 세션을 캐싱(재활용) 하여 사용 하면 세션을 매번 생성 하지 않는다.
     * setSessionWaitTimeout 은 세션이 유효 하지 않을 때 대기 시간 설정
     * setPoolSize 는 세션을 최대 몇개 생성 할지 설정 하는 옵션
     * setTestSession 은 세션이 유효 한지 확인 하는 옵션 - 테스트 용으로 있는줄 알았음...
     */
    @Bean
    fun sftpSessionFactory() =
        CachingSessionFactory(
            defaultSessionFactory()
        ).apply {
            setSessionWaitTimeout(1000)
            setPoolSize(3)
            setTestSession(true)
        }
}
