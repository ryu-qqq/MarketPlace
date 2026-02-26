# 교환 관리 API 스펙 (Backend)

> 본 문서는 프론트엔드 명세를 기반으로 백엔드 API 스펙을 정의합니다.
> 교환(Exchange)은 동일 상품군 내 옵션 변경(색상, 사이즈 등)만 가능한 클레임 유형입니다.

---

## 📋 목차

1. [공통 타입 정의](#1-공통-타입-정의)
2. [공통 변경 사항](#2-공통-변경-사항)
3. [API 매핑 테이블](#3-api-매핑-테이블)
4. [조회/필터/KPI API](#4-조회필터kpi-api)
5. [상세/히스토리 API](#5-상세히스토리-api)
6. [상태변경/배치 API](#6-상태변경배치-api)
7. [단건 수정 API](#7-단건-수정-api)
8. [교환 전용 API](#8-교환-전용-api)

---

## 1. 공통 타입 정의

### 1-1. ExchangeStatus (enum)

```typescript
// 교환 전용 상태 (수거 + 재배송 복합 프로세스)

enum ExchangeStatus {
  REQUESTED    // 교환 요청됨
  COLLECTING   // 수거 중 (원상품)
  COLLECTED    // 수거 완료 (원상품)
  PREPARING    // 교환상품 준비 중 (검수 포함)
  SHIPPING     // 재배송 중 (교환상품)
  COMPLETED    // 교환 완료
  REJECTED     // 교환 거부
  CANCELLED    // 취소됨
}
```

#### 상태 전이도

```
REQUESTED → COLLECTING → COLLECTED → PREPARING → SHIPPING → COMPLETED

* REQUESTED → CANCELLED (고객 취소)
* REQUESTED → REJECTED (판매자 거부)
* COLLECTING → CANCELLED (고객 취소)
* PREPARING → REJECTED (검수 불합격 등)
```

### 1-2. ExchangeReasonType (enum)

```typescript
enum ExchangeReasonType {
  SIZE_CHANGE         // 사이즈 변경
  COLOR_CHANGE        // 색상 변경
  OPTION_CHANGE       // 기타 옵션 변경
  WRONG_OPTION_SENT   // 잘못된 옵션 배송 (판매자 귀책)
  DEFECTIVE           // 상품 불량/파손 (판매자 귀책)
  OTHER               // 기타
}
```

#### 귀책 매핑

| 사유 | 귀책 |
|------|------|
| `SIZE_CHANGE` | 구매자 |
| `COLOR_CHANGE` | 구매자 |
| `OPTION_CHANGE` | 구매자 |
| `WRONG_OPTION_SENT` | 판매자 |
| `DEFECTIVE` | 판매자 |
| `OTHER` | 판단 필요 (기본: 구매자) |

### 1-3. ShipmentMethodType (enum)

```typescript
// REFUND_SPEC.md와 동일
enum ShipmentMethodType {
  COURIER            // 택배
  QUICK              // 퀵서비스
  VISIT              // 직접 방문 전달
  AUTO_PICKUP        // 자동 수거
  DESIGNATED_COURIER // 지정 택배
}
```

### 1-4. FeePayer (enum)

```typescript
enum FeePayer {
  SELLER  // 판매자 귀책
  BUYER   // 구매자 귀책
}
```

### 1-5. 배송 구조 (교환 특이사항)

```
[교환 배송 구조]
- 원상품 수거: ClaimShipment (클레임 도메인)
- 교환상품 발송: 새 Order의 Shipment (주문 도메인)

→ ClaimShipment는 수거 전용, ShipmentRole enum 불필요
→ ExchangeClaim은 linkedOrderId로 새 주문 참조
```

### 1-6. ClaimReason (interface)

```typescript
interface ClaimReason {
  reasonType: ExchangeReasonType;
  reasonDetail: string;
}
```

### 1-7. ShipmentMethod (interface)

```typescript
interface ShipmentMethod {
  type: ShipmentMethodType;
  courierCode: string | null;
  courierName: string | null;
}
```

### 1-8. ShippingFeeInfo (interface)

```typescript
interface ShippingFeeInfo {
  amount: number;           // 배송비 금액
  payer: FeePayer;          // 부담 주체
  includeInPackage: boolean; // 동봉 여부
}
```

### 1-9. ExchangeOption (interface)

```typescript
// 교환 대상 옵션 정보
interface ExchangeOption {
  originalOption: ProductOption;  // 기존 옵션
  targetOption: ProductOption;    // 교환 희망 옵션
}

interface ProductOption {
  productId: number;          // 상품 ID (Long)
  optionId: number;           // 옵션 ID (Long)
  optionName: string;         // 옵션명 (예: "블랙 / XL")
  optionValues: OptionValue[];
}

interface OptionValue {
  name: string;   // 옵션 항목명 (예: "색상", "사이즈")
  value: string;  // 옵션 값 (예: "블랙", "XL")
}
```

### 1-10. ClaimShipment (interface)

```typescript
// ClaimShipment는 수거 전용 (환불/교환 모두 원상품 반송에만 사용)
// 교환상품 발송은 새 Order의 Shipment가 담당
interface ClaimShipment {
  shipmentId: string;                // 배송 ID (UUIDv7)
  claimId: string;                   // 클레임 ID (UUIDv7)
  claimType: "REFUND" | "EXCHANGE";  // 클레임 타입
  status: string;                    // PENDING, IN_TRANSIT, DELIVERED
  method: ShipmentMethod;
  trackingNumber: string | null;
  feeInfo: ShippingFeeInfo;
  sender: {
    name: string;
    phone: string;
    address: Address;
  };
  receiver: {
    name: string;
    phone: string;
    address: Address;
  };
  shippedAt: string | null;
  receivedAt: string | null;
}
```

### 1-11. AmountAdjustment (interface)

```typescript
// 교환 시 금액 조정 (옵션 가격 차이)
interface AmountAdjustment {
  originalPrice: number;          // 기존 상품 가격
  targetPrice: number;            // 교환 상품 가격
  priceDifference: number;        // 가격 차이 (양수: 추가결제, 음수: 부분환불)
  additionalPaymentRequired: boolean;  // 추가 결제 필요 여부
  partialRefundRequired: boolean;      // 부분 환불 필요 여부
  collectShippingFee: number;     // 수거 배송비
  reshipShippingFee: number;      // 재배송 배송비
  totalShippingFee: number;       // 총 배송비
  shippingFeePayer: FeePayer;     // 배송비 부담 주체
}
```

### 1-12. ApiResponse (interface)

```typescript
interface ApiResponse<T> {
  data: T;
  timestamp: string;  // ISO 8601 + Timezone
  requestId: string;  // UUID v7
}

interface PageApiResponse<T> extends ApiResponse<PageData<T>> {}

interface PageData<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
```

---

## 2. 공통 변경 사항

모든 API에 적용되는 공통 변경 사항입니다.

### 2-1. API 경로

| 도메인 | 경로 |
|--------|------|
| 환불 (Refund) | `/refunds/*` |
| **교환 (Exchange)** | `/exchanges/*` |

**사유**: 도메인 분리 - 환불(Refund)과 교환(Exchange)은 별도 도메인으로 분리

### 2-2. 응답 래핑 구조

모든 응답은 아래 형식으로 래핑됩니다:

```json
{
  "data": { /* 실제 응답 데이터 */ },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

### 2-3. claimId 타입

| 타입 | 형식 |
|------|------|
| `claimId` | `string` (UUIDv7) |

**사유**: 분산 시스템 호환성, 시간순 정렬 가능, 예측 불가능한 ID

### 2-4. 시간 필드명 표준화

| 패턴 | 설명 |
|------|------|
| `*At` suffix | 시간 필드 (createdAt, updatedAt, inspectedAt 등) |

### 2-5. DELETE 메서드 금지

리소스 삭제가 아닌 상태 변경은 PATCH 사용

---

## 3. API 매핑 테이블

| API | Method | 설명 |
|-----|--------|------|
| `/exchanges/summary` | GET | KPI 요약 조회 |
| `/exchanges` | GET | 교환 목록 조회 |
| `/exchanges/{claimId}` | GET | 교환 상세 조회 |
| `/exchanges/{claimId}/histories` | POST | 수기 메모 등록 |
| `/exchanges/{claimId}/status` | PATCH | 단건 상태 변경 |
| `/exchanges/status/batch` | POST | 배치 상태 변경 |
| `/exchanges/collect/batch` | POST | 수거 완료 배치 |
| `/exchanges/prepare/batch` | POST | 준비 완료 배치 (새 주문 생성 포함) |
| `/exchanges/complete/batch` | POST | 교환 완료 배치 |
| `/exchanges/reject/batch` | POST | 교환 거부 배치 |
| `/exchanges/convert-to-refund/batch` | POST | 환불로 전환 배치 |
| `/exchanges/{claimId}/reason` | PATCH | 교환 사유 수정 |
| `/exchanges/{claimId}/option` | PATCH | 교환 옵션 수정 |
| `/exchanges/{claimId}/shipment` | PATCH | 수거 배송 정보 수정 |
| `/exchanges/available-options` | GET | 교환 가능 옵션 조회 |

---

## 4. 조회/필터/KPI API

### 4-1. KPI 요약 조회

**`GET /exchanges/summary`**

**Response 200**

```json
{
  "data": {
    "requested": 5,
    "collecting": 3,
    "collected": 2,
    "preparing": 1,  -- 제거 
    "shipping": 2,
    "completed": 10,
    "rejected": 1,
    "cancelled": 0
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 4-2. 교환 목록 조회

**`GET /exchanges`**

**Request (Query)**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `dateType` | enum | X | `REQUESTED` \| `COMPLETED` (요청일, 교환완료일) |
| `startDate` | string | Y | 시작일 (YYYY-MM-DD) |
| `endDate` | string | Y | 종료일 (YYYY-MM-DD) |
| `status` | enum[] | X | ExchangeStatus 목록 |
| `shopIds` | number[] | X | 샵 ID 목록 |
| `partnerIds` | number[] | X | 파트너 ID 목록 |
| `isHold` | boolean | X | 교환보류 여부 |
| `searchField` | enum | X | `CLAIM_NUMBER` \| `ORDER_NUMBER` \| `CUSTOMER_NAME` \| `CUSTOMER_PHONE` \| `PRODUCT_NAME` |
| `searchWord` | string | X | 검색어 |
| `page` | number | O | 페이지 번호 (0부터, 기본값: 0) |
| `size` | number | O | 페이지 크기 (기본값: 20) |
| `sort` | enum | N | `requestedAt` \| `completedAt` |
| `sortDirection` | enum | N | `ASC` \| `DESC` |

**Response 200**

```json
{
  "data": {
    "content": [
      {
        "payment": { /* PaymentInfo */ },
        "orderProduct": { /* OrderProductInfo */ },
        "buyerInfo": { /* BuyerInfo */ },
        "receiverInfo": { /* ReceiverInfo */ },
        "claimInfo": {
          "claimId": "01234567-89ab-cdef-0123-456789abcdef",
          "claimNumber": "EXC-20240115-0001",
          "status": "COLLECTING",
          "reason": {
            "reasonType": "SIZE_CHANGE",
            "reasonDetail": "L사이즈로 교환 희망"
          },
          "exchangeOption": {
            "originalOption": {
              "productId": 10001,
              "optionId": 20001,
              "optionName": "블랙 / M",
              "optionValues": [
                { "name": "색상", "value": "블랙" },
                { "name": "사이즈", "value": "M" }
              ]
            },
            "targetOption": {
              "productId": 10001,
              "optionId": 20002,
              "optionName": "블랙 / L",
              "optionValues": [
                { "name": "색상", "value": "블랙" },
                { "name": "사이즈", "value": "L" }
              ]
            }
          },
          "amountAdjustment": {
            "originalPrice": 50000,
            "targetPrice": 50000,
            "priceDifference": 0,
            "additionalPaymentRequired": false,
            "partialRefundRequired": false,
            "collectShippingFee": 3000,
            "reshipShippingFee": 0,
            "totalShippingFee": 3000,
            "shippingFeePayer": "BUYER"
          },
          "createdAt": "2024-01-15T09:00:00+09:00"
        }
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
  "requestId": "req-01234567-89ab-cdef"
}
```

---

## 5. 상세/히스토리 API

### 5-1. 교환 상세 조회

**`GET /exchanges/{claimId}`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 교환 클레임 ID |

**Response 200**

```json
{
  "data": {
    "payment": { /* PaymentInfo */ },
    "orderProducts": [ /* OrderProductInfo[] */ ],
    "buyerInfo": { /* BuyerInfo */ },
    "receiverInfo": { /* ReceiverInfo */ },
    "claimInfo": {
      "claimId": "01234567-89ab-cdef-0123-456789abcdef",
      "claimNumber": "EXC-20240115-0001",
      "status": "PREPARING",
      "reason": {
        "reasonType": "SIZE_CHANGE",
        "reasonDetail": "L사이즈로 교환 희망"
      },
      "exchangeOption": {
        "originalOption": {
          "productId": 10001,
          "optionId": 20001,
          "optionName": "블랙 / M",
          "optionValues": [
            { "name": "색상", "value": "블랙" },
            { "name": "사이즈", "value": "M" }
          ]
        },
        "targetOption": {
          "productId": 10001,
          "optionId": 20002,
          "optionName": "블랙 / L",
          "optionValues": [
            { "name": "색상", "value": "블랙" },
            { "name": "사이즈", "value": "L" }
          ]
        }
      },
      "collectShipment": {
        "shipmentId": "ship-collect-uuid",
        "claimId": "01234567-89ab-cdef-0123-456789abcdef",
        "claimType": "EXCHANGE",
        "status": "DELIVERED",
        "method": {
          "type": "COURIER",
          "courierCode": "CJ",
          "courierName": "CJ대한통운"
        },
        "trackingNumber": "1234567890",
        "feeInfo": {
          "amount": 3000,
          "payer": "BUYER",
          "includeInPackage": false
        },
        "sender": {
          "name": "홍길동",
          "phone": "010-1234-5678",
          "address": {
            "zipCode": "12345",
            "address1": "서울시 강남구",
            "address2": "테헤란로 123"
          }
        },
        "receiver": {
          "name": "물류센터",
          "phone": "02-1234-5678",
          "address": {
            "zipCode": "54321",
            "address1": "경기도 이천시",
            "address2": "물류센터"
          }
        },
        "shippedAt": "2024-01-15T10:00:00+09:00",
        "receivedAt": "2024-01-17T14:30:00+09:00"
      },
      "amountAdjustment": {
        "originalPrice": 50000,
        "targetPrice": 50000,
        "priceDifference": 0,
        "additionalPaymentRequired": false,
        "partialRefundRequired": false,
        "collectShippingFee": 3000,
        "reshipShippingFee": 0,
        "totalShippingFee": 3000,
        "shippingFeePayer": "BUYER"
      },
      "linkedOrderId": "order-98765432-uuid",
      "requestedAt": "2024-01-15T09:00:00+09:00",
      "createdAt": "2024-01-15T09:00:00+09:00"
    },
    "claimHistories": [
      {
        "historyId": "hst-uuid-1",
        "type": "STATUS_CHANGE",
        "title": "상태 변경",
        "message": "REQUESTED → COLLECTING",
        "actor": {
          "actorType": "SYSTEM",
          "actorId": "system",
          "actorName": "시스템"
        },
        "createdAt": "2024-01-15T09:30:00+09:00"
      },
      {
        "historyId": "hst-uuid-2",
        "type": "STATUS_CHANGE",
        "title": "상태 변경",
        "message": "COLLECTING → COLLECTED",
        "actor": {
          "actorType": "SYSTEM",
          "actorId": "system",
          "actorName": "시스템"
        },
        "createdAt": "2024-01-17T14:30:00+09:00"
      },
      {
        "historyId": "hst-uuid-3",
        "type": "STATUS_CHANGE",
        "title": "상태 변경",
        "message": "COLLECTED → PREPARING",
        "actor": {
          "actorType": "ADMIN",
          "actorId": "admin_17",
          "actorName": "홍길동"
        },
        "createdAt": "2024-01-17T15:00:00+09:00"
      }
    ]
  },
  "timestamp": "2024-01-17T16:00:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 5-2. 수기 메모 등록

**`POST /exchanges/{claimId}/histories`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 교환 클레임 ID |

**Request Body**

```json
{
  "message": "검수 진행 중. 상품 상태 양호."
}
```

**Response 201**

```json
{
  "data": {
    "historyId": "hst-01234567-89ab-cdef"
  },
  "timestamp": "2024-01-17T15:30:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

## 6. 상태변경/배치 API

### 6-1. 단건 상태 변경

**`PATCH /exchanges/{claimId}/status`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 교환 클레임 ID |

**Request Body**

```json
{
  "targetStatus": "COLLECTING",
  "reason": {
    "reasonType": "SIZE_CHANGE",
    "reasonDetail": "L사이즈로 교환"
  },
  "memo": "CS 확인 후 수거중 전환"
}
```

**Response 204 No Content**

---

### 6-2. 배치 상태 변경 (공통)

**`POST /exchanges/status/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2", "uuid-3"],
  "targetStatus": "COLLECTED",
  "memo": "일괄 수거완료 처리"
}
```

**Response 200**

```json
{
  "data": {
    "requestedCount": 3,
    "successCount": 2,
    "failCount": 1,
    "results": [
      {
        "claimId": "uuid-1",
        "success": true,
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      },
      {
        "claimId": "uuid-2",
        "success": true,
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      },
      {
        "claimId": "uuid-3",
        "success": false,
        "errorCode": "INVALID_STATE",
        "errorMessage": "현재 상태에서 해당 상태로 변경할 수 없습니다."
      }
    ]
  },
  "timestamp": "2024-01-17T12:00:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 6-3. 수거 완료 처리 (배치)

**`POST /exchanges/collect/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "memo": "물류센터 입고 완료"
}
```

**Response 200**: BatchResult 구조 동일

---

### 6-4. 준비 완료 처리 (배치) 

검수 포함하여 교환상품 준비 완료 처리

**`POST /exchanges/prepare/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "memo": "검수 완료, 교환상품 준비됨"
}
```

**Response 200**

```json
{
  "data": {
    "requestedCount": 2,
    "successCount": 2,
    "failCount": 0,
    "results": [
      {
        "claimId": "uuid-1",
        "success": true,
        "beforeStatus": "COLLECTED",
        "afterStatus": "PREPARING"
      },
      {
        "claimId": "uuid-2",
        "success": true,
        "beforeStatus": "COLLECTED",
        "afterStatus": "PREPARING"
      }
    ]
  },
  "timestamp": "2024-01-18T10:00:00+09:00",
  "requestId": "req-xxx"
}
```

---

### 6-5. 교환 완료 처리 (배치)

**`POST /exchanges/complete/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "memo": "고객 수령 확인"
}
```

**Response 200**: BatchResult 구조 동일

---

### 6-6. 교환 거부 처리 (배치)

**`POST /exchanges/reject/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1"],
  "reason": {
    "reasonType": "OTHER",
    "reasonDetail": "재고 소진으로 교환 불가"
  },
  "memo": "CS 협의 완료"
}
```

**Response 200**: BatchResult 구조 동일

---

### 6-7. 환불로 전환 처리 (배치)

검수 불합격이거나 교환 불가 시 환불로 전환

**`POST /exchanges/convert-to-refund/batch`**

**Request Body**

```json
{
  "items": [
    {
      "claimId": "uuid-1",
      "refundReason": {
        "reasonType": "OTHER",
        "reasonDetail": "교환 옵션 재고 소진으로 환불 전환"
      }
    }
  ],
  "memo": "고객 동의 후 환불 전환"
}
```

**Response 200**

```json
{
  "data": {
    "requestedCount": 1,
    "successCount": 1,
    "failCount": 0,
    "results": [
      {
        "claimId": "uuid-1",
        "success": true,
        "refundClaimId": "refund-uuid-1"
      }
    ]
  },
  "timestamp": "2024-01-18T12:00:00+09:00",
  "requestId": "req-xxx"
}
```

---


## 8. 교환 전용 API

### 8-1. 교환 가능 옵션 조회

동일 상품군 내 교환 가능한 옵션 목록 조회

**`GET /exchanges/available-options`**

**Request (Query)**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productGroupId` | number | Y | 상품군 ID (Long) |

**Response 200**

```json
{
  "data": {
    "productGroupId": 10001,
    "productName": "프리미엄 코튼 티셔츠",
    "options": [
      {
        "optionId": 20001,
        "optionName": "블랙 / M",
        "optionValues": [
          { "name": "색상", "value": "블랙" },
          { "name": "사이즈", "value": "M" }
        ],
        "price": 50000,
        "stockQuantity": 0,
        "isAvailable": false
      },
      {
        "optionId": 20002,
        "optionName": "블랙 / L",
        "optionValues": [
          { "name": "색상", "value": "블랙" },
          { "name": "사이즈", "value": "L" }
        ],
        "price": 50000,
        "stockQuantity": 15,
        "isAvailable": true
      },
      {
        "optionId": 20003,
        "optionName": "블랙 / XL",
        "optionValues": [
          { "name": "색상", "value": "블랙" },
          { "name": "사이즈", "value": "XL" }
        ],
        "price": 52000,
        "stockQuantity": 8,
        "isAvailable": true
      },
      {
        "optionId": 20004,
        "optionName": "화이트 / M",
        "optionValues": [
          { "name": "색상", "value": "화이트" },
          { "name": "사이즈", "value": "M" }
        ],
        "price": 50000,
        "stockQuantity": 0,
        "isAvailable": false
      }
    ]
  },
  "timestamp": "2024-01-15T10:00:00+09:00",
  "requestId": "req-xxx"
}
```

---

## 부록: 교환 vs 환불 비교

| 구분 | 환불 (Refund) | 교환 (Exchange) |
|------|---------------|-----------------|
| **목적** | 상품 반환 후 금액 환불 | 상품 반환 후 다른 옵션 상품 수령 |
| **배송 구조** | ClaimShipment (수거) | ClaimShipment (수거) + 새 Order의 Shipment (발송) |
| **검수** | PREPARING 단계에서 암묵적 진행 | PREPARING 단계에서 암묵적 진행 |
| **금액 처리** | 환불 | 차액 정산 (추가결제 또는 부분환불) |
| **완료 조건** | 환불 완료 | 새 주문의 배송 완료 |

---

## 부록: 상태 전이 매트릭스

| 현재 상태 | 가능한 전이 | 트리거 |
|----------|------------|--------|
| `REQUESTED` | `COLLECTING`, `CANCELLED`, `REJECTED` | 수거 시작 / 고객취소 / 거부 |
| `COLLECTING` | `COLLECTED`, `CANCELLED` | 수거 완료 / 고객취소 |
| `COLLECTED` | `PREPARING`, `REJECTED` | 검수 시작 / 거부 |
| `PREPARING` | `SHIPPING`, `REJECTED` | 새 주문 배송 시작 (이벤트) / 거부 |
| `SHIPPING` | `COMPLETED` | 새 주문 배송 완료 (이벤트) |
| `COMPLETED` | (최종 상태) | - |
| `REJECTED` | (최종 상태) | - |
| `CANCELLED` | (최종 상태) | - |

### 상태별 설명

| 상태 | 설명 |
|------|------|
| `REQUESTED` | 교환 요청됨 (고객이 교환 신청) |
| `COLLECTING` | 수거 중 (원상품 반송 진행중) |
| `COLLECTED` | 수거 완료 (원상품 물류센터 도착) |
| `PREPARING` | 교환상품 준비 중 (검수 + 새 주문 생성) |
| `SHIPPING` | 재배송 중 (새 주문의 배송 시작됨, 이벤트로 전이) |
| `COMPLETED` | 교환 완료 (새 주문의 배송 완료, 이벤트로 전이) |
| `REJECTED` | 교환 거부 (교환 불가 판정) |
| `CANCELLED` | 취소됨 (고객 또는 시스템에 의한 취소) |

### 이벤트 연동

```
[새 주문 생성 시점]
- PREPARING 단계에서 검수 완료 후 새 주문(Order) 생성
- ExchangeClaim.linkedOrderId에 새 주문 ID 저장

[새 주문 → ExchangeClaim 이벤트]
- Order.SHIPPING → ExchangeClaim.SHIPPING (이벤트 구독)
- Order.DELIVERED → ExchangeClaim.COMPLETED (이벤트 구독)
```
