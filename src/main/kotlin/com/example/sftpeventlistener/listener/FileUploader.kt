package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.path.FilePath.ORIGIN_PUSH_FILE_DIRECTORY
import org.apache.sshd.sftp.client.SftpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.file.remote.handler.FileTransferringMessageHandler
import org.springframework.integration.file.remote.session.CachingSessionFactory
import org.springframework.integration.sftp.dsl.Sftp
import org.springframework.messaging.MessageChannel

@Configuration
@EnableIntegration
class FileUploader(
    private val sessionFactory: CachingSessionFactory<SftpClient.DirEntry>,
) {

    @Bean
    fun fileUploadChannel(): MessageChannel {
        return DirectChannel()
    }

    /**
     * sftp 서버로 파일을 업로드 하는 설정
     * useTemporaryFileName 은 파일을 업로드 할 때 임시 파일을 사용 할지 설정 하는 옵션
     * false 로 설정 하면 파일을 업로드 할 때 원본 파일명 으로 업로드 한다.
     */
    @Bean
    fun sftpOutboundFlow(): IntegrationFlow {
        return IntegrationFlow.from(fileUploadChannel())
            .handle<FileTransferringMessageHandler<SftpClient.DirEntry>>(
                Sftp.outboundAdapter(sessionFactory)
                    .useTemporaryFileName(false)
                    .remoteDirectory(ORIGIN_PUSH_FILE_DIRECTORY)
            ).get()
    }

}