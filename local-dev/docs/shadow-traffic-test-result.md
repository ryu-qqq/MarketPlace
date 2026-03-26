# Shadow Traffic 비교 테스트 결과

- 작성일: 2026-03-25
- MarketPlace legacy-api: `localhost:8081`
- setof-legacy-web-api-admin: `localhost:48083` (Docker)
- DB: Stage RDS (market + luxurydb on 13308)
- Redis: Docker marketplace-redis (6379)

---

## 테스트 환경 구성

### 해결한 문제
1. **MainJpaConfig legacy 엔티티 충돌**: `PersistenceManagedTypes`에서 legacy 패키지 제외
2. **Legacy QueryDSL @Qualifier 누락**: 3개 Repository에 `legacyJpaQueryFactory` 지정
3. **setof-legacy Docker Redis**: `REDIS_HOST=host.docker.internal`로 호스트 Redis 연결

### 필요 환경변수 (bootstrap-legacy-api)
```
LEGACY_DB_ENABLED=true
LEGACY_DB_HOST=127.0.0.1
LEGACY_DB_PORT=13308
LEGACY_DB_NAME=luxurydb
DB_HOST=127.0.0.1 / DB_PORT=13308 / DB_NAME=market
REDIS_HOST=127.0.0.1 / REDIS_PORT=6379
LEGACY_TOKEN_SECRET=<JWT_SECRET>
FILEFLOW_BASE_URL=http://localhost:9999 (dummy)
AUTHHUB_BASE_URL=http://localhost:9999 (dummy)
```

---

## 비교 결과

### Phase 1: GET 조회 비교

| API | MarketPlace | setof-legacy | 일치 | 비고 |
|---|---|---|---|---|
| 셀러 조회 | 200 ✅ | 200 ✅ | ❌ 구조 다름 | MP: sellerId/sellerName/bizNo, SF: email/passwordHash/roleType |
| 주문 목록 | 200 ✅ | 500 ❌ | - | SF: HV000028 Hibernate Validator 예외 |
| 상품 목록 | 404 (데이터 없음) | 500 ❌ | - | SF: 같은 HV000028 |
| QnA 목록 | 400 | 500 ❌ | - | SF: 같은 HV000028 |
| 배송사 코드 | 200 ✅ | 400 ❌ | - | SF: Redis 캐시 'shipmentCodes' 미등록 |

### Phase 2: Shadow POST

| API | 결과 | 비고 |
|---|---|---|
| 주문 수정 | 405 | POST가 아니라 PUT이어야 함 — 스크립트 수정 필요 |

---

## 세토프 레거시 Docker 이슈 (세토프팀 전달)

### 1. HV000028: Hibernate Validator isValid 예외 (주문/상품/QnA)

```json
{
  "status": 500,
  "message": "HV000028: Unexpected exception during isValid call.",
  "error": "UnExpectedException"
}
```

- **영향**: 주문/상품/QnA 목록 조회 전부 500
- **추정 원인**: Docker JVM timezone 설정 또는 날짜 필드 validation 문제
- **재현**: `GET /api/v1/orders?page=0&size=5` (인증 토큰 포함)

### 2. Redis 캐시 미등록 (배송사 코드)

```json
{
  "status": 400,
  "message": "Cannot find cache named 'shipmentCodes' for Builder[...]"
}
```

- **영향**: 배송사 코드 조회 실패
- **원인**: Redis 캐시 매니저에 'shipmentCodes' 캐시가 등록되지 않음
- **재현**: `GET /api/v1/shipment/company-codes`

### 3. 셀러 조회 응답 구조 차이

MarketPlace:
```json
{"sellerId": 1, "sellerName": "admin", "bizNo": "1091359166"}
```

setof-legacy:
```json
{"sellerId": 1, "email": "e2e@test.com", "passwordHash": "$2b$...", "roleType": "SELLER", "approvalStatus": "APPROVED"}
```

- **원인**: 양쪽이 다른 쿼리/테이블 조회 — MarketPlace는 seller 테이블, setof는 administrators 테이블 기반
- **판단 필요**: 어느 쪽 응답이 맞는지 합의 필요

---

## MarketPlace 측 수정 완료

- `MainJpaConfig`: legacy 엔티티 제외 필터 추가 (multi-datasource 공존)
- Legacy QueryDSL Repository: `@Qualifier("legacyJpaQueryFactory")` 3개 추가
- `bootstrap-legacy-api` 로컬 기동 성공 + 인증 토큰 발급 확인

---

## 재현 방법

```bash
# 1. Stage RDS 포트포워딩
bash local-dev/scripts/aws-port-forward-stage.sh &

# 2. Redis
docker start marketplace-redis

# 3. setof-legacy Docker (JWT_SECRET + REDIS_HOST 필수)
docker run -d --name setof-legacy-web-api-admin \
  --add-host=host.docker.internal:host-gateway \
  -p 48083:8080 \
  -e SPRING_PROFILES_ACTIVE=stage \
  -e DB_HOST=host.docker.internal -e DB_PORT=13308 \
  -e DB_NAME=setof -e DB_USERNAME=admin -e "DB_PASSWORD=..." \
  -e REDIS_HOST=host.docker.internal -e REDIS_PORT=6379 \
  -e JWT_SECRET=... \
  -e AWS_ACCESS_KEY=dummy -e AWS_SECRET_KEY=dummy \
  -e KAKAO_CLIENT_ID=dummy -e KAKAO_CLIENT_SECRET=dummy \
  -e PORTONE_ENABLED=false -e PORTONE_API_KEY=dummy -e PORTONE_API_SECRET=dummy \
  -e SLACK_TOKEN=dummy -e SECURITY_SERVICE_TOKEN_SECRET=dummy \
  setof-commerce-aws-legacy-web-api-admin:latest

# 4. MarketPlace legacy-api
LEGACY_DB_ENABLED=true LEGACY_DB_HOST=127.0.0.1 LEGACY_DB_PORT=13308 \
LEGACY_DB_NAME=luxurydb ... \
./gradlew :bootstrap:bootstrap-legacy-api:bootRun -x test

# 5. Shadow 비교 테스트
bash local-dev/scripts/shadow-compare-test.sh
```
