package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.extension.inputStream
import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_META_DATA_STORE
import com.example.sftpeventlistener.path.FilePath.ORIGIN_PULL_FILE_DIRECTORY
import org.apache.sshd.sftp.client.SftpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.core.MessageSource
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice
import org.springframework.integration.metadata.ConcurrentMetadataStore
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter
import org.springframework.integration.sftp.inbound.SftpStreamingMessageSource
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate
import org.springframework.integration.transformer.StreamTransformer
import org.springframework.messaging.MessageHandler
import java.io.InputStream

/**
 * Sftp 에서 파일을 읽어 Stream 으로 변환 하는 이벤트 리스너 입니다.
 * LocalDirectory 에 별도로 저장 하지 않고 Stream 으로 변환 하여 처리 합니다.
 */
@Configuration
@EnableIntegration
class FileStreamingEventListener(
    private val sessionFactory: CachingSessionFactory<SftpClient.DirEntry>,
) {
    @Bean
    fun metadataStore(): ConcurrentMetadataStore =
        PropertiesPersistingMetadataStore().apply {
            setBaseDirectory(LOCAL_FILE_META_DATA_STORE)
            afterPropertiesSet()
        }

    @Bean
    @InboundChannelAdapter(channel = "stream")
    fun ftpMessageSource(): MessageSource<InputStream> =
        SftpStreamingMessageSource(template()).apply {
            setRemoteDirectory(ORIGIN_PULL_FILE_DIRECTORY)
            setFilter(
                SftpPersistentAcceptOnceFileListFilter(
                    metadataStore(),
                    "prefix-"
                )
            )
            maxFetchSize = 1
        }

    /**
     * Sftp 에서 읽어온 파일을 Stream 으로 변환 할 때 인코딩 설정 입니다.
     */
    @Bean
    @Transformer(inputChannel = "stream", outputChannel = "data")
    fun transformer(): org.springframework.integration.transformer.Transformer =
        StreamTransformer("UTF-8")

    /**
     * Sftp 원격 directory 에서 파일을 읽어 오기 위한 template 입니다.
     */
    @Bean
    fun template(): SftpRemoteFileTemplate =
        SftpRemoteFileTemplate(sessionFactory)

    @Bean
    @ServiceActivator(inputChannel = "data", adviceChain = ["after"])
    fun handle(): MessageHandler =
        MessageHandler {  message ->
            message.inputStream.use { inputStream ->
                println(inputStream)
            }
        }

    /**
     * 에러 발생 시 로그를 남기기 위한 advice 입니다.
     * 에러 발생 시 로그를 남기고, 에러를 다시 던지게 됩니다.
     */
    @Bean
    fun after(): ExpressionEvaluatingRequestHandlerAdvice =
        ExpressionEvaluatingRequestHandlerAdvice().apply {
            setPropagateEvaluationFailures(true)
        }

}