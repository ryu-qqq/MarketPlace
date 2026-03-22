# Internal Webhook API 명세서

자사몰 → MarketPlace 내부 웹훅 수신 API.
VPC 내부 통신이므로 인증 없음. 자사몰도 SalesChannel로 등록되어 있으므로 기존 InboundOrder/ClaimSync 파이프라인 재사용.

---

## 공통 사항

| 항목 | 값 |
|------|-----|
| Base Path | `/api/v1/internal/webhooks` |
| 패키지 위치 | `adapter-in/rest-api/.../internal/` |
| 인증 | 없음 (VPC 내부 Local DNS) |
| 처리 방식 | 동기 |

### 공통 응답 구조

```json
// 성공 (200 OK)
{
  "code": 200,
  "message": "success",
  "data": { ... }  // 엔드포인트별 상이
}
```

---

## 1. 주문 생성 (ORDER_CREATED)

자사몰 결제 완료 시 호출. 기존 `InboundOrderReceiveCoordinator.receiveAll()` 재사용.

### Endpoint

```
POST /api/v1/internal/webhooks/orders/created
```

### Request Body

```json
{
  "salesChannelId": 3,
  "shopId": 10,
  "externalOrderNo": "OWN-20260321-00001",
  "orderedAt": "2026-03-21T10:30:00Z",
  "buyerName": "홍길동",
  "buyerEmail": "hong@example.com",
  "buyerPhone": "010-1234-5678",
  "paymentMethod": "CARD",
  "totalPaymentAmount": 89000,
  "paidAt": "2026-03-21T10:30:05Z",
  "items": [
    {
      "externalProductOrderId": "OWN-ITEM-001",
      "externalProductId": "PROD-100",
      "externalOptionId": "OPT-200",
      "externalProductName": "캐시미어 니트",
      "externalOptionName": "블랙 / M",
      "externalImageUrl": "https://cdn.example.com/img.jpg",
      "unitPrice": 89000,
      "quantity": 1,
      "totalAmount": 89000,
      "discountAmount": 0,
      "paymentAmount": 89000,
      "receiverName": "홍길동",
      "receiverPhone": "010-1234-5678",
      "receiverZipCode": "06123",
      "receiverAddress": "서울시 강남구",
      "receiverAddressDetail": "101동 202호",
      "deliveryRequest": "부재 시 문앞"
    }
  ]
}
```

### Request DTO → Application DTO 매핑

| Request 필드 | → | Application DTO |
|-------------|---|-----------------|
| 전체 | → | `ExternalOrderPayload` |
| items[*] | → | `ExternalOrderItemPayload` |
| salesChannelId, shopId | → | `coordinator.receiveAll()` 파라미터 |

### Response Body (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "created": 1,
    "pending": 0,
    "duplicated": 0,
    "failed": 0
  }
}
```

### 재사용 컴포넌트

- `InboundOrderReceiveCoordinator.receiveAll(payloads, salesChannelId, shopId, now)`
- 응답: `InboundOrderPollingResult`

---

## 2. 즉시 취소 (ORDER_CANCELLED)

판매자 확인 전 즉시 취소. 기존 `ClaimSyncCoordinator.syncAll()` 재사용.

### Endpoint

```
POST /api/v1/internal/webhooks/orders/cancelled
```

### Request Body

```json
{
  "salesChannelId": 3,
  "externalOrderId": "OWN-20260321-00001",
  "items": [
    {
      "externalProductOrderId": "OWN-ITEM-001",
      "cancelReason": "단순 변심",
      "cancelDetailedReason": "다른 색상으로 재주문 예정",
      "cancelQuantity": 1
    }
  ]
}
```

### Request DTO → Application DTO 매핑

| Request 필드 | → | ExternalClaimPayload 필드 |
|-------------|---|--------------------------|
| externalOrderId | → | externalOrderId |
| items[*].externalProductOrderId | → | externalProductOrderId |
| (고정값) "CANCEL" | → | claimType |
| (고정값) "CANCEL_REQUEST" | → | claimStatus |
| items[*].cancelReason | → | claimReason |
| items[*].cancelDetailedReason | → | claimDetailedReason |
| items[*].cancelQuantity | → | requestQuantity |
| (고정값) "BUYER" | → | requestChannel |
| 나머지 | → | null |

### Response Body (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 1,
    "refundSynced": 0,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  }
}
```

### 재사용 컴포넌트

- `ClaimSyncCoordinator.syncAll(payloads, salesChannelId)`
- 응답: `ClaimSyncResult`

---

## 3. 반품 요청 (RETURN_REQUESTED)

배송 완료 후 구매자 반품 요청. 기존 `ClaimSyncCoordinator.syncAll()` 재사용.

### Endpoint

```
POST /api/v1/internal/webhooks/orders/return-requested
```

### Request Body

```json
{
  "salesChannelId": 3,
  "externalOrderId": "OWN-20260321-00001",
  "items": [
    {
      "externalProductOrderId": "OWN-ITEM-001",
      "returnReason": "상품 불량",
      "returnDetailedReason": "지퍼 파손",
      "returnQuantity": 1,
      "collectDeliveryCompany": "CJ대한통운",
      "collectTrackingNumber": "1234567890"
    }
  ]
}
```

### Request DTO → Application DTO 매핑

| Request 필드 | → | ExternalClaimPayload 필드 |
|-------------|---|--------------------------|
| externalOrderId | → | externalOrderId |
| items[*].externalProductOrderId | → | externalProductOrderId |
| (고정값) "RETURN" | → | claimType |
| (고정값) "RETURN_REQUEST" | → | claimStatus |
| items[*].returnReason | → | claimReason |
| items[*].returnDetailedReason | → | claimDetailedReason |
| items[*].returnQuantity | → | requestQuantity |
| items[*].collectDeliveryCompany | → | collectDeliveryCompany |
| items[*].collectTrackingNumber | → | collectTrackingNumber |
| (고정값) "BUYER" | → | requestChannel |
| 나머지 | → | null |

### Response Body (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 0,
    "refundSynced": 1,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  }
}
```

### 재사용 컴포넌트

- `ClaimSyncCoordinator.syncAll(payloads, salesChannelId)`
- 응답: `ClaimSyncResult`

---

## 4. 반품 철회 (RETURN_WITHDRAWN)

구매자가 반품 요청 취소. 기존 `ClaimSyncCoordinator.syncAll()` 재사용.

### Endpoint

```
POST /api/v1/internal/webhooks/orders/return-withdrawn
```

### Request Body

```json
{
  "salesChannelId": 3,
  "externalOrderId": "OWN-20260321-00001",
  "items": [
    {
      "externalProductOrderId": "OWN-ITEM-001"
    }
  ]
}
```

### Request DTO → Application DTO 매핑

| Request 필드 | → | ExternalClaimPayload 필드 |
|-------------|---|--------------------------|
| externalOrderId | → | externalOrderId |
| items[*].externalProductOrderId | → | externalProductOrderId |
| (고정값) "RETURN" | → | claimType |
| (고정값) "RETURN_REJECT" | → | claimStatus |
| 나머지 | → | null |

> 참고: 기존 RefundClaimSyncHandler에서 `RETURN_REJECT` 상태는 `REFUND_REJECTED` 액션으로 매핑됨.
> 구매자 "철회"와 판매자 "거절"의 비즈니스 의미가 다를 수 있어 별도 claimStatus 추가 검토 필요.

### Response Body (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 0,
    "refundSynced": 1,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  }
}
```

### 재사용 컴포넌트

- `ClaimSyncCoordinator.syncAll(payloads, salesChannelId)`
- 응답: `ClaimSyncResult`

---

## 5. 구매 확정 (PURCHASE_CONFIRMED)

배송 완료 후 자동/수동 구매 확정. `ConfirmOrderUseCase` 직접 호출 (클레임이 아닌 주문 상태 전환).

### Endpoint

```
POST /api/v1/internal/webhooks/orders/purchase-confirmed
```

### Request Body

```json
{
  "salesChannelId": 3,
  "externalOrderId": "OWN-20260321-00001",
  "items": [
    {
      "externalProductOrderId": "OWN-ITEM-001"
    }
  ]
}
```

### 처리 흐름

ClaimSync 재사용 불가 — 구매확정은 클레임이 아님.
별도 처리 필요:

```
1. ExternalOrderItemMappingReadManager
     .findBySalesChannelIdAndExternalProductOrderId(salesChannelId, externalProductOrderId)
   → 내부 orderItemId 역조회

2. ConfirmOrderUseCase.execute(
     new OrderItemStatusCommand(List.of(orderItemId), "WEBHOOK")
   )
   → OrderItem.confirm() 호출
```

### Response Body (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 재사용 컴포넌트

- `ExternalOrderItemMappingReadManager` (ID 역조회)
- `ConfirmOrderUseCase.execute(OrderItemStatusCommand)`

---

## 패키지 구조

```
adapter-in/rest-api/.../internal/
├── InternalWebhookEndpoints.java
├── controller/
│   ├── OrderCreatedWebhookController.java
│   ├── OrderCancelledWebhookController.java
│   ├── ReturnRequestedWebhookController.java
│   ├── ReturnWithdrawnWebhookController.java
│   └── PurchaseConfirmedWebhookController.java
├── dto/
│   ├── request/
│   │   ├── OrderCreatedWebhookRequest.java
│   │   ├── OrderCancelledWebhookRequest.java
│   │   ├── ReturnRequestedWebhookRequest.java
│   │   ├── ReturnWithdrawnWebhookRequest.java
│   │   └── PurchaseConfirmedWebhookRequest.java
│   └── response/
│       ├── OrderCreatedWebhookResponse.java     (= InboundOrderPollingResult 래핑)
│       └── ClaimSyncWebhookResponse.java        (= ClaimSyncResult 래핑)
└── mapper/
    └── InternalWebhookApiMapper.java

application 레이어: 신규 코드 없음 (기존 Coordinator/UseCase 직접 호출)
단, PURCHASE_CONFIRMED용 UseCase는 Controller에서 직접 조합:
  - ExternalOrderItemMappingReadManager (기존)
  - ConfirmOrderUseCase (기존)
```

---

## Application 레이어 확장 검토 (PURCHASE_CONFIRMED)

Controller에서 2개 컴포넌트를 직접 조합하는 것이 API-CTR-007(비즈니스 로직 금지) 위반 가능성 있음.
필요시 `inboundorder` 패키지에 UseCase 추가:

```java
// application/inboundorder/port/in/command/
public interface ReceivePurchaseConfirmedWebhookUseCase {
    void execute(long salesChannelId, List<String> externalProductOrderIds);
}

// application/inboundorder/service/command/
@Service
public class ReceivePurchaseConfirmedWebhookService implements ReceivePurchaseConfirmedWebhookUseCase {
    // ExternalOrderItemMappingReadManager + ConfirmOrderUseCase 조합
}
```

---

## 미결 사항

1. **RETURN_WITHDRAWN**: 기존 RefundClaimSyncHandler의 `RETURN_REJECT` 상태를 "철회"로 재사용할지, 별도 claimStatus(`RETURN_WITHDRAWN`) 추가할지
2. **멱등성**: ORDER_CREATED는 InboundOrder의 externalOrderNo 중복 체크로 보장. ClaimSync는 ClaimSyncLog로 보장. PURCHASE_CONFIRMED는 OrderItem.confirm()이 이미 CONFIRMED면 예외 발생 → 멱등 처리 필요 여부
3. **SecurityConfig**: `/api/v1/internal/**` 경로를 permitAll로 추가 필요
