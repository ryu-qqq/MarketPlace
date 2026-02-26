# Order 도메인 스펙 V4

## 개요

입점형 이커머스의 주문 도메인입니다. 결제 완료 시 상품 단위로 주문이 생성됩니다.

> **V3 → V4 변경사항**
> - 레거시 네이밍 유지: BuyerInfo, PaymentDetail, ReceiverInfo, PaymentShipmentInfo, SettlementInfo
> - OrderProduct에서 ProductGroupSnapShotDetails 제거 → productGroupName, price 직접 포함
> - 기존 레거시 응답 구조와 호환성 유지
> - orderId: number → string (UUIDv7)
> - orderNumber 추가 (ORD-YYYYMMDD-XXXX)
> - legacyOrderId 추가 (마이그레이션 호환)

---

## 주문 리스트 조회

**`GET /orders`**

### Request (Query Parameters)

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `dateField` | Enum | O | `ORDERED` \| `SHIPPED` \| `DELIVERED` |
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `status` | List | X | 주문 상태 (예: `ORDERED,SHIPPED`) |
| `sellerIds` | List | X | 판매자 ID (예: `1,2,3`) |
| `searchField` | Enum | X | 검색 필드 |
| `searchWord` | String | X | 검색어 |
| `sort` | String | X | 정렬 (예: `orderId,desc`) |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

### Response 200

```typescript
interface OrderListResponse {
  data: PageData<OrderListItem>;
  timestamp: string;
  requestId: string;
}

interface OrderListItem {
  // ═══════════════════════════════════════════════════════════════
  // 주문 기본 정보
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                          // 주문 ID (UUIDv7)
  orderNumber: string;                      // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;             // 레거시 주문 ID (마이그레이션 호환)

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (레거시 BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (레거시 PaymentDetail)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentDetail;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (레거시 ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (레거시 PaymentShipmentInfo)
  // ═══════════════════════════════════════════════════════════════
  paymentShipmentInfo: PaymentShipmentInfo;

  // ═══════════════════════════════════════════════════════════════
  // 정산 정보 (레거시 SettlementInfo)
  // ═══════════════════════════════════════════════════════════════
  settlementInfo: SettlementInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (레거시 OrderProduct - ProductGroupSnapShotDetails 평탄화)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (외부몰 주문인 경우에만, 자사몰이면 null)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 취소 요약 정보 (배송 전 취소, 취소 없으면 null) - 신규 추가
  // ═══════════════════════════════════════════════════════════════
  cancel: {
    hasActiveCancel: boolean;             // 진행 중인 취소 존재 여부
    totalCancelledQty: number;            // 총 취소 수량 (진행 중 + 완료)
    cancelableQty: number;                // 추가 취소 가능 수량
    latest: {                             // 가장 최근 취소 정보
      cancelId: string;                   // 취소 ID (UUIDv7)
      cancelNumber: string;               // 취소 번호 (CAN-YYYYMMDD-XXXX)
      type: CancelType;                   // 취소 유형 (BUYER_REQUEST, SELLER_CANCEL)
      status: CancelStatus;               // 취소 상태
      qty: number;                        // 취소 수량
      requestedAt: string;                // 취소 신청 일시
    } | null;
  } | null;

  // ═══════════════════════════════════════════════════════════════
  // 클레임 요약 정보 (환불/교환, 배송 후, 클레임 없으면 null)
  // ═══════════════════════════════════════════════════════════════
  claim: {
    hasActiveClaim: boolean;              // 진행 중인 클레임 존재 여부
    activeCount: number;                  // 진행 중인 클레임 수
    totalClaimedQty: number;              // 총 클레임 수량 (진행 중 + 완료)
    claimableQty: number;                 // 추가 클레임 가능 수량 (주문수량 - 클레임수량)
    latest: {                             // 가장 최근 클레임 정보
      claimId: string;                    // 클레임 ID (UUIDv7)
      claimNumber: string;                // 클레임 번호 (CLM-YYYYMMDD-XXXX)
      type: ClaimType;                    // 클레임 유형 (REFUND, EXCHANGE)
      status: ClaimStatus;                // 클레임 상태
      qty: number;                        // 클레임 수량
      requestedAt: string;                // 클레임 신청 일시
    } | null;
  } | null;
}
```

---

## 주문 단건 조회

**`GET /orders/{orderId}`**

### Response 200

```typescript
interface OrderDetailResponse {
  data: OrderDetail;
  timestamp: string;
  requestId: string;
}

interface OrderDetail {
  // ═══════════════════════════════════════════════════════════════
  // 주문 기본 정보
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                          // 주문 ID (UUIDv7)
  orderNumber: string;                      // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;             // 레거시 주문 ID (마이그레이션 호환)

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보 (레거시 BuyerInfo)
  // ═══════════════════════════════════════════════════════════════
  buyerInfo: BuyerInfo;

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보 (레거시 PaymentDetail)
  // ═══════════════════════════════════════════════════════════════
  payment: PaymentDetail;

  // ═══════════════════════════════════════════════════════════════
  // 수령인 정보 (레거시 ReceiverInfo)
  // ═══════════════════════════════════════════════════════════════
  receiverInfo: ReceiverInfo;

  // ═══════════════════════════════════════════════════════════════
  // 배송 정보 (레거시 PaymentShipmentInfo)
  // ═══════════════════════════════════════════════════════════════
  paymentShipmentInfo: PaymentShipmentInfo;

  // ═══════════════════════════════════════════════════════════════
  // 정산 정보 (레거시 SettlementInfo)
  // ═══════════════════════════════════════════════════════════════
  settlementInfo: SettlementInfo;

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (레거시 OrderProduct - ProductGroupSnapShotDetails 평탄화)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;

  // ═══════════════════════════════════════════════════════════════
  // 외부몰 주문 정보 (외부몰 주문인 경우에만, 자사몰이면 null)
  // ═══════════════════════════════════════════════════════════════
  externalOrderInfo: ExternalOrderInfo | null;

  // ═══════════════════════════════════════════════════════════════
  // 취소 요약 정보 (배송 전 취소, 취소 없으면 null) - 신규 추가
  // ═══════════════════════════════════════════════════════════════
  cancel: {
    hasActiveCancel: boolean;               // 진행 중인 취소 존재 여부
    totalCancelledQty: number;              // 총 취소 수량 (진행 중 + 완료)
    cancelableQty: number;                  // 추가 취소 가능 수량
    latest: {                               // 가장 최근 취소 정보
      cancelId: string;                     // 취소 ID (UUIDv7)
      cancelNumber: string;                 // 취소 번호 (CAN-YYYYMMDD-XXXX)
      type: CancelType;                     // 취소 유형 (BUYER_REQUEST, SELLER_CANCEL)
      status: CancelStatus;                 // 취소 상태
      qty: number;                          // 취소 수량
      requestedAt: string;                  // 취소 신청 일시
    } | null;
  } | null;

  // ═══════════════════════════════════════════════════════════════
  // 클레임 요약 정보 (환불/교환, 배송 후, 클레임 없으면 null)
  // ═══════════════════════════════════════════════════════════════
  claim: {
    hasActiveClaim: boolean;                // 진행 중인 클레임 존재 여부
    activeCount: number;                    // 진행 중인 클레임 수
    totalClaimedQty: number;                // 총 클레임 수량 (진행 중 + 완료)
    claimableQty: number;                   // 추가 클레임 가능 수량 (주문수량 - 클레임수량)
    latest: {                               // 가장 최근 클레임 정보
      claimId: string;                      // 클레임 ID (UUIDv7)
      claimNumber: string;                  // 클레임 번호 (CLM-YYYYMMDD-XXXX)
      type: ClaimType;                      // 클레임 유형 (REFUND, EXCHANGE)
      status: ClaimStatus;                  // 클레임 상태
      qty: number;                          // 클레임 수량
      requestedAt: string;                  // 클레임 신청 일시
    } | null;
  } | null;

  // ═══════════════════════════════════════════════════════════════
  // 단건 조회 전용 필드
  // ═══════════════════════════════════════════════════════════════

  // 주문 상태 변경 이력
  orderHistories: OrderHistoryItem[];       // 상태 변경 이력 목록 (최신순)

  // 취소 ID 목록 (전체) - 신규 추가
  cancelIds: string[];                      // 해당 주문의 모든 취소 ID (상세 조회 링크용)

  // 취소 상세 목록 (최근 3개) - 신규 추가
  cancels: CancelItem[];                    // 취소 상세 정보 (최신순, 최대 3개)

  // 클레임 ID 목록 (전체)
  claimIds: string[];                       // 해당 주문의 모든 클레임 ID (상세 조회 링크용)

  // 클레임 상세 목록 (최근 3개)
  claims: ClaimItem[];                      // 클레임 상세 정보 (최신순, 최대 3개)
}
```

---

## 공통 인터페이스

### BuyerInfo

구매자 정보 (레거시 동일)

```typescript
interface BuyerInfo {
  buyerName: string;                        // 구매자 이름
  buyerEmail: string;                       // 구매자 이메일
  buyerPhoneNumber: string;                 // 구매자 연락처
}
```

---

### PaymentDetail

결제 정보 (레거시 동일)

```typescript
interface PaymentDetail {
  paymentId: number;                        // 결제 ID (auto increment)
  paymentAgencyId: string;                  // PG사 거래 ID
  paymentStatus: PaymentStatus;             // 결제 상태
  paymentMethod: PaymentMethodEnum;         // 결제 수단
  paymentDate: string;                      // 결제 일시 (yyyy-MM-dd HH:mm:ss)
  canceledDate: string | null;              // 취소 일시
  userId: number;                           // 사용자 ID (구매자)
  siteName: SiteName;                       // 사이트 (판매 채널)
  billAmount: Money;                        // 청구 금액 (결제금액 + 마일리지)
  paymentAmount: Money;                     // 실결제 금액
  usedMileageAmount: Money;                 // 사용 마일리지
}
```

---

### ReceiverInfo

수령인 정보 (레거시 동일)

```typescript
interface ReceiverInfo {
  receiverName: string;                     // 수령인 이름
  receiverPhoneNumber: string;              // 수령인 연락처
  addressLine1: string;                     // 기본 주소 (도로명/지번)
  addressLine2: string;                     // 상세 주소 (동/호수)
  zipCode: string;                          // 우편번호
  deliveryRequest: string | null;           // 배송 요청사항
}
```

---

### PaymentShipmentInfo

배송 정보 (레거시 동일)

```typescript
interface PaymentShipmentInfo {
  deliveryStatus: DeliveryStatus;           // 배송 상태
  shipmentCompanyCode: string | null;       // 택배사 코드
  invoice: string | null;                   // 송장번호
  shipmentCompletedDate: string | null;     // 출고 완료 일시 (yyyy-MM-dd HH:mm:ss)
}
```

---

### SettlementInfo

정산 정보 (레거시 동일)

```typescript
interface SettlementInfo {
  commissionRate: number;                   // 수수료율 (%)
  fee: number;                              // 수수료 금액
  expectationSettlementAmount: Money;       // 예상 정산 금액
  settlementAmount: Money;                  // 정산 금액
  shareRatio: number;                       // 쉐어 비율 (%)
  expectedSettlementDay: string | null;     // 정산 예정일 (yyyy-MM-dd HH:mm:ss)
  settlementDay: string | null;             // 정산 완료일 (yyyy-MM-dd HH:mm:ss)
}
```

---

### OrderProduct

주문 상품 정보 (ProductGroupSnapShotDetails 평탄화)

```typescript
interface OrderProduct {
  // ───────────────────────────────────────────────────────────────
  // 주문 식별
  // ───────────────────────────────────────────────────────────────
  orderId: string;                          // 주문 ID (UUIDv7)

  // ───────────────────────────────────────────────────────────────
  // 상품 정보 (ProductGroupSnapShotDetails에서 추출)
  // ───────────────────────────────────────────────────────────────
  productGroupName: string;                 // 상품 그룹명 (상품명)
  price: Price;                             // 가격 정보 (Price 객체)

  // ───────────────────────────────────────────────────────────────
  // 브랜드 정보 (BaseBrandContext)
  // ───────────────────────────────────────────────────────────────
  brand: Brand;

  // ───────────────────────────────────────────────────────────────
  // 상품 식별자
  // ───────────────────────────────────────────────────────────────
  productGroupId: number;                   // 상품 그룹 ID
  productId: number;                        // 상품 ID (SKU)

  // ───────────────────────────────────────────────────────────────
  // 판매자 및 배송
  // ───────────────────────────────────────────────────────────────
  sellerName: string;                       // 판매자명
  productGroupMainImageUrl: string;         // 상품 대표 이미지 URL
  deliveryArea: string;                     // 배송 지역

  // ───────────────────────────────────────────────────────────────
  // 주문 상세
  // ───────────────────────────────────────────────────────────────
  productQuantity: number;                  // 주문 수량
  orderStatus: OrderStatus;                 // 주문 상태
  regularPrice: Money;                      // 정가
  orderAmount: Money;                       // 주문 금액
  totalExpectedRefundMileageAmount: Money;  // 예상 환불 마일리지

  // ───────────────────────────────────────────────────────────────
  // 옵션 정보
  // ───────────────────────────────────────────────────────────────
  option: string;                           // 옵션 문자열 (예: "블랙 L")
  skuNumber: string;                        // SKU 번호
  options: OptionDto[];                     // 옵션 상세 목록
}
```

---

### Price

가격 정보 (ProductGroupSnapShotDetails.price에서 추출)

```typescript
interface Price {
  regularPrice: number;                     // 정가 (원)
  currentPrice: number;                     // 현재가 (원)
  salePrice: number;                        // 판매가 (원)
  directDiscountPrice: number;              // 직접 할인가 (원)
  directDiscountRate: number;               // 직접 할인율 (%)
  discountRate: number;                     // 할인율 (%)
}
```

---

### Brand

브랜드 정보 (BaseBrandContext)

```typescript
interface Brand {
  brandId: number;                          // 브랜드 ID
  brandName: string;                        // 브랜드명
}
```

---

### OptionDto

옵션 상세 정보

```typescript
interface OptionDto {
  optionName: string;                       // 옵션명 (예: "색상", "사이즈")
  optionValue: string;                      // 옵션값 (예: "블랙", "L")
}
```

---

### ExternalOrderInfo

외부몰 주문 정보 (외부몰 주문인 경우에만 존재)

```typescript
interface ExternalOrderInfo {
  shopId: number;                           // 샵 ID (Long)
  shopCode: string;                         // 샵 코드 (NAVER-001, COUPANG-001, 11ST-001 등)
  shopOrderNo: string;                      // 외부몰 주문번호
  shopOrderStatus: string;                  // 외부몰 주문상태 (원본 그대로)
  shopOrderedAt: string;                    // 외부몰 주문일시
}
```

---

### OrderHistoryItem

주문 상태 변경 이력 (단건 조회 전용)

```typescript
interface OrderHistoryItem {
  orderId: string;                          // 주문 ID (UUIDv7)
  changeReason: string;                     // 상태 변경 사유 (출고 처리, 배송 완료 등)
  changeDetailReason: string;               // 상세 변경 사유
  orderStatus: OrderStatus;                 // 변경된 주문 상태
  createdAt: string;                        // 이력 생성 일시
  updatedAt: string;                        // 이력 수정 일시
}
```

---

### ClaimItem

클레임 상세 항목 (단건 조회에서 최근 3개까지 표시)

```typescript
interface ClaimItem {
  // ───────────────────────────────────────────────────────────────
  // 클레임 기본 정보
  // ───────────────────────────────────────────────────────────────
  claimId: string;                          // 클레임 ID (UUIDv7)
  claimNumber: string;                      // 클레임 번호 (CLM-YYYYMMDD-XXXX)
  type: ClaimType;                          // 클레임 유형 (REFUND: 환불, EXCHANGE: 교환)
  status: ClaimStatus;                      // 클레임 상태
  qty: number;                              // 클레임 수량

  // ───────────────────────────────────────────────────────────────
  // 클레임 사유
  // ───────────────────────────────────────────────────────────────
  reason: {
    reasonType: ClaimReason;                // 사유 유형 (CHANGE_OF_MIND, DEFECTIVE_PRODUCT 등)
    reasonDetail: string | null;            // 상세 사유 (고객 입력)
  };

  // ───────────────────────────────────────────────────────────────
  // 회수 정보
  // ───────────────────────────────────────────────────────────────
  collectMethod: CollectMethod | null;      // 회수 방식 (SELF_DELIVERY: 직접발송, AUTO_PICKUP: 자동수거)

  // ───────────────────────────────────────────────────────────────
  // 환불 정보 (REFUND 유형인 경우에만)
  // ───────────────────────────────────────────────────────────────
  refundInfo: {
    originalAmount: Money;                  // 원 상품 금액
    deductionAmount: Money;                 // 차감 금액 (반품 배송비 등)
    deductionReason: string | null;         // 차감 사유
    finalAmount: Money;                     // 최종 환불 금액 (= original - deduction)
    refundMethod: string;                   // 환불 방식 (ORIGINAL_PAYMENT: 원결제수단)
    refundedAt: string | null;              // 환불 완료 일시
  } | null;

  // ───────────────────────────────────────────────────────────────
  // 교환 정보 (EXCHANGE 유형인 경우에만)
  // ───────────────────────────────────────────────────────────────
  exchangeInfo: {
    newProductGroupId: number;              // 교환 상품 그룹 ID
    newProductId: number;                   // 교환 상품 ID (SKU)
    newOptionName: string;                  // 교환 옵션명
    priceDifference: Money;                 // 가격 차이 (+: 추가결제, -: 환불)
    newOrderId: string | null;              // 교환 출고용 신규 주문 ID (UUIDv7)
    newOrderNumber: string | null;          // 교환 출고용 신규 주문번호
  } | null;

  // ───────────────────────────────────────────────────────────────
  // 일시 정보
  // ───────────────────────────────────────────────────────────────
  requestedAt: string;                      // 클레임 신청 일시
  completedAt: string | null;               // 클레임 완료 일시 (승인된 경우)
  rejectedAt: string | null;                // 클레임 거절 일시 (거절된 경우)
  createdAt: string;                        // 생성 일시
}
```

---

### CancelItem

취소 상세 항목 (단건 조회에서 최근 3개까지 표시) - 신규 추가

```typescript
interface CancelItem {
  // ───────────────────────────────────────────────────────────────
  // 취소 기본 정보
  // ───────────────────────────────────────────────────────────────
  cancelId: string;                         // 취소 ID (UUIDv7)
  cancelNumber: string;                     // 취소 번호 (CAN-YYYYMMDD-XXXX)
  type: CancelType;                         // 취소 유형 (BUYER_REQUEST, SELLER_CANCEL)
  status: CancelStatus;                     // 취소 상태
  qty: number;                              // 취소 수량

  // ───────────────────────────────────────────────────────────────
  // 취소 사유
  // ───────────────────────────────────────────────────────────────
  reason: {
    reasonType: CancelReasonType;           // 사유 유형 (CHANGE_OF_MIND, OUT_OF_STOCK 등)
    reasonDetail: string | null;            // 상세 사유
  };

  // ───────────────────────────────────────────────────────────────
  // 환불 정보 (승인된 경우)
  // ───────────────────────────────────────────────────────────────
  refundInfo: {
    originalAmount: Money;                  // 원 상품 금액
    finalAmount: Money;                     // 최종 환불 금액
    refundMethod: string;                   // 환불 방식 (ORIGINAL_PAYMENT: 원결제수단)
    refundedAt: string | null;              // 환불 완료 일시
  } | null;

  // ───────────────────────────────────────────────────────────────
  // 일시 정보
  // ───────────────────────────────────────────────────────────────
  requestedAt: string;                      // 취소 신청 일시
  completedAt: string | null;               // 완료 일시
  createdAt: string;                        // 생성 일시
  // 참고: memo, rejectReason 등은 cancelHistories에서 조회
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

// 결제 수단
type PaymentMethodEnum =
  | 'CARD'              // 카드
  | 'BANK_TRANSFER'     // 계좌이체
  | 'VIRTUAL_ACCOUNT'   // 가상계좌
  | 'PHONE'             // 휴대폰 결제
  | 'KAKAO_PAY'         // 카카오페이
  | 'NAVER_PAY'         // 네이버페이
  | 'TOSS_PAY';         // 토스페이

// 배송 상태
type DeliveryStatus =
  | 'READY'             // 배송 준비
  | 'SHIPPED'           // 출고됨
  | 'IN_TRANSIT'        // 배송 중
  | 'DELIVERED'         // 배송 완료
  | 'FAILED';           // 배송 실패

// 사이트 (판매 채널)
type SiteName =
  | 'SETOF'             // 자사몰
  | 'NAVER'             // 네이버 스마트스토어
  | 'COUPANG'           // 쿠팡
  | 'KAKAO'             // 카카오
  | 'ZIGZAG';           // 지그재그

// 국가
type Origin =
  | 'KR'                // 한국
  | 'US'                // 미국
  | 'CN'                // 중국
  | 'JP';               // 일본

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
  | 'CANCELLED'         // 클레임 취소
  | 'CONVERTED';        // 전환됨 (환불→교환 등)

// 클레임 사유
type ClaimReason =
  | 'CHANGE_OF_MIND'    // 단순 변심
  | 'DEFECTIVE_PRODUCT' // 상품 불량
  | 'WRONG_PRODUCT'     // 오배송
  | 'DELAYED_DELIVERY'  // 배송 지연
  | 'PRODUCT_MISMATCH'  // 상품 정보 불일치
  | 'OTHER';            // 기타

// 회수 방식
type CollectMethod =
  | 'SELF_DELIVERY'     // 직접 발송
  | 'AUTO_PICKUP';      // 자동 수거

// ═══════════════════════════════════════════════════════════════
// 취소 관련 Enum (신규 추가)
// ═══════════════════════════════════════════════════════════════

// 취소 유형
type CancelType =
  | 'BUYER_REQUEST'     // 구매자 취소 요청
  | 'SELLER_CANCEL';    // 판매자 직접 취소

// 취소 상태
type CancelStatus =
  | 'REQUESTED'         // 취소 요청됨 (구매자 요청, 승인 대기)
  | 'APPROVED'          // 취소 승인됨
  | 'REJECTED'          // 취소 반려됨
  | 'COMPLETED'         // 취소 완료 (환불 완료)
  | 'CANCELLED';        // 취소 요청 자체가 취소됨 (구매자가 취소 요청 철회)

// 취소 사유 유형
type CancelReasonType =
  // 구매자 사유
  | 'CHANGE_OF_MIND'    // 단순 변심
  | 'WRONG_ORDER'       // 잘못 주문함
  | 'FOUND_CHEAPER'     // 다른 곳에서 더 저렴하게 구매
  | 'DELIVERY_DELAY'    // 배송 지연
  // 판매자 사유
  | 'OUT_OF_STOCK'      // 재고 부족
  | 'PRODUCT_ISSUE'     // 상품 문제
  | 'PRICE_ERROR'       // 가격 오류
  | 'SHIPPING_ISSUE'    // 배송 불가
  // 공통
  | 'OTHER';            // 기타
```

---

## 레거시 매핑

### 리스트 조회 구조 변경

| V4 | 레거시 | 변경 사항 |
|----|--------|----------|
| `orderId` | `orderId` | 동일 (auto increment) |
| `legacyOrderId` | - | **신규** (마이그레이션 호환) |
| `buyerInfo` | `BuyerInfo` | 동일 |
| `payment` | `PaymentDetail` | 동일 |
| `receiverInfo` | `ReceiverInfo` | 동일 |
| `paymentShipmentInfo` | `PaymentShipmentInfo` | 동일 |
| `settlementInfo` | `SettlementInfo` | 동일 |
| `orderProduct` | `OrderProduct` | `productGroupName`, `price` 직접 포함 |
| `externalOrderInfo` | - | **신규** (외부몰 주문 정보) |
| `cancel` | - | **신규** (취소 요약 정보, 배송 전) |
| `claim` | - | **신규** (클레임 요약 정보, 배송 후) |
| - | `orderHistories` | **제거** (리스트에서 불필요) |
| - | `ProductGroupSnapShotDetails` | **제거** (필드 평탄화) |

### 단건 조회 구조 변경

| V4 | 레거시 | 변경 사항 |
|----|--------|----------|
| `legacyOrderId` | - | **신규** (마이그레이션 호환) |
| `externalOrderInfo` | - | **신규** (외부몰 주문 정보) |
| `cancel` | - | **신규** (취소 요약 정보, 배송 전) |
| `cancelIds` | - | **신규** (취소 ID 목록) |
| `cancels` | - | **신규** (취소 상세 목록, 최근 3개) |
| `claim` | - | **신규** (클레임 요약 정보, 배송 후) |
| `claimIds` | - | **신규** (클레임 ID 목록) |
| `claims` | - | **신규** (클레임 상세 목록, 최근 3개) |
| `orderHistories` | `OrderHistoryResponse` | 동일 (단건에서만 포함) |

### OrderProduct 변경 상세

**제거된 필드:**
- `productGroupDetails: ProductGroupSnapShotDetails`

**추가된 필드 (ProductGroupSnapShotDetails에서 추출):**
- `productGroupName: string` - 상품명
- `price: Price` - 가격 정보 객체

**유지된 필드:**
- `orderId`, `brand`, `productGroupId`, `productId`
- `sellerName`, `productGroupMainImageUrl`, `deliveryArea`
- `productQuantity`, `orderStatus`, `regularPrice`, `orderAmount`
- `totalExpectedRefundMileageAmount`, `option`, `skuNumber`, `options`

---

## 취소 API

> **상세 스펙**: [cancel_spec.md](./cancel_spec.md) 참조
>
> 취소 API는 모두 **배치 방식**으로 제공됩니다.

### 판매취소 (배치)

**`POST /cancels/seller-cancel/batch`**

재고 부족, 상품 문제 등의 사유로 셀러가 직접 주문을 일괄 취소합니다.

**Request Body**

```typescript
interface SellerCancelBatchRequest {
  items: {
    orderId: string;                        // 주문 ID (UUIDv7)
    cancelQty: number;                      // 취소 수량
  }[];
  reason: {
    reasonType: CancelReasonType;           // 취소 사유 (판매자 사유만)
    reasonDetail?: string;                  // 상세 사유
  };
  memo?: string;                            // 메모 (선택)
}
```

**Response 200**: `CancelBatchResult` (상세: cancel_spec.md)

---

### 구매취소 승인 (배치)

**`POST /cancels/approve/batch`**

구매자가 요청한 취소를 일괄 승인합니다.

**Request Body**

```typescript
interface CancelApproveBatchRequest {
  cancelIds: string[];                      // 취소 ID 목록 (UUIDv7)
  memo?: string;                            // 승인 메모 (선택)
}
```

**Response 200**: `CancelBatchResult` (상세: cancel_spec.md)

---

### 구매취소 거부 (배치)

**`POST /cancels/reject/batch`**

구매자가 요청한 취소를 일괄 거부합니다.

**Request Body**

```typescript
interface CancelRejectBatchRequest {
  cancelIds: string[];                      // 취소 ID 목록 (UUIDv7)
  reason: {
    reasonType: string;                     // 거부 사유 타입
    reasonDetail: string;                   // 거부 상세 사유
  };
  memo?: string;                            // 처리 메모 (선택)
}
```

**Response 200**: `CancelBatchResult` (상세: cancel_spec.md)

---

## 취소 관련 Enum

> 취소 관련 Enum(`CancelType`, `CancelStatus`, `CancelReasonType`)은 상단 **Enum 정의** 섹션에 통합되었습니다.

---

## 취소 API 요약

| 기능 | 엔드포인트 | 응답 | 설명 |
|------|-----------|------|------|
| 판매취소 (배치) | `POST /cancels/seller-cancel/batch` | 200 | 셀러가 직접 일괄 취소 |
| 구매취소 승인 (배치) | `POST /cancels/approve/batch` | 200 | 구매자 취소 요청 일괄 승인 |
| 구매취소 거부 (배치) | `POST /cancels/reject/batch` | 200 | 구매자 취소 요청 일괄 거부 |
