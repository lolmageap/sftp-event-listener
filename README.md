## Local Database Setting
- H2
h2 database 를 사용 할 때는 별도의 설정을 하지 않는다.

- Mysql
Docker 를 사용 하여 Mysql 을 구동 하고 yml 파일을 설정 한다.

```bash
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password -d mysql
```
※ Mysql 에 Connection 설정 할 때 매개 변수로 allowPublicKeyRetrieval=true 를 추가 해야 한다. (Sftp 서버와의 호환성 문제)

## Sftp Workflow

1. sftp server receive 폴더에 파일이 적재 되면 자동 으로 파일을 **무조건** 읽어 오고 저장 한다.
2. 읽어온 원본 파일은 poller 가 주기적 으로 호출 한다.
3. metadataStore 에 poller 가 처리한 파일의 정보를 저장 하여 재처리 를 방지 한다.
4. poller 에 의해 호출된 파일은 handler 에서 처리 된다.

---

## SFTP Local Test
Local 에서 Docker 로 SFTP 서버를 구동 하여 테스트 를 진행 한다.

SFTP 서버는 `atmoz/sftp` 를 사용 한다.

```bash
docker run -p 2222:22 -d --name one atmoz/sftp user:password:::pull,push
docker run -p 2223:22 -d --name two atmoz/sftp user:password:::pull,push
```

### SFTP 서버 접속
```bash
sftp -P 2222 user@localhost
sftp -P 2223 user@localhost
```

#### SFTP 서버 접속 정보
- password: password

### SFTP 서버에 수동 으로 파일 업로드 하는 방법

sftp 서버에 접속한 뒤
```bash
cd pull
put README.MD
```
