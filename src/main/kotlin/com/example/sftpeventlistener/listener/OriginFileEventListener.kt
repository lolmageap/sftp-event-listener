package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.service.FileService
import com.example.sftpeventlistener.path.FilePath.ORIGIN_FILE_DIRECTORY
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.file.dsl.Files
import org.springframework.messaging.MessageChannel
import java.io.File

/**
 *  sftp 에서 local 로 복사 해오는 클래스
 *  ex) 설정해 놓은 경로에 파일이 생성 되면 파일을 읽어서 처리 하는 설정
 *  handle 에서 파일을 읽어서 처리 하는 로직을 작성 하면 됨
 */
@Configuration
@EnableIntegration
class OriginFileEventListener(
    private val fileService: FileService,
) {

    @Bean
    fun fileInputChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    fun fileReadingFlow(): IntegrationFlow {
        return IntegrationFlow.from(
            Files.inboundAdapter(
                File(ORIGIN_FILE_DIRECTORY)
            )
        ) { p ->
            p.poller(
                Pollers.fixedDelay(1000)
            )
        }.channel(
            fileInputChannel()
        ).handle<File> { file, _ ->
            fileService.copyToLocal(file)
            fileService.deleteOriginFile(file)
        }.get()
    }

}