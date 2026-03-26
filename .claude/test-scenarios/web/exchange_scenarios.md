# Exchange(교환) E2E 테스트 시나리오 설계

## 분석 요약

- 대상: `web:exchange` 도메인
- Query 엔드포인트: 3개 (summary, list, detail)
- Command 엔드포인트: 10개 (request, approve, collect, prepare, reject, ship, complete, convert-to-refund, hold, histories)
- 총 시나리오: 42개 (P0: 26개, P1: 12개, P2: 4개)

---

## 도메인 핵심 정보

### 상태 전이 규칙 (ExchangeStatus)

```
REQUESTED
  ├─→ COLLECTING   (approve/batch)       [startCollecting]
  ├─→ REJECTED     (reject/batch)        [reject]
  └─→ CANCELLED    (내부 cancel)

COLLECTING
  ├─→ COLLECTED    (collect/batch)       [completeCollection]
  └─→ CANCELLED    (내부 cancel)

COLLECTED
  ├─→ PREPARING    (prepare/batch)       [startPreparing]
  └─→ REJECTED     (reject/batch)        [reject]

PREPARING
  ├─→ SHIPPING     (ship/batch)          [startShipping]
  └─→ REJECTED     (reject/batch)        [reject]

SHIPPING
  └─→ COMPLETED    (complete/batch)      [complete]

COMPLETED / REJECTED / CANCELLED  →  종료 (전이 불가)
```

### V4 간극 규칙

- `RequestExchangeBatchApiRequest.ExchangeRequestItemApiRequest.orderId` = 내부 `orderItemId` (UUID)
- null 문자열 필드는 `""` 로 반환
- null 금액 필드는 `0` 으로 반환

### Hold 규칙

- `hold()`: holdInfo가 이미 있으면 `ALREADY_HOLD` 예외
- `releaseHold()`: holdInfo가 null이면 `NOT_HOLD_STATUS` 예외
- `HoldExchangeBatchApiRequest.isHold = true` → 보류 설정, `false` → 보류 해제

### 소유권 검증

- `ExchangeClaim.sellerId` 가 요청자의 `sellerId` 와 일치해야 처리 허용
- 불일치 시 `ExchangeOwnershipMismatchException` 발생 → 400/403

---

## Request DTO Validation 요약

| DTO | 필드 | 검증 |
|-----|------|------|
| RequestExchangeBatchApiRequest | items | @NotEmpty |
| ExchangeRequestItemApiRequest | orderId | @NotBlank |
| ExchangeRequestItemApiRequest | exchangeQty | @Positive |
| ExchangeRequestItemApiRequest | reasonType | @NotBlank |
| ExchangeRequestItemApiRequest | originalSkuCode | @NotBlank |
| ExchangeRequestItemApiRequest | targetSkuCode | @NotBlank |
| ExchangeRequestItemApiRequest | targetQuantity | @Positive |
| ApproveExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |
| CollectExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |
| PrepareExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |
| RejectExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |
| ShipExchangeBatchApiRequest | items | @NotEmpty |
| ShipItemApiRequest | exchangeClaimId | @NotBlank |
| ShipItemApiRequest | linkedOrderId | @NotBlank |
| ShipItemApiRequest | deliveryCompany | @NotBlank |
| ShipItemApiRequest | trackingNumber | @NotBlank |
| CompleteExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |
| ConvertToRefundBatchApiRequest | exchangeClaimIds | @NotEmpty |
| HoldExchangeBatchApiRequest | exchangeClaimIds | @NotEmpty |

---

## Query 시나리오

### Q01. GET /api/v1/market/exchanges/summary (교환 요약 조회)

#### [Q01-1] 교환 건 없을 때 요약 조회 - 빈 카운트 반환
- **우선순위**: P0
- **사전 데이터**: 없음 (tearDown 후 클린 상태)
- **요청**: `GET /exchanges/summary`
- **기대 응답**: 200, `data` not null, 모든 카운트 0
- **검증 포인트**: 빈 DB에서도 200 반환, null 대신 0 반환

#### [Q01-2] 다양한 상태 교환 건 존재 시 요약 조회
- **우선순위**: P0
- **사전 데이터**:
  ```
  ExchangeClaimJpaEntityFixtures.requestedEntity("sum-001")
  ExchangeClaimJpaEntityFixtures.requestedEntity("sum-002")
  ExchangeClaimJpaEntityFixtures.collectingEntity() → id 변경 ("sum-003")
  ExchangeClaimJpaEntityFixtures.completedEntity() → id 변경 ("sum-004")
  ExchangeClaimJpaEntityFixtures.rejectedEntity() → id 변경 ("sum-005")
  ```
- **요청**: `GET /exchanges/summary`
- **기대 응답**: 200, `data` not null, REQUESTED 카운트 2 이상

---

### Q02. GET /api/v1/market/exchanges (교환 목록 조회)

#### [Q02-1] 데이터 없을 때 빈 목록 반환
- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /exchanges?page=0&size=10`
- **기대 응답**: 200, `data.content` 빈 배열 or size=0

#### [Q02-2] 데이터 존재 시 목록 페이징 조회
- **우선순위**: P0
- **사전 데이터**: `requestedEntity` 5건 (id: list-001 ~ list-005)
- **요청**: `GET /exchanges?page=0&size=3`
- **기대 응답**: 200, `data.content.size()` = 3, `data.totalElements` >= 5

#### [Q02-3] 상태 필터로 REQUESTED만 조회
- **우선순위**: P1
- **사전 데이터**: REQUESTED 3건 + COMPLETED 2건
  ```
  requestedEntity("filter-req-001"), requestedEntity("filter-req-002"), requestedEntity("filter-req-003")
  entityWithStatus("filter-cmp-001", "COMPLETED"), entityWithStatus("filter-cmp-002", "COMPLETED")
  ```
- **요청**: `GET /exchanges?statuses=REQUESTED&page=0&size=10`
- **기대 응답**: 200, 반환 건 모두 status=REQUESTED

#### [Q02-4] 복수 상태 필터 조회 (REQUESTED + COLLECTING)
- **우선순위**: P1
- **사전 데이터**: REQUESTED 2건 + COLLECTING 1건 + COMPLETED 1건
- **요청**: `GET /exchanges?statuses=REQUESTED&statuses=COLLECTING&page=0&size=10`
- **기대 응답**: 200, COMPLETED 건 제외

#### [Q02-5] 날짜 범위 필터 조회
- **우선순위**: P1
- **사전 데이터**: requestedEntity 3건 (과거 데이터)
- **요청**: `GET /exchanges?dateField=REQUESTED&startDate=2020-01-01&endDate=2030-12-31&page=0&size=10`
- **기대 응답**: 200, 범위 내 건 포함

---

### Q03. GET /api/v1/market/exchanges/{exchangeClaimId} (교환 상세 조회)

#### [Q03-1] 존재하는 교환 건 상세 조회
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("detail-001")` 1건 저장
- **요청**: `GET /exchanges/detail-001`
- **기대 응답**: 200, `data.id` = "detail-001" (or 동일 값), `data.status` = "REQUESTED"

#### [Q03-2] 존재하지 않는 ID로 상세 조회 - 404
- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `GET /exchanges/non-existent-id-999`
- **기대 응답**: 404

#### [Q03-3] COMPLETED 상태 상세 조회 - linkedOrderId 확인
- **우선순위**: P1
- **사전 데이터**: `completedEntity()` 저장 (linkedOrderId = "ORDER-20260101-9999")
- **요청**: `GET /exchanges/{id}`
- **기대 응답**: 200, `data.linkedOrderId` 값 존재 (not null, not "")

---

## Command 시나리오

### C01. POST /api/v1/market/exchanges/request/batch (교환 요청)

#### [C01-1] 유효한 요청으로 교환 생성 성공
- **우선순위**: P0
- **사전 데이터**: ORDERED 상태 OrderItem 1건 저장
  ```java
  OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity("order-exc-001");
  orderRepository.save(order);
  OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem("order-exc-001");
  OrderItemJpaEntity saved = orderItemRepository.save(item);
  String orderItemId = saved.getId();
  ```
- **요청**:
  ```json
  POST /exchanges/request/batch
  {
    "items": [{
      "orderId": "{orderItemId}",
      "exchangeQty": 1,
      "reasonType": "SIZE_CHANGE",
      "reasonDetail": "사이즈 교환 요청",
      "originalProductId": 1000,
      "originalSkuCode": "SKU-RED-M",
      "targetProductGroupId": 1001,
      "targetProductId": 2001,
      "targetSkuCode": "SKU-RED-XL",
      "targetQuantity": 1
    }]
  }
  ```
- **기대 응답**: 200, `data.totalCount` = 1, `data.successCount` = 1, `data.failureCount` = 0
- **DB 검증**: `exchangeClaimRepository.findAll().size()` = 1, status = "REQUESTED"

#### [C01-2] items 빈 목록 요청 - 400
- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `{ "items": [] }`
- **기대 응답**: 400

#### [C01-3] orderId(orderItemId) 누락 - 400
- **우선순위**: P0
- **요청**: `items[0].orderId` = null 또는 빈 문자열
- **기대 응답**: 400

#### [C01-4] exchangeQty = 0 (비양수) - 400
- **우선순위**: P0
- **요청**: `items[0].exchangeQty` = 0
- **기대 응답**: 400 (@Positive 위반)

#### [C01-5] reasonType 누락 - 400
- **우선순위**: P1
- **요청**: `items[0].reasonType` = null
- **기대 응답**: 400

#### [C01-6] 존재하지 않는 orderItemId → 배치 부분 실패
- **우선순위**: P1
- **사전 데이터**: OrderItem 1건 저장 + 존재하지 않는 ID 1건
- **요청**: 유효한 item 1건 + 존재하지 않는 orderId 1건
- **기대 응답**: 200, `data.totalCount` = 1 (or 2), `data.successCount` = 1, `data.failureCount` >= 0
- **비고**: 존재하지 않는 OrderItem은 조회 시 무시되거나 실패 처리됨

---

### C02. POST /api/v1/market/exchanges/approve/batch (교환 승인 - 수거 시작)

#### [C02-1] REQUESTED 상태 교환 승인 성공
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("approve-001")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["approve-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: `exchangeClaimRepository.findById("approve-001")` 의 `exchangeStatus` = "COLLECTING"

#### [C02-2] 이미 COLLECTING 상태 → 잘못된 상태 전이 실패
- **우선순위**: P0
- **사전 데이터**: `collectingEntity()` (status = COLLECTING) 저장
- **요청**: `{ "exchangeClaimIds": ["{id}"] }`
- **기대 응답**: 200, `data.failureCount` = 1 (배치 부분 실패) 또는 400
- **비고**: COLLECTING → COLLECTING 전이 불가. 배치 처리 방식에 따라 전체 실패 or 부분 실패

#### [C02-3] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [] }`
- **기대 응답**: 400

---

### C03. POST /api/v1/market/exchanges/collect/batch (수거 완료)

#### [C03-1] COLLECTING 상태 교환 수거 완료 성공
- **우선순위**: P0
- **사전 데이터**: `collectingEntity()` 저장 (id 지정: "collect-001")
  - 연관 OrderItem도 저장 필요
- **요청**: `{ "exchangeClaimIds": ["collect-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "COLLECTED"

#### [C03-2] REQUESTED 상태에서 수거 완료 시도 → 잘못된 전이
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("collect-fail-001")` 저장
- **요청**: `{ "exchangeClaimIds": ["collect-fail-001"] }`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400 (REQUESTED → COLLECTED 전이 불가)

#### [C03-3] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [] }`
- **기대 응답**: 400

---

### C04. POST /api/v1/market/exchanges/prepare/batch (준비 완료)

#### [C04-1] COLLECTED 상태 교환 준비 완료 성공
- **우선순위**: P0
- **사전 데이터**: `entityWithStatus("prepare-001", "COLLECTED")` 저장 + 연관 OrderItem
- **요청**: `{ "exchangeClaimIds": ["prepare-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "PREPARING"

#### [C04-2] REQUESTED 상태에서 준비 시도 → 잘못된 전이
- **우선순위**: P1
- **사전 데이터**: `requestedEntity("prepare-fail-001")` 저장
- **요청**: `{ "exchangeClaimIds": ["prepare-fail-001"] }`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400

---

### C05. POST /api/v1/market/exchanges/reject/batch (거절)

#### [C05-1] REQUESTED 상태 교환 거절 성공
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("reject-001")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["reject-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "REJECTED"

#### [C05-2] COLLECTED 상태 교환 거절 성공 (거절 가능 상태)
- **우선순위**: P1
- **사전 데이터**: `entityWithStatus("reject-collected-001", "COLLECTED")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["reject-collected-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "REJECTED"
- **비고**: REJECTABLE = REQUESTED, COLLECTED, PREPARING 세 상태에서 가능

#### [C05-3] PREPARING 상태 교환 거절 성공
- **우선순위**: P1
- **사전 데이터**: `entityWithStatus("reject-prep-001", "PREPARING")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["reject-prep-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "REJECTED"

#### [C05-4] COMPLETED 상태 교환 거절 시도 → 잘못된 전이
- **우선순위**: P0
- **사전 데이터**: `completedEntity()` 저장
- **요청**: `{ "exchangeClaimIds": ["{id}"] }`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400

#### [C05-5] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [] }`
- **기대 응답**: 400

---

### C06. POST /api/v1/market/exchanges/ship/batch (재배송 출고)

#### [C06-1] PREPARING 상태 교환 재배송 출고 성공
- **우선순위**: P0
- **사전 데이터**: `entityWithStatus("ship-001", "PREPARING")` + 연관 OrderItem 저장
- **요청**:
  ```json
  POST /exchanges/ship/batch
  {
    "items": [{
      "exchangeClaimId": "ship-001",
      "linkedOrderId": "ORDER-20260319-0001",
      "deliveryCompany": "CJ대한통운",
      "trackingNumber": "1234567890"
    }]
  }
  ```
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "SHIPPING", linkedOrderId = "ORDER-20260319-0001"

#### [C06-2] items 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "items": [] }`
- **기대 응답**: 400

#### [C06-3] linkedOrderId 누락 - 400
- **우선순위**: P1
- **요청**: `items[0].linkedOrderId` = null
- **기대 응답**: 400

#### [C06-4] COLLECTED 상태에서 출고 시도 → 잘못된 전이
- **우선순위**: P1
- **사전 데이터**: `entityWithStatus("ship-fail-001", "COLLECTED")` 저장
- **요청**: 유효한 ship 요청
- **기대 응답**: 200, `data.failureCount` = 1 또는 400 (COLLECTED → SHIPPING 불가, PREPARING 필요)

---

### C07. POST /api/v1/market/exchanges/complete/batch (교환 완료)

#### [C07-1] SHIPPING 상태 교환 완료 성공
- **우선순위**: P0
- **사전 데이터**: `entityWithStatus("complete-001", "SHIPPING")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["complete-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**: status = "COMPLETED", completedAt not null

#### [C07-2] PREPARING 상태에서 완료 시도 → 잘못된 전이
- **우선순위**: P0
- **사전 데이터**: `entityWithStatus("complete-fail-001", "PREPARING")` 저장
- **요청**: `{ "exchangeClaimIds": ["complete-fail-001"] }`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400

#### [C07-3] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [] }`
- **기대 응답**: 400

---

### C08. POST /api/v1/market/exchanges/convert-to-refund/batch (교환 → 환불 전환)

#### [C08-1] REQUESTED 상태 교환 건 환불 전환 성공
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("cvt-001")` + 연관 OrderItem 저장
- **요청**: `{ "exchangeClaimIds": ["cvt-001"] }`
- **기대 응답**: 200, `data.successCount` = 1
- **DB 검증**:
  - exchange: status = "CANCELLED" (or 별도 상태)
  - refund_claims 테이블에 새 환불 건 생성 확인 (`refundClaimRepository.findAll().size()` > 0)
  - claim_histories에 이력 기록 확인
- **비고**: 도메인 간 상태 동기화 시나리오. ExchangeClaim cancel + RefundClaim 신규 생성

#### [C08-2] COMPLETED 상태 교환 건 환불 전환 시도 → 실패
- **우선순위**: P1
- **사전 데이터**: `completedEntity()` 저장
- **요청**: `{ "exchangeClaimIds": ["{id}"] }`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400 (종료 상태 전환 불가)

#### [C08-3] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [] }`
- **기대 응답**: 400

---

### C09. PATCH /api/v1/market/exchanges/hold/batch (보류/보류 해제)

#### [C09-1] 교환 건 보류 설정 성공
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("hold-001")` 저장
- **요청**:
  ```json
  PATCH /exchanges/hold/batch
  {
    "exchangeClaimIds": ["hold-001"],
    "isHold": true,
    "memo": "CS 확인 필요"
  }
  ```
- **기대 응답**: 200
- **DB 검증**: `exchangeClaimRepository.findById("hold-001")` 의 `holdReason` = "CS 확인 필요", `holdAt` not null

#### [C09-2] 보류 해제 성공
- **우선순위**: P0
- **사전 데이터**: holdReason/holdAt이 설정된 Entity 직접 저장
  ```java
  // holdReason, holdAt 컬럼에 값이 있는 엔티티를 직접 DB에 저장
  ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity("unhold-001");
  // → 이후 hold API 먼저 호출하여 보류 상태 만든 후 해제 테스트
  ```
- **요청**:
  ```json
  PATCH /exchanges/hold/batch
  {
    "exchangeClaimIds": ["unhold-001"],
    "isHold": false,
    "memo": null
  }
  ```
- **기대 응답**: 200
- **DB 검증**: `holdReason` = null, `holdAt` = null

#### [C09-3] 이미 보류 상태인 건에 다시 보류 설정 → ALREADY_HOLD 오류
- **우선순위**: P1
- **사전 데이터**: 먼저 hold API 호출하여 보류 상태로 만든 뒤 재호출
- **요청**: `isHold: true`, 동일 exchangeClaimId
- **기대 응답**: 200, `data.failureCount` = 1 또는 400

#### [C09-4] 보류 상태가 아닌 건에 보류 해제 시도 → NOT_HOLD_STATUS 오류
- **우선순위**: P1
- **사전 데이터**: `requestedEntity("not-hold-001")` (보류 미설정)
- **요청**: `isHold: false`, `exchangeClaimIds: ["not-hold-001"]`
- **기대 응답**: 200, `data.failureCount` = 1 또는 400

#### [C09-5] 빈 목록 - 400
- **우선순위**: P0
- **요청**: `{ "exchangeClaimIds": [], "isHold": true }`
- **기대 응답**: 400

---

### C10. POST /api/v1/market/exchanges/{exchangeClaimId}/histories (이력 메모 추가)

#### [C10-1] 존재하는 교환 건에 메모 추가 성공
- **우선순위**: P0
- **사전 데이터**: `requestedEntity("history-001")` 저장
- **요청**:
  ```json
  POST /exchanges/history-001/histories
  {
    "memo": "CS 팀 확인 완료. 교환 처리 진행."
  }
  ```
- **기대 응답**: 200 or 201
- **DB 검증**: `claimHistoryRepository.findAll()` 에서 claimType="EXCHANGE", claimId="history-001" 건 존재

#### [C10-2] 존재하지 않는 교환 건에 메모 추가 → 404
- **우선순위**: P0
- **사전 데이터**: 없음
- **요청**: `POST /exchanges/non-existent-id/histories`
- **기대 응답**: 404

---

## 전체 플로우 시나리오

### FLOW-1. 교환 요청 → 승인 → 수거 완료 → 준비 → 출고 → 완료 (해피패스 전체)

- **우선순위**: P0
- **사전 데이터**: ORDERED 상태 OrderItem 1건

```
Step 1: POST /exchanges/request/batch
        items[0].orderId = {orderItemId}, exchangeQty=1, reasonType="SIZE_CHANGE", ...
        → 200, successCount=1, exchangeClaimId 응답에서 추출

Step 2: POST /exchanges/approve/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1
        DB: status = COLLECTING

Step 3: POST /exchanges/collect/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1
        DB: status = COLLECTED

Step 4: POST /exchanges/prepare/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1
        DB: status = PREPARING

Step 5: POST /exchanges/ship/batch
        items[0] = {exchangeClaimId, linkedOrderId="ORDER-20260319-FLOW1", deliveryCompany="CJ대한통운", trackingNumber="TRK-FLOW-001"}
        → 200, successCount=1
        DB: status = SHIPPING, linkedOrderId = "ORDER-20260319-FLOW1"

Step 6: POST /exchanges/complete/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1
        DB: status = COMPLETED, completedAt not null

Step 7: GET /exchanges/{exchangeClaimId}
        → 200, data.status = "COMPLETED", data.linkedOrderId not blank
```

---

### FLOW-2. 교환 요청 → 거절

- **우선순위**: P0
- **사전 데이터**: ORDERED 상태 OrderItem 1건

```
Step 1: POST /exchanges/request/batch
        → 200, successCount=1, exchangeClaimId 추출

Step 2: POST /exchanges/reject/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1
        DB: status = REJECTED

Step 3: GET /exchanges/{exchangeClaimId}
        → 200, data.status = "REJECTED"
```

---

### FLOW-3. 교환 → 환불 전환 (ConvertToRefund)

- **우선순위**: P0
- **사전 데이터**: ORDERED 상태 OrderItem 1건

```
Step 1: POST /exchanges/request/batch
        → 200, successCount=1, exchangeClaimId 추출

Step 2: POST /exchanges/convert-to-refund/batch
        exchangeClaimIds = [{exchangeClaimId}]
        → 200, successCount=1

Step 3: GET /exchanges/{exchangeClaimId}
        → 200, data.status = "CANCELLED" (또는 전환된 상태)

Step 4: (별도) GET /refunds or refundClaimRepository 직접 검증
        → 새 환불 건 생성 확인 (도메인 간 동기화)
```

---

### FLOW-4. 보류 토글 (보류 설정 → 확인 → 해제)

- **우선순위**: P0
- **사전 데이터**: `requestedEntity("hold-flow-001")` 저장

```
Step 1: PATCH /exchanges/hold/batch
        { "exchangeClaimIds": ["hold-flow-001"], "isHold": true, "memo": "검토 필요" }
        → 200
        DB: holdReason = "검토 필요", holdAt not null

Step 2: GET /exchanges/hold-flow-001
        → 200, data.holdInfo not null (보류 중 표시)

Step 3: PATCH /exchanges/hold/batch
        { "exchangeClaimIds": ["hold-flow-001"], "isHold": false }
        → 200
        DB: holdReason = null, holdAt = null

Step 4: GET /exchanges/hold-flow-001
        → 200, data.holdInfo = null (보류 해제 확인)
```

---

### FLOW-5. 목록 조회 → 상세 조회 연계

- **우선순위**: P1
- **사전 데이터**: `requestedEntity` 3건 저장

```
Step 1: GET /exchanges?page=0&size=10
        → 200, content.size() > 0
        응답에서 첫 번째 item의 id(exchangeClaimId) 추출

Step 2: GET /exchanges/{extractedId}
        → 200, data.id = extractedId
        상세 필드 (status, reason, exchangeOption 등) 검증
```

---

### FLOW-6. 배치 부분 실패 시나리오

- **우선순위**: P1
- **사전 데이터**: REQUESTED 1건 + COMPLETED 1건 (이미 종료 상태)

```
Step 1: POST /exchanges/approve/batch
        exchangeClaimIds = ["{requested_id}", "{completed_id}"]
        → 200
        data.totalCount = 2
        data.successCount = 1   (REQUESTED → COLLECTING 성공)
        data.failureCount = 1   (COMPLETED → COLLECTING 전이 불가)

Step 2: DB 검증
        requested_id → status = "COLLECTING"
        completed_id → status = "COMPLETED" (변경 없음)
```

---

### FLOW-7. 이력 메모 추가 → 상세 조회 연계

- **우선순위**: P1
- **사전 데이터**: `requestedEntity("memo-flow-001")` 저장

```
Step 1: POST /exchanges/memo-flow-001/histories
        { "memo": "1차 CS 확인 완료" }
        → 200 or 201

Step 2: POST /exchanges/memo-flow-001/histories
        { "memo": "2차 처리 완료" }
        → 200 or 201

Step 3: (DB 검증) claimHistoryRepository에서 claimId="memo-flow-001" 건 2개 확인
```

---

## 인증/인가 시나리오

### AUTH-1. 비인증 요청으로 Command API 호출 - 401

- **우선순위**: P1
- **요청**: `givenUnauthenticated()` 로 `POST /exchanges/approve/batch`
- **기대 응답**: 401

### AUTH-2. 비인증 요청으로 Query API 호출 - 401

- **우선순위**: P1
- **요청**: `givenUnauthenticated()` 로 `GET /exchanges`
- **기대 응답**: 401

### AUTH-3. 권한 없는 사용자 Command API 호출 - 403

- **우선순위**: P2
- **요청**: `givenSellerUser("org-001", "product-group:read")` 로 `POST /exchanges/approve/batch`
- **기대 응답**: 403

### AUTH-4. 소유권 불일치 sellerId 교환 건 처리 시도

- **우선순위**: P2
- **사전 데이터**: sellerId=100L 교환 건 저장 (DEFAULT_SELLER_ID)
- **요청**: sellerId=999L 인 사용자 컨텍스트로 approve 호출
- **기대 응답**: 200, `data.failureCount` = 1 또는 400/403

---

## Fixture 설계

### 필요 Repository 목록

```java
@Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
@Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
@Autowired private OrderJpaRepository orderRepository;
@Autowired private OrderItemJpaRepository orderItemRepository;
// ConvertToRefund 시나리오 추가 시:
// @Autowired private RefundClaimJpaRepository refundClaimRepository;
```

### setUp/tearDown 패턴

```java
@BeforeEach
void setUp() {
    claimHistoryRepository.deleteAll();
    exchangeClaimRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}

@AfterEach
void tearDown() {
    claimHistoryRepository.deleteAll();
    exchangeClaimRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
}
```

### 주요 사전 데이터 설정 패턴

#### 패턴 A: 요청(request) 시나리오 - OrderItem 시딩 필요

```java
private String seedOrderItem(String orderId) {
    OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
    orderRepository.save(order);
    OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
    return orderItemRepository.save(item).getId();
}
```

#### 패턴 B: 상태 전이 시나리오 - ExchangeClaim 직접 시딩

```java
// REQUESTED 상태
ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity("exc-001");
exchangeClaimRepository.save(entity);

// 특정 상태 지정
ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.entityWithStatus("exc-002", "COLLECTED");
exchangeClaimRepository.save(entity);
```

#### 패턴 C: ID 추출이 필요한 시나리오 (request → 이후 단계)

```java
// request 배치 호출 후 응답에서 ID 추출
String exchangeClaimId = given().spec(givenSuperAdmin())
    .body(requestBody)
    .when()
    .post(REQUEST_BATCH)
    .then()
    .statusCode(200)
    .extract()
    .jsonPath()
    .getString("data.results[0].id");  // 실제 응답 구조에 따라 조정
```

#### 패턴 D: 다양한 상태 목록 조회용

```java
// 조회/필터 테스트 전 다양한 상태 데이터 직접 시딩
exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.requestedEntity("q-001"));
exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.requestedEntity("q-002"));
exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.entityWithStatus("q-003", "COLLECTING"));
exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.completedEntity()); // id 변경 필요
exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.rejectedEntity());  // id 변경 필요
```

### testFixtures 활용

```java
// 사용 가능한 Fixtures
ExchangeClaimJpaEntityFixtures.requestedEntity()           // REQUESTED 기본
ExchangeClaimJpaEntityFixtures.requestedEntity(String id)  // ID 지정 REQUESTED
ExchangeClaimJpaEntityFixtures.collectingEntity()          // COLLECTING (id 고정 주의)
ExchangeClaimJpaEntityFixtures.completedEntity()           // COMPLETED (id 고정 주의)
ExchangeClaimJpaEntityFixtures.rejectedEntity()            // REJECTED (id 고정 주의)
ExchangeClaimJpaEntityFixtures.entityWithStatus(id, status) // 상태 지정
ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(id, orderItemId) // orderItemId 지정
ExchangeClaimJpaEntityFixtures.minimalEntity(id)           // 최소 필드 (option/adjustment 없음)

ClaimHistoryJpaEntityFixtures.exchangeStatusChangeEntity(claimId) // EXCHANGE 이력
ClaimHistoryJpaEntityFixtures.manualMemoEntity(claimId)           // 수기 메모 이력
```

---

## 시나리오 카운트 요약

| 구분 | P0 | P1 | P2 | 합계 |
|------|----|----|----|----|
| Query (summary/list/detail) | 5 | 5 | 0 | 10 |
| Command (request/approve/collect/prepare/reject/ship/complete/convert/hold/histories) | 15 | 6 | 0 | 21 |
| 전체 플로우 | 4 | 3 | 0 | 7 |
| 인증/인가 | 2 | 2 | 2 | 4 (P2 포함) |
| **합계** | **26** | **16** | **2** | **42** |

---

## 구현 파일 경로

다음 경로에 테스트 파일을 구현합니다:

```
integration-test/src/test/java/com/ryuqq/marketplace/integration/exchange/
  ExchangeQueryE2ETest.java      (Q01~Q03 시나리오)
  ExchangeCommandE2ETest.java    (C01~C10 시나리오)
  ExchangeFlowE2ETest.java       (FLOW-1 ~ FLOW-7, AUTH 시나리오)
```

Tags 설정:
```java
@Tag("e2e")
@Tag("exchange")
@Tag("query")   // QueryE2ETest
@Tag("command") // CommandE2ETest
@Tag("flow")    // FlowE2ETest
```

---

## 다음 단계

```
/test-e2e web:exchange
```

위 명령으로 이 시나리오 문서를 기반으로 실제 Java E2E 테스트 코드를 생성합니다.
