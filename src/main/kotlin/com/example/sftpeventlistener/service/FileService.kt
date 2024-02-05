package com.example.sftpeventlistener.service

import com.example.sftpeventlistener.path.FilePath.ERROR_FILE_DIRECTORY
import com.example.sftpeventlistener.path.FilePath.SETTLEMENT_SUCCESS_FILE_DIRECTORY
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.File

/**
* 무중단 배포 시 sqs 와 같은 메시지 브로커 를 사용 하면 좋을 듯
* 현재는 파일을 읽어서 처리 하는 방식 으로 구현 되어 있음
* 파일을 읽어서 처리 하는 방식 으로 구현 되어 있음
**/

private val logger = KotlinLogging.logger {}
@Service
class FileService {

    fun copyToSettlementSuccess(localFile: File) {
        val settlementSuccessPath = File("$SETTLEMENT_SUCCESS_FILE_DIRECTORY/${localFile.name}")

        try {
            localFile.copyTo(
                target = settlementSuccessPath,
                overwrite = false,
            )
        } catch (e: FileAlreadyExistsException) {
            logger.error { "File already exists: ${localFile.name}" }
        }
    }

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

    fun deleteLocalFile(localFile: File) {
        if (localFile.exists()) {
            localFile.delete()
        }
    }

}

/**
 * TODO : 이런 식으로 closable 을 사용 하여 파일을 복사 하도록 수정 하자!
 * TODO : 그렇지 않으면 memory leak 이 발생 할 수 있음 ㅠㅠ
 * BufferedReader(FileReader(localFile)).use { reader ->
 *     BufferedWriter(FileWriter(errorFile)).use { writer ->
 *         reader.copyTo(writer)
 *     }
 * }
 */