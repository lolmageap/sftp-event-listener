package com.example.sftpeventlistener

import com.example.sftpeventlistener.FilePath.ORIGIN_FILE_DIRECTORY
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
 *  설정해 놓은 경로에 파일을 읽어서 처리 하는 설정 클래스
 *  ex) 설정해 놓은 경로에 파일이 생성 되면 파일을 읽어서 처리 하는 설정
 *  handle 에서 파일을 읽어서 처리 하는 로직을 작성 하면 됨
 */
@Configuration
@EnableIntegration
class FileIntegrationConfig(
    private val fileEvent: FileEvent,
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
                Pollers.fixedDelay(5000)
            )
        }.channel(
            fileInputChannel()
        ).handle<File> { file, header ->
            try {
                fileEvent.copy(file, header)
            } catch (e: Exception) {
                println("Error reading file: ${e.message}")
            }
        }.get()
    }

}