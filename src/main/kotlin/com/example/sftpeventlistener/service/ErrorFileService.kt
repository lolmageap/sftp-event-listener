package com.example.sftpeventlistener.service

import com.example.sftpeventlistener.path.FilePath.ERROR_FILE_DIRECTORY
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.*

private val logger = KotlinLogging.logger {}

@Service
class ErrorFileService {

    fun copyToError(localFile: File) {
        val errorFile = File("$ERROR_FILE_DIRECTORY/${localFile.name}")

        try {
            localFile.copyTo(
                target = errorFile,
                overwrite = false,
            )
        } catch (e: Exception) {
            logger.error { "파일 복사 실패: ${e.message}" }
        }
    }

}
