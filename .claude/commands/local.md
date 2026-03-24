---
description: 로컬에서 Stage/Prod 인프라에 연결하여 부트스트랩 모듈 실행. RDS + Redis 포트포워딩 + 환경변수 자동 세팅.
tags: [project]
---

# /local - 로컬 실행 환경 자동 세팅

Stage 또는 Prod 인프라(RDS, Redis)에 포트포워딩으로 연결하고, 지정한 부트스트랩 모듈을 로컬에서 실행합니다.

## 사용 형식

```
/local <환경> [모듈]
```

- 환경: `stage` (기본) 또는 `prod`
- 모듈: `scheduler` (기본), `web-api`, `legacy-api`, `worker`

**예시:**
- `/local stage` → stage 인프라 + scheduler 실행
- `/local stage scheduler` → 동일
- `/local stage web-api` → stage 인프라 + web-api 실행

## 실행 절차

### 1단계: 포트포워딩 확인/시작

| 환경 | RDS 포트 | Redis 포트 | 스크립트 |
|------|---------|-----------|---------|
| stage | 13308 | 16381 | `local-dev/scripts/aws-port-forward-stage.sh` |
| prod | 13307 | 16380 | `local-dev/scripts/aws-port-forward.sh` |

1. `lsof -i :<RDS_PORT>`로 RDS 포트포워딩 확인
2. `lsof -i :<REDIS_PORT>`로 Redis 포트포워딩 확인
3. 둘 다 활성화되어 있으면 2단계로
4. 하나라도 없으면 사용자에게 포트포워딩 스크립트 실행을 안내:
   ```
   ! bash local-dev/scripts/aws-port-forward-stage.sh
   ```
5. 포트포워딩이 올라오면 계속 진행

### 2단계: 환경변수 세팅

**메모리에서 DB credential 조회**: `memory/reference_db_credentials.md`

#### Stage 환경변수

```bash
# === DB (Stage RDS via port-forward) ===
DB_HOST=127.0.0.1
DB_PORT=13308
DB_NAME=market
DB_USERNAME=admin
DB_PASSWORD=<메모리에서 조회>

# === Legacy DB (같은 RDS, 같은 스키마) ===
LEGACY_DB_NAME=market
LEGACY_DB_USERNAME=admin
LEGACY_DB_PASSWORD=<메모리에서 조회>

# === Redis (Stage ElastiCache via port-forward) ===
REDIS_HOST=127.0.0.1
REDIS_PORT=16381

# === 네이버 Commerce API (Stage shop 테이블에서 조회) ===
# Stage DB: SELECT api_key, api_secret FROM shop WHERE channel_code = 'NAVER';
NAVER_COMMERCE_CLIENT_ID=<shop.api_key>
NAVER_COMMERCE_CLIENT_SECRET=<shop.api_secret>

# === 외부 서비스 (더미 — 로컬에서 실제 호출 안 됨) ===
FILEFLOW_SERVICE_TOKEN=local-dummy-token
AUTHHUB_SERVICE_TOKEN=local-dummy-token
OPENAI_API_KEY=sk-dummy-local-key
SQS_INTELLIGENCE_ORCHESTRATION_URL=https://sqs.ap-northeast-2.amazonaws.com/000000000000/dummy
SQS_INTELLIGENCE_DESCRIPTION_ANALYSIS_URL=https://sqs.ap-northeast-2.amazonaws.com/000000000000/dummy
SQS_INTELLIGENCE_OPTION_ANALYSIS_URL=https://sqs.ap-northeast-2.amazonaws.com/000000000000/dummy
SQS_INTELLIGENCE_NOTICE_ANALYSIS_URL=https://sqs.ap-northeast-2.amazonaws.com/000000000000/dummy
SQS_INTELLIGENCE_AGGREGATION_URL=https://sqs.ap-northeast-2.amazonaws.com/000000000000/dummy
```

#### Prod 환경변수

```bash
DB_HOST=127.0.0.1
DB_PORT=13307
DB_NAME=market
DB_USERNAME=admin
DB_PASSWORD=<메모리에서 조회>
LEGACY_DB_NAME=market
LEGACY_DB_USERNAME=admin
LEGACY_DB_PASSWORD=<메모리에서 조회>
REDIS_HOST=127.0.0.1
REDIS_PORT=16380
# 나머지 동일
```

### 3단계: Gradle bootRun 실행

```bash
<환경변수들> ./gradlew :bootstrap:bootstrap-<모듈>:bootRun \
  --args='--spring.profiles.active=<환경> --spring.jpa.hibernate.ddl-auto=none --spring.flyway.enabled=false'
```

**모듈 → Gradle 경로 매핑:**

| 모듈 | Gradle 경로 | 서버 포트 |
|------|-----------|---------|
| scheduler | `:bootstrap:bootstrap-scheduler` | 8083 |
| web-api | `:bootstrap:bootstrap-web-api` | 8080 |
| legacy-api | `:bootstrap:bootstrap-legacy-api` | 8081 |
| worker | `:bootstrap:bootstrap-worker` | 8082 |

### 4단계: 기동 확인

1. `Started *Application` 로그 확인 (성공)
2. 에러 발생 시:
   - `missing column` → Flyway 마이그레이션 필요 (flyway.enabled=true로 재실행)
   - `serviceToken must be set` → 해당 환경변수 더미값 추가
   - `Connection refused` Redis → Redis 포트포워딩 확인
   - `Cannot create PoolableConnectionFactory` → RDS 포트포워딩 확인

### 주의사항

- **Flyway**: 기본 비활성화. 새 마이그레이션 적용이 필요하면 `--spring.flyway.enabled=true` 추가
- **Hibernate validation**: `ddl-auto=none`으로 스킵 (stage DB 스키마와 로컬 코드가 다를 수 있음)
- **SQS/외부 서비스**: 더미 URL이므로 SQS 발행, FileFlow 업로드 등은 실패함. 스케줄러의 폴링/DB 작업은 정상 동작.
- **Prod 환경**: 읽기 전용으로만 사용. 절대 쓰기 작업 실행 금지.
