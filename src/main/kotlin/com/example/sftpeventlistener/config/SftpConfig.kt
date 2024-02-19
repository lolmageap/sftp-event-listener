package com.example.sftpeventlistener.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory

/**
 * <h2> Sftp Connection 설정 입니다.</h2>
 * <h2> DefaultSftpSessionFactory 를 통해 Sftp 연결을 설정 합니다.</h2>
 * <p> - CachingSessionFactory 는 세션을 관리 하며, 세션을 재사용 합니다.</p>
 * <p> - CachingSessionFactory 를 설정 하지 않으면 세션이 매번 생성 됩니다. (critical issue)</p>
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
     * <p>CachingSessionFactory 를 사용 하여 세션을 캐싱 하여 사용</p>
     * <p>세션을 캐싱(재활용) 하여 사용 하면 세션을 매번 생성 하지 않는다.</p>
     * <p>setSessionWaitTimeout 은 세션이 유효 하지 않을 때 대기 시간 설정</p>
     * <p>setPoolSize 는 세션을 최대 몇개 생성 할지 설정 하는 옵션</p>
     * <p>setTestSession 은 세션이 유효 한지 확인 하는 옵션 - 테스트 용으로 있는줄 알았음...</p>
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
