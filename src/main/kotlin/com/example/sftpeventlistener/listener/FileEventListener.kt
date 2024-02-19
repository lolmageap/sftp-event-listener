package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_DIRECTORY
import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_META_DATA_STORE
import com.example.sftpeventlistener.path.FilePath.ORIGIN_PULL_FILE_DIRECTORY
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
 * <h2> Sftp 에서 파일을 읽어 오는 이벤트 리스너 입니다.</h2>
 * <h3> IntegrationFlows 설명 : </h3>
 * <p> - from : 어디서 읽어올 지 </p>
 * <p> - channel : 어디로 보낼지 </p>
 * <p> - handle : 어떻게 처리할 지 </p>
 */
@Configuration
@EnableIntegration
class FileEventListener(
    private val sessionFactory: CachingSessionFactory<SftpClient.DirEntry>,
    private val settlementUseCase: SettlementUseCase,
) {
    @Bean
    fun fileInputChannel(): MessageChannel {
        return DirectChannel()
    }

    /**
     *  <p>metadataStore 는 파일을 읽었 는지 안읽었 는지 확인 하기 위한 저장소</p>
     *  <p>( 서버가 다운 되었 다가 다시 올라 오면 중복 처리 되는 것을 방지 하기 위한 설정)</p>
     */
    @Bean
    fun metadataStore(): ConcurrentMetadataStore =
        PropertiesPersistingMetadataStore().apply {
            setBaseDirectory(LOCAL_FILE_META_DATA_STORE)
            afterPropertiesSet()
        }

    /**
     * <h2> Sftp inboundAdapter 는 Sftp 서버 에서 파일을 읽어 오는 역할을 합니다.</h2>
     * <p> - remoteDirectory : Sftp 서버의 어느 directory 에서 읽어올 지 </p>
     * <p> - localDirectory : 읽어온 파일을 어디에 저장할 지 </p>
     * <p> - deleteRemoteFiles : 읽어온 파일을 Sftp 서버 에서 삭제할 지 </p>
     * <p> - localFilter: sftp 에서 local 로 읽어온 파일을 poller 의 필터 대상 으로 적용 </p>
     * <p> - localFilter.prefix : 절대 변하면 안되는 구분자 값 (prefix 로 localFilter 의 대상을 파악함) </p>
     * <p> - filter : Success 에 파일이 존재 하지 않으면 handle 을 실행 시키지 않습니다. (읽어 오기는 함) </p>
     * <p> - poller : 얼마 마다 읽어올 지 (읽어 오면 handle 에서 파일을 처리 합니다.) </p>
     */
    @Bean
    fun fileReadingFlow(): IntegrationFlow {
        return IntegrationFlow.from(
            Sftp.inboundAdapter(sessionFactory)
                .remoteDirectory(ORIGIN_PULL_FILE_DIRECTORY)
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