package com.example.sftpeventlistener.listener

import com.example.sftpeventlistener.path.FilePath.LOCAL_FILE_DIRECTORY
import com.example.sftpeventlistener.usecase.SettlementUseCase
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
 * sftp 에서 local 로 복사 해온 파일을 처리 하는 설정 클래스
 *
 * local 경로로 파일이 생성 되면 파일을 읽어서 처리 한다.
 */
@Configuration
@EnableIntegration
class LocalFileEventListener(
    private val settlementUseCase: SettlementUseCase,
) {

    @Bean
    fun settlementChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    fun settlement(): IntegrationFlow {
        return IntegrationFlow.from(
            Files.inboundAdapter(
                File(LOCAL_FILE_DIRECTORY)
            )
        ) { p ->
            p.poller(
                Pollers.fixedDelay(1000)
            )
        }.channel(
            settlementChannel()
        ).handle<File> { file, _ ->
            settlementUseCase.settlement(file)
        }.get()
    }

}