# Shadow Traffic 검증 시스템 — 전체 오버뷰

## 시스템 구성

| 시스템 | 역할 | 담당 |
|--------|------|------|
| **Gateway** | Shadow 라우팅 + SQS 발행 | Gateway 팀 |
| **MarketPlace** (Stage, 8081) | POST/PUT/PATCH 트랜잭션 롤백 + 스냅샷 | MarketPlace 팀 |
| **Shadow Lambda** | 비교 + 결과 저장 | MarketPlace 팀 |
| **Redis** | 스냅샷 저장소 (TTL 10분) | 인프라 (기존) |
| **DynamoDB** | 비교 결과 저장 (TTL 7일) | 인프라 (기존) |

## 전체 흐름

### GET

```
셀러 → Gateway → 레거시 서버 (응답)
              → SQS 발행 (메타데이터)
              → Lambda → 레거시 + Stage 동시 호출 → deepdiff 비교
                      → DynamoDB 저장 + CloudWatch 메트릭
```

### POST /auth/authentication (토큰 발급 — GET과 동일 취급)

```
셀러 → Gateway → 레거시 서버 (응답)
              → SQS 발행 (메타데이터, correlationId 없음)
              → Lambda → 레거시 + Stage 동시 호출 → deepdiff 비교
                      → DynamoDB 저장 + CloudWatch 메트릭

※ DB write가 없으므로 Stage 선행 호출/롤백/스냅샷 불필요
```

### POST/PUT/PATCH (auth 제외)

```
셀러 → Gateway
         │
         ├─① Stage (8081) 호출 [X-Shadow-Mode: verify, X-Shadow-Correlation-Id: uuid-123]
         │    ├── ShadowTransactionFilter: TX 시작
         │    ├── 컨트롤러 → 서비스 → DB write (전체 실행)
         │    ├── 응답 캡처 → Redis 저장 (shadow:snapshot:uuid-123, TTL 10분)
         │    ├── TX 롤백 (DB 원상복구)
         │    └── Gateway에 응답 반환 (Gateway는 이 응답을 버림)
         │
         ├─② 레거시 서버 호출 (정상 처리)
         │    └── 셀러에게 응답 반환
         │
         └─③ SQS 발행 (메타데이터 + correlationId: uuid-123)
              │
              └─④ Lambda 수신
                   ├── Redis에서 스냅샷 조회 (shadow:snapshot:uuid-123)
                   ├── DMS 복제 대기 (7초)
                   ├── Stage GET API 호출 (복제된 데이터 조회)
                   ├── 스냅샷 응답 vs GET 응답 deepdiff 비교
                   └── DynamoDB 저장 + CloudWatch 메트릭
```

## 작업 문서

| 문서 | 대상 | 위치 |
|------|------|------|
| Gateway 작업 명세 | Gateway 팀 | `docs/shadow-traffic-gateway-spec.md` |
| Lambda 작업 명세 | MarketPlace 팀 | `docs/shadow-traffic-lambda-spec.md` |

## 단계별 일정

| 순서 | 작업 | 담당 | 의존성 |
|------|------|------|--------|
| 1 | MarketPlace: bootstrap-legacy-api + ShadowTransactionFilter Stage 배포 | MarketPlace | 없음 |
| 2 | Gateway: ShadowPreMirrorFilter + correlationId 추가 | Gateway | Step 1 |
| 3 | Lambda: GET dry-run 해제 (실제 호출) | MarketPlace | 없음 |
| 4 | Lambda: Redis 클라이언트 + POST/PUT/PATCH 처리 | MarketPlace | Step 1, 2 |
| 5 | 통합 테스트 (10% 트래픽) | 양 팀 | Step 2, 4 |
| 6 | 트래픽 비율 확대 (50% → 100%) | 양 팀 | Step 5 |
| 7 | 불일치율 0% 확인 → 트래픽 전환 결정 | 양 팀 | Step 6 |
