# Settlement E2E 테스트 시나리오 설계

## 1. 입력 분석

- api-endpoints 문서: 없음 (소스 코드 직접 분석)
- api-flow 문서: 없음 (소스 코드 직접 분석)
- Controller 분석: SettlementQueryController, SettlementCommandController
- Query 엔드포인트: 2개
- Command 엔드포인트: 5개

---

## 2. 엔드포인트 목록

### Base Path
```
GET/POST /api/v1/market/settlements
```

### Query 엔드포인트

| ID  | Method | Path                          | 설명                  |
|-----|--------|-------------------------------|-----------------------|
| Q1  | GET    | /settlements                  | 정산 원장 목록 페이징 조회 |
| Q2  | GET    | /settlements/daily            | 일별 정산 내역 조회      |

### Command 엔드포인트

| ID  | Method | Path                              | 설명                      |
|-----|--------|-----------------------------------|---------------------------|
| C1  | POST   | /settlements/{settlementId}/hold  | 개별 정산 보류              |
| C2  | POST   | /settlements/{settlementId}/release | 보류된 정산 해제           |
| C3  | POST   | /settlements/complete/batch       | 정산 원장 일괄 완료 (CONFIRMED) |
| C4  | POST   | /settlements/hold/batch           | 정산 원장 일괄 보류 (HOLD)  |
| C5  | POST   | /settlements/release/batch        | 정산 원장 일괄 보류 해제 (PENDING 복원) |

---

## 3. 도메인 상태 전이 규칙

### Settlement 상태 전이
```
CALCULATING  →  CONFIRMED         (confirm)
CONFIRMED    →  PAYOUT_REQUESTED  (requestPayout)
PAYOUT_REQUESTED → COMPLETED      (complete)
CALCULATING/CONFIRMED → HOLD      (hold) ← API 노출
HOLD         → CALCULATING        (releaseHold) ← API 노출
```

### SettlementEntry 상태 전이
```
PENDING   → CONFIRMED   (confirm / completeBatch) ← API 노출
PENDING   → HOLD        (hold / holdBatch)         ← API 노출
HOLD      → PENDING     (release / releaseBatch)   ← API 노출
CONFIRMED → SETTLED     (내부 배치, API 미노출)
```

### 상태 전이 위반 → 에러 코드
| 에러 코드       | HTTP | 설명                      |
|----------------|------|---------------------------|
| STL-001        | 404  | 정산 없음                 |
| STL-002        | 400  | 유효하지 않은 상태 전이   |
| STL-006        | 400  | 보류 사유 필수             |
| STE-001        | 404  | 정산 원장 없음             |
| STE-002        | 400  | 유효하지 않은 원장 상태 전이 |

---

## 4. 시나리오 설계

### Q1: GET /settlements - 정산 원장 목록 페이징 조회

**Request 파라미터:**
- `status` (List, 선택): PENDING, HOLD, CONFIRMED
- `sellerIds` (List, 선택): 셀러 ID 목록 필터
- `searchField` (선택): ORDER_ID, ORDER_NUMBER, PRODUCT_NAME, BUYER_NAME
- `searchWord` (선택): 검색어
- `startDate` / `endDate` (선택): 날짜 범위 (YYYY-MM-DD)
- `page` (@NotNull): 페이지 번호
- `size` (@NotNull): 페이지 크기

**Validation:** page, size = @NotNull

---

#### [Q1-1] 데이터 없을 때 빈 페이지 반환 (P0)
- 사전 데이터: 없음
- Request: `GET /settlements?page=0&size=10`
- Expected: 200, `data.content` 빈 배열, `data.totalElements=0`

#### [Q1-2] 데이터 존재 시 정상 페이징 조회 (P0)
- 사전 데이터: SettlementEntryJpaEntity 3건 (PENDING, HOLD, CONFIRMED)
- Request: `GET /settlements?page=0&size=10`
- Expected: 200, `data.content.size=3`, `data.totalElements=3`
- 응답 필드 검증: `settlementId`, `status`, `orderId`, `sellerId`, `amounts`, `expectedSettlementDay`

#### [Q1-3] page, size 파라미터 페이징 동작 확인 (P1)
- 사전 데이터: SettlementEntryJpaEntity 5건 (PENDING)
- Request: `GET /settlements?page=0&size=2`
- Expected: 200, `data.content.size=2`, `data.totalElements=5`

#### [Q1-4] status 필터 - PENDING만 조회 (P1)
- 사전 데이터: PENDING 2건, HOLD 1건, CONFIRMED 1건
- Request: `GET /settlements?status=PENDING&page=0&size=10`
- Expected: 200, `data.content.size=2`

#### [Q1-5] status 필터 - 복합 상태 (PENDING, HOLD) 조회 (P1)
- 사전 데이터: PENDING 2건, HOLD 1건, CONFIRMED 1건
- Request: `GET /settlements?status=PENDING&status=HOLD&page=0&size=10`
- Expected: 200, `data.content.size=3`

#### [Q1-6] sellerIds 필터 동작 확인 (P1)
- 사전 데이터: sellerId=1L 2건, sellerId=2L 2건
- Request: `GET /settlements?sellerIds=1&page=0&size=10`
- Expected: 200, `data.content.size=2`, 모든 항목 `sellerId=1`

#### [Q1-7] page 누락 시 400 (P0)
- Request: `GET /settlements?size=10`
- Expected: 400

#### [Q1-8] size 누락 시 400 (P0)
- Request: `GET /settlements?page=0`
- Expected: 400

#### [Q1-9] 권한 없는 사용자 - 403 (P1)
- 인증: `settlement:write` 권한만 있는 사용자
- Request: `GET /settlements?page=0&size=10`
- Expected: 403

#### [Q1-10] 비인증 요청 - 401 (P1)
- Request: `GET /settlements?page=0&size=10` (인증 헤더 없음)
- Expected: 401

---

### Q2: GET /settlements/daily - 일별 정산 내역 조회

**Request 파라미터:**
- `startDate` (@NotNull): 시작일 (YYYY-MM-DD)
- `endDate` (@NotNull): 종료일 (YYYY-MM-DD)
- `sellerIds` (선택): 셀러 ID 목록
- `page` (@NotNull): 페이지 번호
- `size` (@NotNull): 페이지 크기

---

#### [Q2-1] 유효한 날짜 범위로 정상 조회 - 데이터 없을 때 (P0)
- 사전 데이터: 없음
- Request: `GET /settlements/daily?startDate=2026-03-01&endDate=2026-03-31&page=0&size=10`
- Expected: 200, `data.content` 빈 배열 또는 0건

#### [Q2-2] 유효한 날짜 범위로 정상 조회 - 데이터 있을 때 (P0)
- 사전 데이터: SettlementJpaEntity COMPLETED 2건 (각기 다른 날짜)
- Request: `GET /settlements/daily?startDate=2026-03-01&endDate=2026-03-31&page=0&size=10`
- Expected: 200, 날짜별 집계 결과 반환, `settlementDay`, `totalSalesAmount`, `fee` 등 포함

#### [Q2-3] startDate 누락 시 400 (P0)
- Request: `GET /settlements/daily?endDate=2026-03-31&page=0&size=10`
- Expected: 400

#### [Q2-4] endDate 누락 시 400 (P0)
- Request: `GET /settlements/daily?startDate=2026-03-01&page=0&size=10`
- Expected: 400

#### [Q2-5] sellerIds 필터 동작 확인 (P1)
- 사전 데이터: sellerId=1L 2건 COMPLETED, sellerId=2L 2건 COMPLETED
- Request: `GET /settlements/daily?startDate=2026-03-01&endDate=2026-03-31&sellerIds=1&page=0&size=10`
- Expected: 200, sellerId=1 기준 집계만 반환

#### [Q2-6] 권한 없는 사용자 - 403 (P1)
- 인증: `settlement:write` 권한만 있는 사용자
- Request: `GET /settlements/daily?startDate=2026-03-01&endDate=2026-03-31&page=0&size=10`
- Expected: 403

#### [Q2-7] 비인증 요청 - 401 (P1)
- Expected: 401

---

### C1: POST /settlements/{settlementId}/hold - 개별 정산 보류

**Request Body:**
```json
{ "reason": "이상 거래 의심으로 인한 보류" }
```

**Validation:** reason = @NotBlank

---

#### [C1-1] CALCULATING 상태 정산 보류 성공 (P0)
- 사전 데이터: SettlementJpaEntity CALCULATING 1건 (id="stl-hold-calc-001")
- Request: `POST /settlements/stl-hold-calc-001/hold` + `{ "reason": "이상 거래 의심" }`
- Expected: 200
- DB 검증: `settlementJpaRepository.findById("stl-hold-calc-001").getSettlementStatus() == "HOLD"`, `holdReason` 값 확인

#### [C1-2] CONFIRMED 상태 정산 보류 성공 (P0)
- 사전 데이터: SettlementJpaEntity CONFIRMED 1건 (id="stl-hold-conf-001")
- Request: `POST /settlements/stl-hold-conf-001/hold` + `{ "reason": "이상 거래 의심" }`
- Expected: 200
- DB 검증: status = "HOLD", holdReason 설정 확인

#### [C1-3] reason 누락 시 400 (P0)
- 사전 데이터: SettlementJpaEntity CALCULATING 1건
- Request: `POST /settlements/{id}/hold` + `{ "reason": "" }` 또는 reason 필드 없음
- Expected: 400

#### [C1-4] 존재하지 않는 settlementId - 404 (P0)
- Request: `POST /settlements/non-existent-id/hold` + `{ "reason": "테스트" }`
- Expected: 404

#### [C1-5] HOLD 상태 정산 재보류 시 상태 전이 오류 - 400 (P1)
- 사전 데이터: SettlementJpaEntity HOLD 1건 (id="stl-hold-already-001")
- Request: `POST /settlements/stl-hold-already-001/hold` + `{ "reason": "재보류 시도" }`
- Expected: 400 (INVALID_STATUS_TRANSITION)

#### [C1-6] COMPLETED 상태 정산 보류 시도 - 400 (P1)
- 사전 데이터: SettlementJpaEntity COMPLETED 1건 (id="stl-hold-complete-001")
- Request: `POST /settlements/stl-hold-complete-001/hold` + `{ "reason": "테스트" }`
- Expected: 400 (INVALID_STATUS_TRANSITION)

#### [C1-7] 권한 없는 사용자 - 403 (P1)
- 인증: `settlement:read` 권한만 있는 사용자
- Expected: 403

#### [C1-8] 비인증 요청 - 401 (P1)
- Expected: 401

---

### C2: POST /settlements/{settlementId}/release - 개별 정산 보류 해제

**Request Body:** 없음

---

#### [C2-1] HOLD 상태 정산 보류 해제 성공 (P0)
- 사전 데이터: SettlementJpaEntity HOLD 1건 (id="stl-release-001")
- Request: `POST /settlements/stl-release-001/release`
- Expected: 200
- DB 검증: `settlementJpaRepository.findById("stl-release-001").getSettlementStatus() == "CALCULATING"`, `holdReason == null`

#### [C2-2] 존재하지 않는 settlementId - 404 (P0)
- Request: `POST /settlements/non-existent-id/release`
- Expected: 404

#### [C2-3] CALCULATING 상태 정산 해제 시도 - 400 (P1)
- 사전 데이터: SettlementJpaEntity CALCULATING 1건 (id="stl-release-calc-001")
- Request: `POST /settlements/stl-release-calc-001/release`
- Expected: 400 (INVALID_STATUS_TRANSITION: CALCULATING → CALCULATING 불가)

#### [C2-4] 권한 없는 사용자 - 403 (P1)
- 인증: `settlement:read` 권한만 있는 사용자
- Expected: 403

#### [C2-5] 비인증 요청 - 401 (P1)
- Expected: 401

---

### C3: POST /settlements/complete/batch - 정산 원장 일괄 완료 (CONFIRMED)

**Request Body:**
```json
{ "settlementIds": ["entry-id-001", "entry-id-002"] }
```

**Validation:** settlementIds = @NotEmpty

**비즈니스 규칙:** Entry 상태가 PENDING인 경우만 CONFIRMED 전이 가능

---

#### [C3-1] PENDING 상태 Entry 일괄 완료 성공 (P0)
- 사전 데이터: SettlementEntryJpaEntity PENDING 2건 (id="entry-complete-001", "entry-complete-002")
- Request: `POST /settlements/complete/batch` + `{ "settlementIds": ["entry-complete-001", "entry-complete-002"] }`
- Expected: 200
- DB 검증: 두 Entry의 `entryStatus == "CONFIRMED"`

#### [C3-2] settlementIds 빈 배열 - 400 (P0)
- Request: `POST /settlements/complete/batch` + `{ "settlementIds": [] }`
- Expected: 400

#### [C3-3] settlementIds 누락 - 400 (P0)
- Request: `POST /settlements/complete/batch` + `{}`
- Expected: 400

#### [C3-4] HOLD 상태 Entry 완료 시도 - 400 (P1)
- 사전 데이터: SettlementEntryJpaEntity HOLD 1건 (id="entry-complete-hold-001")
- Request: `POST /settlements/complete/batch` + `{ "settlementIds": ["entry-complete-hold-001"] }`
- Expected: 400 (STE-002: INVALID_STATUS_TRANSITION, HOLD → CONFIRMED 불가)

#### [C3-5] 일부 존재하지 않는 ID 포함 시 처리 (P2)
- 사전 데이터: Entry 1건만 존재
- Request: `POST /settlements/complete/batch` + `{ "settlementIds": ["existing-id", "non-existent-id"] }`
- Expected: 동작 방식 확인 (전체 실패 또는 부분 성공 - 구현에 따라 기록)

#### [C3-6] 권한 없는 사용자 - 403 (P1)
- 인증: `settlement:read` 권한만 있는 사용자
- Expected: 403

#### [C3-7] 비인증 요청 - 401 (P1)
- Expected: 401

---

### C4: POST /settlements/hold/batch - 정산 원장 일괄 보류 (HOLD)

**Request Body:**
```json
{ "settlementIds": ["entry-id-001", "entry-id-002"], "holdReason": "이상 거래 의심" }
```

**Validation:** settlementIds = @NotEmpty, holdReason = @NotBlank

**비즈니스 규칙:** Entry 상태가 PENDING인 경우만 HOLD 전이 가능

---

#### [C4-1] PENDING 상태 Entry 일괄 보류 성공 (P0)
- 사전 데이터: SettlementEntryJpaEntity PENDING 2건 (id="entry-hold-001", "entry-hold-002")
- Request: `POST /settlements/hold/batch` + `{ "settlementIds": ["entry-hold-001", "entry-hold-002"], "holdReason": "이상 거래" }`
- Expected: 200
- DB 검증: 두 Entry의 `entryStatus == "HOLD"`

#### [C4-2] settlementIds 빈 배열 - 400 (P0)
- Request: `POST /settlements/hold/batch` + `{ "settlementIds": [], "holdReason": "이상 거래" }`
- Expected: 400

#### [C4-3] holdReason 누락 - 400 (P0)
- Request: `POST /settlements/hold/batch` + `{ "settlementIds": ["entry-hold-001"] }`
- Expected: 400

#### [C4-4] holdReason 빈 문자열 - 400 (P0)
- Request: `POST /settlements/hold/batch` + `{ "settlementIds": ["entry-hold-001"], "holdReason": "" }`
- Expected: 400

#### [C4-5] CONFIRMED 상태 Entry 보류 시도 - 400 (P1)
- 사전 데이터: SettlementEntryJpaEntity CONFIRMED 1건 (id="entry-hold-conf-001")
- Request: `POST /settlements/hold/batch` + `{ "settlementIds": ["entry-hold-conf-001"], "holdReason": "테스트" }`
- Expected: 400 (STE-002: INVALID_STATUS_TRANSITION, CONFIRMED → HOLD 불가)

#### [C4-6] 권한 없는 사용자 - 403 (P1)
- Expected: 403

#### [C4-7] 비인증 요청 - 401 (P1)
- Expected: 401

---

### C5: POST /settlements/release/batch - 정산 원장 일괄 보류 해제 (PENDING 복원)

**Request Body:**
```json
{ "settlementIds": ["entry-id-001", "entry-id-002"] }
```

**Validation:** settlementIds = @NotEmpty

**비즈니스 규칙:** Entry 상태가 HOLD인 경우만 PENDING 전이 가능

---

#### [C5-1] HOLD 상태 Entry 일괄 보류 해제 성공 (P0)
- 사전 데이터: SettlementEntryJpaEntity 상태 "HOLD" 2건 (id="entry-release-001", "entry-release-002")
- Request: `POST /settlements/release/batch` + `{ "settlementIds": ["entry-release-001", "entry-release-002"] }`
- Expected: 200
- DB 검증: 두 Entry의 `entryStatus == "PENDING"`

#### [C5-2] settlementIds 빈 배열 - 400 (P0)
- Request: `POST /settlements/release/batch` + `{ "settlementIds": [] }`
- Expected: 400

#### [C5-3] settlementIds 누락 - 400 (P0)
- Request: `POST /settlements/release/batch` + `{}`
- Expected: 400

#### [C5-4] PENDING 상태 Entry 해제 시도 - 400 (P1)
- 사전 데이터: SettlementEntryJpaEntity PENDING 1건 (id="entry-release-pending-001")
- Request: `POST /settlements/release/batch` + `{ "settlementIds": ["entry-release-pending-001"] }`
- Expected: 400 (STE-002: INVALID_STATUS_TRANSITION, PENDING → PENDING 불가)

#### [C5-5] 권한 없는 사용자 - 403 (P1)
- Expected: 403

#### [C5-6] 비인증 요청 - 401 (P1)
- Expected: 401

---

## 5. 전체 플로우 시나리오

### [FLOW-1] 정산 보류 → 해제 플로우 (Settlement 단위) (P0)

```
1. CALCULATING 상태 Settlement 저장
2. POST /{id}/hold + reason → 200 확인
3. DB 검증: status=HOLD, holdReason 설정됨
4. POST /{id}/release → 200 확인
5. DB 검증: status=CALCULATING, holdReason=null
```

### [FLOW-2] 정산 원장 일괄 보류 → 해제 → 완료 플로우 (P0)

```
1. PENDING 상태 SettlementEntry 2건 저장
2. POST /hold/batch + 두 entry ID → 200
3. DB 검증: 두 Entry status=HOLD
4. POST /release/batch + 두 entry ID → 200
5. DB 검증: 두 Entry status=PENDING
6. POST /complete/batch + 두 entry ID → 200
7. DB 검증: 두 Entry status=CONFIRMED
```

### [FLOW-3] 목록 조회 → 필터 조회 연계 (P1)

```
1. PENDING 2건, HOLD 1건, CONFIRMED 1건 저장
2. GET /settlements?page=0&size=10 → totalElements=4 확인
3. GET /settlements?status=PENDING&page=0&size=10 → content.size=2 확인
4. GET /settlements?status=HOLD&page=0&size=10 → content.size=1 확인
```

---

## 6. Fixture 설계

### 필요 Repository

| Repository | 용도 |
|---|---|
| `SettlementJpaRepository` | Settlement 보류/해제 테스트 사전 데이터 |
| `SettlementEntryJpaRepository` | Entry 목록 조회, 일괄 처리 사전 데이터 |

### testFixtures 현황

| Fixtures 클래스 | 위치 |
|---|---|
| `SettlementJpaEntityFixtures` | `adapter-out/persistence-mysql/src/testFixtures` |
| `SettlementEntryJpaEntityFixtures` | `adapter-out/persistence-mysql/src/testFixtures` |

### 주요 Fixtures 메서드

**SettlementJpaEntityFixtures:**
- `calculatingEntity()` - CALCULATING 상태 (기본 ID)
- `calculatingEntity(String id)` - CALCULATING 상태 (ID 지정)
- `confirmedEntity()` - CONFIRMED 상태
- `holdEntity()` - HOLD 상태
- `completedEntity()` - COMPLETED 상태
- `entityWithStatus(String id, String status)` - 상태 지정
- `calculatingEntityWithSeller(String id, long sellerId)` - 셀러 ID 지정

**SettlementEntryJpaEntityFixtures:**
- `salesPendingEntity()` - SALES 타입, PENDING 상태 (기본 ID)
- `salesPendingEntity(String id)` - PENDING 상태 (ID 지정)
- `salesConfirmedEntity()` - CONFIRMED 상태
- `salesSettledEntity()` - SETTLED 상태
- `entityWithStatus(String id, String status)` - 상태 지정
- `pendingEntityWithOrderItemId(String id, String orderItemId)` - orderItemId 지정

### HOLD 상태 Entry Fixture 미존재 → 직접 생성 필요

```java
// HOLD 상태 Entry는 현재 Fixture에 없음 → entityWithStatus() 사용
SettlementEntryJpaEntityFixtures.entityWithStatus("entry-hold-001", "HOLD")
```

### setUp 패턴

```java
@BeforeEach
void setUp() {
    settlementEntryJpaRepository.deleteAll();
    settlementJpaRepository.deleteAll();
}

@AfterEach
void tearDown() {
    settlementEntryJpaRepository.deleteAll();
    settlementJpaRepository.deleteAll();
}
```

---

## 7. 인증 컨텍스트 정리

| 컨텍스트 | 메서드 | 필요 권한 |
|---|---|---|
| 정상 조회 | `givenSuperAdmin()` | `settlement:read` (*:* 포함) |
| 정상 수정 | `givenSuperAdmin()` | `settlement:write` (*:* 포함) |
| 권한 없는 조회 | `givenWithPermission("order:read")` | settlement:read 없음 → 403 |
| 권한 없는 수정 | `givenWithPermission("settlement:read")` | settlement:write 없음 → 403 |
| 비인증 | `givenUnauthenticated()` | 없음 → 401 |

---

## 8. 시나리오 통계

| 분류 | 개수 |
|---|---|
| Q1 (정산 원장 목록 조회) | 10개 |
| Q2 (일별 정산 조회) | 7개 |
| C1 (개별 보류) | 8개 |
| C2 (개별 해제) | 5개 |
| C3 (일괄 완료) | 7개 |
| C4 (일괄 보류) | 7개 |
| C5 (일괄 해제) | 6개 |
| FLOW | 3개 |
| **합계** | **53개** |

| 우선순위 | 개수 |
|---|---|
| P0 | 33개 |
| P1 | 18개 |
| P2 | 2개 |

---

## 9. 주의사항

1. **Entry ID와 settlementId 혼용 주의**: `SettlementCommandController`의 배치 API는 내부적으로 `SettlementEntry` ID를 받지만 파라미터명이 `settlementIds`임. 실제 SettlementEntry의 ID를 넘겨야 함.

2. **V4 API 간극 규칙**: 응답의 `orderId` 필드는 내부 `orderItemId`에 매핑됨. 조회 응답 검증 시 `orderId` 필드 사용.

3. **날짜 형식**: `startDate`, `endDate`는 `YYYY-MM-DD` 형식. 잘못된 형식 입력 시 별도 400 응답 케이스 추가 가능.

4. **일별 정산 집계**: `GET /settlements/daily`는 `SettlementEntry`가 아닌 `Settlement` 집계 기반. `COMPLETED` 상태 Settlement 데이터 사전 설정 필요.

---

## 10. 다음 단계

```bash
/test-e2e admin:settlement
```

테스트 클래스 생성 대상:
- `SettlementQueryE2ETest.java`
- `SettlementCommandE2ETest.java`
- `SettlementFlowE2ETest.java`
