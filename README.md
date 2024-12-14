# FF-IV 디스코드 챗봇 🐸
author : toto9602<br>
Last Updated : 2024.09.12c

> [파이널판타지14] (이하 FF-IV) 관련 퀴즈를 출제하는 챗봇입니다. 🙃 

## Tech Stack
- Spring Boot, Kotlin, Docker & Docker-compose
- JDA(Java Discord API) & JDA-utilities

## Available Commands
### / 설명
- 봇이 지원하는 명령어 목록을 안내합니다.

### /잡 {questionCount}
- FF-IV 인게임 내 [ 잡 ] 관련 문항을 questionCount 개만큼 출제합니다.

### /점지 {questionCount}
- FF-IV 인게임 내 [ 점지 ] 스킬 관련 문항을 questionCount 개만큼 출제합니다.

### /무적 {questionCount}
- FF-IV 인게임 내 탱커의 무적기 관련 문항을 questionCount 개만큼 출제합니다.

### 중단!
- 진행 중이던 문제 풀이를 즉시 중단합니다.
## Core Dependencies

- JDA : 5.1.0
- JDA-chewtils : 2.0
