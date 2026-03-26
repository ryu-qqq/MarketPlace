# 세토프 웹훅 연동 가이드

> MarketPlace 내부 웹훅 API 연동 문서
> 최종 수정: 2026-03-25

---

## 1. 개요

세토프(자사몰)에서 주문/취소/반품/구매확정 이벤트가 발생하면, MarketPlace의 내부 웹훅 API를 호출하여 이벤트를 전달합니다.

MarketPlace는 수신된 이벤트를 InboundOrder / ClaimSync 파이프라인으로 처리합니다.

### Base URL

| 환경 | Base URL |
|------|----------|
| 로컬 | `http://localhost:8080` |
| Stage | `https://stage-marketplace-api.{도메인}` |
| Production | `https://marketplace-api.{도메인}` |

### 엔드포인트 목록

| 엔드포인트 | 설명 |
|------------|------|
| `POST /api/v1/market/internal/webhooks/orders/created` | 주문 생성 (결제 완료) |
| `POST /api/v1/market/internal/webhooks/orders/cancelled` | 즉시 취소 (판매자 확인 전) |
| `POST /api/v1/market/internal/webhooks/orders/return-requested` | 반품 요청 (배송 완료 후) |
| `POST /api/v1/market/internal/webhooks/orders/return-withdrawn` | 반품 철회 (구매자 요청 취소) |
| `POST /api/v1/market/internal/webhooks/orders/purchase-confirmed` | 구매 확정 (배송 완료 후 자동/수동) |

---

## 2. 인증

### 웹훅 경로는 인증 불필요

`/api/v1/market/internal/webhooks/**` 경로는 **VPC 내부 통신** 전용으로, Spring Security에서 `permitAll()`로 설정되어 있습니다.

**인증 헤더 없이** 호출할 수 있습니다.

> **참고**: `/api/v1/market/internal/**` 경로 중 webhooks가 아닌 다른 경로는 `X-Service-Token` 헤더가 필요합니다(`ROLE_INTERNAL_SERVICE` 권한 필요). 웹훅 경로만 예외적으로 인증이 면제됩니다.

### 공통 요청 헤더

```
Content-Type: application/json
```

---

## 3. 공통 응답 형식

### 성공 응답 (200 OK)

모든 성공 응답은 `ApiResponse` 래퍼로 감싸집니다.

```json
{
  "data": { ... },
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| data | Object/null | 응답 데이터 (엔드포인트별 상이) |
| timestamp | String | 응답 시간 (ISO 8601, `yyyy-MM-dd HH:mm:ss`) |
| requestId | String | 요청 추적 ID (traceId 또는 UUID) |

### 에러 응답 (RFC 7807 ProblemDetail)

에러 시 `application/problem+json` 형식으로 반환됩니다.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for request",
  "instance": "/api/v1/market/internal/webhooks/orders/created",
  "timestamp": "2026-03-25T10:30:00Z",
  "code": "VALIDATION_FAILED",
  "errors": {
    "salesChannelId": "must be positive",
    "items": "must not be empty"
  }
}
```

---

## 4. API 상세

### 4.1 POST /api/v1/market/internal/webhooks/orders/created (주문 생성)

**설명**: 세토프에서 결제가 완료되면 MarketPlace에 주문을 수신합니다. InboundOrder 파이프라인으로 진입하여 주문이 생성됩니다.

**Request Body**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| salesChannelId | long | O (@Positive) | 판매채널 ID |
| shopId | long | O (@Positive) | 샵 ID |
| externalOrderNo | String | O (@NotBlank) | 세토프 주문번호 |
| orderedAt | String (ISO 8601) | - | 주문일시 (예: `2026-03-25T01:00:00Z`) |
| buyerName | String | - | 구매자명 |
| buyerEmail | String | - | 구매자 이메일 |
| buyerPhone | String | - | 구매자 전화번호 |
| paymentMethod | String | - | 결제수단 (예: `CARD`) |
| totalPaymentAmount | int | - | 총 결제금액 |
| paidAt | String (ISO 8601) | - | 결제일시 |
| items | Array | O (@NotEmpty) | 주문 아이템 목록 |

**items 배열 요소**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| externalProductOrderId | String | O (@NotBlank) | 세토프 상품주문 ID |
| externalProductId | String | - | 세토프 상품 ID |
| externalOptionId | String | - | 세토프 옵션 ID |
| externalProductName | String | - | 상품명 |
| externalOptionName | String | - | 옵션명 |
| externalImageUrl | String | - | 상품 이미지 URL |
| unitPrice | int | - | 개당 판매가 |
| quantity | int | O (@Positive) | 수량 |
| totalAmount | int | - | 합계 금액 |
| discountAmount | int | - | 할인 금액 |
| paymentAmount | int | - | 실결제 금액 |
| receiverName | String | - | 수령인명 |
| receiverPhone | String | - | 수령인 전화번호 |
| receiverZipCode | String | - | 우편번호 |
| receiverAddress | String | - | 주소 |
| receiverAddressDetail | String | - | 상세주소 |
| deliveryRequest | String | - | 배송 요청사항 |

**Request 예시**:

```json
{
  "salesChannelId": 1,
  "shopId": 10,
  "externalOrderNo": "SETOF-ORD-20260325-001",
  "orderedAt": "2026-03-25T01:00:00Z",
  "buyerName": "홍길동",
  "buyerEmail": "buyer@example.com",
  "buyerPhone": "010-1234-5678",
  "paymentMethod": "CARD",
  "totalPaymentAmount": 30000,
  "paidAt": "2026-03-25T01:01:00Z",
  "items": [
    {
      "externalProductOrderId": "SETOF-PO-001",
      "externalProductId": "PROD-001",
      "externalOptionId": "OPT-001",
      "externalProductName": "테스트 상품",
      "externalOptionName": "블랙 / M",
      "externalImageUrl": "https://example.com/image.jpg",
      "unitPrice": 30000,
      "quantity": 1,
      "totalAmount": 30000,
      "discountAmount": 0,
      "paymentAmount": 30000,
      "receiverName": "김수령",
      "receiverPhone": "010-9999-8888",
      "receiverZipCode": "12345",
      "receiverAddress": "서울시 강남구",
      "receiverAddressDetail": "테헤란로 123",
      "deliveryRequest": "문 앞에 놔주세요"
    }
  ]
}
```

**Response Body** (`data` 필드):

| 필드 | 타입 | 설명 |
|------|------|------|
| total | int | 전체 수신 건수 |
| created | int | 변환 완료 건수 |
| pending | int | 매핑 대기 건수 |
| duplicated | int | 중복 건수 |
| failed | int | 실패 건수 |

**Response 예시**:

```json
{
  "data": {
    "total": 1,
    "created": 1,
    "pending": 0,
    "duplicated": 0,
    "failed": 0
  },
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.2 POST /api/v1/market/internal/webhooks/orders/cancelled (즉시 취소)

**설명**: 세토프에서 판매자 확인 전 즉시 취소가 발생하면 호출합니다. ClaimSync 파이프라인에서 `CANCEL/CANCEL_REQUEST`로 처리됩니다.

**Request Body**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| salesChannelId | long | O (@Positive) | 판매채널 ID |
| externalOrderId | String | O (@NotBlank) | 세토프 주문번호 |
| items | Array | O (@NotEmpty) | 취소 아이템 목록 |

**items 배열 요소**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| externalProductOrderId | String | O (@NotBlank) | 세토프 상품주문 ID |
| cancelReason | String | - | 취소 사유 |
| cancelDetailedReason | String | - | 취소 상세 사유 |
| cancelQuantity | int | O (@Positive) | 취소 수량 |

**Request 예시**:

```json
{
  "salesChannelId": 1,
  "externalOrderId": "SETOF-ORD-20260325-001",
  "items": [
    {
      "externalProductOrderId": "SETOF-PO-001",
      "cancelReason": "고객 변심",
      "cancelDetailedReason": "다른 상품으로 구매 예정",
      "cancelQuantity": 1
    }
  ]
}
```

**Response Body** (`data` 필드):

| 필드 | 타입 | 설명 |
|------|------|------|
| totalProcessed | int | 전체 처리 건수 |
| cancelSynced | int | 취소 동기화 건수 |
| refundSynced | int | 반품 동기화 건수 |
| exchangeSynced | int | 교환 동기화 건수 |
| skipped | int | 스킵 건수 (매핑 없는 경우) |
| failed | int | 실패 건수 |

**Response 예시**:

```json
{
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 1,
    "refundSynced": 0,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  },
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.3 POST /api/v1/market/internal/webhooks/orders/return-requested (반품 요청)

**설명**: 배송 완료 후 구매자가 반품을 요청하면 호출합니다. ClaimSync 파이프라인에서 `RETURN/RETURN_REQUEST`로 처리되어 RefundClaim이 생성됩니다.

**Request Body**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| salesChannelId | long | O (@Positive) | 판매채널 ID |
| externalOrderId | String | O (@NotBlank) | 세토프 주문번호 |
| items | Array | O (@NotEmpty) | 반품 요청 아이템 목록 |

**items 배열 요소**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| externalProductOrderId | String | O (@NotBlank) | 세토프 상품주문 ID |
| returnReason | String | - | 반품 사유 |
| returnDetailedReason | String | - | 반품 상세 사유 |
| returnQuantity | int | O (@Positive) | 반품 수량 |
| collectDeliveryCompany | String | - | 수거 택배사 (예: `CJ대한통운`) |
| collectTrackingNumber | String | - | 수거 송장번호 |

**Request 예시**:

```json
{
  "salesChannelId": 1,
  "externalOrderId": "SETOF-ORD-20260325-001",
  "items": [
    {
      "externalProductOrderId": "SETOF-PO-001",
      "returnReason": "상품 불량",
      "returnDetailedReason": "수령 후 파손 확인",
      "returnQuantity": 1,
      "collectDeliveryCompany": "CJ대한통운",
      "collectTrackingNumber": "1234567890123"
    }
  ]
}
```

**Response Body** (`data` 필드): 취소 웹훅과 동일한 `ClaimSyncWebhookResponse` 구조.

**Response 예시**:

```json
{
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 0,
    "refundSynced": 1,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  },
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.4 POST /api/v1/market/internal/webhooks/orders/return-withdrawn (반품 철회)

**설명**: 구매자가 반품 요청을 취소(철회)하면 호출합니다. ClaimSync 파이프라인에서 `RETURN/RETURN_REJECT`로 처리되어 `REFUND_REJECTED` 액션이 수행됩니다.

**Request Body**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| salesChannelId | long | O (@Positive) | 판매채널 ID |
| externalOrderId | String | O (@NotBlank) | 세토프 주문번호 |
| items | Array | O (@NotEmpty) | 반품 철회 아이템 목록 |

**items 배열 요소**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| externalProductOrderId | String | O (@NotBlank) | 세토프 상품주문 ID |

**Request 예시**:

```json
{
  "salesChannelId": 1,
  "externalOrderId": "SETOF-ORD-20260325-001",
  "items": [
    {
      "externalProductOrderId": "SETOF-PO-001"
    }
  ]
}
```

**Response Body** (`data` 필드): `ClaimSyncWebhookResponse` 구조 (취소/반품 웹훅과 동일).

**Response 예시**:

```json
{
  "data": {
    "totalProcessed": 1,
    "cancelSynced": 0,
    "refundSynced": 0,
    "exchangeSynced": 0,
    "skipped": 0,
    "failed": 0
  },
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 4.5 POST /api/v1/market/internal/webhooks/orders/purchase-confirmed (구매 확정)

**설명**: 배송 완료 후 자동 또는 수동으로 구매가 확정되면 호출합니다. 이미 확정된 항목은 무시됩니다 (멱등성 보장).

**Request Body**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| salesChannelId | long | O (@Positive) | 판매채널 ID |
| externalOrderId | String | O (@NotBlank) | 세토프 주문번호 |
| items | Array | O (@NotEmpty) | 구매 확정 아이템 목록 |

**items 배열 요소**:

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| externalProductOrderId | String | O (@NotBlank) | 세토프 상품주문 ID |

**Request 예시**:

```json
{
  "salesChannelId": 1,
  "externalOrderId": "SETOF-ORD-20260325-001",
  "items": [
    {
      "externalProductOrderId": "SETOF-PO-001"
    }
  ]
}
```

**Response Body** (`data` 필드): `null` (성공 시 데이터 없음)

**Response 예시**:

```json
{
  "data": null,
  "timestamp": "2026-03-25 10:30:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 5. 에러 코드

모든 에러 응답은 RFC 7807 ProblemDetail 형식이며, `x-error-code` 응답 헤더에도 에러 코드가 포함됩니다.

| HTTP 상태 | 에러 코드 | 설명 |
|-----------|-----------|------|
| 400 | `VALIDATION_FAILED` | 요청 바디 유효성 검증 실패 (@NotBlank, @Positive, @NotEmpty 등) |
| 400 | `BINDING_FAILED` | 요청 파라미터 바인딩 실패 |
| 400 | `CONSTRAINT_VIOLATION` | 제약 조건 위반 |
| 400 | `INVALID_ARGUMENT` | 잘못된 인자 |
| 400 | `INVALID_FORMAT` | JSON 파싱 실패 |
| 400 | `MISSING_PARAMETER` | 필수 파라미터 누락 |
| 404 | `RESOURCE_NOT_FOUND` | 리소스 없음 |
| 405 | `METHOD_NOT_ALLOWED` | 지원하지 않는 HTTP 메서드 |
| 409 | `STATE_CONFLICT` | 상태 충돌 (IllegalStateException) |
| 500 | `INTERNAL_ERROR` | 서버 내부 오류 |

**에러 응답 예시 (400 Validation Failed)**:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for request",
  "instance": "/api/v1/market/internal/webhooks/orders/created",
  "timestamp": "2026-03-25T10:30:00Z",
  "code": "VALIDATION_FAILED",
  "errors": {
    "salesChannelId": "must be positive",
    "items": "must not be empty"
  }
}
```

---

## 6. 테스트 방법

### 6.1 주문 생성 (curl)

```bash
curl -X POST http://localhost:8080/api/v1/market/internal/webhooks/orders/created \
  -H "Content-Type: application/json" \
  -d '{
    "salesChannelId": 1,
    "shopId": 10,
    "externalOrderNo": "SETOF-ORD-TEST-001",
    "orderedAt": "2026-03-25T01:00:00Z",
    "buyerName": "홍길동",
    "buyerEmail": "buyer@example.com",
    "buyerPhone": "010-1234-5678",
    "paymentMethod": "CARD",
    "totalPaymentAmount": 30000,
    "paidAt": "2026-03-25T01:01:00Z",
    "items": [
      {
        "externalProductOrderId": "SETOF-PO-TEST-001",
        "externalProductId": "PROD-001",
        "externalOptionId": "OPT-001",
        "externalProductName": "테스트 상품",
        "externalOptionName": "블랙 / M",
        "externalImageUrl": "https://example.com/image.jpg",
        "unitPrice": 30000,
        "quantity": 1,
        "totalAmount": 30000,
        "discountAmount": 0,
        "paymentAmount": 30000,
        "receiverName": "김수령",
        "receiverPhone": "010-9999-8888",
        "receiverZipCode": "12345",
        "receiverAddress": "서울시 강남구",
        "receiverAddressDetail": "테헤란로 123",
        "deliveryRequest": "문 앞에 놔주세요"
      }
    ]
  }'
```

### 6.2 즉시 취소 (curl)

```bash
curl -X POST http://localhost:8080/api/v1/market/internal/webhooks/orders/cancelled \
  -H "Content-Type: application/json" \
  -d '{
    "salesChannelId": 1,
    "externalOrderId": "SETOF-ORD-TEST-001",
    "items": [
      {
        "externalProductOrderId": "SETOF-PO-TEST-001",
        "cancelReason": "고객 변심",
        "cancelDetailedReason": "다른 상품으로 구매 예정",
        "cancelQuantity": 1
      }
    ]
  }'
```

### 6.3 반품 요청 (curl)

```bash
curl -X POST http://localhost:8080/api/v1/market/internal/webhooks/orders/return-requested \
  -H "Content-Type: application/json" \
  -d '{
    "salesChannelId": 1,
    "externalOrderId": "SETOF-ORD-TEST-001",
    "items": [
      {
        "externalProductOrderId": "SETOF-PO-TEST-001",
        "returnReason": "상품 불량",
        "returnDetailedReason": "수령 후 파손 확인",
        "returnQuantity": 1,
        "collectDeliveryCompany": "CJ대한통운",
        "collectTrackingNumber": "1234567890123"
      }
    ]
  }'
```

### 6.4 반품 철회 (curl)

```bash
curl -X POST http://localhost:8080/api/v1/market/internal/webhooks/orders/return-withdrawn \
  -H "Content-Type: application/json" \
  -d '{
    "salesChannelId": 1,
    "externalOrderId": "SETOF-ORD-TEST-001",
    "items": [
      {
        "externalProductOrderId": "SETOF-PO-TEST-001"
      }
    ]
  }'
```

### 6.5 구매 확정 (curl)

```bash
curl -X POST http://localhost:8080/api/v1/market/internal/webhooks/orders/purchase-confirmed \
  -H "Content-Type: application/json" \
  -d '{
    "salesChannelId": 1,
    "externalOrderId": "SETOF-ORD-TEST-001",
    "items": [
      {
        "externalProductOrderId": "SETOF-PO-TEST-001"
      }
    ]
  }'
```

---

## 7. 주의사항

### 7.1 멱등성 보장

- **주문 생성**: 동일한 `externalOrderNo`로 중복 호출 시 두 번째부터 `duplicated` 카운트로 처리됩니다. 에러 없이 200 OK 반환.
- **구매 확정**: 이미 CONFIRMED 상태인 OrderItem에 재호출해도 에러 없이 200 OK 반환.
- **취소/반품**: 매핑이 없는 `externalProductOrderId`로 호출 시 `skipped` 카운트로 처리됩니다. 에러 없이 200 OK 반환.

### 7.2 선행 조건

- **즉시 취소 / 반품 요청 / 반품 철회 / 구매 확정**: `externalProductOrderId`에 대한 ExternalOrderItemMapping이 존재해야 정상 처리됩니다. 매핑이 없으면 `skipped`로 처리됩니다.
- 매핑은 주문 생성 웹훅 호출 시 InboundOrder 파이프라인에서 자동 생성됩니다. 따라서 **주문 생성을 먼저 호출**한 후 나머지 웹훅을 호출해야 합니다.

### 7.3 재시도 정책 권장

- 네트워크 오류나 5xx 에러 발생 시 **최대 3회 재시도** 권장.
- 재시도 간격: 1초, 3초, 10초 (지수 백오프).
- 멱등성이 보장되므로 안전하게 재시도 가능.

### 7.4 타임아웃 설정

- 권장 연결 타임아웃: **3초**
- 권장 읽기 타임아웃: **10초**

### 7.5 호출 순서

일반적인 주문 라이프사이클:

```
주문 생성(created) → 구매 확정(purchase-confirmed)
주문 생성(created) → 즉시 취소(cancelled)
주문 생성(created) → [배송 완료] → 반품 요청(return-requested) → 반품 철회(return-withdrawn)
```

### 7.6 날짜/시간 형식

- 모든 날짜/시간 필드는 **ISO 8601 UTC** 형식을 사용합니다.
- 예: `2026-03-25T01:00:00Z`
- Java 타입: `Instant`

---

## 8. E2E 테스트 결과 (2026-03-25)

MarketPlace 로컬 서버(8080) + 테스트 데이터(external_order_item_mappings) 기반 실제 호출 검증.

| # | 웹훅 | HTTP | 처리 결과 | 비고 |
|---|---|---|---|---|
| 1 | 주문 생성 (/created) | ✅ 200 | pending:1 | inbound 파이프라인 적재 |
| 2 | 구매확정 (/purchase-confirmed) | ✅ 200 | 정상 처리 | CONFIRMED 상태에서 호출 |
| 3 | 즉시 취소 (/cancelled) | ✅ 200 | cancelSynced:1 | READY 상태에서 호출 |
| 4 | 반품 요청 (/return-requested) | ✅ 200 | refundSynced:1 | CONFIRMED → RETURN_REQUESTED |
| 5 | 반품 철회 (/return-withdrawn) | ✅ 200 | skipped:1 | refund_claim 없으면 정상 스킵 |

**주의**: 테스트 데이터로 검증 시 `external_order_item_mappings` 테이블에 매핑이 있어야 합니다. 매핑 없으면 모두 `skipped` 처리.
