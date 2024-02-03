package com.example.sftpeventlistener

import com.example.sftpeventlistener.FilePath.LOCAL_FILE_DIRECTORY
import org.springframework.messaging.MessageHeaders
import org.springframework.stereotype.Service
import java.io.File

/**
* 무중단 배포 시 sqs 와 같은 메시지 브로커 를 사용 하면 좋을 듯
* 현재는 파일을 읽어서 처리 하는 방식 으로 구현 되어 있음
* 파일을 읽어서 처리 하는 방식 으로 구현 되어 있음
**/

@Service
class FileEvent {

    fun copy(file: File, header: MessageHeaders) {
        val localPath = File("$LOCAL_FILE_DIRECTORY/${file.name}")
        file.copyTo(
            target = localPath,
            overwrite = true,
        )
    }

}

/*   {
*       file_originalFile=/Users/cherhy/IdeaProjects/sftp-event-listener/src/main/resources/static/sftp/hi,
*       id=7032bc51-9b75-6967-e135-4e3bf848a900,
*       file_name=hi,
*       file_relativePath=hi,
*       timestamp=1706945027440
*    }
*/