package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_DIRECTORY
import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_META_DATA_STORE
import com.example.sftpeventlistener.path.FilePath.ORIGIN_FILE_DIRECTORY
import com.example.sftpeventlistener.usecase.SettlementUseCase
import org.apache.sshd.sftp.client.SftpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.metadata.ConcurrentMetadataStore
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore
import org.springframework.integration.sftp.dsl.Sftp
import org.springframework.messaging.MessageChannel
import java.io.File

/**
 *  sftp 에서 local 로 복사 해오는 클래스
 *
 *  ex) 설정해 놓은 경로에 파일이 생성 되면 파일을 읽어서 처리 하는 설정
 */

@Configuration
@EnableIntegration
class OriginFileEventListener(
    private val sessionFactory: CachingSessionFactory<SftpClient.DirEntry>,
    private val settlementUseCase: SettlementUseCase,
) {
    @Bean
    fun fileInputChannel(): MessageChannel {
        return DirectChannel()
    }

    /**
     *  metadataStore 는 파일을 읽었 는지 안읽었 는지 확인 하기 위한 저장소
     *
     *  localFilter 에서 읽은 데이터 는 중복 처리 하지 않기 위한 설정
     *
     *  ( 서버가 다운 되었 다가 다시 올라 오면 중복 처리 되는 것을 방지 하기 위한 설정)
     */
    @Bean
    fun metadataStore(): ConcurrentMetadataStore =
        PropertiesPersistingMetadataStore().apply {
            setBaseDirectory(LOCAL_FILE_META_DATA_STORE)
            afterPropertiesSet()
        }

    /**
     * sftp 서버에서 파일을 읽어서 local 로 복사 하는 설정
     *
     * poller 를 사용 하여 주기적으로 파일을 읽어서 처리 하도록 설정
     *
     * fileInputChannel 에서 파일을 읽어서 처리 하도록 설정
     *
     * handle 에서 파일을 읽어서 처리 하는 로직을 구현
     *
     * 파일을 읽어서 처리 하는 로직은 settlementUseCase 에서 구현
     */
    @Bean
    fun fileReadingFlow(): IntegrationFlow {
        return IntegrationFlow.from(
            Sftp.inboundAdapter(sessionFactory)
                .remoteDirectory(ORIGIN_FILE_DIRECTORY)
                .localDirectory(File(LOCAL_FILE_DIRECTORY))
                .deleteRemoteFiles(false)
                .localFilter(
                    FileSystemPersistentAcceptOnceFileListFilter(
                        metadataStore(),
                        "prefix",
                    )
                )
        ) { p ->
            p.poller(
                Pollers.fixedDelay(1000)
            )
        }.channel(
            fileInputChannel()
        ).handle<File> { file, _ ->
            settlementUseCase.settlement(file)
        }
        .get()
    }

}