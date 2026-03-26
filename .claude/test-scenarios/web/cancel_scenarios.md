# Cancel(취소) E2E 통합 테스트 시나리오

## 분석 요약

- 대상 도메인: Cancel (취소)
- API 접두사: /api/v1/market/cancels
- Query 엔드포인트: 3개 (summary, list, detail)
- Command 엔드포인트: 4개 (seller-cancel, approve, reject, histories)
- 총 시나리오: 28개 (P0: 18, P1: 8, P2: 2)
- 문서 기반: api-endpoints/web/oms_endpoints.md, api-flows/oms_flows.md

---

## 도메인 핵심 규칙

### CancelStatus 상태 전이

```
REQUESTED --> APPROVED   (approve 가능)
REQUESTED --> REJECTED   (reject 가능)
REQUESTED --> CANCELLED  (withdraw 가능)
APPROVED  --> COMPLETED  (complete 가능)
```

### CancelType 구분

- SELLER_CANCEL: 판매자 직접 취소. 생성 즉시 APPROVED 상태. OrderItem 상태 동시 CANCELLED 전환.
- BUYER_CANCEL: 구매자 요청. REQUESTED 상태로 생성. 판매자 승인(APPROVED)/거절(REJECTED) 필요.

### V4 API 간극 규칙 (전 API 공통 적용)

- orderId 파라미터 = 내부 orderItemId (UUIDv7)
- null 값은 빈 문자열("") 로 반환
- null 금액은 0으로 반환

### 오류 코드 매핑

| 에러코드 | HTTP 상태 | 상황 |
|---------|-----------|------|
| CAN-001 | 404 | 취소 건 미존재 |
| CAN-002 | 400 | 유효하지 않은 상태 전이 |
| CAN-003 | 409 | 이미 취소된 요청 |
| CAN-005 | 400 | cancelQty <= 0 |
| CAN-006 | 400 | 취소 불가 주문(상태 불일치) |
| CAN-008 | 403 | 소유권 불일치(다른 sellerId) |

---

## Fixture 설계

### 필요 Repository

| Repository | 용도 |
|------------|------|
| OrderJpaRepository | Order 시딩/정리 |
| OrderItemJpaRepository | OrderItem 시딩/정리 (취소 대상) |
| CancelJpaRepository | Cancel 직접 시딩 (Query 테스트용) |
| CancelOutboxJpaRepository | Outbox 정리 |
| ClaimHistoryJpaRepository | ClaimHistory 검증/정리 |
| OrderItemHistoryJpaRepository | OrderItem 이력 검증 |

### testFixtures 활용 현황

| Fixture 클래스 | 재사용 가능 메서드 |
|---------------|-------------------|
| OrderJpaEntityFixtures | orderedEntity(String id) |
| OrderItemJpaEntityFixtures | defaultItem(String orderId) - status="READY" |

### 신규 생성 필요 Fixture

`CancelJpaEntityFixtures` 클래스 신규 생성 필요.

- `requestedEntity(String cancelId, String orderItemId, long sellerId)` - BUYER_CANCEL, REQUESTED 상태
- `approvedEntity(String cancelId, String orderItemId, long sellerId)` - SELLER_CANCEL, APPROVED 상태
- `rejectedEntity(String cancelId, String orderItemId, long sellerId)` - BUYER_CANCEL, REJECTED 상태
- `entityWithStatus(String cancelId, String orderItemId, String status)` - 상태 지정 범용

### setUp / tearDown 패턴

```java
@BeforeEach
void setUp() {
    claimHistoryRepository.deleteAll();
    cancelOutboxRepository.deleteAll();
    cancelRepository.deleteAll();
    orderItemHistoryRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}

@AfterEach
void tearDown() {
    claimHistoryRepository.deleteAll();
    cancelOutboxRepository.deleteAll();
    cancelRepository.deleteAll();
    orderItemHistoryRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}
```

### 공통 시딩 헬퍼 (seedOrderItem)

기존 ShipmentFlowE2ETest 패턴 재사용:

```java
private String seedOrderItem(String orderId) {
    orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
    OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
    // DEFAULT_SELLER_ID = 10L 확인 필수
    return orderItemRepository.save(item).getId();
}
```

---

## 인증 컨텍스트

| 역할 | 메서드 | sellerId 해석 |
|------|--------|--------------|
| SUPER_ADMIN | givenSuperAdmin() | resolveSellerIdOrNull() = null (전체 조회) |
| 셀러 사용자 | givenSellerUser(orgId, "cancel:write") | resolveCurrentSellerId() = DB에서 orgId로 조회 |

### 주의: 소유권 검증 테스트

`CancelCommandController.sellerCancelBatch()` / `addMemo()`는 `accessChecker.resolveCurrentSellerId()`를 사용하므로 sellerId가 OrderItemJpaEntityFixtures.DEFAULT_SELLER_ID(10L)와 일치해야 합니다. 소유권 불일치 테스트는 다른 orgId로 요청합니다.

---

## 테스트 클래스 구조

```
integration-test/src/test/java/com/ryuqq/marketplace/integration/cancel/
├── CancelQueryE2ETest.java      - Q12, Q13, Q14 (summary, list, detail)
├── CancelCommandE2ETest.java    - C19~C22 개별 Command 검증
└── CancelFlowE2ETest.java       - 전체 플로우 (seller-cancel→조회, buyer-cancel→approve→조회 등)
```

---

## Query 시나리오 (Q12 ~ Q14)

### Q12: GET /api/v1/market/cancels/summary

**테스트 클래스**: `CancelQueryE2ETest` > `CancelSummaryTest`

#### [Q12-1] 취소 데이터 없을 때 요약 조회 - 빈 카운트 반환 (P0)

```
전제 조건: 취소 데이터 없음
요청: GET /cancels/summary (SUPER_ADMIN)
기대: 200, data != null, 모든 status 카운트 = 0 또는 빈 맵
검증 포인트: 빈 상태에서도 200 응답 반환
```

#### [Q12-2] 다양한 상태의 취소 데이터 존재 시 요약 조회 (P0)

```
전제 조건:
  - Cancel 직접 시딩: REQUESTED 2건, APPROVED 1건, REJECTED 1건
  (orderItemId는 실제 OrderItem과 무관하게 직접 insert 가능)
요청: GET /cancels/summary (SUPER_ADMIN)
기대: 200, data != null
DB 검증: REQUESTED=2, APPROVED=1, REJECTED=1 반영
```

#### [Q12-3] 권한 없는 사용자 요약 조회 - 403 (P1)

```
요청: GET /cancels/summary (givenWithPermission("order:read"))
기대: 403 Forbidden
```

#### [Q12-4] 비인증 요청 요약 조회 - 401 (P1)

```
요청: GET /cancels/summary (givenUnauthenticated())
기대: 401 Unauthorized
```

---

### Q13: GET /api/v1/market/cancels

**테스트 클래스**: `CancelQueryE2ETest` > `CancelListTest`

#### [Q13-1] 데이터 없을 때 취소 목록 조회 - 빈 페이지 반환 (P0)

```
전제 조건: 취소 데이터 없음
요청: GET /cancels?page=0&size=10 (SUPER_ADMIN)
기대: 200, data.content.size() = 0, data.totalElements = 0
```

#### [Q13-2] 취소 목록 존재 시 정상 페이징 조회 (P0)

```
전제 조건: Cancel 3건 직접 시딩 (다양한 상태)
요청: GET /cancels?page=0&size=10 (SUPER_ADMIN)
기대: 200, data.content.size() = 3, data.totalElements = 3
```

#### [Q13-3] page, size 파라미터 페이징 동작 확인 (P1)

```
전제 조건: Cancel 5건 직접 시딩
요청: GET /cancels?page=0&size=2
기대: 200, data.content.size() = 2, data.totalElements = 5
```

#### [Q13-4] statuses 필터 동작 확인 (P1)

```
전제 조건:
  - Cancel REQUESTED 2건
  - Cancel APPROVED 1건
  - Cancel REJECTED 1건
요청: GET /cancels?statuses=REQUESTED&page=0&size=10
기대: 200, data.content.size() = 2 (REQUESTED만 필터링)
```

#### [Q13-5] types 필터 동작 확인 (P1)

```
전제 조건:
  - Cancel SELLER_CANCEL 2건
  - Cancel BUYER_CANCEL 2건
요청: GET /cancels?types=SELLER_CANCEL&page=0&size=10
기대: 200, data.content.size() = 2 (SELLER_CANCEL만 필터링)
```

#### [Q13-6] 복합 필터 - statuses + types 조합 (P2)

```
전제 조건: SELLER_CANCEL/APPROVED 1건, BUYER_CANCEL/REQUESTED 1건, BUYER_CANCEL/REJECTED 1건
요청: GET /cancels?statuses=APPROVED&types=SELLER_CANCEL&page=0&size=10
기대: 200, data.content.size() = 1
```

---

### Q14: GET /api/v1/market/cancels/{cancelId}

**테스트 클래스**: `CancelQueryE2ETest` > `CancelDetailTest`

#### [Q14-1] 존재하는 cancelId로 상세 조회 (P0)

```
전제 조건: Cancel 1건 직접 시딩 (cancelId = "cancel-detail-001")
요청: GET /cancels/cancel-detail-001 (SUPER_ADMIN)
기대: 200, data.cancelId = "cancel-detail-001"
검증 포인트: cancelNumber, orderItemId, cancelStatus, cancelType 정상 반환
```

#### [Q14-2] 존재하지 않는 cancelId로 상세 조회 - 404 (P0)

```
전제 조건: 취소 데이터 없음
요청: GET /cancels/non-existent-cancel-id (SUPER_ADMIN)
기대: 404 Not Found (CAN-001)
```

---

## Command 시나리오

### C19: POST /api/v1/market/cancels/seller-cancel/batch

**테스트 클래스**: `CancelCommandE2ETest` > `SellerCancelBatchTest`

**비즈니스 규칙**: 판매자 취소는 SELLER_CANCEL 타입으로 생성 즉시 APPROVED. OrderItem 상태 CANCELLED 전환.

#### [C19-1] 유효한 OrderItem 1건 판매자 취소 성공 (P0)

```
전제 조건: READY 상태 OrderItem 1건 시딩 (sellerId=10)
요청: POST /cancels/seller-cancel/batch (givenSellerUser with cancel:write)
  body: {
    "items": [{
      "orderId": "{orderItemId}",
      "cancelQty": 1,
      "reasonType": "OUT_OF_STOCK",
      "reasonDetail": "재고 소진"
    }]
  }
기대: 200, data.totalCount=1, data.successCount=1, data.failureCount=0
DB 검증:
  - cancelRepository.count() = 1
  - cancelRepository.findByOrderItemId(orderItemId).cancelStatus = "APPROVED"
  - orderItemRepository.findById(orderItemId).deliveryStatus = "CANCELLED"
```

#### [C19-2] OrderItem 2건 일괄 판매자 취소 성공 (P0)

```
전제 조건: READY 상태 OrderItem 2건 시딩
요청: POST /cancels/seller-cancel/batch
  body: { "items": [item1, item2] }
기대: 200, data.totalCount=2, data.successCount=2, data.failureCount=0
DB 검증: Cancel 2건 APPROVED, OrderItem 2건 CANCELLED
```

#### [C19-3] items 빈 목록 요청 - 400 (P0)

```
요청: POST /cancels/seller-cancel/batch
  body: { "items": [] }
기대: 400 Bad Request (@NotEmpty 검증)
```

#### [C19-4] orderId 누락 - 400 (P0)

```
요청: POST /cancels/seller-cancel/batch
  body: { "items": [{ "cancelQty": 1, "reasonType": "OUT_OF_STOCK" }] }
기대: 400 Bad Request (@NotBlank orderId 검증)
```

#### [C19-5] cancelQty = 0 (양수 아님) - 400 (P0)

```
요청: POST /cancels/seller-cancel/batch
  body: { "items": [{ "orderId": "any-id", "cancelQty": 0, "reasonType": "OUT_OF_STOCK" }] }
기대: 400 Bad Request (@Positive cancelQty 검증)
```

#### [C19-6] reasonType 누락 - 400 (P0)

```
요청: POST /cancels/seller-cancel/batch
  body: { "items": [{ "orderId": "any-id", "cancelQty": 1 }] }
기대: 400 Bad Request (@NotBlank reasonType 검증)
```

#### [C19-7] 존재하지 않는 orderItemId - 부분 성공 처리 (P1)

```
전제 조건: OrderItem 1건 시딩 (itemId1)
요청: POST /cancels/seller-cancel/batch
  body: { "items": [item1(valid), item2(존재하지 않는 ID)] }
기대: 200, data.totalCount=2, data.successCount=1, data.failureCount=1
검증 포인트: BatchResultApiResponse의 results 배열에서 각각 success 여부 확인
```

#### [C19-8] 권한 없는 사용자 요청 - 403 (P1)

```
요청: POST /cancels/seller-cancel/batch (givenWithPermission("order:read"))
기대: 403 Forbidden
```

#### [C19-9] 비인증 요청 - 401 (P1)

```
요청: POST /cancels/seller-cancel/batch (givenUnauthenticated())
기대: 401 Unauthorized
```

---

### C20: POST /api/v1/market/cancels/approve/batch

**테스트 클래스**: `CancelCommandE2ETest` > `ApproveCancelBatchTest`

**비즈니스 규칙**: REQUESTED 상태 Cancel만 APPROVED 가능. OrderItem 상태 CANCELLED 전환.

#### [C20-1] REQUESTED 상태 Cancel 승인 성공 (P0)

```
전제 조건:
  - OrderItem 1건 시딩
  - Cancel 직접 시딩: cancelId="cancel-approve-001", cancelStatus="REQUESTED"
요청: POST /cancels/approve/batch (SUPER_ADMIN)
  body: { "cancelIds": ["cancel-approve-001"] }
기대: 200, data.totalCount=1, data.successCount=1, data.failureCount=0
DB 검증:
  - cancelRepository.findById("cancel-approve-001").cancelStatus = "APPROVED"
  - orderItemRepository.findById(orderItemId).deliveryStatus = "CANCELLED"
```

#### [C20-2] 복수 Cancel 일괄 승인 (P0)

```
전제 조건: REQUESTED 상태 Cancel 2건 직접 시딩
요청: POST /cancels/approve/batch
  body: { "cancelIds": ["cancel-app-001", "cancel-app-002"] }
기대: 200, data.totalCount=2, data.successCount=2, data.failureCount=0
```

#### [C20-3] cancelIds 빈 목록 - 400 (P0)

```
요청: POST /cancels/approve/batch
  body: { "cancelIds": [] }
기대: 400 Bad Request (@NotEmpty 검증)
```

#### [C20-4] 존재하지 않는 cancelId 승인 - 부분 성공 (P1)

```
전제 조건: REQUESTED Cancel 1건 시딩
요청: POST /cancels/approve/batch
  body: { "cancelIds": ["valid-cancel-id", "non-existent-id"] }
기대: 200, data.totalCount=2, data.successCount=1, data.failureCount=1
```

#### [C20-5] REJECTED 상태 Cancel 승인 시도 - 상태 전이 불가 (P1)

```
전제 조건: Cancel 직접 시딩: cancelStatus="REJECTED"
요청: POST /cancels/approve/batch
  body: { "cancelIds": ["cancel-rejected-001"] }
기대: 200, data.failureCount=1 (CAN-002 상태 전이 불가로 배치 실패 처리)
검증 포인트: results[0].success = false
```

---

### C21: POST /api/v1/market/cancels/reject/batch

**테스트 클래스**: `CancelCommandE2ETest` > `RejectCancelBatchTest`

**비즈니스 규칙**: REQUESTED 상태 Cancel만 REJECTED 가능. OrderItem 상태 원복(READY 유지).

#### [C21-1] REQUESTED 상태 Cancel 거절 성공 (P0)

```
전제 조건:
  - OrderItem 1건 시딩
  - Cancel 직접 시딩: cancelId="cancel-reject-001", cancelStatus="REQUESTED"
요청: POST /cancels/reject/batch (SUPER_ADMIN)
  body: { "cancelIds": ["cancel-reject-001"] }
기대: 200, data.totalCount=1, data.successCount=1, data.failureCount=0
DB 검증:
  - cancelRepository.findById("cancel-reject-001").cancelStatus = "REJECTED"
  - orderItemRepository.findById(orderItemId).deliveryStatus = "READY" (원복 확인)
```

#### [C21-2] cancelIds 빈 목록 - 400 (P0)

```
요청: POST /cancels/reject/batch
  body: { "cancelIds": [] }
기대: 400 Bad Request (@NotEmpty 검증)
```

#### [C21-3] APPROVED 상태 Cancel 거절 시도 - 상태 전이 불가 (P1)

```
전제 조건: Cancel 직접 시딩: cancelStatus="APPROVED"
요청: POST /cancels/reject/batch
  body: { "cancelIds": ["cancel-approved-001"] }
기대: 200, data.failureCount=1 (CAN-002 배치 실패 처리)
```

#### [C21-4] 복수 취소 일괄 거절 (P1)

```
전제 조건: REQUESTED 상태 Cancel 3건 직접 시딩
요청: POST /cancels/reject/batch
  body: { "cancelIds": ["id1", "id2", "id3"] }
기대: 200, data.totalCount=3, data.successCount=3, data.failureCount=0
```

---

### C22: POST /api/v1/market/cancels/{cancelId}/histories

**테스트 클래스**: `CancelCommandE2ETest` > `AddCancelHistoryMemoTest`

#### [C22-1] 존재하는 cancelId에 메모 등록 성공 (P0)

```
전제 조건:
  - OrderItem 1건 시딩
  - Cancel 직접 시딩: cancelId="cancel-memo-001"
요청: POST /cancels/cancel-memo-001/histories (givenSellerUser with cancel:write)
  body: { "message": "판매자 메모: 고객 요청으로 취소 처리함" }
기대: 201 Created, data.historyId != null
DB 검증: claimHistoryRepository.count() = 1
```

#### [C22-2] message 빈 문자열 - 400 (P0)

```
요청: POST /cancels/any-cancel-id/histories
  body: { "message": "" }
기대: 400 Bad Request (@NotBlank 검증)
```

#### [C22-3] message 누락 - 400 (P0)

```
요청: POST /cancels/any-cancel-id/histories
  body: {}
기대: 400 Bad Request (@NotBlank message 검증)
```

#### [C22-4] 존재하지 않는 cancelId에 메모 등록 - 404 (P1)

```
전제 조건: 취소 데이터 없음
요청: POST /cancels/non-existent-id/histories
  body: { "message": "메모" }
기대: 404 Not Found (CAN-001)
```

---

## 전체 플로우 시나리오

**테스트 클래스**: `CancelFlowE2ETest`

### [FLOW-1] 판매자 취소 → 조회 플로우 (P0)

**목적**: 판매자가 직접 취소 후 목록/상세에서 확인되는 전체 플로우 검증

```
Step 1. 전제 조건 설정
  - Order 1건 시딩
  - OrderItem 1건 시딩 (READY 상태, sellerId=10)

Step 2. 판매자 취소 요청 (POST /cancels/seller-cancel/batch)
  - givenSellerUser(orgId, "cancel:write")
  - body: { "items": [{ "orderId": {orderItemId}, "cancelQty": 1, "reasonType": "OUT_OF_STOCK" }] }
  - 검증: 200, data.successCount=1

Step 3. 취소 목록 조회 (GET /cancels?page=0&size=10)
  - givenSuperAdmin()
  - 검증: 200, data.totalElements=1

Step 4. 목록에서 cancelId 추출
  - data.content[0].cancelId 추출

Step 5. 취소 상세 조회 (GET /cancels/{cancelId})
  - 검증: 200, data.cancelStatus="APPROVED", data.cancelType="SELLER_CANCEL"

Step 6. DB 직접 검증
  - orderItemRepository.findById(orderItemId).deliveryStatus = "CANCELLED"
  - cancelRepository.count() = 1
```

### [FLOW-2] 판매자 취소 → 취소 요약 반영 플로우 (P0)

**목적**: seller-cancel 후 summary에 APPROVED 건수 반영 확인

```
Step 1. 취소 데이터 없을 때 summary 조회 → 모든 카운트 0 확인

Step 2. OrderItem 시딩 후 판매자 취소 요청 (APPROVED 상태 생성)

Step 3. summary 재조회
  - 검증: APPROVED 카운트 = 1 반영
```

### [FLOW-3] 구매자 취소 요청 → 판매자 승인 → OrderItem CANCELLED 전체 플로우 (P0)

**목적**: REQUESTED → APPROVED 상태 전이 + OrderItem 상태 변경 검증

```
Step 1. 전제 조건 설정
  - Order 1건 + OrderItem 1건 시딩

Step 2. Cancel 직접 시딩 (BUYER_CANCEL, REQUESTED 상태)
  - cancelId = "cancel-flow-buyer-001"
  - orderItemId = 시딩된 orderItem ID

Step 3. 취소 목록 조회 - REQUESTED 1건 확인 (GET /cancels?statuses=REQUESTED)
  - 검증: data.totalElements = 1

Step 4. 취소 승인 (POST /cancels/approve/batch)
  - body: { "cancelIds": ["cancel-flow-buyer-001"] }
  - 검증: 200, data.successCount=1

Step 5. 취소 상세 조회 (GET /cancels/cancel-flow-buyer-001)
  - 검증: 200, data.cancelStatus="APPROVED"

Step 6. DB 검증
  - orderItemRepository.findById(orderItemId).deliveryStatus = "CANCELLED"
```

### [FLOW-4] 구매자 취소 요청 → 판매자 거절 플로우 (P0)

**목적**: REQUESTED → REJECTED 상태 전이 + OrderItem 상태 원복 검증

```
Step 1. Order + OrderItem 시딩

Step 2. Cancel 직접 시딩 (BUYER_CANCEL, REQUESTED)

Step 3. 취소 거절 (POST /cancels/reject/batch)
  - body: { "cancelIds": ["cancel-reject-flow-001"] }
  - 검증: 200, data.successCount=1

Step 4. 취소 상세 조회 (GET /cancels/{cancelId})
  - 검증: 200, data.cancelStatus="REJECTED"

Step 5. DB 검증
  - orderItemRepository.findById(orderItemId).deliveryStatus = "READY" (원복)
```

### [FLOW-5] 배치 부분 실패 플로우 (P0)

**목적**: 취소 가능 항목과 불가 항목이 혼재할 때 BatchResultApiResponse 동작 검증

```
Step 1. 전제 조건 설정
  - OrderItem 1건 시딩 (itemId1)
  - Cancel 직접 시딩: REQUESTED 상태 1건 (cancelId1), APPROVED 상태 1건 (cancelId2)

Step 2. 승인 배치 요청 (cancelId1=REQUESTED, cancelId2=APPROVED)
  - POST /cancels/approve/batch
  - body: { "cancelIds": ["cancelId1", "cancelId2"] }
  - 검증: 200, data.totalCount=2, data.successCount=1, data.failureCount=1

Step 3. 결과 상세 검증
  - data.results 배열에서 cancelId1.success=true 확인
  - data.results 배열에서 cancelId2.success=false 확인 (CAN-002 상태 전이 불가)
```

### [FLOW-6] ClaimHistory 메모 추가 플로우 (P1)

**목적**: 취소 건에 메모 등록 후 이력 저장 검증

```
Step 1. OrderItem + Cancel 시딩 (REQUESTED 상태)

Step 2. 메모 등록 (POST /cancels/{cancelId}/histories)
  - body: { "message": "고객 요청으로 취소 처리함" }
  - 검증: 201 Created, data.historyId != null (historyId는 non-null String)

Step 3. DB 검증
  - claimHistoryRepository.count() = 1

Step 4. 메모 추가 재요청 (2번째 메모)
  - 검증: 201 Created, data.historyId != null (각 메모 별도 이력)

Step 5. DB 검증
  - claimHistoryRepository.count() = 2
```

### [FLOW-7] 소유권 검증 플로우 (P1)

**목적**: 다른 sellerId로 판매자 취소 요청 시 소유권 불일치 오류 검증

```
전제 조건:
  - OrderItem 시딩 (sellerId=10 = DEFAULT_SELLER_ID)
  - 다른 orgId로 givenSellerUser 설정 (sellerId가 10이 아닌 셀러)

요청: POST /cancels/seller-cancel/batch (잘못된 seller)
  body: { "items": [{ "orderId": orderItemId, "cancelQty": 1, "reasonType": "OUT_OF_STOCK" }] }

기대: 200, data.failureCount=1 (CAN-008 소유권 불일치로 배치 실패)
   또는 403 (구현에 따라 전체 거부)
검증 포인트: DB에 Cancel이 생성되지 않음 (cancelRepository.count() = 0)
```

---

## 시나리오 우선순위 요약

| 우선순위 | 수량 | 테스트 ID |
|---------|------|----------|
| P0 | 18 | Q12-1, Q12-2, Q13-1, Q13-2, Q14-1, Q14-2, C19-1~6, C20-1~3, C21-1~2, C22-1~3, FLOW-1~5 |
| P1 | 8 | Q12-3, Q12-4, Q13-3~5, C19-7~9, C20-4~5, C21-3~4, C22-4, FLOW-6~7 |
| P2 | 2 | Q13-6, 추가 복합 필터 시나리오 |

---

## 구현 시 주의사항

### 1. OrderItemJpaEntityFixtures.DEFAULT_SELLER_ID = 10L

모든 취소 Command 테스트에서 givenSellerUser의 orgId가 sellerId=10에 매핑되는 조직이어야 합니다. `ResolveSellerIdByOrganizationService`가 조직 ID를 sellerId로 변환하므로 테스트 DB에 해당 seller 레코드가 있어야 합니다. 실제 구현 시 givenSuperAdmin()을 기본으로 사용하고, 소유권 검증 시나리오에서만 givenSellerUser를 활용합니다.

### 2. Cancel 직접 시딩 방식 (Query 테스트)

Query 테스트는 복잡한 선행 플로우 없이 `CancelJpaRepository.save(CancelJpaEntity.create(...))` 로 직접 시딩합니다. OrderItem과의 실제 FK 의존성이 없다면 orderItemId를 임의 UUID로 설정 가능합니다 (H2 환경 FK 미강제 여부 확인 필요).

### 3. Outbox 정리 순서

CancelOutboxJpaRepository는 Cancel 삭제 전 먼저 정리해야 합니다 (FK 제약 가능성).

### 4. BatchResultApiResponse 응답 구조

기존 ShipmentFlowE2ETest 참조:
- `data.totalCount` - 전체 처리 시도 건수
- `data.successCount` - 성공 건수
- `data.failureCount` - 실패 건수
- `data.results[]` - 개별 결과 (success boolean, id, errorMessage)

### 5. V4 간극: orderId = orderItemId

`SellerCancelBatchApiRequest.SellerCancelItemApiRequest.orderId` 필드는 실제로 내부 `orderItemId` (UUIDv7)를 전달해야 합니다.

---

## 다음 단계

```
/test-e2e web:cancel
```

구현 시 참조 파일:
- 기존 E2E 패턴: /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/integration-test/src/test/java/com/ryuqq/marketplace/integration/shipment/ShipmentFlowE2ETest.java
- E2ETestBase: /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/integration-test/src/test/java/com/ryuqq/marketplace/integration/E2ETestBase.java
- Cancel Aggregate: /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/domain/src/main/java/com/ryuqq/marketplace/domain/cancel/aggregate/Cancel.java
- CancelJpaEntity: /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/adapter-out/persistence-mysql/src/main/java/com/ryuqq/marketplace/adapter/out/persistence/cancel/entity/CancelJpaEntity.java
