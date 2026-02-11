# 취소 관리 API 스펙 (Cancel)

> **적용 범위**: 배송 시작 **전** (PENDING ~ PREPARING) 취소만 담당
>
> ⚠️ **배송 후**는 → **Claim (환불/교환)** 도메인 참조
> - 환불: `/refunds/*` → [CLAIM_SPEC_UNIFIED.md](./CLAIM_SPEC_UNIFIED.md)
> - 교환: `/exchanges/*` → [CLAIM_SPEC_UNIFIED.md](./CLAIM_SPEC_UNIFIED.md)

---

> **공통 타입 참조**: 이 문서에서 사용하는 `BuyerInfo`, `PaymentDetail`, `ReceiverInfo`, `PaymentShipmentInfo`, `OrderProduct`, `ExternalOrderInfo` 등의 인터페이스는 [ORDER_SPEC_V4.md](./ORDER_SPEC_V4.md)를 참조하세요.

---

## 📋 목차

1. [도메인 개요](#1-도메인-개요)
2. [Cancel 엔티티](#2-cancel-엔티티)
3. [타입 정의](#3-타입-정의)
4. [취소 API](#4-취소-api)
5. [조회 API](#5-조회-api)
6. [상태 전이](#6-상태-전이)

---

## 1. 도메인 개요

### 1-1. Cancel vs Claim 구분

| 구분 | Cancel (취소) | Claim (환불/교환) |
|------|--------------|------------------|
| **시점** | 배송 시작 **전** | 배송 완료 **후** |
| **Order 상태** | PENDING, CONFIRMED, PREPARING | DELIVERED |
| **상품 수거** | ❌ 불필요 | ✅ 필요 (ClaimShipment) |
| **API 경로** | `/orders/{orderId}/*`, `/cancels/*` | `/refunds/*`, `/exchanges/*` |

### 1-2. 취소 가능 조건

| Order 상태 | 취소 가능 | 비고 |
|------------|:--------:|------|
| `PENDING` | ✅ | 결제 대기 중 |
| `CONFIRMED` | ✅ | 결제 완료, 상품 준비 전 |
| `PREPARING` | ✅ | 상품 준비 중 (준비 중단 필요) |
| `SHIPPING` | ❌ | 이미 배송 시작 → 배송 완료 후 Claim으로 |
| `DELIVERED` | ❌ | Claim (환불/교환)으로 유도 |

### 1-3. 취소 타입

| 타입 | 설명 | 승인 필요 | 트리거 |
|------|------|:--------:|--------|
| `BUYER_CANCEL` | 구매자가 취소 요청 | ✅ | 구매자 신청 → 판매자 승인/거부 |
| `SELLER_CANCEL` | 판매자가 직접 취소 | ❌ | 판매자가 즉시 처리 (재고 부족 등) |

---

## 2. Cancel 엔티티

### 2-1. 필드 정의

| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| `cancelId` | string (UUIDv7) | ✅ | 취소 고유 ID |
| `cancelNumber` | string | ✅ | 취소 번호 (CAN-YYYYMMDD-XXXX) |
| `orderId` | string (UUIDv7) | ✅ | 원 주문 참조 |
| `type` | CancelType | ✅ | 취소 타입 (BUYER_CANCEL / SELLER_CANCEL) |
| `status` | CancelStatus | ✅ | 취소 상태 |
| `cancelQty` | number | ✅ | 취소 수량 |
| `reason` | CancelReasonType | ✅ | 취소 사유 |
| `reasonDetail` | string | | 상세 사유 |
| `refundInfo` | RefundInfo | | 환불 정보 |
| `requestedAt` | string | ✅ | 요청 시각 |
| `completedAt` | string | | 완료 시각 |
| `createdAt` | string | ✅ | 생성 시각 |
| `updatedAt` | string | ✅ | 수정 시각 |

> **참고**: 처리 메모, 거부 사유 등은 `cancelHistories`에 기록됩니다. (Refund/Exchange와 동일한 패턴)

### 2-2. 취소 번호 형식

```
CAN-YYYYMMDD-XXXX
예: CAN-20240115-0001
```

---

## 3. 타입 정의

### 3-1. CancelType (enum)

```typescript
enum CancelType {
  BUYER_CANCEL   // 구매자 취소 요청
  SELLER_CANCEL  // 판매자 직접 취소
}
```

### 3-2. CancelStatus (enum)

```typescript
enum CancelStatus {
  REQUESTED   // 취소 요청됨 (구매자 취소 시, 승인 대기)
  APPROVED    // 승인됨 (판매자 취소 시 즉시 / 구매자 취소 승인 시)
  REJECTED    // 거부됨 (구매자 취소 거부 시)
  COMPLETED   // 완료 (환불까지 완료)
  CANCELLED   // 취소됨 (고객 취소 시)
}
```

### 3-3. CancelReasonType (enum)

```typescript
enum CancelReasonType {
  // === 구매자 사유 ===
  CHANGE_OF_MIND      // 단순 변심
  WRONG_ORDER         // 잘못 주문함
  FOUND_CHEAPER       // 더 저렴한 곳 발견
  DELIVERY_TOO_SLOW   // 배송 예정일 너무 늦음

  // === 판매자 사유 ===
  OUT_OF_STOCK        // 재고 부족
  PRODUCT_DISCONTINUED // 상품 단종
  PRICE_ERROR         // 가격 오류
  SHIPPING_UNAVAILABLE // 배송 불가 지역
  PRODUCT_ISSUE       // 상품 문제

  OTHER               // 기타
}
```

#### 사유별 취소 타입 매핑

| 사유 | BUYER_CANCEL | SELLER_CANCEL |
|------|:------------:|:-------------:|
| `CHANGE_OF_MIND` | ✅ | ❌ |
| `WRONG_ORDER` | ✅ | ❌ |
| `FOUND_CHEAPER` | ✅ | ❌ |
| `DELIVERY_TOO_SLOW` | ✅ | ❌ |
| `OUT_OF_STOCK` | ❌ | ✅ |
| `PRODUCT_DISCONTINUED` | ❌ | ✅ |
| `PRICE_ERROR` | ❌ | ✅ |
| `SHIPPING_UNAVAILABLE` | ❌ | ✅ |
| `PRODUCT_ISSUE` | ❌ | ✅ |
| `OTHER` | ✅ | ✅ |

---

## 4. 취소 API

### 4-1. 판매취소 (배치)

**`POST /cancels/seller-cancel/batch`**

재고 부족, 상품 문제 등의 사유로 셀러가 직접 주문을 일괄 취소합니다.
승인 절차 없이 **즉시 처리**됩니다.

**Request Body**

```typescript
interface SellerCancelBatchRequest {
  items: SellerCancelItem[];      // 취소 대상 목록
  reason: {
    reasonType: CancelReasonType; // 취소 사유 (판매자 사유만 허용)
    reasonDetail?: string;        // 상세 사유 (선택)
  };
  memo?: string;                  // 메모 (선택)
}

interface SellerCancelItem {
  orderId: string;                // 주문 ID (UUIDv7)
  cancelQty: number;              // 취소 수량
}
```

**Request Example**

```json
{
  "items": [
    { "orderId": "order-uuid-v7-1", "cancelQty": 1 },
    { "orderId": "order-uuid-v7-2", "cancelQty": 2 },
    { "orderId": "order-uuid-v7-3", "cancelQty": 1 }
  ],
  "reason": {
    "reasonType": "OUT_OF_STOCK",
    "reasonDetail": "재고 소진으로 인한 취소"
  },
  "memo": "일괄 판매취소 처리"
}
```

**Response 200**

```typescript
interface SellerCancelBatchResult {
  data: {
    requestedCount: number;       // 요청 건수
    successCount: number;         // 성공 건수
    failCount: number;            // 실패 건수
    results: SellerCancelBatchResultItem[];
  };
  timestamp: string;
  requestId: string;
}

interface SellerCancelBatchResultItem {
  orderId: string;                // 주문 ID
  success: boolean;               // 처리 성공 여부
  cancelId?: string;              // 생성된 취소 ID (성공 시)
  cancelNumber?: string;          // 생성된 취소 번호 (성공 시)
  errorCode?: string;             // 에러 코드 (실패 시)
  errorMessage?: string;          // 에러 메시지 (실패 시)
}
```

**Response Example**

```json
{
  "data": {
    "requestedCount": 3,
    "successCount": 2,
    "failCount": 1,
    "results": [
      {
        "orderId": "order-uuid-v7-1",
        "success": true,
        "cancelId": "cancel-uuid-v7-1",
        "cancelNumber": "CAN-20240115-0001"
      },
      {
        "orderId": "order-uuid-v7-2",
        "success": true,
        "cancelId": "cancel-uuid-v7-2",
        "cancelNumber": "CAN-20240115-0002"
      },
      {
        "orderId": "order-uuid-v7-3",
        "success": false,
        "errorCode": "ORDER_NOT_CANCELLABLE",
        "errorMessage": "취소 불가 상태입니다 (SHIPPING)"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

**에러 코드 (개별 항목)**

| 에러 코드 | 상황 |
|-----------|------|
| `ORDER_NOT_FOUND` | 존재하지 않는 주문 |
| `ORDER_NOT_CANCELLABLE` | 취소 불가 상태 (SHIPPING 이후) |
| `ALREADY_CANCELLED` | 이미 취소된 주문 |
| `INVALID_CANCEL_REASON` | 판매자 취소에 구매자 사유 사용 |
| `INVALID_CANCEL_QTY` | 취소 수량이 주문 수량 초과 |
| `REFUND_FAILED` | PG사 환불 실패 |

---

### 4-2. 구매취소 승인 (배치)

**`POST /cancels/approve/batch`**

구매자가 요청한 취소를 일괄 승인합니다.

**Request Body**

```typescript
interface CancelApproveBatchRequest {
  cancelIds: string[];            // 취소 ID 목록 (UUIDv7)
  memo?: string;                  // 승인 메모 (선택)
}
```

**Request Example**

```json
{
  "cancelIds": [
    "cancel-uuid-v7-1",
    "cancel-uuid-v7-2",
    "cancel-uuid-v7-3"
  ],
  "memo": "일괄 승인 처리"
}
```

**Response 200**

```typescript
interface CancelBatchResult {
  data: {
    requestedCount: number;       // 요청 건수
    successCount: number;         // 성공 건수
    failCount: number;            // 실패 건수
    results: CancelBatchResultItem[];
  };
  timestamp: string;
  requestId: string;
}

interface CancelBatchResultItem {
  cancelId: string;               // 취소 ID
  success: boolean;               // 처리 성공 여부
  beforeStatus?: CancelStatus;    // 이전 상태 (성공 시)
  afterStatus?: CancelStatus;     // 변경된 상태 (성공 시)
  errorCode?: string;             // 에러 코드 (실패 시)
  errorMessage?: string;          // 에러 메시지 (실패 시)
}
```

**Response Example**

```json
{
  "data": {
    "requestedCount": 3,
    "successCount": 2,
    "failCount": 1,
    "results": [
      {
        "cancelId": "cancel-uuid-v7-1",
        "success": true,
        "beforeStatus": "REQUESTED",
        "afterStatus": "APPROVED"
      },
      {
        "cancelId": "cancel-uuid-v7-2",
        "success": true,
        "beforeStatus": "REQUESTED",
        "afterStatus": "APPROVED"
      },
      {
        "cancelId": "cancel-uuid-v7-3",
        "success": false,
        "errorCode": "ALREADY_PROCESSED",
        "errorMessage": "이미 처리된 취소 요청입니다"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

**에러 코드 (개별 항목)**

| 에러 코드 | 상황 |
|-----------|------|
| `CANCEL_NOT_FOUND` | 존재하지 않는 취소 요청 |
| `INVALID_STATUS` | 승인 불가 상태 (REQUESTED가 아님) |
| `ALREADY_PROCESSED` | 이미 처리된 취소 요청 |
| `REFUND_FAILED` | PG사 환불 실패 |

---

### 4-3. 구매취소 거부 (배치)

**`POST /cancels/reject/batch`**

구매자가 요청한 취소를 일괄 거부합니다.

**Request Body**

```typescript
interface CancelRejectBatchRequest {
  cancelIds: string[];            // 취소 ID 목록 (UUIDv7)
  reason: {
    reasonType: string;           // 거부 사유 타입
    reasonDetail: string;         // 거부 상세 사유
  };
  memo?: string;                  // 처리 메모 (선택)
}
```

**Request Example**

```json
{
  "cancelIds": [
    "cancel-uuid-v7-1",
    "cancel-uuid-v7-2"
  ],
  "reason": {
    "reasonType": "SHIPPING_STARTED",
    "reasonDetail": "이미 출고 처리된 상품입니다"
  },
  "memo": "일괄 거부 처리"
}
```

**Response 200**

CancelBatchResult 구조 동일

**Response Example**

```json
{
  "data": {
    "requestedCount": 2,
    "successCount": 2,
    "failCount": 0,
    "results": [
      {
        "cancelId": "cancel-uuid-v7-1",
        "success": true,
        "beforeStatus": "REQUESTED",
        "afterStatus": "REJECTED"
      },
      {
        "cancelId": "cancel-uuid-v7-2",
        "success": true,
        "beforeStatus": "REQUESTED",
        "afterStatus": "REJECTED"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

**에러 코드 (개별 항목)**

| 에러 코드 | 상황 |
|-----------|------|
| `CANCEL_NOT_FOUND` | 존재하지 않는 취소 요청 |
| `INVALID_STATUS` | 거부 불가 상태 (REQUESTED가 아님) |
| `ALREADY_PROCESSED` | 이미 처리된 취소 요청 |

---

## 5. 조회 API

### 리스트 vs 상세 조회 차이점

| 구분 | 리스트 조회 (`GET /cancels`) | 상세 조회 (`GET /cancels/{cancelId}`) |
|------|------------------------------|--------------------------------------|
| **용도** | 취소 목록 스캔, 필터링 | 개별 취소 건 상세 확인 |
| **취소 정보** | `cancelInfo` (CancelInfo) | `cancelInfo` (CancelDetailInfo) |
| **주문 정보** | buyerInfo, payment, receiverInfo, paymentShipmentInfo, orderProduct, externalOrderInfo | 동일 |
| **cancelInfo 내 환불 정보** | refundInfo (요약) | refundInfo (전체) |
| **cancelInfo 내 타임스탬프** | requestedAt, completedAt | requestedAt, completedAt, createdAt, updatedAt |
| **취소 이력 (최상위)** | ❌ 미포함 | ✅ `cancelHistories` 배열 포함 |

#### 필드별 포함 여부

**cancelInfo 내부 필드**

| 필드 | 리스트 (CancelInfo) | 상세 (CancelDetailInfo) | 설명 |
|------|:-------------------:|:-----------------------:|------|
| `cancelId` | ✅ | ✅ | 취소 ID |
| `cancelNumber` | ✅ | ✅ | 취소 번호 |
| `type` | ✅ | ✅ | 취소 타입 |
| `status` | ✅ | ✅ | 취소 상태 |
| `cancelQty` | ✅ | ✅ | 취소 수량 |
| `reason` | ✅ | ✅ | 취소 사유 |
| `reasonDetail` | ✅ | ✅ | 상세 사유 |
| `refundInfo` | ✅ | ✅ | 환불 정보 |
| `requestedAt` | ✅ | ✅ | 요청 시각 |
| `completedAt` | ✅ | ✅ | 완료 시각 |
| `createdAt` | ❌ | ✅ | 생성 시각 |
| `updatedAt` | ❌ | ✅ | 수정 시각 |

> **참고**: 처리 메모, 거부 사유 등은 `cancelHistories`에 기록됩니다.

**최상위 레벨 필드**

| 필드 | 리스트 | 상세 | 설명 |
|------|:------:|:----:|------|
| `orderId`, `orderNumber`, `legacyOrderId` | ✅ | ✅ | 주문 식별 정보 |
| `buyerInfo` | ✅ | ✅ | 구매자 정보 |
| `payment` | ✅ | ✅ | 결제 정보 |
| `receiverInfo` | ✅ | ✅ | 수령인 정보 |
| `paymentShipmentInfo` | ✅ | ✅ | 배송 정보 |
| `orderProduct` | ✅ | ✅ | 주문 상품 정보 |
| `externalOrderInfo` | ✅ | ✅ | 외부몰 정보 |
| `cancelHistories` | ❌ | ✅ | 취소 상태 변경 이력 |

---

### 5-1. 취소 KPI 요약

**`GET /cancels/summary`**

**Response 200**

```json
{
  "data": {
    "requested": 5,
    "approved": 3,
    "rejected": 1,
    "completed": 10
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

---

### 5-2. 취소 목록 조회

**`GET /cancels`**

**Request (Query)**

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| `type` | enum[] | | `BUYER_CANCEL` \| `SELLER_CANCEL` |
| `status` | enum[] | | CancelStatus |
| `dateType` | enum | ✅ | `REQUESTED` \| `COMPLETED` (요청일, 완료일) |
| `startDate` | string | ✅ | 시작일 (YYYY-MM-DD) |
| `endDate` | string | ✅ | 종료일 (YYYY-MM-DD) |
| `shopIds` | number[] | X | 샵 ID 목록 |
| `partnerIds` | number[] | X | 파트너 ID 목록 |
| `searchField` | enum | X | `CANCEL_NUMBER` \| `ORDER_NUMBER` \| `CUSTOMER_NAME` \| `CUSTOMER_PHONE` \| `PRODUCT_NAME` |
| `searchWord` | string | | 검색어 |
| `page` | number | ✅ | 페이지 번호 (0부터) |
| `size` | number | ✅ | 페이지 크기 |
| `sort` | enum | N | `requestedAt` \| `completedAt` |
| `sortDirection` | enum | N | `ASC` \| `DESC` |


**Response 200**

```typescript
interface CancelListResponse {
  data: PageData<CancelListItem>;
  timestamp: string;
  requestId: string;
}

interface CancelListItem {
  // ═══════════════════════════════════════════════════════════════
  // 취소 정보 (cancelInfo)
  // ═══════════════════════════════════════════════════════════════
  cancelInfo: CancelInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 기본 정보 (ORDER_SPEC_V4 참조)
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                            // 주문 ID (UUIDv7)
  orderNumber: string;                        // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;               // 레거시 주문 ID

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (ORDER_SPEC_V4.BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (ORDER_SPEC_V4.PaymentDetail)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentDetail;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (ORDER_SPEC_V4.ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (ORDER_SPEC_V4.PaymentShipmentInfo)
  // ═══════════════════════════════════════════════════════════════
  paymentShipmentInfo: PaymentShipmentInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (ORDER_SPEC_V4.OrderProduct)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (ORDER_SPEC_V4.ExternalOrderInfo)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;
}

// 취소 정보 객체
interface CancelInfo {
  cancelId: string;                           // 취소 ID (UUIDv7)
  cancelNumber: string;                       // 취소 번호 (CAN-YYYYMMDD-XXXX)
  type: CancelType;                           // 취소 타입 (BUYER_CANCEL / SELLER_CANCEL)
  status: CancelStatus;                       // 취소 상태
  cancelQty: number;                          // 취소 수량
  reason: CancelReasonType;                   // 취소 사유
  reasonDetail: string | null;                // 상세 사유
  refundInfo: RefundInfo | null;              // 환불 정보
  requestedAt: string;                        // 요청 시각
  completedAt: string | null;                 // 완료 시각
  // 참고: memo, rejectReason 등은 cancelHistories에서 조회
}
```

**Response Example**

```json
{
  "data": {
    "content": [
      {
        "cancelInfo": {
          "cancelId": "cancel-uuid-v7",
          "cancelNumber": "CAN-20240115-0001",
          "type": "SELLER_CANCEL",
          "status": "COMPLETED",
          "cancelQty": 1,
          "reason": "OUT_OF_STOCK",
          "reasonDetail": "재고 소진",
          "refundInfo": {
            "refundAmount": 50000,
            "refundMethod": "ORIGINAL_PAYMENT",
            "refundStatus": "COMPLETED",
            "refundedAt": "2024-01-15T10:35:00+09:00",
            "pgRefundId": "pg-refund-12345"
          },
          "requestedAt": "2024-01-15T10:00:00+09:00",
          "completedAt": "2024-01-15T10:30:00+09:00"
        },

        "orderId": "order-uuid-v7",
        "orderNumber": "ORD-20240115-0001",
        "legacyOrderId": 12345,

        "buyerInfo": {
          "buyerName": "홍길동",
          "buyerEmail": "hong@example.com",
          "buyerPhoneNumber": "010-1234-5678"
        },

        "payment": {
          "paymentId": 10001,
          "paymentAgencyId": "pg-tx-12345",
          "paymentStatus": "COMPLETED",
          "paymentMethod": "CARD",
          "paymentDate": "2024-01-15 09:00:00",
          "canceledDate": "2024-01-15 10:30:00",
          "userId": 1001,
          "siteName": "SETOF",
          "billAmount": 50000,
          "paymentAmount": 48000,
          "usedMileageAmount": 2000
        },

        "receiverInfo": {
          "receiverName": "홍길동",
          "receiverPhoneNumber": "010-1234-5678",
          "addressLine1": "서울시 강남구 테헤란로 123",
          "addressLine2": "456호",
          "zipCode": "06234",
          "country": "KR",
          "deliveryRequest": "문 앞에 놔주세요"
        },

        "paymentShipmentInfo": {
          "deliveryStatus": "PENDING",
          "shipmentCompanyCode": null,
          "invoice": null,
          "shipmentCompletedDate": null
        },

        "orderProduct": {
          "orderId": "order-uuid-v7",
          "productGroupName": "프리미엄 티셔츠",
          "price": {
            "regularPrice": 59000,
            "currentPrice": 50000,
            "salePrice": 50000,
            "directDiscountPrice": 0,
            "directDiscountRate": 0,
            "discountRate": 15
          },
          "brand": {
            "brandId": 101,
            "brandName": "SETOF BRAND"
          },
          "productGroupId": 10001,
          "productId": 20001,
          "sellerName": "패션스토어",
          "productGroupMainImageUrl": "https://cdn.example.com/products/10001/main.jpg",
          "deliveryArea": "전국",
          "productQuantity": 1,
          "orderStatus": "CANCELLED",
          "regularPrice": 59000,
          "orderAmount": 50000,
          "totalExpectedRefundMileageAmount": 500,
          "option": "블랙 L",
          "skuNumber": "SKU-BLK-L-001",
          "options": [
            { "optionName": "색상", "optionValue": "블랙" },
            { "optionName": "사이즈", "optionValue": "L" }
          ]
        },

        "externalOrderInfo": null
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

---

### 5-3. 취소 상세 조회

**`GET /cancels/{cancelId}`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `cancelId` | string (UUID) | 취소 ID |

**Response 200**

```typescript
interface CancelDetailResponse {
  data: CancelDetail;
  timestamp: string;
  requestId: string;
}

interface CancelDetail {
  // ═══════════════════════════════════════════════════════════════
  // 취소 정보 (cancelInfo)
  // ═══════════════════════════════════════════════════════════════
  cancelInfo: CancelDetailInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 기본 정보 (ORDER_SPEC_V4 참조)
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                            // 주문 ID (UUIDv7)
  orderNumber: string;                        // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;               // 레거시 주문 ID

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (ORDER_SPEC_V4.BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (ORDER_SPEC_V4.PaymentDetail)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentDetail;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (ORDER_SPEC_V4.ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (ORDER_SPEC_V4.PaymentShipmentInfo)
  // ═══════════════════════════════════════════════════════════════
  paymentShipmentInfo: PaymentShipmentInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (ORDER_SPEC_V4.OrderProduct)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (ORDER_SPEC_V4.ExternalOrderInfo)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 취소 이력 (상세 조회 전용, 최상위 레벨)
  // ═══════════════════════════════════════════════════════════════
  cancelHistories: CancelHistoryItem[];
}

// 취소 상세 정보 객체 (상세 조회 전용)
interface CancelDetailInfo {
  cancelId: string;                           // 취소 ID (UUIDv7)
  cancelNumber: string;                       // 취소 번호 (CAN-YYYYMMDD-XXXX)
  type: CancelType;                           // 취소 타입 (BUYER_CANCEL / SELLER_CANCEL)
  status: CancelStatus;                       // 취소 상태
  cancelQty: number;                          // 취소 수량
  reason: CancelReasonType;                   // 취소 사유
  reasonDetail: string | null;                // 상세 사유
  refundInfo: RefundInfo | null;              // 환불 정보
  requestedAt: string;                        // 요청 시각
  completedAt: string | null;                 // 완료 시각
  createdAt: string;                          // 생성 시각
  updatedAt: string;                          // 수정 시각
  // 참고: memo, rejectReason 등은 cancelHistories에서 조회
}

interface CancelHistoryItem {
  historyId: string;                          // 이력 ID (UUIDv7)
  type: string;                               // 이력 타입 (STATUS_CHANGE 등)
  title: string;                              // 제목
  message: string;                            // 메시지
  actor: {
    actorType: ActorType;                     // 액터 타입 (CUSTOMER, SELLER, ADMIN, SYSTEM)
    actorId: string;                          // 액터 ID
    actorName: string;                        // 액터 이름
  };
  createdAt: string;                          // 이력 생성 시각
}

enum ActorType {
  CUSTOMER = 'CUSTOMER',
  SELLER = 'SELLER',
  ADMIN = 'ADMIN',
  SYSTEM = 'SYSTEM'
}
```

**Response Example**

```json
{
  "data": {
    "cancelInfo": {
      "cancelId": "cancel-uuid-v7",
      "cancelNumber": "CAN-20240115-0001",
      "type": "BUYER_CANCEL",
      "status": "APPROVED",
      "cancelQty": 1,
      "reason": "CHANGE_OF_MIND",
      "reasonDetail": "다른 색상으로 재주문 예정",
      "refundInfo": {
        "refundAmount": 50000,
        "refundMethod": "ORIGINAL_PAYMENT",
        "refundStatus": "COMPLETED",
        "refundedAt": "2024-01-15T10:35:00+09:00",
        "pgRefundId": "pg-refund-12345"
      },
      "requestedAt": "2024-01-15T10:00:00+09:00",
      "completedAt": "2024-01-15T10:30:00+09:00",
      "createdAt": "2024-01-15T10:00:00+09:00",
      "updatedAt": "2024-01-15T10:35:00+09:00"
    },

    "orderId": "order-uuid-v7",
    "orderNumber": "ORD-20240115-0001",
    "legacyOrderId": 12345,

    "buyerInfo": {
      "buyerName": "홍길동",
      "buyerEmail": "hong@example.com",
      "buyerPhoneNumber": "010-1234-5678"
    },

    "payment": {
      "paymentId": 10001,
      "paymentAgencyId": "pg-tx-12345",
      "paymentStatus": "CANCELLED",
      "paymentMethod": "CARD",
      "paymentDate": "2024-01-15 09:00:00",
      "canceledDate": "2024-01-15 10:30:00",
      "userId": 1001,
      "siteName": "SETOF",
      "billAmount": 50000,
      "paymentAmount": 48000,
      "usedMileageAmount": 2000
    },

    "receiverInfo": {
      "receiverName": "홍길동",
      "receiverPhoneNumber": "010-1234-5678",
      "addressLine1": "서울시 강남구 테헤란로 123",
      "addressLine2": "456호",
      "zipCode": "06234",
      "country": "KR",
      "deliveryRequest": "문 앞에 놔주세요"
    },

    "paymentShipmentInfo": {
      "deliveryStatus": "PENDING",
      "shipmentCompanyCode": null,
      "invoice": null,
      "shipmentCompletedDate": null
    },

    "orderProduct": {
      "orderId": "order-uuid-v7",
      "productGroupName": "프리미엄 티셔츠",
      "price": {
        "regularPrice": 59000,
        "currentPrice": 50000,
        "salePrice": 50000,
        "directDiscountPrice": 0,
        "directDiscountRate": 0,
        "discountRate": 15
      },
      "brand": {
        "brandId": 101,
        "brandName": "SETOF BRAND"
      },
      "productGroupId": 10001,
      "productId": 20001,
      "sellerName": "패션스토어",
      "productGroupMainImageUrl": "https://cdn.example.com/products/10001/main.jpg",
      "deliveryArea": "전국",
      "productQuantity": 1,
      "orderStatus": "CANCELLED",
      "regularPrice": 59000,
      "orderAmount": 50000,
      "totalExpectedRefundMileageAmount": 500,
      "option": "블랙 L",
      "skuNumber": "SKU-BLK-L-001",
      "options": [
        { "optionName": "색상", "optionValue": "블랙" },
        { "optionName": "사이즈", "optionValue": "L" }
      ]
    },

    "externalOrderInfo": null,

    "cancelHistories": [
      {
        "historyId": "hst-uuid-1",
        "type": "STATUS_CHANGE",
        "title": "취소 요청",
        "message": "구매자가 취소를 요청했습니다.",
        "actor": {
          "actorType": "CUSTOMER",
          "actorId": "1001",
          "actorName": "홍길동"
        },
        "createdAt": "2024-01-15T10:00:00+09:00"
      },
      {
        "historyId": "hst-uuid-2",
        "type": "MANUAL",
        "title": "고객 메모",
        "message": "빠른 환불 부탁드립니다",
        "actor": {
          "actorType": "CUSTOMER",
          "actorId": "1001",
          "actorName": "홍길동"
        },
        "createdAt": "2024-01-15T10:00:01+09:00"
      },
      {
        "historyId": "hst-uuid-3",
        "type": "STATUS_CHANGE",
        "title": "취소 승인",
        "message": "REQUESTED → APPROVED",
        "actor": {
          "actorType": "ADMIN",
          "actorId": "admin_17",
          "actorName": "관리자"
        },
        "createdAt": "2024-01-15T10:30:00+09:00"
      }
    ]
  },
  "timestamp": "2024-01-15T11:00:00+09:00",
  "requestId": "req-uuid-v7"
}
```

---

## 6. 상태 전이

### 6-1. 구매자 취소 흐름

```
REQUESTED ──→ APPROVED ──→ COMPLETED
    │
    └──→ REJECTED
```

| 현재 상태 | 가능한 전이 | 트리거 |
|----------|------------|--------|
| REQUESTED | APPROVED | 판매자 승인 (`/cancels/approve/batch`) |
| REQUESTED | REJECTED | 판매자 거부 (`/cancels/reject/batch`) |
| APPROVED | COMPLETED | 환불 완료 (자동) |
| REJECTED | (최종 상태) | - |
| COMPLETED | (최종 상태) | - |

### 6-2. 판매자 취소 흐름

```
(즉시) APPROVED ──→ COMPLETED
```

| 현재 상태 | 가능한 전이 | 트리거 |
|----------|------------|--------|
| APPROVED | COMPLETED | 환불 완료 (자동) |
| COMPLETED | (최종 상태) | - |

> 판매자 취소는 승인 절차 없이 **즉시 APPROVED** 상태로 생성됩니다.

### 6-3. Order 상태 연동

| Cancel 상태 | Order 상태 변경 |
|-------------|----------------|
| REQUESTED | 변경 없음 (취소 대기 표시) |
| APPROVED | → `CANCELLED` |
| REJECTED | 변경 없음 (원래 상태 유지) |
| COMPLETED | `CANCELLED` 유지 |

---

## 부록: API 요약

| 기능 | 엔드포인트 | Method | 설명 |
|------|-----------|--------|------|
| 판매취소 (배치) | `/cancels/seller-cancel/batch` | POST | 셀러 직접 일괄 취소 (즉시) |
| 구매취소 승인 (배치) | `/cancels/approve/batch` | POST | 판매자가 일괄 승인 |
| 구매취소 거부 (배치) | `/cancels/reject/batch` | POST | 판매자가 일괄 거부 |
| 취소 KPI | `/cancels/summary` | GET | 상태별 건수 |
| 취소 목록 | `/cancels` | GET | 필터링 지원 |
| 취소 상세 | `/cancels/{cancelId}` | GET | 상세 정보 |

---

## 부록: Cancel vs Claim 비교표

| 항목 | Cancel (취소) | Claim (환불/교환) |
|------|--------------|------------------|
| **시점** | 배송 전 (PREPARING까지) | 배송 후 (DELIVERED) |
| **상품 수거** | ❌ | ✅ |
| **승인 필요** | 구매자 취소만 | 상황에 따라 |
| **API 경로** | `/orders/*/cancel-*`, `/cancels/*` | `/refunds/*`, `/exchanges/*` |
| **상태 변경** | 배치만 지원 (`/cancels/*/batch`) | 배치만 지원 (`/*/batch`) |
| **환불** | 전액 환불 (기본) | 차감 가능 |
| **번호 형식** | CAN-YYYYMMDD-XXXX | RFD-/EXC-YYYYMMDD-XXXX |
