# Settlement 도메인 스펙 V1

## 개요

정산을 담당하는 도메인입니다. 주문에 대한 정산 상태를 관리합니다.
실제 PG사 요청은 발생하지 않으며, 정산 상태만 관리합니다.

---

## SettlementStatus (Enum)

| 상태 | 설명 |
|------|------|
| PENDING | 정산 대기 |
| HOLD | 정산 보류 |
| COMPLETED | 정산 완료 |

### 상태 흐름

```
PENDING ──→ COMPLETED
    │
    └──→ HOLD ──→ PENDING ──→ COMPLETED
```

---

## 일별 정산 내역 조회

**`GET /settlements/daily`**

기간별 일별 정산 통계를 조회합니다.

### Request (Query Parameters)

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `sellerIds` | List | X | 판매자 ID (예: `1,2,3`) |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

### Response 200

```typescript
interface DailySettlementListResponse {
  data: PageData<DailySettlementItem>;
  timestamp: string;
  requestId: string;
}

interface DailySettlementItem {
  // ═══════════════════════════════════════════════════════════════
  // 정산일 정보
  // ═══════════════════════════════════════════════════════════════
  settlementDay: string;                    // 정산 예정일 (YYYY-MM-DD)
  settlementCompleteDay: string | null;     // 정산 완료일 (YYYY-MM-DD)

  // ═══════════════════════════════════════════════════════════════
  // 주문 건수
  // ═══════════════════════════════════════════════════════════════
  orderCount: number;                       // 총 주문 건수
  ourMallOrderCount: number;                // 자사몰 주문 건수
  externalMallOrderCount: number;           // 외부몰 주문 건수

  // ═══════════════════════════════════════════════════════════════
  // 금액 정보
  // ═══════════════════════════════════════════════════════════════
  totalSalesAmount: Money;                  // 총 판매 금액

  // 할인 금액
  discount: {
    totalAmount: Money;                     // 총 할인 금액
    sellerAmount: Money;                    // 셀러 부담 할인
    platformAmount: Money;                  // 플랫폼 부담 할인
  };

  // 마일리지
  mileage: {
    totalAmount: Money;                     // 총 마일리지
    sellerAmount: Money;                    // 셀러 부담 마일리지
    platformAmount: Money;                  // 플랫폼 부담 마일리지
  };

  // 수수료
  fee: {
    totalAmount: Money;                     // 총 수수료
  };

  // ═══════════════════════════════════════════════════════════════
  // 정산 금액
  // ═══════════════════════════════════════════════════════════════
  expectedSettlementAmount: Money;          // 예상 정산 금액
  settlementAmount: Money;                  // 확정 정산 금액
}
```

---

## 정산 대상 리스트 조회

**`GET /settlements`**

정산 대상이 되는 주문 리스트를 조회합니다.

### Request (Query Parameters)

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `startDate` | String | O | 시작일 (YYYY-MM-DD) |
| `endDate` | String | O | 종료일 (YYYY-MM-DD) |
| `status` | List | X | 정산 상태 (예: `PENDING,HOLD`) |
| `sellerIds` | List | X | 판매자 ID (예: `1,2,3`) |
| `searchField` | Enum | X | 검색 필드 |
| `searchWord` | String | X | 검색어 |
| `page` | Number | O | 페이지 번호 (0부터) |
| `size` | Number | O | 페이지 크기 |

#### searchField 옵션

| 값 | 설명 |
|------|------|
| `ORDER_ID` | 주문 ID |
| `ORDER_NUMBER` | 주문 번호 |
| `PRODUCT_NAME` | 상품명 |
| `BUYER_NAME` | 구매자명 |
| `PAYMENT_ID` | 결제 ID |
| `PAYMENT_NUMBER` | 결제 번호 |

### Response 200

```typescript
interface SettlementListResponse {
  data: PageData<SettlementListItem>;
  timestamp: string;
  requestId: string;
}

interface SettlementListItem {
  // ═══════════════════════════════════════════════════════════════
  // 정산 기본 정보
  // ═══════════════════════════════════════════════════════════════
  settlementId: string;                     // 정산 ID (UUIDv7)
  status: SettlementStatus;                 // 정산 상태

  // ═══════════════════════════════════════════════════════════════
  // 주문 정보
  // ═══════════════════════════════════════════════════════════════
  orderId: string;                          // 주문 ID (UUIDv7)
  orderNumber: string;                      // 주문 번호 (ORD-YYYYMMDD-XXXX)
  legacyOrderId: number | null;             // 레거시 주문 ID

  // ═══════════════════════════════════════════════════════════════
  // 주문 상품 정보 (ORDER_SPEC_V4 OrderProduct 참조)
  // ═══════════════════════════════════════════════════════════════
  orderProduct: OrderProduct;               // ORDER_SPEC_V4.md 참조

  // ═══════════════════════════════════════════════════════════════
  // 구매자 정보
  // ═══════════════════════════════════════════════════════════════
  buyer: {
    buyerId: number;                        // 구매자 ID
    buyerName: string;                      // 구매자명
  };

  // ═══════════════════════════════════════════════════════════════
  // 판매자 정보
  // ═══════════════════════════════════════════════════════════════
  seller: {
    sellerId: number;                       // 판매자 ID
    sellerName: string;                     // 판매자명
  };

  // ═══════════════════════════════════════════════════════════════
  // 결제 정보
  // ═══════════════════════════════════════════════════════════════
  payment: {
    paymentId: string;                      // 결제 ID (UUIDv7)
    paymentNumber: string;                  // 결제 번호
  };

  // ═══════════════════════════════════════════════════════════════
  // 금액 정보 (확장 가능 구조)
  // ═══════════════════════════════════════════════════════════════
  amounts: {
    // 판매 금액
    salesAmount: Money;                     // 총 판매 금액

    // 차감 항목 (셀러/플랫폼 부담 구분 가능)
    deductions: SettlementDeduction[];      // 차감 항목 목록

    // 수수료
    fee: {
      amount: Money;                        // 수수료 금액
      rate: number;                         // 수수료율 (%)
    };

    // 정산 금액
    expectedSettlementAmount: Money;        // 예상 정산 금액
    settlementAmount: Money;                // 확정 정산 금액
  };

  // ═══════════════════════════════════════════════════════════════
  // 일시 정보
  // ═══════════════════════════════════════════════════════════════
  orderedAt: string;                        // 주문 일시
  deliveredAt: string | null;               // 배송 완료 일시
  expectedSettlementDay: string;            // 정산 예정일 (YYYY-MM-DD)
  settlementDay: string | null;             // 정산 완료일 (YYYY-MM-DD)

  // ═══════════════════════════════════════════════════════════════
  // 보류 정보 (HOLD 상태인 경우)
  // ═══════════════════════════════════════════════════════════════
  holdInfo: {
    holdReason: string;                     // 보류 사유
    holdAt: string;                         // 보류 일시
  } | null;
}
```

---

## 정산 상태 변경 - 다건

**`POST /settlements/status/batch`**

정산 상태를 일괄 변경합니다.

### Request Body

```typescript
interface SettlementStatusBatchRequest {
  settlementIds: string[];                  // 정산 ID 목록 (UUIDv7)
  targetStatus: SettlementStatus;           // 변경할 상태
  reason?: string;                          // 변경 사유 (HOLD인 경우 필수)
}
```

### Response 204 No Content

(응답 본문 없음)

### 에러 코드

| 에러 코드 | 상황 |
|-----------|------|
| `SETTLEMENT_NOT_FOUND` | 존재하지 않는 정산 |
| `INVALID_STATUS_TRANSITION` | 유효하지 않은 상태 전환 |
| `HOLD_REASON_REQUIRED` | HOLD 상태 변경 시 사유 필수 |

---

## 정산 완료 처리 - 다건

**`POST /settlements/complete/batch`**

정산을 완료 처리합니다. (PENDING → COMPLETED)

### Request Body

```typescript
interface SettlementCompleteBatchRequest {
  settlementIds: string[];                  // 정산 ID 목록 (UUIDv7)
}
```

### Response 204 No Content

(응답 본문 없음)

### 에러 코드

| 에러 코드 | 상황 |
|-----------|------|
| `SETTLEMENT_NOT_FOUND` | 존재하지 않는 정산 |
| `NOT_PENDING_STATUS` | PENDING 상태가 아님 |
| `HAS_HOLD_SETTLEMENT` | 보류 중인 정산 포함 |

---

## 정산 보류 처리 - 다건

**`POST /settlements/hold/batch`**

정산을 보류 처리합니다. (PENDING → HOLD)

### Request Body

```typescript
interface SettlementHoldBatchRequest {
  settlementIds: string[];                  // 정산 ID 목록 (UUIDv7)
  holdReason: string;                       // 보류 사유 (필수)
}
```

### Response 204 No Content

(응답 본문 없음)

### 에러 코드

| 에러 코드 | 상황 |
|-----------|------|
| `SETTLEMENT_NOT_FOUND` | 존재하지 않는 정산 |
| `NOT_PENDING_STATUS` | PENDING 상태가 아님 |
| `ALREADY_HOLD` | 이미 보류 상태 |

---

## 정산 보류 해제 - 다건

**`POST /settlements/release/batch`**

정산 보류를 해제합니다. (HOLD → PENDING)

### Request Body

```typescript
interface SettlementReleaseBatchRequest {
  settlementIds: string[];                  // 정산 ID 목록 (UUIDv7)
}
```

### Response 204 No Content

(응답 본문 없음)

### 에러 코드

| 에러 코드 | 상황 |
|-----------|------|
| `SETTLEMENT_NOT_FOUND` | 존재하지 않는 정산 |
| `NOT_HOLD_STATUS` | HOLD 상태가 아님 |

---

## 타입 정의

### SettlementDeduction

차감 항목 (할인, 마일리지, 쿠폰 등 확장 가능)

```typescript
interface SettlementDeduction {
  type: DeductionType;                      // 차감 유형
  payer: DeductionPayer;                    // 부담 주체
  amount: Money;                            // 차감 금액
  description?: string;                     // 상세 설명 (선택)
}
```

---

## Enum 정의

```typescript
// 정산 상태
type SettlementStatus =
  | 'PENDING'             // 정산 대기
  | 'HOLD'                // 정산 보류
  | 'COMPLETED';          // 정산 완료

// 차감 유형 (확장 가능)
type DeductionType =
  | 'DISCOUNT'            // 할인
  | 'MILEAGE'             // 마일리지
  | 'COUPON'              // 쿠폰
  | 'POINT';              // 포인트

// 차감 부담 주체
type DeductionPayer =
  | 'SELLER'              // 셀러 부담
  | 'PLATFORM';           // 플랫폼 부담
```

---

## API 요약

| 기능 | 엔드포인트 | 응답 | 설명 |
|------|-----------|------|------|
| 일별 정산 내역 조회 | `GET /settlements/daily` | 200 | 기간별 일별 통계 |
| 정산 대상 리스트 조회 | `GET /settlements` | 200 | 개별 주문 단위 정산 목록 |
| 정산 상태 변경 (다건) | `POST /settlements/status/batch` | 204 | 상태 일괄 변경 |
| 정산 완료 처리 (다건) | `POST /settlements/complete/batch` | 204 | PENDING → COMPLETED |
| 정산 보류 처리 (다건) | `POST /settlements/hold/batch` | 204 | PENDING → HOLD |
| 정산 보류 해제 (다건) | `POST /settlements/release/batch` | 204 | HOLD → PENDING |
