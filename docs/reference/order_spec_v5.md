# Order 도메인 스펙 V5

## 개요

입점형 이커머스 OMS의 주문 관리 API입니다.
네이버 상품주문 API 패턴을 참고하여 **상품주문(order_item) 단위**로 리스트/상세를 제공합니다.

> **V4 → V5 변경사항**
> - DB 실데이터 기준으로 응답 필드 재설계 (V4의 미존재 필드 전부 제거)
> - 네이버 productOrder 패턴 채택: 리스트 1행 = 상품주문 1건
> - 각 상품주문 행에 소속 주문(order) 정보 포함 → 프론트에서 주문 그룹핑 가능
> - 상세 조회도 상품주문 단건 조회 (`GET /order-items/{orderItemId}`)
> - cancel/claim ID는 UUIDv7 (string)
> - V4의 cancel/claim 요약 구조 유지 (상품주문 단위)

---

## 상품주문 리스트 조회

**`GET /orders`**

> 네이버의 `상품주문 내역 조회` 대응. 각 행이 상품주문(order_item) 1건.
> 같은 주문의 상품들은 `order.orderId`로 그룹핑 가능.

### Request (Query Parameters)

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `dateField` | Enum | O | `ORDERED` \| `SHIPPED` \| `DELIVERED` |
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `status` | List | X | 주문 상태 필터 (예: `ORDERED,SHIPPED`) |
| `searchField` | Enum | X | 검색 대상 (`ORDER_ID`, `ORDER_NUMBER`, `CUSTOMER_NAME`, `PRODUCT_NAME`) |
| `searchWord` | String | X | 검색어 |
| `sortKey` | Enum | X | 정렬 키 (`CREATED_AT`, `ORDERED_AT`, `UPDATED_AT`). 기본값: `CREATED_AT` |
| `sortDirection` | Enum | X | 정렬 방향 (`ASC`, `DESC`). 기본값: `DESC` |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

### Response 200

```typescript
interface ProductOrderListResponse {
  data: PageData<ProductOrderListItem>;
  timestamp: string;
  requestId: string;
}

interface ProductOrderListItem {
  // ═══════════════════════════════════════════════════════════════
  // 소속 주문 정보 (프론트에서 주문 그룹핑용)
  // ═══════════════════════════════════════════════════════════════
  order: {
    orderId: string;                    // 주문 ID (UUIDv7)
    orderNumber: string;                // 주문번호 (ORD-YYYYMMDD-XXXX)
    status: OrderStatus;                // 주문 상태
    salesChannelId: number;             // 판매채널 ID
    shopId: number;                     // 샵 ID
    shopCode: string;                   // 샵 코드 (NAVER-001, COUPANG-001 등)
    shopName: string;                   // 샵 이름
    externalOrderNo: string | null;     // 외부몰 주문번호
    externalOrderedAt: string | null;   // 외부몰 주문일시 (ISO 8601)
    buyerName: string;                  // 구매자명
    buyerEmail: string;                 // 구매자 이메일
    buyerPhone: string;                 // 구매자 연락처
    createdAt: string;                  // 주문 생성일시 (ISO 8601)
    updatedAt: string;                  // 주문 수정일시 (ISO 8601)
  };

  // ═══════════════════════════════════════════════════════════════
  // 상품주문 정보 (이 행의 주체)
  // ═══════════════════════════════════════════════════════════════
  productOrder: {
    orderItemId: number;                // 상품주문 ID
    productGroupId: number;             // 상품그룹 ID
    productId: number;                  // 상품 ID (SKU)
    sellerId: number;                   // 판매자 ID
    brandId: number;                    // 브랜드 ID
    skuCode: string | null;             // SKU 코드
    productGroupName: string;           // 상품명
    brandName: string;                  // 브랜드명
    sellerName: string;                 // 판매자명
    mainImageUrl: string | null;        // 대표 이미지 URL
    externalProductId: string | null;   // 외부 상품 ID
    externalOptionId: string | null;    // 외부 옵션 ID
    externalProductName: string | null; // 외부 상품명
    externalOptionName: string | null;  // 외부 옵션명
    externalImageUrl: string | null;    // 외부 이미지 URL
    unitPrice: number;                  // 개당 판매가 (원)
    quantity: number;                   // 주문 수량
    totalAmount: number;                // 총 금액
    discountAmount: number;             // 할인 금액
    paymentAmount: number;              // 실결제 금액
  };

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (주문 레벨, 상세와 동일 구조)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 수령인/배송
  // ═══════════════════════════════════════════════════════════════
  receiver: {
    receiverName: string;               // 수령인명
    receiverPhone: string;              // 수령인 연락처
    receiverZipcode: string;            // 우편번호
    receiverAddress: string;            // 기본 주소
    receiverAddressDetail: string;      // 상세 주소
    deliveryRequest: string | null;     // 배송 요청사항
  };

  // ═══════════════════════════════════════════════════════════════
  // 배송 상태
  // ═══════════════════════════════════════════════════════════════
  delivery: {
    deliveryStatus: string | null;      // 배송 상태
    shipmentCompanyCode: string | null; // 택배사 코드
    invoice: string | null;             // 송장번호
    shipmentCompletedDate: string | null; // 출고완료일시 (ISO 8601)
  };

  // ═══════════════════════════════════════════════════════════════
  // 취소 요약 (배송 전 취소, 취소 없으면 null)
  // ═══════════════════════════════════════════════════════════════
  cancel: {
    hasActiveCancel: boolean;             // 진행 중인 취소 존재 여부
    totalCancelledQty: number;            // 총 취소 수량 (진행 중 + 완료)
    cancelableQty: number;                // 추가 취소 가능 수량
    latest: {                             // 가장 최근 취소 정보
      cancelId: string;                   // 취소 ID (UUIDv7)
      cancelNumber: string;               // 취소 번호
      status: CancelStatus;               // 취소 상태
      qty: number;                        // 취소 수량
      requestedAt: string;                // 취소 신청 일시 (ISO 8601)
    } | null;
  } | null;

  // ═══════════════════════════════════════════════════════════════
  // 클레임 요약 (환불/교환, 배송 후, 클레임 없으면 null)
  // ═══════════════════════════════════════════════════════════════
  claim: {
    hasActiveClaim: boolean;              // 진행 중인 클레임 존재 여부
    activeCount: number;                  // 진행 중인 클레임 수
    totalClaimedQty: number;              // 총 클레임 수량 (진행 중 + 완료)
    claimableQty: number;                 // 추가 클레임 가능 수량 (주문수량 - 클레임수량)
    latest: {                             // 가장 최근 클레임 정보
      claimId: string;                    // 클레임 ID (UUIDv7)
      claimNumber: string;                // 클레임 번호
      type: ClaimType;                    // 클레임 유형 (REFUND, EXCHANGE)
      status: ClaimStatus;                // 클레임 상태
      qty: number;                        // 클레임 수량
      requestedAt: string;                // 클레임 신청 일시 (ISO 8601)
    } | null;
  } | null;
}
```

---

## 상품주문 상세 조회

**`GET /orders/{orderItemId}`**

> 네이버의 `상품주문 상세 조회` 대응. 상품주문 1건의 전체 정보.

### Response 200

```typescript
interface ProductOrderDetailResponse {
  data: ProductOrderDetail;
  timestamp: string;
  requestId: string;
}

interface ProductOrderDetail {
  // ═══════════════════════════════════════════════════════════════
  // 소속 주문 정보
  // ═══════════════════════════════════════════════════════════════
  order: {
    orderId: string;                    // 주문 ID (UUIDv7)
    orderNumber: string;                // 주문번호 (ORD-YYYYMMDD-XXXX)
    status: OrderStatus;                // 주문 상태
    salesChannelId: number;             // 판매채널 ID
    shopId: number;                     // 샵 ID
    shopCode: string;                   // 샵 코드
    shopName: string;                   // 샵 이름
    externalOrderNo: string | null;     // 외부몰 주문번호
    externalOrderedAt: string | null;   // 외부몰 주문일시 (ISO 8601)
    buyerName: string;                  // 구매자명
    buyerEmail: string;                 // 구매자 이메일
    buyerPhone: string;                 // 구매자 연락처
    createdAt: string;                  // 주문 생성일시 (ISO 8601)
    updatedAt: string;                  // 주문 수정일시 (ISO 8601)
  };

  // ═══════════════════════════════════════════════════════════════
  // 상품주문 정보
  // ═══════════════════════════════════════════════════════════════
  productOrder: {
    orderItemId: number;                // 상품주문 ID
    productGroupId: number;             // 상품그룹 ID
    productId: number;                  // 상품 ID (SKU)
    sellerId: number;                   // 판매자 ID
    brandId: number;                    // 브랜드 ID
    skuCode: string | null;             // SKU 코드
    productGroupName: string;           // 상품명
    brandName: string;                  // 브랜드명
    sellerName: string;                 // 판매자명
    mainImageUrl: string | null;        // 대표 이미지 URL
    externalProductId: string | null;   // 외부 상품 ID
    externalOptionId: string | null;    // 외부 옵션 ID
    externalProductName: string | null; // 외부 상품명
    externalOptionName: string | null;  // 외부 옵션명
    externalImageUrl: string | null;    // 외부 이미지 URL
    unitPrice: number;                  // 개당 판매가 (원)
    quantity: number;                   // 주문 수량
    totalAmount: number;                // 총 금액
    discountAmount: number;             // 할인 금액
    paymentAmount: number;              // 실결제 금액
  };

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (주문 레벨)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 수령인/배송
  // ═══════════════════════════════════════════════════════════════
  receiver: {
    receiverName: string;               // 수령인명
    receiverPhone: string;              // 수령인 연락처
    receiverZipcode: string;            // 우편번호
    receiverAddress: string;            // 기본 주소
    receiverAddressDetail: string;      // 상세 주소
    deliveryRequest: string | null;     // 배송 요청사항
  };

  // ═══════════════════════════════════════════════════════════════
  // 배송 상태
  // ═══════════════════════════════════════════════════════════════
  delivery: {
    deliveryStatus: string | null;      // 배송 상태
    shipmentCompanyCode: string | null; // 택배사 코드
    invoice: string | null;             // 송장번호
    shipmentCompletedDate: string | null; // 출고완료일시 (ISO 8601)
  };

  // ═══════════════════════════════════════════════════════════════
  // 정산 정보
  // ═══════════════════════════════════════════════════════════════
  settlement: {
    commissionRate: number;             // 수수료율 (%)
    fee: number;                        // 수수료 금액
    expectationSettlementAmount: number; // 예상 정산 금액
    settlementAmount: number;           // 정산 금액
    shareRatio: number;                 // 쉐어 비율 (%)
    expectedSettlementDay: string | null; // 정산 예정일
    settlementDay: string | null;       // 정산 완료일
  };

  // ═══════════════════════════════════════════════════════════════
  // 이 상품주문의 취소 목록
  // ═══════════════════════════════════════════════════════════════
  cancels: CancelInfo[];

  // ═══════════════════════════════════════════════════════════════
  // 이 상품주문의 클레임 목록
  // ═══════════════════════════════════════════════════════════════
  claims: ClaimInfo[];

  // ═══════════════════════════════════════════════════════════════
  // 주문 타임라인 (주문 레벨)
  // ═══════════════════════════════════════════════════════════════
  timeLine: TimeLineItem[];
}
```

---

## 공통 인터페이스

### PaymentInfo

```typescript
interface PaymentInfo {
  paymentId: number;                  // 결제 ID
  paymentStatus: string;              // 결제 상태
  paymentMethod: string;              // 결제 수단
  paymentAgencyId: string | null;     // PG사 거래 ID
  paymentAmount: number;              // 결제 금액 (원)
  paidAt: string | null;              // 결제일시 (ISO 8601)
  canceledAt: string | null;          // 취소일시 (ISO 8601)
}
```

### CancelInfo

```typescript
interface CancelInfo {
  cancelId: string;                   // 취소 ID (UUIDv7)
  orderItemId: number;                // 취소 대상 상품주문 ID
  cancelNumber: string;               // 취소번호
  cancelStatus: string;               // 취소 상태
  quantity: number;                   // 취소 수량
  reasonType: string;                 // 취소 사유 유형
  reasonDetail: string | null;        // 취소 상세 사유
  originalAmount: number;             // 원 금액
  refundAmount: number;               // 환불 금액
  refundMethod: string | null;        // 환불 수단
  refundedAt: string | null;          // 환불일시 (ISO 8601)
  requestedAt: string;                // 취소 요청일시 (ISO 8601)
  completedAt: string | null;         // 취소 완료일시 (ISO 8601)
}
```

### ClaimInfo

```typescript
interface ClaimInfo {
  claimId: string;                    // 클레임 ID (UUIDv7)
  orderItemId: number;                // 클레임 대상 상품주문 ID
  claimNumber: string;                // 클레임번호
  claimType: string;                  // 클레임 유형 (REFUND, EXCHANGE)
  claimStatus: string;                // 클레임 상태
  quantity: number;                   // 클레임 수량
  reasonType: string;                 // 클레임 사유 유형
  reasonDetail: string | null;        // 클레임 상세 사유
  collectMethod: string | null;       // 회수 방식
  originalAmount: number;             // 원 금액
  deductionAmount: number;            // 차감 금액
  deductionReason: string | null;     // 차감 사유
  refundAmount: number;               // 환불 금액
  refundMethod: string | null;        // 환불 수단
  refundedAt: string | null;          // 환불일시 (ISO 8601)
  requestedAt: string;                // 클레임 요청일시 (ISO 8601)
  completedAt: string | null;         // 클레임 완료일시 (ISO 8601)
  rejectedAt: string | null;          // 클레임 거절일시 (ISO 8601)
}
```

### TimeLineItem

```typescript
interface TimeLineItem {
  historyId: number;                  // 이력 ID
  fromStatus: string;                 // 이전 상태
  toStatus: string;                   // 변경된 상태
  changedBy: string;                  // 변경자
  reason: string | null;              // 변경 사유
  changedAt: string;                  // 변경일시 (ISO 8601)
}
```

---

## 주문 요약 (대시보드)

**`GET /orders/summary`**

### Response 200

```typescript
interface OrderSummaryResponse {
  data: OrderSummary;
  timestamp: string;
  requestId: string;
}

interface OrderSummary {
  ordered: number;                    // 주문완료 건수
  preparing: number;                  // 상품준비중 건수
  shipped: number;                    // 출고완료 건수
  delivered: number;                  // 배송완료 건수
  confirmed: number;                  // 구매확정 건수
  cancelled: number;                  // 취소 건수
  claimInProgress: number;            // 클레임진행중 건수
  refunded: number;                   // 환불완료 건수
  exchanged: number;                  // 교환완료 건수
}
```

---

## Enum 정의

```typescript
// 주문 상태
type OrderStatus =
  | 'ORDERED'           // 주문 완료
  | 'PREPARING'         // 상품 준비 중
  | 'SHIPPED'           // 출고 완료
  | 'DELIVERED'         // 배송 완료
  | 'CONFIRMED'         // 구매 확정
  | 'CANCELLED'         // 취소됨
  | 'CLAIM_IN_PROGRESS' // 클레임 진행 중
  | 'REFUNDED'          // 환불 완료
  | 'EXCHANGED';        // 교환 완료

// 결제 상태
type PaymentStatus =
  | 'PENDING'           // 결제 대기
  | 'COMPLETED'         // 결제 완료
  | 'PARTIALLY_REFUNDED' // 부분 환불
  | 'FULLY_REFUNDED'    // 전액 환불
  | 'CANCELLED';        // 결제 취소

// 배송 상태
type DeliveryStatus =
  | 'READY'             // 배송 준비
  | 'SHIPPED'           // 출고됨
  | 'IN_TRANSIT'        // 배송 중
  | 'DELIVERED'         // 배송 완료
  | 'FAILED';           // 배송 실패

// 클레임 유형
type ClaimType =
  | 'REFUND'            // 환불
  | 'EXCHANGE';         // 교환

// 클레임 상태
type ClaimStatus =
  | 'REQUESTED'         // 클레임 신청
  | 'COLLECTING'        // 수거 중
  | 'COLLECTED'         // 수거 완료
  | 'COMPLETED'         // 클레임 완료
  | 'REJECTED'          // 클레임 거절
  | 'CANCELLED';        // 취소됨

// 취소 상태
type CancelStatus =
  | 'REQUESTED'         // 취소 요청됨
  | 'APPROVED'          // 승인됨
  | 'REJECTED'          // 거부됨
  | 'COMPLETED'         // 완료
  | 'CANCELLED';        // 취소됨

// 날짜 검색 필드
type OrderDateField =
  | 'ORDERED'           // 주문일
  | 'SHIPPED'           // 출고일
  | 'DELIVERED';        // 배송완료일

// 검색 필드
type OrderSearchField =
  | 'ORDER_ID'          // 주문 ID
  | 'ORDER_NUMBER'      // 주문번호
  | 'CUSTOMER_NAME'     // 구매자명
  | 'PRODUCT_NAME';     // 상품명

// 정렬 키
type OrderSortKey =
  | 'CREATED_AT'        // 생성일
  | 'ORDERED_AT'        // 주문일
  | 'UPDATED_AT';       // 수정일
```

---

## DB → API 매핑

### 리스트 쿼리

메인 쿼리: `order_items` JOIN `orders` LEFT JOIN `payments`

| API 필드 | DB 테이블.칼럼 |
|---------|---------------|
| `order.orderId` | `orders.id` |
| `order.orderNumber` | `orders.order_number` |
| `order.status` | `orders.status` |
| `order.salesChannelId` | `orders.sales_channel_id` |
| `order.shopId` | `orders.shop_id` |
| `order.shopCode` | `orders.shop_code` |
| `order.shopName` | `orders.shop_name` |
| `order.externalOrderNo` | `orders.external_order_no` |
| `order.externalOrderedAt` | `orders.external_ordered_at` |
| `order.buyerName` | `orders.buyer_name` |
| `order.buyerEmail` | `orders.buyer_email` |
| `order.buyerPhone` | `orders.buyer_phone` |
| `order.createdAt` | `orders.created_at` |
| `order.updatedAt` | `orders.updated_at` |
| `productOrder.*` | `order_items.*` |
| `payment.*` | `payments.*` (LEFT JOIN) |
| `receiver.*` | `order_items.receiver_*` |
| `delivery.*` | `order_items.delivery_status, shipment_*` |

> cancel/claim 요약은 리스트 기본 쿼리 실행 후, orderItemId 목록으로 배치 조회하여 조합.
> - `hasActiveCancel/Claim`: status NOT IN ('COMPLETED','REJECTED','CANCELLED') 존재 여부
> - `totalCancelledQty/totalClaimedQty`: SUM(quantity)
> - `cancelableQty`: productOrder.quantity - totalCancelledQty
> - `claimableQty`: productOrder.quantity - totalClaimedQty
> - `latest`: requestedAt DESC LIMIT 1

### 상세 쿼리 (멀티 쿼리)

1. `findOrderItem(orderItemId)` → 상품주문 + JOIN orders + LEFT JOIN payments
2. `findCancelsByOrderItemId(orderItemId)` → 해당 상품주문의 취소 목록
3. `findClaimsByOrderItemId(orderItemId)` → 해당 상품주문의 클레임 목록
4. `findOrderHistories(orderId)` → 주문 타임라인 (주문 레벨)
