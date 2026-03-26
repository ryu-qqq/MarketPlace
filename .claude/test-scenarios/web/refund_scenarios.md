# Refund E2E 통합 테스트 시나리오

## 요약

- 대상: `web:refund` (환불 클레임)
- Query 시나리오: 12개
- Command 시나리오: 20개
- 전체 플로우 시나리오: 5개
- 총합: 37개 (P0: 24개, P1: 11개, P2: 2개)

---

## 분석 결과

### 엔드포인트 목록

| 구분 | Method | Path | UseCase |
|------|--------|------|---------|
| Q09 | GET | /api/v1/market/refunds/summary | GetRefundSummaryUseCase |
| Q10 | GET | /api/v1/market/refunds | GetRefundListUseCase |
| Q11 | GET | /api/v1/market/refunds/{refundClaimId} | GetRefundDetailUseCase |
| C14 | POST | /api/v1/market/refunds/request/batch | RequestRefundBatchUseCase |
| C15 | POST | /api/v1/market/refunds/approve/batch | ApproveRefundBatchUseCase |
| C16 | POST | /api/v1/market/refunds/reject/batch | RejectRefundBatchUseCase |
| C17 | PATCH | /api/v1/market/refunds/hold/batch | HoldRefundBatchUseCase |
| C18 | POST | /api/v1/market/refunds/{refundClaimId}/histories | AddClaimHistoryMemoUseCase |

### V4 간극 규칙 (oms_flows.md)

- `orderId` 필드 = 내부 `orderItemId` (프론트 V4 간극)
- null 값 → `""` 으로 직렬화
- null 금액 → `0`

### RefundStatus 상태 전이 규칙 (RefundStatus.java)

```
REQUESTED  → COLLECTING  (collectible: approve 동작)
COLLECTING → COLLECTED   (collection_completable)
COLLECTED  → COMPLETED   (completable)
REQUESTED/COLLECTING/COLLECTED → REJECTED  (rejectable)
REQUESTED/COLLECTING           → CANCELLED (cancellable)
```

### hold() 도메인 규칙 (RefundClaim.java)

- `holdReason == null || blank` → 기본값 `"보류 처리"` 적용
- 이미 보류 상태에서 hold() 호출 → `ALREADY_HOLD` 예외
- holdInfo == null 상태에서 releaseHold() 호출 → `NOT_HOLD_STATUS` 예외

### Validation 규칙 (Request DTO)

**RequestRefundBatchApiRequest:**
- `items`: `@NotEmpty` - 빈 목록 불가
- `items[].orderId`: `@NotBlank` - 공백 불가
- `items[].refundQty`: `@Positive` - 1 이상
- `items[].reasonType`: `@NotBlank` - 공백 불가
- `items[].reasonDetail`: 선택 (optional)

**ApproveRefundBatchApiRequest / RejectRefundBatchApiRequest:**
- `refundClaimIds`: `@NotEmpty` - 빈 목록 불가

**HoldRefundBatchApiRequest:**
- `refundClaimIds`: `@NotEmpty` - 빈 목록 불가
- `isHold`: boolean (보류 여부)
- `memo`: 선택 (null 허용 → 도메인에서 기본값 처리)

**AddClaimHistoryMemoApiRequest:**
- `message`: 필수 (공통 DTO)

---

## Fixture 설계

### 필요 Repository

| Repository | 용도 |
|-----------|------|
| `OrderJpaRepository` | Order 사전 데이터 (RefundClaim은 OrderItem 필요) |
| `OrderItemJpaRepository` | OrderItem 사전 데이터 (환불 요청의 대상) |
| `RefundClaimJpaRepository` | RefundClaim 직접 시딩 (승인/거절/보류 테스트) |
| `RefundOutboxJpaRepository` | Outbox 생성 검증 및 tearDown 정리 |
| `ClaimHistoryJpaRepository` | 이력 생성 검증 및 tearDown 정리 |

### testFixtures 현황

| Fixtures 클래스 | 상태 | 위치 |
|----------------|------|------|
| `OrderJpaEntityFixtures` | 존재 | adapter-out/persistence-mysql/testFixtures |
| `OrderItemJpaEntityFixtures` | 존재 | adapter-out/persistence-mysql/testFixtures |
| `ClaimHistoryJpaEntityFixtures` | 존재 | adapter-out/persistence-mysql/testFixtures |
| `RefundClaimJpaEntityFixtures` | **미존재** | 신규 생성 필요 |

### setUp / tearDown 전략

```java
@BeforeEach
void setUp() {
    // 의존 관계 역순으로 삭제
    claimHistoryRepository.deleteAll();
    refundOutboxRepository.deleteAll();
    refundClaimRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}

@AfterEach
void tearDown() {
    claimHistoryRepository.deleteAll();
    refundOutboxRepository.deleteAll();
    refundClaimRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}
```

### 사전 데이터 시딩 패턴

```
// 환불 요청 테스트용: OrderItem 시딩 후 ID 캡처
private String seedOrderItemForRefund(String orderId) {
    OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
    orderRepository.save(order);
    OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
    // sellerId = DEFAULT_SELLER_ID(10L) - 소유권 테스트 시 주의
    return orderItemRepository.save(item).getId();
}

// 승인/거절/보류 테스트용: RefundClaim 직접 시딩
private String seedRequestedRefundClaim(String id, String orderItemId) {
    RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity(id, orderItemId, DEFAULT_SELLER_ID);
    return refundClaimRepository.save(entity).getId();
}
```

---

## Query 시나리오 (12개)

### Q09: GET /api/v1/market/refunds/summary

#### [QUERY-01] 환불 요약 조회 - 데이터 존재 시 정상 반환

- **우선순위**: P0
- **사전 데이터**: 다양한 상태의 RefundClaim 직접 시딩 (REQUESTED 2건, COLLECTING 1건, COMPLETED 1건, REJECTED 1건)
- **요청**: `GET /refunds/summary` (SUPER_ADMIN)
- **기대 결과**: 200, `data` 존재, 각 상태 카운트 포함
- **검증 포인트**: `statusCode(200)`, `body("data", notNullValue())`

#### [QUERY-02] 환불 요약 조회 - 데이터 없을 때 0 카운트 반환

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /refunds/summary` (SUPER_ADMIN)
- **기대 결과**: 200, `data` 존재 (모든 카운트 0)
- **검증 포인트**: `statusCode(200)`, `body("data", notNullValue())`

---

### Q10: GET /api/v1/market/refunds

#### [QUERY-03] 환불 목록 조회 - 데이터 존재 시 목록 반환

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 3건 시딩
- **요청**: `GET /refunds?page=0&size=10` (SUPER_ADMIN)
- **기대 결과**: 200, `data.content.size() >= 3`
- **검증 포인트**: `statusCode(200)`, `body("data.content", hasSize(greaterThanOrEqualTo(3)))`

#### [QUERY-04] 환불 목록 조회 - 데이터 없을 때 빈 목록

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /refunds?page=0&size=10` (SUPER_ADMIN)
- **기대 결과**: 200, `data.content.size() == 0`
- **검증 포인트**: `statusCode(200)`, `body("data.content", empty())`

#### [QUERY-05] 환불 목록 조회 - 상태 필터 (REQUESTED)

- **우선순위**: P1
- **사전 데이터**: REQUESTED 2건, COLLECTING 1건, COMPLETED 1건 시딩
- **요청**: `GET /refunds?statuses=REQUESTED&page=0&size=10` (SUPER_ADMIN)
- **기대 결과**: 200, `data.content` 모두 `status == "REQUESTED"`
- **검증 포인트**: `statusCode(200)`, `body("data.content.status", everyItem(equalTo("REQUESTED")))`

#### [QUERY-06] 환불 목록 조회 - 페이징 동작 확인

- **우선순위**: P1
- **사전 데이터**: REQUESTED 5건 시딩
- **요청**: `GET /refunds?page=0&size=2` (SUPER_ADMIN)
- **기대 결과**: 200, `data.content.size() == 2`, `data.totalElements == 5`
- **검증 포인트**: `statusCode(200)`, `body("data.content.size()", equalTo(2))`

#### [QUERY-07] 환불 목록 조회 - 날짜 범위 필터

- **우선순위**: P1
- **사전 데이터**: REQUESTED 2건 시딩
- **요청**: `GET /refunds?dateField=REQUESTED&startDate=2020-01-01&endDate=2099-12-31&page=0&size=10`
- **기대 결과**: 200, 정상 반환
- **검증 포인트**: `statusCode(200)`, `body("data", notNullValue())`

---

### Q11: GET /api/v1/market/refunds/{refundClaimId}

#### [QUERY-08] 환불 상세 조회 - 존재하는 ID

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 1건 시딩, ID 캡처
- **요청**: `GET /refunds/{refundClaimId}` (SUPER_ADMIN)
- **기대 결과**: 200, `data.id == refundClaimId`
- **검증 포인트**: `statusCode(200)`, `body("data.id", equalTo(refundClaimId))`

#### [QUERY-09] 환불 상세 조회 - 존재하지 않는 ID

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /refunds/01900000-0000-7000-0000-000000000999` (SUPER_ADMIN)
- **기대 결과**: 404
- **검증 포인트**: `statusCode(404)`

#### [QUERY-10] 환불 상세 조회 - COMPLETED 상태 (refundInfo 포함 확인)

- **우선순위**: P1
- **사전 데이터**: COMPLETED 상태 RefundClaim 1건 시딩 (originalAmount, finalAmount 설정)
- **요청**: `GET /refunds/{refundClaimId}` (SUPER_ADMIN)
- **기대 결과**: 200, `data.status == "COMPLETED"`, refundInfo 필드 존재
- **검증 포인트**: `statusCode(200)`, `body("data.status", equalTo("COMPLETED"))`

#### [QUERY-11] 환불 상세 조회 - 보류 상태 (holdInfo 포함 확인)

- **우선순위**: P1
- **사전 데이터**: holdReason, holdAt이 설정된 RefundClaim 1건 시딩
- **요청**: `GET /refunds/{refundClaimId}` (SUPER_ADMIN)
- **기대 결과**: 200, holdInfo 관련 필드 반환
- **검증 포인트**: `statusCode(200)`, `body("data", notNullValue())`

#### [QUERY-12] 환불 상세 조회 - 비인증 요청 401

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /refunds/any-id` (비인증)
- **기대 결과**: 401
- **검증 포인트**: `statusCode(401)`

---

## Command 시나리오 (20개)

### C14: POST /api/v1/market/refunds/request/batch

#### [CMD-01] 환불 요청 배치 성공 - 단건

- **우선순위**: P0
- **사전 데이터**: OrderItem 1건 시딩 (READY 상태, sellerId=10L), orderItemId 캡처
- **요청 Body**:
  ```json
  {
    "items": [
      {
        "orderId": "{orderItemId}",
        "refundQty": 1,
        "reasonType": "CHANGE_OF_MIND",
        "reasonDetail": "단순 변심입니다"
      }
    ]
  }
  ```
- **기대 결과**: 200, `data.totalCount==1`, `data.successCount==1`, `data.failureCount==0`
- **DB 검증**: `refundClaimRepository.findAll().size() >= 1`
- **Outbox 검증**: `refundOutboxRepository.findAll().size() >= 1`
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(1))`

#### [CMD-02] 환불 요청 배치 성공 - 다건

- **우선순위**: P0
- **사전 데이터**: OrderItem 2건 시딩 (각각 다른 orderId)
- **요청 Body**:
  ```json
  {
    "items": [
      {"orderId": "{orderItemId1}", "refundQty": 1, "reasonType": "CHANGE_OF_MIND"},
      {"orderId": "{orderItemId2}", "refundQty": 2, "reasonType": "DEFECTIVE_PRODUCT"}
    ]
  }
  ```
- **기대 결과**: 200, `data.totalCount==2`, `data.successCount==2`
- **DB 검증**: `refundClaimRepository.count() == 2`

#### [CMD-03] 환불 요청 - reasonDetail 없이 요청 (optional 필드)

- **우선순위**: P1
- **사전 데이터**: OrderItem 1건 시딩
- **요청 Body**: `reasonDetail` 필드 없이 전송
- **기대 결과**: 200, 성공
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(1))`

#### [CMD-04] 환불 요청 Validation 실패 - items 빈 목록

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"items": []}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

#### [CMD-05] 환불 요청 Validation 실패 - orderId 누락

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"items": [{"refundQty": 1, "reasonType": "CHANGE_OF_MIND"}]}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

#### [CMD-06] 환불 요청 Validation 실패 - refundQty = 0 (Positive 위반)

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"items": [{"orderId": "xxx", "refundQty": 0, "reasonType": "CHANGE_OF_MIND"}]}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

#### [CMD-07] 환불 요청 - 존재하지 않는 orderItemId (부분 실패)

- **우선순위**: P1
- **사전 데이터**: OrderItem 1건 시딩 (id1)
- **요청 Body**: 유효한 id1 + 존재하지 않는 id2 (999...) 포함
- **기대 결과**: 200, `data.totalCount == 1 (or 2)`, 일부 실패 처리
- **비고**: Validator가 조회 결과 없는 항목을 어떻게 처리하는지 확인. 존재하는 1건은 성공 처리.
- **검증 포인트**: `statusCode(200)`

---

### C15: POST /api/v1/market/refunds/approve/batch

#### [CMD-08] 환불 승인 배치 성공 - REQUESTED → COLLECTING

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 2건 직접 시딩, ID 캡처
- **요청 Body**: `{"refundClaimIds": ["{id1}", "{id2}"]}`
- **기대 결과**: 200, `data.totalCount==2`, `data.successCount==2`
- **DB 검증**: `refundClaimRepository.findById(id1).get().getRefundStatus() == "COLLECTING"`
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(2))`

#### [CMD-09] 환불 승인 - 유효하지 않은 상태 전이 (COMPLETED → COLLECTING)

- **우선순위**: P1
- **사전 데이터**: COMPLETED 상태 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{completedId}"]}`
- **기대 결과**: 200, `data.failureCount==1` (배치 부분 실패 패턴)
- **비고**: COMPLETED는 COLLECTING으로 전이 불가 (RefundStatus.canTransitionTo 위반)
- **검증 포인트**: `statusCode(200)`, `body("data.failureCount", equalTo(1))`

#### [CMD-10] 환불 승인 Validation 실패 - refundClaimIds 빈 목록

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"refundClaimIds": []}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

---

### C16: POST /api/v1/market/refunds/reject/batch

#### [CMD-11] 환불 거절 배치 성공 - REQUESTED → REJECTED

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 2건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{id1}", "{id2}"]}`
- **기대 결과**: 200, `data.successCount==2`
- **DB 검증**: `refundStatus == "REJECTED"`
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(2))`

#### [CMD-12] 환불 거절 - COLLECTING 상태에서 거절 (rejectable)

- **우선순위**: P1
- **사전 데이터**: COLLECTING 상태 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{collectingId}"]}`
- **기대 결과**: 200, `data.successCount==1`
- **비고**: COLLECTING은 REJECTED 허용 (REJECTABLE = {REQUESTED, COLLECTING, COLLECTED})
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(1))`

#### [CMD-13] 환불 거절 - COMPLETED 상태에서 거절 (불가)

- **우선순위**: P1
- **사전 데이터**: COMPLETED 상태 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{completedId}"]}`
- **기대 결과**: 200, `data.failureCount==1`
- **비고**: COMPLETED는 REJECTED 불가 (종료 상태)
- **검증 포인트**: `statusCode(200)`, `body("data.failureCount", equalTo(1))`

#### [CMD-14] 환불 거절 Validation 실패 - 빈 목록

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"refundClaimIds": []}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

---

### C17: PATCH /api/v1/market/refunds/hold/batch

#### [CMD-15] 환불 보류 설정 성공 - memo 있음

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 2건 직접 시딩 (holdReason=null, holdAt=null)
- **요청 Body**: `{"refundClaimIds": ["{id1}", "{id2}"], "isHold": true, "memo": "CS 확인 필요"}`
- **기대 결과**: 200, `data.successCount==2`
- **DB 검증**: `findById(id1).get().getHoldReason() == "CS 확인 필요"`
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(2))`

#### [CMD-16] 환불 보류 설정 - null memo → 기본값 "보류 처리" 적용

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{id}"], "isHold": true}` (memo 필드 없음)
- **기대 결과**: 200, `data.successCount==1`
- **DB 검증**: `findById(id).get().getHoldReason() == "보류 처리"` (RefundClaim.hold() 기본값 로직)
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(1))`

#### [CMD-17] 환불 보류 해제 성공

- **우선순위**: P0
- **사전 데이터**: holdReason="CS 확인 필요", holdAt=설정된 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{id}"], "isHold": false}`
- **기대 결과**: 200, `data.successCount==1`
- **DB 검증**: `findById(id).get().getHoldReason() == null`, `getHoldAt() == null`
- **검증 포인트**: `statusCode(200)`, `body("data.successCount", equalTo(1))`

#### [CMD-18] 환불 보류 설정 - 이미 보류 상태 (ALREADY_HOLD 예외)

- **우선순위**: P1
- **사전 데이터**: holdReason 이미 설정된 RefundClaim 1건 직접 시딩
- **요청 Body**: `{"refundClaimIds": ["{id}"], "isHold": true, "memo": "중복 보류"}`
- **기대 결과**: 200, `data.failureCount==1`
- **비고**: 배치 부분 실패 패턴. ALREADY_HOLD 예외 → 해당 항목 실패 처리
- **검증 포인트**: `statusCode(200)`, `body("data.failureCount", equalTo(1))`

#### [CMD-19] 환불 보류 해제 - 보류 상태 아님 (NOT_HOLD_STATUS 예외)

- **우선순위**: P1
- **사전 데이터**: holdReason=null인 REQUESTED 상태 RefundClaim 1건 시딩
- **요청 Body**: `{"refundClaimIds": ["{id}"], "isHold": false}`
- **기대 결과**: 200, `data.failureCount==1`
- **비고**: NOT_HOLD_STATUS 예외 → 해당 항목 실패 처리
- **검증 포인트**: `statusCode(200)`, `body("data.failureCount", equalTo(1))`

#### [CMD-20] 환불 보류 Validation 실패 - 빈 목록

- **우선순위**: P0
- **사전 데이터**: 없음
- **요청 Body**: `{"refundClaimIds": [], "isHold": true}`
- **기대 결과**: 400
- **검증 포인트**: `statusCode(400)`

---

### C18: POST /api/v1/market/refunds/{refundClaimId}/histories

#### [CMD-21] ClaimHistory 메모 추가 성공

- **우선순위**: P0
- **사전 데이터**: REQUESTED 상태 RefundClaim 1건 직접 시딩, ID 캡처
- **요청 Body**: `{"message": "CS 처리 메모입니다"}`
- **기대 결과**: 201, `data.historyId` 존재 (non-null, non-empty)
- **DB 검증**: `claimHistoryRepository.count() == 1`
- **검증 포인트**: `statusCode(201)`, `body("data.historyId", notNullValue())`

#### [CMD-22] ClaimHistory 메모 추가 - 존재하지 않는 refundClaimId

- **우선순위**: P1
- **사전 데이터**: 없음
- **요청 PathVariable**: `01900000-0000-7000-0000-000000000999` (존재하지 않는 ID)
- **요청 Body**: `{"message": "메모"}`
- **기대 결과**: 404
- **검증 포인트**: `statusCode(404)`

---

## 전체 플로우 시나리오 (5개)

### [FLOW-01] 환불 요청 → 승인 → OrderItem REFUNDING 전환 (해피패스)

- **우선순위**: P0
- **분류**: 핵심 비즈니스 플로우

**Step 1 - OrderItem 시딩:**
- OrderItem 1건 저장 (READY 상태, orderItemId 캡처)

**Step 2 - 환불 요청 (C14):**
```
POST /refunds/request/batch
Body: {"items": [{"orderId": "{orderItemId}", "refundQty": 1, "reasonType": "CHANGE_OF_MIND"}]}
기대: 200, successCount==1
```
- DB 검증: RefundClaim 생성 확인, refundClaimId 캡처
- Outbox 검증: RefundOutbox 생성 확인 (REQUEST 타입)

**Step 3 - 환불 클레임 목록 조회로 생성 확인 (Q10):**
```
GET /refunds?page=0&size=10
기대: 200, content.size() >= 1
```

**Step 4 - 환불 승인 (C15):**
```
POST /refunds/approve/batch
Body: {"refundClaimIds": ["{refundClaimId}"]}
기대: 200, successCount==1
```
- DB 검증: refundStatus == "COLLECTING"
- Outbox 검증: RefundOutbox 추가 생성 확인 (APPROVE 타입)

**Step 5 - 상세 조회로 COLLECTING 상태 확인 (Q11):**
```
GET /refunds/{refundClaimId}
기대: 200, data.status == "COLLECTING"
```

---

### [FLOW-02] 환불 요청 → 거절 플로우

- **우선순위**: P0
- **분류**: 거절 플로우

**Step 1 - OrderItem 시딩 후 환불 요청 (C14):**
```
POST /refunds/request/batch
기대: 200, refundClaimId 캡처
```

**Step 2 - 환불 거절 (C16):**
```
POST /refunds/reject/batch
Body: {"refundClaimIds": ["{refundClaimId}"]}
기대: 200, successCount==1
```
- DB 검증: refundStatus == "REJECTED"

**Step 3 - 상세 조회로 REJECTED 확인 (Q11):**
```
GET /refunds/{refundClaimId}
기대: 200, data.status == "REJECTED"
```

---

### [FLOW-03] 보류 설정 → 보류 해제 토글 플로우

- **우선순위**: P0
- **분류**: 보류 토글 플로우

**Step 1 - REQUESTED 상태 RefundClaim 직접 시딩:**
- refundClaimId 캡처

**Step 2 - 보류 설정 (C17, isHold=true):**
```
PATCH /refunds/hold/batch
Body: {"refundClaimIds": ["{id}"], "isHold": true, "memo": "보류 사유"}
기대: 200, successCount==1
```
- DB 검증: holdReason == "보류 사유", holdAt != null

**Step 3 - 보류 상세 조회 (Q11):**
```
GET /refunds/{refundClaimId}
기대: 200, holdInfo 필드 존재
```

**Step 4 - 보류 해제 (C17, isHold=false):**
```
PATCH /refunds/hold/batch
Body: {"refundClaimIds": ["{id}"], "isHold": false}
기대: 200, successCount==1
```
- DB 검증: holdReason == null, holdAt == null

**Step 5 - 해제 후 상세 조회 확인 (Q11):**
```
GET /refunds/{refundClaimId}
기대: 200, holdInfo 없음 또는 null
```

---

### [FLOW-04] null 보류 사유 → 기본값 "보류 처리" 검증 플로우

- **우선순위**: P0
- **분류**: 도메인 규칙 검증 (RefundClaim.hold() 기본값 로직)

**Step 1 - REQUESTED 상태 RefundClaim 직접 시딩**

**Step 2 - memo 없이 보류 설정 (C17):**
```
PATCH /refunds/hold/batch
Body: {"refundClaimIds": ["{id}"], "isHold": true}  ← memo 필드 없음
기대: 200, successCount==1
```

**Step 3 - DB 직접 검증:**
```java
RefundClaimJpaEntity saved = refundClaimRepository.findById(id).get();
assertThat(saved.getHoldReason()).isEqualTo("보류 처리");  // 도메인 기본값
assertThat(saved.getHoldAt()).isNotNull();
```

---

### [FLOW-05] 배치 부분 실패 플로우 (혼합 상태)

- **우선순위**: P1
- **분류**: 배치 처리 부분 실패

**Step 1 - RefundClaim 시딩:**
- REQUESTED 상태 1건 (id1) - 승인 가능
- COMPLETED 상태 1건 (id2) - 승인 불가

**Step 2 - 승인 배치 (C15) - 혼합 결과:**
```
POST /refunds/approve/batch
Body: {"refundClaimIds": ["{id1}", "{id2}"]}
기대: 200 (배치는 항상 200),
     data.totalCount == 2,
     data.successCount == 1,
     data.failureCount == 1
```
- id1: COLLECTING 전환 성공
- id2: 상태 전이 불가 → 실패 처리

**Step 3 - 부분 성공 결과 검증:**
```java
// id1은 COLLECTING으로 변경됨
assertThat(refundClaimRepository.findById(id1).get().getRefundStatus()).isEqualTo("COLLECTING");
// id2는 COMPLETED 그대로
assertThat(refundClaimRepository.findById(id2).get().getRefundStatus()).isEqualTo("COMPLETED");
```

---

## 인증/인가 시나리오 (인라인)

각 Command 테스트 클래스 내 `@Nested @Tag("auth")` 섹션으로 포함:

| 시나리오 | 우선순위 | 기대 결과 |
|---------|---------|---------|
| 비인증 요청으로 환불 요청 배치 시도 | P0 | 401 |
| 비인증 요청으로 목록 조회 시도 | P0 | 401 |
| 권한 없는 사용자 환불 승인 시도 (ROLE_USER + 잘못된 권한) | P1 | 403 |
| 다른 sellerId 소유 RefundClaim 승인 시도 | P1 | 200 with failureCount (소유권 검증 배치 실패) |

---

## 소유권 검증 시나리오

#### [OWNER-01] 다른 셀러 소유 RefundClaim 조작 시도

- **우선순위**: P1
- **배경**: RefundClaim은 sellerId를 가지며, 셀러 사용자는 자신의 sellerId와 일치하는 클레임만 처리 가능
- **사전 데이터**: sellerId=100L인 RefundClaim 1건 시딩
- **요청**: sellerId가 다른 셀러 사용자로 승인 배치 시도
- **기대 결과**: 200, `data.failureCount==1` (`RefundOwnershipMismatchException` → 배치 실패 처리)
- **검증 포인트**: `statusCode(200)`, `body("data.failureCount", equalTo(1))`

---

## 테스트 클래스 구조 제안

```
integration-test/src/test/java/com/ryuqq/marketplace/integration/refund/
├── RefundQueryE2ETest.java        -- Q09~Q11: 조회 시나리오 (QUERY-01~12)
├── RefundCommandE2ETest.java      -- C14~C18: 단위 커맨드 시나리오 (CMD-01~22)
└── RefundFlowE2ETest.java         -- 전체 플로우 시나리오 (FLOW-01~05)
```

### 테스트 태그 전략

```java
// 클래스 레벨
@Tag("e2e")
@Tag("refund")
@Tag("flow")   // 또는 "command", "query"

// 메서드 레벨 (중요도)
@Tag("P0")   // 필수
@Tag("P1")   // 중요
@Tag("P2")   // 보완
```

---

## 신규 생성 필요 파일

### RefundClaimJpaEntityFixtures (신규 생성 필요)

위치: `adapter-out/persistence-mysql/src/testFixtures/java/com/ryuqq/marketplace/adapter/out/persistence/refund/RefundClaimJpaEntityFixtures.java`

```
필요 팩토리 메서드:
- requestedEntity(String id, String orderItemId, long sellerId)
- requestedEntity(String id)  -- DEFAULT_ORDER_ITEM_ID, DEFAULT_SELLER_ID 사용
- collectingEntity(String id)
- completedEntity(String id)
- rejectedEntity(String id)
- heldEntity(String id, String holdReason)  -- holdReason + holdAt 설정
- entityWithStatus(String id, String status)
- entityWithSellerId(String id, long sellerId)  -- 소유권 테스트용
```

**참조 모델**: `ExchangeClaimJpaEntityFixtures.java`

**RefundClaimJpaEntity.create() 파라미터 순서:**
```java
RefundClaimJpaEntity.create(
    id, claimNumber, orderItemId, sellerId, refundQty,
    refundStatus, reasonType, reasonDetail,
    originalAmount, finalAmount, deductionAmount, deductionReason,
    refundMethod, refundedAt, claimShipmentId,
    holdReason, holdAt,
    requestedBy, processedBy,
    requestedAt, processedAt, completedAt,
    createdAt, updatedAt
)
```

---

## 주의사항

1. **OrderItemFixtures sellerId 불일치**: `OrderItemJpaEntityFixtures.defaultItem()`의 `DEFAULT_SELLER_ID = 10L`이고, `ExchangeClaimJpaEntityFixtures.DEFAULT_SELLER_ID = 100L`이다. 소유권 검증 테스트 시 sellerId를 명시적으로 일치시켜야 한다.

2. **환불 요청 시 orderId = orderItemId**: V4 간극 규칙. `RequestRefundBatchApiRequest`의 `orderId` 필드에 내부 `orderItemId` 값을 전달해야 한다.

3. **Outbox 정리 순서**: `refundOutboxRepository.deleteAll()`은 `refundClaimRepository.deleteAll()` 전에 실행해야 FK 위반이 없다.

4. **배치 응답은 항상 200**: 상태 전이 실패, 소유권 위반 등 비즈니스 예외는 개별 항목의 `success=false`로 처리되며, HTTP 상태코드는 200이다. `@NotEmpty` 등 Validation 실패만 400을 반환한다.

5. **SUPER_ADMIN vs 셀러 권한**: 환불 요청(C14)은 `resolveCurrentSellerId()` (셀러 필수), 승인/거절/보류(C15~C17)는 `resolveSellerIdOrNull()` (SUPER_ADMIN은 null). 소유권 테스트 설계 시 이 차이를 반영한다.
