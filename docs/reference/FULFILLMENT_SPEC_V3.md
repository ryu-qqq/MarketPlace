# Fulfillment (Shipment) 도메인 스펙 V3

## 개요

배송을 담당하는 도메인입니다. 주문에 대한 출고 배송을 관리합니다.

> **V2 → V3 변경사항**
> - ORDER_SPEC_V4와 필드/네이밍 정렬
> - dateField 옵션 변경: PAYMENT, ORDER_CONFIRMED, SHIPPED
> - 외부몰 주문번호 필터 추가 (shopOrderNos)
> - 레거시 네이밍 유지 (BuyerInfo, ReceiverInfo 등)
> - externalOrderInfo 추가
> - shipmentId, orderId: number → string (UUIDv7)
> - shipmentNumber, orderNumber 추가
> - legacyOrderId, legacyShipmentId 추가 (마이그레이션 호환)
> - 리스트에서 PaymentDetail, SettlementInfo 제거 (단건 조회 전용)

---

## 도메인 관계

```
Order ──1:1──→ Shipment (출고 배송)
               ※ 향후 부분 출고 지원 시 1:N + qty 필드 추가 가능
```

---

## Shipment (Aggregate Root)

### 필드 정의

| 필드 | 타입 | 설명 | 필수 |
|------|------|------|------|
| id | ShipmentId | 배송 ID (UUIDv7) | O |
| shipmentNumber | String | 배송 번호 (SHP-YYYYMMDD-XXXX) | O |
| legacyShipmentId | Long | 레거시 배송 ID (마이그레이션) | X |
| orderId | OrderId | 주문 ID (UUIDv7) | O |
| orderNumber | String | 주문 번호 (ORD-YYYYMMDD-XXXX) | O |
| legacyOrderId | Long | 레거시 주문 ID (마이그레이션) | X |
| status | ShipmentStatus | 배송 상태 | O |
| method | ShipmentMethod | 배송 방식 | X |
| trackingNumber | String | 송장번호 | X |
| orderConfirmedAt | Instant | 발주확인 시각 (배송준비중 전환) | X |
| shippedAt | Instant | 출고 시각 (송장 등록) | X |
| deliveredAt | Instant | 배송완료 시각 | X |
| createdAt | Instant | 생성 시각 | O |
| updatedAt | Instant | 수정 시각 | O |

---

## ShipmentStatus (Enum)

### 상태 흐름

```
READY ──→ PREPARING ──→ SHIPPED ──→ IN_TRANSIT ──→ DELIVERED
              │                          │
              └── CANCELLED              └──→ FAILED (배송 실패)
```

### 상태 정의

| 상태 | 설명 | Order 상태 연동 |
|------|------|----------------|
| READY | 결제 완료, 출고 대기 | ORDERED |
| PREPARING | 발주 확인, 배송 준비 중 | PREPARING |
| SHIPPED | 출고됨 (송장 등록, 택배사 인계) | SHIPPED |
| IN_TRANSIT | 배송 중 | SHIPPED |
| DELIVERED | 배송 완료 | DELIVERED |
| FAILED | 배송 실패 | CLAIM_IN_PROGRESS |
| CANCELLED | 취소됨 | CANCELLED |

---

## ShipmentMethodType (Enum)

| 값 | 설명 |
|------|------|
| COURIER | 택배 |
| QUICK | 퀵서비스 |
| VISIT | 직접 방문 전달 |
| DESIGNATED_COURIER | 지정 택배 |

---

## ShipmentMethod (VO)

| 필드 | 타입 | 설명 |
|------|------|------|
| type | ShipmentMethodType | 배송 방식 |
| courierCode | String | 택배사 코드 (VISIT인 경우 null) |
| courierName | String | 택배사 이름 (VISIT인 경우 null) |

---

## Shipment API


**`GET /shipments/summary`**

**Response 200**

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
  "requestId": "req-01234567-89ab-cdef"
}
```

### 배송 리스트 조회

**`GET /shipments`**

**Request (Query Parameters)**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `dateField` | Enum | O | `PAYMENT` \| `ORDER_CONFIRMED` \| `SHIPPED` |
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `status` | List | X | 배송 상태 (예: `PREPARING,SHIPPED`) |
| `sellerIds` | List | X | 판매자 ID (예: `1,2,3`) |
| `shopOrderNos` | List | X | 외부몰 주문번호 (예: `2025012612345,2025012612346`) |
| `searchField` | Enum | X | 검색 필드 |
| `searchWord` | String | X | 검색어 |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

#### dateField 옵션

| 값 | 설명 |
|------|------|
| `PAYMENT` | 결제일 기준 |
| `ORDER_CONFIRMED` | 발주확인일 기준 (배송준비중 전환 시점) |
| `SHIPPED` | 발송처리일 기준 (송장 등록, 배송중 전환 시점) |

#### searchField 옵션

| 값 | 설명 |
|------|------|
| `ORDER_ID` | 주문 ID |
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
  // 배송 기본 정보
  // ═══════════════════════════════════════════════════════════════
  shipmentId: string;                       // 배송 ID (UUIDv7)
  shipmentNumber: string;                   // 배송 번호 (SHP-YYYYMMDD-XXXX)
  legacyShipmentId: number | null;          // 레거시 배송 ID (마이그레이션 호환)
  status: ShipmentStatus;                   // 배송 상태

  // ═══════════════════════════════════════════════════════════════
  // 주문 정보
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                          // 주문 ID (UUIDv7)
  orderNumber: string;                      // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;             // 레거시 주문 ID (마이그레이션 호환)

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (레거시 BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (레거시 ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (레거시 OrderProduct)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (외부몰이면 값, 자사몰이면 null)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 배송 상세 정보
  // ═══════════════════════════════════════════════════════════════
  shipmentMethod: ShipmentMethod | null;    // 배송 방식
  trackingNumber: string | null;            // 송장번호
  orderConfirmedAt: string | null;          // 발주확인 일시 (배송준비중 전환)
  shippedAt: string | null;                 // 출고 일시 (송장 등록)
  deliveredAt: string | null;               // 배송완료 일시
  createdAt: string;                        // 생성 일시
}
```

---

### 배송 단건 조회

**`GET /shipments/{shipmentId}`**

**Response 200**

```typescript
interface ShipmentDetailResponse {
  data: ShipmentDetail;
  timestamp: string;
  requestId: string;
}

interface ShipmentDetail {
  // ═══════════════════════════════════════════════════════════════
  // 배송 기본 정보
  // ═══════════════════════════════════════════════════════════════
  shipmentId: string;                       // 배송 ID (UUIDv7)
  shipmentNumber: string;                   // 배송 번호 (SHP-YYYYMMDD-XXXX)
  legacyShipmentId: number | null;          // 레거시 배송 ID (마이그레이션 호환)
  status: ShipmentStatus;                   // 배송 상태

  // ═══════════════════════════════════════════════════════════════
  // 주문 정보
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                          // 주문 ID (UUIDv7)
  orderNumber: string;                      // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;             // 레거시 주문 ID (마이그레이션 호환)

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (레거시 BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (레거시 PaymentDetail) - 단건 조회 전용
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentDetail;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (레거시 ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 정산 정보 (레거시 SettlementInfo) - 단건 조회 전용
  // ═══════════════════════════════════════════════════════════════
  settlementInfo: SettlementInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (레거시 OrderProduct)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (외부몰이면 값, 자사몰이면 null)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 배송 상세 정보
  // ═══════════════════════════════════════════════════════════════
  shipmentMethod: ShipmentMethod | null;    // 배송 방식
  trackingNumber: string | null;            // 송장번호
  orderConfirmedAt: string | null;          // 발주확인 일시 (배송준비중 전환)
  shippedAt: string | null;                 // 출고 일시 (송장 등록)
  deliveredAt: string | null;               // 배송완료 일시
  createdAt: string;                        // 생성 일시
  updatedAt: string;                        // 수정 일시
}
```

---

### 발주 확인 (배송준비중 전환) - 다건

**`POST /shipments/confirm/batch`**

**Request Body**

```typescript
interface ConfirmBatchRequest {
  orderIds: string[];                       // 발주 확인할 주문 ID 목록 (UUIDv7)
}
```

**Response 200**

```typescript
interface ConfirmBatchResponse {
  data: ConfirmBatchResult;
  timestamp: string;
  requestId: string;
}

interface ConfirmBatchResult {
  requestedCount: number;
  successCount: number;
  failCount: number;
  results: Array<ConfirmSuccessItem | ConfirmFailureItem>;
}

interface ConfirmSuccessItem {
  orderId: string;                          // 주문 ID (UUIDv7)
  success: true;
  shipmentId: string;                       // 배송 ID (UUIDv7)
  shipmentNumber: string;                   // 배송 번호 (SHP-YYYYMMDD-XXXX)
  status: 'PREPARING';
  orderConfirmedAt: string;
}

interface ConfirmFailureItem {
  orderId: string;                          // 주문 ID (UUIDv7)
  success: false;
  errorCode: string;
  errorMessage: string;
}
```

---

### 배송 처리 (발송 처리) - 다건

**`POST /shipments/ship/batch`**

**Request Body**

```typescript
interface ShipBatchRequest {
  requests: Array<{
    orderId: string;                        // 주문 ID (UUIDv7)
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
  data: ShipBatchResult;
  timestamp: string;
  requestId: string;
}

interface ShipBatchResult {
  requestedCount: number;
  successCount: number;
  failCount: number;
  results: Array<ShipSuccessItem | ShipFailureItem>;
}

interface ShipSuccessItem {
  orderId: string;                          // 주문 ID (UUIDv7)
  success: true;
  shipment: {
    shipmentId: string;                     // 배송 ID (UUIDv7)
    shipmentNumber: string;                 // 배송 번호 (SHP-YYYYMMDD-XXXX)
    status: 'SHIPPED';
    method: ShipmentMethod;
    trackingNumber: string;
    shippedAt: string;
  };
}

interface ShipFailureItem {
  orderId: string;                          // 주문 ID (UUIDv7)
  success: false;
  errorCode: string;
  errorMessage: string;
}
```

**에러 코드**

| 에러 코드 | 상황 |
|-----------|------|
| `ORDER_NOT_FOUND` | 존재하지 않는 주문 |
| `ORDER_NOT_READY` | 출고 가능 상태가 아님 |
| `ALREADY_SHIPPED` | 이미 출고된 주문 |
| `INVALID_TRACKING_NUMBER` | 잘못된 송장번호 |

---

### 배송 처리 (발송 처리) - 단건

**`POST /orders/{orderId}/ship`**

**Request Body**

```typescript
interface ShipRequest {
  method: {
    type: ShipmentMethodType;
    courierCode: string;
  };
  trackingNumber: string;
}
```

**Response 200**

```typescript
interface ShipResponse {
  data: {
    shipmentId: string;                     // 배송 ID (UUIDv7)
    shipmentNumber: string;                 // 배송 번호 (SHP-YYYYMMDD-XXXX)
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

## 공통 인터페이스 (ORDER_SPEC_V4 참조)

### BuyerInfo

```typescript
interface BuyerInfo {
  buyerName: string;                        // 구매자 이름
  buyerEmail: string;                       // 구매자 이메일
  buyerPhoneNumber: string;                 // 구매자 연락처
}
```



### ReceiverInfo

```typescript
interface ReceiverInfo {
  receiverName: string;                     // 수령인 이름
  receiverPhoneNumber: string;              // 수령인 연락처
  addressLine1: string;                     // 기본 주소
  addressLine2: string;                     // 상세 주소
  zipCode: string;                          // 우편번호
  country: Origin;                          // 국가
  deliveryRequest: string | null;           // 배송 요청사항
}
```

### OrderProduct

```typescript
interface OrderProduct {
  orderId: number;                          // 주문 ID
  productGroupName: string;                 // 상품명
  price: Price;                             // 가격 정보
  brand: Brand;                             // 브랜드 정보
  productGroupId: number;                   // 상품 그룹 ID
  productId: number;                        // 상품 ID (SKU)
  sellerName: string;                       // 판매자명
  productGroupMainImageUrl: string;         // 상품 대표 이미지
  deliveryArea: string;                     // 배송 지역
  productQuantity: number;                  // 주문 수량
  orderStatus: OrderStatus;                 // 주문 상태
  regularPrice: Money;                      // 정가
  orderAmount: Money;                       // 주문 금액
  totalExpectedRefundMileageAmount: Money;  // 예상 환불 마일리지
  option: string;                           // 옵션 문자열
  skuNumber: string;                        // SKU 번호
  options: OptionDto[];                     // 옵션 상세 목록
}
```

### ExternalOrderInfo

```typescript
interface ExternalOrderInfo {
  shopId: number;                           // 샵 ID
  shopCode: string;                         // 샵 코드 (NAVER-001, COUPANG-001 등)
  shopOrderNo: string;                      // 외부몰 주문번호
  shopOrderStatus: string;                  // 외부몰 주문상태 (원본)
  shopOrderedAt: string;                    // 외부몰 주문일시
}
```

### ShipmentMethod

```typescript
interface ShipmentMethod {
  type: ShipmentMethodType;                 // 배송 방식
  courierCode: string | null;               // 택배사 코드
  courierName: string | null;               // 택배사명
}
```

---

## Enum 정의

```typescript
// 배송 상태
type ShipmentStatus =
  | 'READY'             // 출고 대기
  | 'PREPARING'         // 배송 준비 중 (발주 확인)
  | 'SHIPPED'           // 출고됨 (송장 등록)
  | 'IN_TRANSIT'        // 배송 중
  | 'DELIVERED'         // 배송 완료
  | 'FAILED'            // 배송 실패
  | 'CANCELLED';        // 취소됨

// 배송 방식
type ShipmentMethodType =
  | 'COURIER'           // 택배
  | 'QUICK'             // 퀵서비스
  | 'VISIT'             // 직접 방문
  | 'DESIGNATED_COURIER'; // 지정 택배

// 주문 상태 (ORDER_SPEC_V4 참조)
type OrderStatus =
  | 'ORDERED'
  | 'PREPARING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'CLAIM_IN_PROGRESS'
  | 'REFUNDED'
  | 'EXCHANGED';

// 결제 상태
type PaymentStatus =
  | 'PENDING'
  | 'COMPLETED'
  | 'PARTIALLY_REFUNDED'
  | 'FULLY_REFUNDED'
  | 'CANCELLED';

// 결제 수단
type PaymentMethodEnum =
  | 'CARD'
  | 'BANK_TRANSFER'
  | 'VIRTUAL_ACCOUNT'
  | 'PHONE'
  | 'KAKAO_PAY'
  | 'NAVER_PAY'
  | 'TOSS_PAY';

// 사이트
type SiteName =
  | 'SETOF'
  | 'NAVER'
  | 'COUPANG'
  | 'KAKAO'
  | 'ZIGZAG';
```

---

## API 요약

| 기능 | 엔드포인트 | Method |
|------|-----------|--------|
| 배송 리스트 조회 | `GET /shipments` | GET |
| 배송 단건 조회 | `GET /shipments/{shipmentId}` | GET |
| 발주 확인 (다건) | `POST /shipments/confirm/batch` | POST |
| 배송 처리 (다건) | `POST /shipments/ship/batch` | POST |
| 배송 처리 (단건) | `POST /orders/{orderId}/ship` | POST |

---

## V2 대비 변경사항

| 항목 | V2 | V3 |
|------|----|----|
| shipmentId, orderId | number (auto increment) | string (UUIDv7) |
| shipmentNumber | - | 추가 (SHP-YYYYMMDD-XXXX) |
| orderNumber | - | 추가 (ORD-YYYYMMDD-XXXX) |
| legacyShipmentId | - | 추가 (마이그레이션 호환) |
| legacyOrderId | - | 추가 (마이그레이션 호환) |
| dateField | CREATED, SHIPPED, DELIVERED | PAYMENT, ORDER_CONFIRMED, SHIPPED |
| 외부몰 필터 | - | shopOrderNos (배열) |
| 응답 구조 | 자체 네이밍 | ORDER_SPEC_V4 네이밍 정렬 |
| externalOrderInfo | - | 추가 |
| 발주 확인 API | - | 추가 (confirm/batch) |
| PREPARING 상태 | - | 추가 (발주 확인 시점) |
| orderConfirmedAt | - | 추가 (발주 확인 시각) |
| 리스트 PaymentDetail | 포함 | 제거 (단건 조회 전용) |
| 리스트 SettlementInfo | 포함 | 제거 (단건 조회 전용) |
