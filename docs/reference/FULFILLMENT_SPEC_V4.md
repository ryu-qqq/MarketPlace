# Fulfillment (Shipment) 도메인 스펙 V4

> **V3 → V4 변경사항**
> - 배송 단위: `orderId` (주문) → `orderItemId` (상품주문) 단위로 변경
> - Shipment 필드: `orderId`, `orderNumber` 제거 → `orderItemId` 추가
> - 응답 구조: Order V5 스타일로 통일 (order, productOrder, receiver 블록 재사용)
> - Command API: `orderId` → `orderItemId`로 변경
> - DB: `shipments.order_id`, `shipments.order_number` 제거 → `shipments.order_item_id` 추가
> - 프론트엔드에서 Order V5와 동일한 컴포넌트/타입 재사용 가능

## 개요

배송을 담당하는 도메인입니다. 상품주문(orderItem) 단위로 출고/배송을 관리합니다.

> **공통 타입 참조**: `order`, `productOrder`, `payment`, `receiver` 블록은 [order_spec_v5.md](./order_spec_v5.md)와 **동일한 구조**입니다.

## 도메인 관계

```
Order ── 1:N ──→ OrderItem ── 1:1 ──→ Shipment (출고 배송)
                                      ※ 향후 부분 출고 지원 시 1:N 확장 가능
```

---

## Shipment (Aggregate Root)

### 필드 정의

| 필드 | 타입 | 설명 | 필수 |
|------|------|------|------|
| id | ShipmentId (UUIDv7) | 배송 ID | O |
| shipmentNumber | String | 배송 번호 (SHP-YYYYMMDD-XXXX) | O |
| orderItemId | Long | 상품주문 ID (order_items.id) | O |
| status | ShipmentStatus | 배송 상태 | O |
| method | ShipmentMethod | 배송 방식 | X |
| trackingNumber | String | 송장번호 | X |
| orderConfirmedAt | Instant | 발주확인 시각 | X |
| shippedAt | Instant | 출고 시각 | X |
| deliveredAt | Instant | 배송완료 시각 | X |
| createdAt | Instant | 생성 시각 | O |
| updatedAt | Instant | 수정 시각 | O |

---

## ShipmentStatus (Enum)

```
READY ──→ PREPARING ──→ SHIPPED ──→ IN_TRANSIT ──→ DELIVERED
              │                          │
              └── CANCELLED              └──→ FAILED
```

| 상태 | 설명 | Order 상태 연동 |
|------|------|----------------|
| READY | 결제 완료, 출고 대기 | ORDERED |
| PREPARING | 발주 확인, 배송 준비 중 | PREPARING |
| SHIPPED | 출고됨 (송장 등록) | SHIPPED |
| IN_TRANSIT | 배송 중 | SHIPPED |
| DELIVERED | 배송 완료 | DELIVERED |
| FAILED | 배송 실패 | CLAIM_IN_PROGRESS |
| CANCELLED | 취소됨 | CANCELLED |

---

## ShipmentMethod (VO)

| 필드 | 타입 | 설명 |
|------|------|------|
| type | ShipmentMethodType | COURIER / QUICK / VISIT / DESIGNATED_COURIER |
| courierCode | String | 택배사 코드 (VISIT인 경우 null) |
| courierName | String | 택배사 이름 (VISIT인 경우 null) |

---

## 조회 API (Query)

### 리스트 vs 상세 차이

| 구분 | 리스트 | 상세 |
|------|--------|------|
| payment | X | O |
| settlement | X | O |
| shipmentInfo.updatedAt | X | O |

### 배송 KPI 요약

**`GET /shipments/summary`**

```json
{
  "data": {
    "ready": 8,
    "preparing": 3,
    "shipped": 2,
    "inTransit": 1,
    "delivered": 0,
    "failed": 0,
    "cancelled": 0
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

---

### 배송 리스트 조회

**`GET /shipments`**

**Request (Query Parameters)**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `dateField` | Enum | O | `PAYMENT` \| `ORDER_CONFIRMED` \| `SHIPPED` |
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `status` | List | X | 배송 상태 (예: `PREPARING,SHIPPED`) |
| `searchField` | Enum | X | 검색 필드 |
| `searchWord` | String | X | 검색어 |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

#### dateField 옵션

| 값 | 설명 |
|------|------|
| `PAYMENT` | 결제일 기준 |
| `ORDER_CONFIRMED` | 발주확인일 기준 |
| `SHIPPED` | 발송처리일 기준 |

#### searchField 옵션

| 값 | 설명 |
|------|------|
| `ORDER_NUMBER` | 주문번호 |
| `TRACKING_NUMBER` | 송장번호 |
| `CUSTOMER_NAME` | 구매자/수령인명 |
| `CUSTOMER_PHONE` | 구매자/수령인 연락처 |
| `PRODUCT_NAME` | 상품명 |
| `SHOP_ORDER_NO` | 외부몰 주문번호 |

**Response 200**

```typescript
interface ShipmentListResponse {
  data: PageData<ShipmentListItem>;
  timestamp: string;
  requestId: string;
}

interface ShipmentListItem {
  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (Shipment 전용)
  // ═══════════════════════════════════════════════════════════════
  shipmentInfo: {
    shipmentId: string;                   // 배송 ID (UUIDv7)
    shipmentNumber: string;               // 배송 번호 (SHP-YYYYMMDD-XXXX)
    status: ShipmentStatus;               // 배송 상태
    method: ShipmentMethod | null;        // 배송 방식
    trackingNumber: string | null;        // 송장번호
    orderConfirmedAt: string | null;      // 발주확인 일시 (ISO 8601)
    shippedAt: string | null;             // 출고 일시 (ISO 8601)
    deliveredAt: string | null;           // 배송완료 일시 (ISO 8601)
    createdAt: string;                    // 생성 일시 (ISO 8601)
  };

  // ═══════════════════════════════════════════════════════════════
  // 소속 주문 정보 (Order V5 order 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  order: {
    orderId: string;
    orderNumber: string;
    status: OrderStatus;
    salesChannelId: number;
    shopId: number;
    shopCode: string;
    shopName: string;
    externalOrderNo: string | null;
    externalOrderedAt: string | null;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    createdAt: string;
    updatedAt: string;
  };

  // ═══════════════════════════════════════════════════════════════
  // 상품주문 정보 (Order V5 productOrder 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  productOrder: {
    orderItemId: number;
    productGroupId: number;
    productId: number;
    sellerId: number;
    brandId: number;
    skuCode: string | null;
    productGroupName: string;
    brandName: string;
    sellerName: string;
    mainImageUrl: string | null;
    externalProductId: string | null;
    externalOptionId: string | null;
    externalProductName: string | null;
    externalOptionName: string | null;
    externalImageUrl: string | null;
    unitPrice: number;
    quantity: number;
    totalAmount: number;
    discountAmount: number;
    paymentAmount: number;
  };

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (Order V5 receiver 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  receiver: {
    receiverName: string;
    receiverPhone: string;
    receiverZipcode: string;
    receiverAddress: string;
    receiverAddressDetail: string;
    deliveryRequest: string | null;
  };
}
```

---

### 배송 단건 조회

**`GET /shipments/{shipmentId}`**

```typescript
interface ShipmentDetailResponse {
  data: ShipmentDetail;
  timestamp: string;
  requestId: string;
}

interface ShipmentDetail {
  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (상세 전용 필드 추가)
  // ═══════════════════════════════════════════════════════════════
  shipmentInfo: {
    shipmentId: string;
    shipmentNumber: string;
    status: ShipmentStatus;
    method: ShipmentMethod | null;
    trackingNumber: string | null;
    orderConfirmedAt: string | null;
    shippedAt: string | null;
    deliveredAt: string | null;
    createdAt: string;
    updatedAt: string;                    // 상세 전용
  };

  // ═══════════════════════════════════════════════════════════════
  // 소속 주문 정보 (Order V5 order 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  order: {
    orderId: string;
    orderNumber: string;
    status: OrderStatus;
    salesChannelId: number;
    shopId: number;
    shopCode: string;
    shopName: string;
    externalOrderNo: string | null;
    externalOrderedAt: string | null;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    createdAt: string;
    updatedAt: string;
  };

  // ═══════════════════════════════════════════════════════════════
  // 상품주문 정보 (Order V5 productOrder 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  productOrder: {
    orderItemId: number;
    productGroupId: number;
    productId: number;
    sellerId: number;
    brandId: number;
    skuCode: string | null;
    productGroupName: string;
    brandName: string;
    sellerName: string;
    mainImageUrl: string | null;
    externalProductId: string | null;
    externalOptionId: string | null;
    externalProductName: string | null;
    externalOptionName: string | null;
    externalImageUrl: string | null;
    unitPrice: number;
    quantity: number;
    totalAmount: number;
    discountAmount: number;
    paymentAmount: number;
  };

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (Order V5 payment 블록 재사용, 상세 전용)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (Order V5 receiver 블록 재사용)
  // ═══════════════════════════════════════════════════════════════
  receiver: {
    receiverName: string;
    receiverPhone: string;
    receiverZipcode: string;
    receiverAddress: string;
    receiverAddressDetail: string;
    deliveryRequest: string | null;
  };

  // ═══════════════════════════════════════════════════════════════
  // 정산 정보 (상세 전용)
  // ═══════════════════════════════════════════════════════════════
  settlement: {
    commissionRate: number;
    fee: number;
    expectationSettlementAmount: number;
    settlementAmount: number;
    shareRatio: number;
    expectedSettlementDay: string | null;
    settlementDay: string | null;
  };
}
```

---

## Command API

### 발주 확인 (배송준비중 전환) - 다건

**`POST /shipments/confirm/batch`**

```typescript
interface ConfirmBatchRequest {
  orderItemIds: number[];               // 발주 확인할 상품주문 ID 목록
}
```

**Response 200**

```typescript
interface ConfirmBatchResponse {
  data: {
    requestedCount: number;
    successCount: number;
    failCount: number;
    results: ConfirmResultItem[];
  };
  timestamp: string;
  requestId: string;
}

interface ConfirmResultItem {
  orderItemId: number;
  success: boolean;
  shipmentId?: string;                  // 배송 ID (UUIDv7, 성공 시)
  shipmentNumber?: string;              // 배송 번호 (성공 시)
  status?: 'PREPARING';
  orderConfirmedAt?: string;
  errorCode?: string;
  errorMessage?: string;
}
```

---

### 배송 처리 (발송 처리) - 다건

**`POST /shipments/ship/batch`**

```typescript
interface ShipBatchRequest {
  requests: Array<{
    orderItemId: number;
    method: {
      type: ShipmentMethodType;
      courierCode: string;
    };
    trackingNumber: string;
  }>;
  memo?: string;
}
```

**Response 200**

```typescript
interface ShipBatchResponse {
  data: {
    requestedCount: number;
    successCount: number;
    failCount: number;
    results: ShipResultItem[];
  };
  timestamp: string;
  requestId: string;
}

interface ShipResultItem {
  orderItemId: number;
  success: boolean;
  shipment?: {
    shipmentId: string;
    shipmentNumber: string;
    status: 'SHIPPED';
    method: ShipmentMethod;
    trackingNumber: string;
    shippedAt: string;
  };
  errorCode?: string;
  errorMessage?: string;
}
```

**에러 코드**

| 에러 코드 | 상황 |
|-----------|------|
| `ORDER_ITEM_NOT_FOUND` | 존재하지 않는 상품주문 |
| `ORDER_NOT_READY` | 출고 가능 상태가 아님 |
| `ALREADY_SHIPPED` | 이미 출고된 상품주문 |
| `INVALID_TRACKING_NUMBER` | 잘못된 송장번호 |

---

### 배송 처리 (발송 처리) - 단건

**`POST /orders/{orderItemId}/ship`**

```typescript
// Request
interface ShipRequest {
  method: {
    type: ShipmentMethodType;
    courierCode: string;
  };
  trackingNumber: string;
}

// Response 200
interface ShipResponse {
  data: {
    shipmentId: string;
    shipmentNumber: string;
    status: 'SHIPPED';
    method: ShipmentMethod;
    trackingNumber: string;
    shippedAt: string;
  };
  timestamp: string;
  requestId: string;
}
```

---

## 공통 인터페이스

### PaymentInfo (Order V5 공통)

```typescript
interface PaymentInfo {
  paymentId: number;
  paymentStatus: string;
  paymentMethod: string;
  paymentAgencyId: string | null;
  paymentAmount: number;
  paidAt: string | null;
  canceledAt: string | null;
}
```

### ShipmentMethod

```typescript
interface ShipmentMethod {
  type: ShipmentMethodType;
  courierCode: string | null;
  courierName: string | null;
}
```

---

## Enum 정의

```typescript
type ShipmentStatus =
  | 'READY' | 'PREPARING' | 'SHIPPED' | 'IN_TRANSIT'
  | 'DELIVERED' | 'FAILED' | 'CANCELLED';

type ShipmentMethodType =
  | 'COURIER' | 'QUICK' | 'VISIT' | 'DESIGNATED_COURIER';

type OrderStatus =
  | 'ORDERED' | 'PREPARING' | 'SHIPPED' | 'DELIVERED' | 'CONFIRMED'
  | 'CANCELLED' | 'CLAIM_IN_PROGRESS' | 'REFUNDED' | 'EXCHANGED';
```

---

## API 요약

| 기능 | 엔드포인트 | Method |
|------|-----------|--------|
| 배송 KPI | `GET /shipments/summary` | GET |
| 배송 리스트 | `GET /shipments` | GET |
| 배송 상세 | `GET /shipments/{shipmentId}` | GET |
| 발주 확인 (다건) | `POST /shipments/confirm/batch` | POST |
| 배송 처리 (다건) | `POST /shipments/ship/batch` | POST |
| 배송 처리 (단건) | `POST /orders/{orderItemId}/ship` | POST |

---

## V3 → V4 변경 요약

| 항목 | V3 | V4 |
|------|----|----|
| 배송 단위 | `orderId` (주문) | `orderItemId` (상품주문) |
| Shipment 필드 | orderId, orderNumber, legacyOrderId, legacyShipmentId | orderItemId |
| DB shipments | order_id, order_number 컬럼 | order_item_id 컬럼 |
| 응답 구조 | V4 Order 스타일 (buyerInfo, orderProduct 등) | V5 스타일 (order, productOrder, receiver) |
| Command 참조 | `orderId` / `orderIds` | `orderItemId` / `orderItemIds` |
| 단건 배송 URL | `POST /orders/{orderId}/ship` | `POST /orders/{orderItemId}/ship` |
| 프론트 재사용 | X (Fulfillment 전용 타입) | O (Order V5 타입 그대로) |
