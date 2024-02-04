# Sftp Event Listener

- Workflow {

    1. sftp server 에 파일이 생성 되면, 해당 파일을 input 이라는 폴더에 저장 합니다.
    2. 저장이 성공 했다면 sftp server 에 저장된 파일을 삭제 합니다.
    3. input 폴더에 저장된 파일을 읽어서 파일의 내용 으로 정산을 실행 시킵니다.
    4. 만약에 정산 결과가 성공적 으로 생성 됐다면 settlement 폴더에 정산 결과 파일을 저장 합니다.
    5. 정산 과정 중에 에러가 발생 하면 error 폴더에 에러 파일을 저장 합니다. - error log 도 같이 저장 합니다.

    }

### 실행 단계

1.  #### sftp 에 접속 하기 위해 pem key 를 생성 해준다.
    - ssh-keygen -t rsa -b 2048 -f ./src/main/resources/static/keys/test_key.pem;

2. #### sftp server 에 접속 하기 위한 정보를 설정 해준다.
    [SFTP Server 설정](src%2Fmain%2Fkotlin%2Fcom%2Fexample%2Fsftpeventlistener%2Fconfig%2FSftpConfig.kt)

3. #### sftp server 가 없다면 docker 를 이용 하여 sftp server 를 실행 시킨다.
    - ex) docker run -p 2222:22 -d atmoz/sftp:alpine user:password:::upload

4. #### 그렇지 않고 로컬 에서 실행 시킬 거면 기본 설정 으로 application 을 실행 한다.

## 주의
한번 처리한 파일과 파일 명이 동일 하면 event listener 가 동작 하지 않는다.