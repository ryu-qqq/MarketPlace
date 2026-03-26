# OMS 주문/배송 E2E 테스트 시나리오 설계

## 대상 엔드포인트

| 구분 | Method | Path | UseCase |
|------|--------|------|---------|
| Q01 | GET | /api/v1/market/orders | GetProductOrderListUseCase |
| Q02 | GET | /api/v1/market/orders/{orderItemId} | GetOrderDetailUseCase |
| Q03 | GET | /api/v1/market/shipments/summary | GetShipmentSummaryUseCase |
| Q04 | GET | /api/v1/market/shipments | GetShipmentListUseCase |
| Q05 | GET | /api/v1/market/shipments/{shipmentId} | GetShipmentDetailUseCase |
| C01 | POST | /api/v1/market/shipments/confirm/batch | ConfirmShipmentBatchUseCase |
| C02 | POST | /api/v1/market/shipments/ship/batch | ShipBatchUseCase |
| C03 | POST | /api/v1/market/shipments/orders/{orderId}/ship | ShipSingleUseCase |

---

## 아키텍처 특이사항 (테스트에 영향)

### V4 간극 규칙 (모든 응답 검증 시 반드시 적용)

| 규칙 | 내용 | 검증 포인트 |
|------|------|------------|
| orderId = orderItemId | 응답의 orderId 필드에 내부 orderItemId가 들어감 | `data.content[0].orderId` == 저장된 orderItemId |
| null → "" | 문자열 null 필드는 ""로 직렬화 | buyerName, receiverName 등 절대 null 반환 안 됨 |
| null 금액 → 0 | 금액 필드 null은 0으로 직렬화 | billAmount, paymentAmount 등 0 반환 |
| legacyOrderId 제거 | 응답에 legacyOrderId 필드 없음 | 응답에 해당 키 미존재 검증 |

### N+1 방지 패턴
- 주문 목록 조회: orderItems 기준 페이징 → orderIds IN 쿼리로 일괄 조회
- 배송 확인/출고 후 Outbox에 이벤트 저장 (ShipmentOutboxJpaRepository 검증 필요)

### OrderItem 상태 전이
```
READY → (confirm) → PREPARING → (ship) → SHIPPED → (deliver) → DELIVERED
```

---

## Fixture 설계

### 필요 Repository 목록

| Repository | 용도 | tearDown 순서 |
|-----------|------|--------------|
| ShipmentOutboxJpaRepository | Outbox 이벤트 검증, tearDown 1순위 | 1 |
| ShipmentJpaRepository | 배송 데이터 직접 시딩 | 2 |
| OrderItemHistoryJpaRepository | 이력 검증, tearDown 필요 | 3 |
| OrderItemJpaRepository | 주문상품 데이터 시딩 | 4 |
| PaymentJpaRepository | 결제 데이터 시딩 (목록/상세 조회 시) | 5 |
| OrderJpaRepository | 주문 데이터 시딩 | 6 |

### 사전 데이터 시딩 헬퍼 패턴

```java
// 기본 주문 + 결제 + 주문상품 세트 저장 (기존 PaymentRefactoringE2ETest 패턴 재사용)
private String seedOrderWithPayment(String orderId, String paymentId, String paymentNumber) {
    orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
    paymentRepository.save(PaymentJpaEntity.create(paymentId, orderId, paymentNumber, ...));
    return orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId)).getId();
}

// 배송 확인까지 완료된 OrderItem 시딩
private String seedConfirmedOrderItem(String orderId) {
    // seedOrderItem 후 confirm/batch API 호출 또는 직접 ShipmentJpaEntityFixtures 사용
    String itemId = seedOrderItem(orderId);
    shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntityWithOrderItemId("shp-" + orderId, itemId));
    return itemId;
}
```

### 기존 Fixtures 활용

- `OrderJpaEntityFixtures.orderedEntity(id)` : READY 상태 주문
- `OrderItemJpaEntityFixtures.defaultItem(orderId)` : READY 상태 주문상품, sellerId=10L
- `ShipmentJpaEntityFixtures.readyEntity(id)` : READY 배송
- `ShipmentJpaEntityFixtures.entityWithStatus(id, "SHIPPED")` : SHIPPED 배송
- `ShipmentJpaEntityFixtures.deliveredEntity()` : DELIVERED 배송

---

## Query 시나리오: 주문 목록 (Q01)

### 엔드포인트 정보
- `GET /api/v1/market/orders`
- 권한: `order:read` 또는 SUPER_ADMIN
- 검색 파라미터: dateField, startDate, endDate, status, searchField, searchWord, sortKey, sortDirection, page, size

---

### [ORDER-Q01-01] 데이터 존재 시 주문 목록 정상 조회
- **Priority**: P0
- **Pre-data**: Order + Payment + OrderItem 2건 저장
- **Request**: `GET /orders`
- **Expected**:
  - HTTP 200
  - `data.content.size()` >= 1
  - `data.content[0].orderId` != null (UUIDv7 형식)
  - `data.content[0].payment.paymentId` != null
  - `data.content[0].payment.paymentNumber` =~ `PAY-\d{8}-\d{4}`
- **V4 간극 검증**: `data.content[0].orderId` 값이 orderItemId와 일치해야 함

---

### [ORDER-Q01-02] 데이터 없을 때 빈 목록 반환
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /orders`
- **Expected**:
  - HTTP 200
  - `data` not null (빈 페이지 객체 반환)

---

### [ORDER-Q01-03] status 필터 - 특정 상태 주문만 조회
- **Priority**: P1
- **Pre-data**: READY 상태 OrderItem 2건 + 다른 상태 1건
- **Request**: `GET /orders?status=READY`
- **Expected**:
  - HTTP 200
  - 반환된 모든 항목의 `orderProduct.orderStatus` == "READY"

---

### [ORDER-Q01-04] 날짜 범위 필터 - dateField=ORDERED
- **Priority**: P1
- **Pre-data**: 오늘 주문 2건 저장
- **Request**: `GET /orders?dateField=ORDERED&startDate=오늘&endDate=오늘`
- **Expected**:
  - HTTP 200
  - `data.content.size()` >= 1

---

### [ORDER-Q01-05] 검색어 필터 - searchField=CUSTOMER_NAME
- **Priority**: P1
- **Pre-data**: buyerName="홍길동" 주문 1건 저장
- **Request**: `GET /orders?searchField=CUSTOMER_NAME&searchWord=홍길동`
- **Expected**:
  - HTTP 200
  - 반환 결과에 해당 구매자 이름 포함

---

### [ORDER-Q01-06] 페이징 - page=0, size=2
- **Priority**: P1
- **Pre-data**: OrderItem 5건 저장
- **Request**: `GET /orders?page=0&size=2`
- **Expected**:
  - HTTP 200
  - `data.content.size()` == 2

---

### [ORDER-Q01-07] 정렬 - sortKey=CREATED_AT, sortDirection=ASC
- **Priority**: P2
- **Pre-data**: OrderItem 3건 저장
- **Request**: `GET /orders?sortKey=CREATED_AT&sortDirection=ASC`
- **Expected**:
  - HTTP 200
  - 반환 순서가 생성일 오름차순

---

### [ORDER-Q01-08] V4 간극 - orderId 필드가 orderItemId를 반환
- **Priority**: P0
- **Pre-data**: Order + Payment + OrderItem 1건 저장 (orderItemId 캡처)
- **Request**: `GET /orders`
- **Expected**:
  - HTTP 200
  - `data.content[0].orderId` == 저장된 orderItemId (NOT orderId)
- **참고**: V4 간극 규칙: response의 orderId 필드 = 내부 orderItemId

---

### [ORDER-Q01-09] V4 간극 - null 문자열 필드는 ""로 반환
- **Priority**: P0
- **Pre-data**: shopOrderNo=null인 자사몰 주문 1건 저장
- **Request**: `GET /orders`
- **Expected**:
  - HTTP 200
  - `data.content[0].externalOrderInfo` == null 또는 관련 문자열 필드 != null

---

### [ORDER-Q01-10] 인증/인가 - 권한 없는 사용자 403
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /orders` (인증 O, order:read 권한 X)
- **Expected**: HTTP 403

---

### [ORDER-Q01-11] 인증/인가 - 비인증 사용자 401
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /orders` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Query 시나리오: 주문 상세 (Q02)

### 엔드포인트 정보
- `GET /api/v1/market/orders/{orderItemId}`
- 권한: `order:read` 또는 SUPER_ADMIN
- PathVariable: orderItemId (UUIDv7 String)

---

### [ORDER-Q02-01] 존재하는 orderItemId로 상세 조회 성공
- **Priority**: P0
- **Pre-data**: Order + Payment + OrderItem 1건 저장, orderItemId 캡처
- **Request**: `GET /orders/{orderItemId}`
- **Expected**:
  - HTTP 200
  - `data.orderId` == orderItemId (V4 간극 규칙)
  - `data.payment.paymentId` != null, UUIDv7 패턴
  - `data.payment.paymentNumber` =~ `PAY-\d{8}-\d{4}`
  - `data.orderHistories` != null (List 타입)
  - `data.cancels` != null (빈 List 허용)
  - `data.claims` != null (빈 List 허용)

---

### [ORDER-Q02-02] 존재하지 않는 orderItemId → 404
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /orders/01940001-0000-7000-8000-000000000999`
- **Expected**: HTTP 404

---

### [ORDER-Q02-03] V4 간극 - legacyOrderId 필드 미포함 검증
- **Priority**: P0
- **Pre-data**: Order + Payment + OrderItem 1건
- **Request**: `GET /orders/{orderItemId}`
- **Expected**:
  - HTTP 200
  - 응답 body에 `legacyOrderId` 키 미존재

---

### [ORDER-Q02-04] V4 간극 - 결제 금액 null → 0 변환 검증
- **Priority**: P0
- **Pre-data**: Payment 없이 OrderItem만 저장 (또는 billAmount=0 결제)
- **Request**: `GET /orders/{orderItemId}`
- **Expected**:
  - HTTP 200
  - `data.payment.billAmount` == 0 (null이 아님)
  - `data.payment.paymentAmount` == 0 (null이 아님)

---

### [ORDER-Q02-05] 취소 이력 있는 주문 상세 조회
- **Priority**: P1
- **Pre-data**: Order + Payment + OrderItem 1건 + CancelJpaEntity 1건 (같은 orderItemId)
- **Request**: `GET /orders/{orderItemId}`
- **Expected**:
  - HTTP 200
  - `data.cancel.hasActiveCancel` == true 또는 `data.cancelIds.size()` >= 1
  - `data.cancels` 최근 3개 이내 반환

---

### [ORDER-Q02-06] 인증/인가 - 권한 없는 사용자 403
- **Priority**: P0
- **Pre-data**: OrderItem 1건 저장
- **Request**: `GET /orders/{orderItemId}` (인증 O, order:read 권한 X)
- **Expected**: HTTP 403

---

### [ORDER-Q02-07] 인증/인가 - 비인증 사용자 401
- **Priority**: P0
- **Request**: `GET /orders/any-id` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Query 시나리오: 배송 요약 (Q03)

### 엔드포인트 정보
- `GET /api/v1/market/shipments/summary`
- 권한: `shipment:read` 또는 SUPER_ADMIN

---

### [SHIPMENT-Q03-01] 다양한 상태의 배송 데이터 요약 조회
- **Priority**: P0
- **Pre-data**:
  - `ShipmentJpaEntityFixtures.readyEntity("ready-001")` 2건
  - `ShipmentJpaEntityFixtures.entityWithStatus("shipped-001", "SHIPPED")` 1건
  - `ShipmentJpaEntityFixtures.entityWithStatus("delivered-001", "DELIVERED")` 1건
- **Request**: `GET /shipments/summary`
- **Expected**:
  - HTTP 200
  - `data` != null (상태별 카운트 포함)

---

### [SHIPMENT-Q03-02] 데이터 없을 때 요약 조회
- **Priority**: P1
- **Pre-data**: 없음
- **Request**: `GET /shipments/summary`
- **Expected**:
  - HTTP 200
  - `data` != null (카운트 0으로 구성된 요약)

---

### [SHIPMENT-Q03-03] 비인증 사용자 요약 조회 → 401
- **Priority**: P0
- **Request**: `GET /shipments/summary` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Query 시나리오: 배송 목록 (Q04)

### 엔드포인트 정보
- `GET /api/v1/market/shipments`
- 권한: `shipment:read` 또는 SUPER_ADMIN
- 검색 파라미터: startDate, endDate, dateField, statuses, sellerIds, shopOrderNos, searchField, searchWord, sortKey, sortDirection, page, size

---

### [SHIPMENT-Q04-01] 배송 목록 정상 조회 (데이터 있을 때)
- **Priority**: P0
- **Pre-data**: ShipmentJpaEntityFixtures로 3건 저장
- **Request**: `GET /shipments?page=0&size=10`
- **Expected**:
  - HTTP 200
  - `data` != null

---

### [SHIPMENT-Q04-02] 배송 목록 빈 결과
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /shipments?page=0&size=10`
- **Expected**:
  - HTTP 200
  - `data` != null (빈 결과)

---

### [SHIPMENT-Q04-03] statuses 필터 - SHIPPED 상태만 조회
- **Priority**: P1
- **Pre-data**:
  - `readyEntity("filter-001")` 1건
  - `entityWithStatus("filter-002", "SHIPPED")` 2건
- **Request**: `GET /shipments?statuses=SHIPPED&page=0&size=10`
- **Expected**:
  - HTTP 200
  - `data` != null

---

### [SHIPMENT-Q04-04] 날짜 범위 필터
- **Priority**: P1
- **Pre-data**: 오늘 생성된 배송 2건
- **Request**: `GET /shipments?dateField=PAYMENT&startDate=오늘&endDate=오늘&page=0&size=10`
- **Expected**:
  - HTTP 200

---

### [SHIPMENT-Q04-05] 페이징 동작 확인
- **Priority**: P1
- **Pre-data**: 배송 5건 저장
- **Request**: `GET /shipments?page=0&size=2`
- **Expected**:
  - HTTP 200
  - 반환 항목 수 == 2

---

### [SHIPMENT-Q04-06] 비인증 사용자 목록 조회 → 401
- **Priority**: P0
- **Request**: `GET /shipments` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Query 시나리오: 배송 상세 (Q05)

### 엔드포인트 정보
- `GET /api/v1/market/shipments/{shipmentId}`
- 권한: `shipment:read` 또는 SUPER_ADMIN

---

### [SHIPMENT-Q05-01] 존재하는 shipmentId로 상세 조회 성공
- **Priority**: P0
- **Pre-data**: `ShipmentJpaEntityFixtures.readyEntity("shp-001")` 저장
- **Request**: `GET /shipments/shp-001`
- **Expected**:
  - HTTP 200
  - `data` != null

---

### [SHIPMENT-Q05-02] 존재하지 않는 shipmentId → 404
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `GET /shipments/non-existent-id`
- **Expected**: HTTP 404

---

### [SHIPMENT-Q05-03] SHIPPED 배송 상세 조회 - 송장 정보 포함
- **Priority**: P1
- **Pre-data**: `ShipmentJpaEntityFixtures.shippedEntity()` 저장
- **Request**: `GET /shipments/{shipmentId}`
- **Expected**:
  - HTTP 200
  - 송장번호, 택배사 코드 등 포함

---

## Command 시나리오: 발주확인 배치 (C01)

### 엔드포인트 정보
- `POST /api/v1/market/shipments/confirm/batch`
- 권한: `shipment:write` 또는 SUPER_ADMIN
- Request Body: `{ "orderIds": ["UUID1", "UUID2"] }` (@NotEmpty)
- 처리 흐름: OrderItem 조회 → confirm() → Shipment 생성 → Outbox 저장

---

### [SHIPMENT-C01-01] 유효한 orderItemId 목록으로 발주확인 성공
- **Priority**: P0
- **Pre-data**: READY 상태 OrderItem 2건 저장, itemId 캡처
- **Request**: `POST /shipments/confirm/batch { "orderIds": [itemId1, itemId2] }`
- **Expected**:
  - HTTP 200
  - `data.totalCount` == 2
  - `data.successCount` == 2
  - `data.failureCount` == 0
  - `data.results.size()` == 2
  - `data.results.findAll { it.success == true }.size()` == 2
- **DB 검증**: `shipmentRepository.findAll().size()` == 2
- **Outbox 검증**: `outboxRepository.findAll().size()` >= 2

---

### [SHIPMENT-C01-02] 일부 존재하지 않는 orderItemId 포함 - 부분 성공
- **Priority**: P0
- **Pre-data**: READY 상태 OrderItem 1건 저장 (itemId1 캡처)
- **Request**: `POST /shipments/confirm/batch { "orderIds": [itemId1, "non-existent-uuid"] }`
- **Expected**:
  - HTTP 200
  - `data.totalCount` == 1 (조회된 건만 처리)
  - `data.successCount` == 1
- **설명**: 존재하지 않는 ID는 조회 단계에서 제외되어 처리 대상에서 빠짐

---

### [SHIPMENT-C01-03] 빈 목록 요청 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `POST /shipments/confirm/batch { "orderIds": [] }`
- **Expected**: HTTP 400 (@NotEmpty Validation 실패)

---

### [SHIPMENT-C01-04] orderIds 필드 자체 누락 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `POST /shipments/confirm/batch { }`
- **Expected**: HTTP 400

---

### [SHIPMENT-C01-05] 권한 없는 사용자 발주확인 → 403
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `POST /shipments/confirm/batch` (shipment:write 권한 없는 사용자)
- **Expected**: HTTP 403

---

### [SHIPMENT-C01-06] 비인증 사용자 발주확인 → 401
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `POST /shipments/confirm/batch` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Command 시나리오: 송장등록 배치 (C02)

### 엔드포인트 정보
- `POST /api/v1/market/shipments/ship/batch`
- 권한: `shipment:write` 또는 SUPER_ADMIN
- Request Body: `{ "items": [{ "orderId", "trackingNumber", "courierCode", "courierName", "shipmentMethodType" }] }` (모두 @NotBlank/@NotNull)

---

### [SHIPMENT-C02-01] 발주확인 후 송장등록 배치 성공
- **Priority**: P0
- **Pre-data**: READY OrderItem 1건 → confirm/batch API로 발주확인 완료
- **Request**:
  ```json
  POST /shipments/ship/batch
  { "items": [{ "orderId": "{orderItemId}", "trackingNumber": "1234567890", "courierCode": "CJ", "courierName": "CJ대한통운", "shipmentMethodType": "COURIER" }] }
  ```
- **Expected**:
  - HTTP 200
  - `data.totalCount` == 1
  - `data.successCount` == 1
  - `data.failureCount` == 0
- **DB 검증**: Shipment 상태가 SHIPPED로 변경됨
- **Outbox 검증**: 출고 Outbox 이벤트 저장됨

---

### [SHIPMENT-C02-02] 복수 건 송장등록 배치 성공
- **Priority**: P0
- **Pre-data**: READY OrderItem 2건 → confirm/batch 후
- **Request**: items 2건으로 구성된 ship/batch 요청
- **Expected**:
  - HTTP 200
  - `data.totalCount` == 2
  - `data.successCount` == 2

---

### [SHIPMENT-C02-03] trackingNumber 누락 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**:
  ```json
  { "items": [{ "orderId": "uuid", "trackingNumber": "", "courierCode": "CJ", "courierName": "CJ", "shipmentMethodType": "COURIER" }] }
  ```
- **Expected**: HTTP 400 (@NotBlank 실패)

---

### [SHIPMENT-C02-04] courierCode 누락 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: courierCode="" 또는 null
- **Expected**: HTTP 400

---

### [SHIPMENT-C02-05] items 빈 목록 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: `{ "items": [] }`
- **Expected**: HTTP 400 (@NotEmpty 실패)

---

### [SHIPMENT-C02-06] orderId 누락 → 400
- **Priority**: P0
- **Request**: `{ "items": [{ "trackingNumber": "123", "courierCode": "CJ", ... }] }` (orderId null)
- **Expected**: HTTP 400 (@NotNull 실패)

---

### [SHIPMENT-C02-07] 비인증 사용자 → 401
- **Priority**: P0
- **Request**: `POST /shipments/ship/batch` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## Command 시나리오: 단건 송장등록 (C03)

### 엔드포인트 정보
- `POST /api/v1/market/shipments/orders/{orderId}/ship`
- 권한: `shipment:write` 또는 SUPER_ADMIN
- PathVariable: orderId (orderItemId)
- Request Body: `{ "trackingNumber", "courierCode", "courierName", "shipmentMethodType" }` (모두 @NotBlank)

---

### [SHIPMENT-C03-01] 발주확인 후 단건 송장등록 성공
- **Priority**: P1
- **Pre-data**: READY OrderItem 1건 → confirm/batch 완료
- **Request**:
  ```json
  POST /shipments/orders/{orderItemId}/ship
  { "trackingNumber": "9876543210", "courierCode": "LOTTE", "courierName": "롯데택배", "shipmentMethodType": "COURIER" }
  ```
- **Expected**:
  - HTTP 200
- **DB 검증**: 해당 OrderItem과 연결된 Shipment 상태 == SHIPPED

---

### [SHIPMENT-C03-02] trackingNumber 누락 → 400
- **Priority**: P0
- **Pre-data**: 없음
- **Request**: trackingNumber="" 또는 없음
- **Expected**: HTTP 400

---

### [SHIPMENT-C03-03] 발주확인 전 단건 송장등록 시도
- **Priority**: P1
- **Pre-data**: READY 상태 OrderItem만 존재 (Shipment 없음)
- **Request**: `POST /shipments/orders/{orderItemId}/ship + valid body`
- **Expected**: HTTP 400 또는 HTTP 404 (Shipment 미존재)
- **설명**: confirm/batch 없이는 Shipment가 없으므로 처리 불가

---

### [SHIPMENT-C03-04] 비인증 사용자 → 401
- **Priority**: P0
- **Request**: `POST /shipments/orders/any-id/ship` (인증 헤더 없음)
- **Expected**: HTTP 401

---

## 전체 플로우 시나리오

### [FLOW-01] 발주확인 → 출고(배치) → 배송목록 조회 정상 플로우 (P0)

```
Pre-data: READY OrderItem 2건 저장 (itemId1, itemId2 캡처)

Step 1: POST /shipments/confirm/batch { "orderIds": [itemId1, itemId2] }
        → HTTP 200, successCount=2, failureCount=0
        → DB: Shipment 2건 생성 확인

Step 2: POST /shipments/ship/batch { "items": [item1_body, item2_body] }
        → HTTP 200, totalCount=2, successCount=2
        → DB: Shipment.status == SHIPPED 확인
        → Outbox: 출고 이벤트 저장 확인

Step 3: GET /shipments?page=0&size=10
        → HTTP 200, data != null
```

---

### [FLOW-02] 발주확인 → 출고(단건) → 배송상세 조회 플로우 (P1)

```
Pre-data: READY OrderItem 1건 저장 (orderItemId 캡처)

Step 1: POST /shipments/confirm/batch { "orderIds": [orderItemId] }
        → HTTP 200, successCount=1

Step 2: POST /shipments/orders/{orderItemId}/ship { "trackingNumber": "9876543210", "courierCode": "LOTTE", "courierName": "롯데택배", "shipmentMethodType": "COURIER" }
        → HTTP 200

Step 3: GET /shipments (목록 조회로 shipmentId 추출)
        → HTTP 200

Step 4: GET /shipments/{shipmentId}
        → HTTP 200, data != null
```

---

### [FLOW-03] 주문목록 조회 → orderItemId 추출 → 주문상세 조회 플로우 (P0)

```
Pre-data: Order + Payment + OrderItem 1건 저장

Step 1: GET /orders
        → HTTP 200
        → response에서 data.content[0].orderId 추출 (= 내부 orderItemId)
        → V4 간극: orderId 필드 = orderItemId임을 검증

Step 2: GET /orders/{추출한 orderId}
        → HTTP 200
        → data.orderId == 추출한 orderId (일관성 검증)
        → data.payment.paymentId != null
        → data.payment.paymentNumber =~ PAY-\d{8}-\d{4}
        → data.orderHistories != null (List)
        → data.cancels != null (빈 List)
        → data.claims != null (빈 List)
        → legacyOrderId 키 미포함 검증
```

---

### [FLOW-04] 배치 처리 부분 실패 시나리오 (P1)

```
Pre-data: READY OrderItem 1건 저장 (itemId1 캡처)

Step 1: POST /shipments/confirm/batch { "orderIds": [itemId1, "non-existent-uuid-999"] }
        → HTTP 200
        → data.totalCount == 1 (존재하는 1건만 처리)
        → data.successCount == 1
        → data.failureCount == 0
        → 설명: 존재하지 않는 ID는 조회 단계에서 제외됨 (failureCount 방식이 아닌 skip 방식)

Step 2: GET /shipments
        → HTTP 200
        → 생성된 Shipment 1건만 존재 확인
```

---

### [FLOW-05] 완전한 OMS 주문-배송 전체 플로우 (P0)

```
Pre-data: Order + Payment + OrderItem 1건 저장 (orderId, orderItemId 캡처)

Step 1: GET /orders
        → HTTP 200
        → content[0].orderId == orderItemId (V4 간극 확인)
        → content[0].orderProduct.orderStatus == "READY"

Step 2: GET /orders/{orderItemId}
        → HTTP 200, 주문상세 확인, legacyOrderId 미포함

Step 3: POST /shipments/confirm/batch { "orderIds": [orderItemId] }
        → HTTP 200, successCount=1

Step 4: GET /shipments/summary
        → HTTP 200, READY 카운트 포함

Step 5: GET /shipments?page=0&size=10
        → HTTP 200, Shipment 1건 조회

Step 6: POST /shipments/ship/batch (송장 등록)
        → HTTP 200, successCount=1

Step 7: GET /shipments/{shipmentId}
        → HTTP 200, SHIPPED 상태 확인

Step 8: GET /orders/{orderItemId}
        → HTTP 200, orderProduct.orderStatus == "SHIPPED" 확인
```

---

## 시나리오 요약

### Query 시나리오 (25개)

| 그룹 | 시나리오 수 | P0 | P1 | P2 |
|------|------------|----|----|-----|
| 주문 목록 (Q01) | 11 | 6 | 4 | 1 |
| 주문 상세 (Q02) | 7 | 6 | 1 | 0 |
| 배송 요약 (Q03) | 3 | 2 | 1 | 0 |
| 배송 목록 (Q04) | 6 | 2 | 3 | 1 |
| 배송 상세 (Q05) | 3 | 2 | 1 | 0 |

### Command 시나리오 (18개)

| 그룹 | 시나리오 수 | P0 | P1 | P2 |
|------|------------|----|----|-----|
| 발주확인 배치 (C01) | 6 | 5 | 1 | 0 |
| 송장등록 배치 (C02) | 7 | 6 | 1 | 0 |
| 단건 송장등록 (C03) | 4 | 3 | 1 | 0 |

### 전체 플로우 시나리오 (5개)

| 시나리오 | Priority |
|----------|----------|
| FLOW-01: 발주확인 → 출고(배치) → 배송목록 | P0 |
| FLOW-02: 발주확인 → 출고(단건) → 배송상세 | P1 |
| FLOW-03: 주문목록 → 주문상세 연계 | P0 |
| FLOW-04: 배치 부분 실패 처리 | P1 |
| FLOW-05: OMS 전체 플로우 | P0 |

### 총계

| 구분 | 총 | P0 | P1 | P2 |
|------|----|----|----|----|
| Query | 30 | 18 | 11 | 1 |
| Command | 18 | 14 | 3 | 0 |
| Flow | 5 | 3 | 2 | 0 |
| **합계** | **53** | **35** | **16** | **1** |

---

## 테스트 구현 시 참조 사항

### 기존 테스트 파일 (패턴 재사용)

- `/Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/integration-test/src/test/java/com/ryuqq/marketplace/integration/shipment/ShipmentFlowE2ETest.java`
  - C01 (confirm/batch), C02 (ship/batch), C03 (단건), Q03 (summary), Q04 (list) 패턴
  - setUp/tearDown 순서 참조

- `/Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/integration-test/src/test/java/com/ryuqq/marketplace/integration/order/PaymentRefactoringE2ETest.java`
  - Q01 (주문목록), Q02 (주문상세), V4 간극 검증 패턴
  - saveOrderWithPayment 헬퍼 패턴
  - UUIDv7 패턴 정규식: `^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$`

### 테스트 클래스 분리 권고

```
integration-test/src/test/java/com/ryuqq/marketplace/integration/
  order/
    OrderQueryE2ETest.java      → Q01 (목록), Q02 (상세), V4 간극 검증
  shipment/
    ShipmentQueryE2ETest.java   → Q03 (요약), Q04 (목록), Q05 (상세)
    ShipmentCommandE2ETest.java → C01 (confirm), C02 (ship/batch), C03 (단건)
    ShipmentFlowE2ETest.java    → 기존 파일, FLOW 시나리오 보완
```

### tearDown 필수 순서 (FK 제약 위반 방지)

```java
@BeforeEach
@AfterEach
void cleanUp() {
    outboxRepository.deleteAll();     // 1. ShipmentOutboxJpaRepository
    shipmentRepository.deleteAll();   // 2. ShipmentJpaRepository
    orderHistoryRepository.deleteAll(); // 3. OrderItemHistoryJpaRepository
    orderItemRepository.deleteAll();  // 4. OrderItemJpaRepository
    paymentRepository.deleteAll();    // 5. PaymentJpaRepository
    orderRepository.deleteAll();      // 6. OrderJpaRepository
}
```

### V4 간극 검증 상수 (테스트 클래스 공통 선언)

```java
private static final String UUID_V7_PATTERN =
    "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
private static final String PAYMENT_NUMBER_PATTERN = "^PAY-\\d{8}-\\d{4}$";
```

### 권한 설정 가이드

| 역할 | 메서드 | 주문 Query | 배송 Command |
|------|--------|-----------|-------------|
| SUPER_ADMIN | `givenSuperAdmin()` | 허용 | 허용 |
| order:read 권한 | `givenWithPermission("order:read")` | 허용 | 거부 |
| shipment:write 권한 | `givenWithPermission("shipment:write")` | 거부 | 허용 |
| 권한 없음 | `givenAuthenticatedUser()` | 403 | 403 |
| 비인증 | `givenUnauthenticated()` | 401 | 401 |
