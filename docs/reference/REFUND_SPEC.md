# 환불 관리 API 스펙 (Backend)

> 본 문서는 프론트엔드 명세 `RETURN_SPEC.md`를 기반으로 백엔드 API 스펙을 재정의합니다.
> 각 엔드포인트별로 기존 명세와의 매핑, 변경 사항, 변경 사유, 그리고 새로운 스펙을 기술합니다.

---

## 📋 목차

1. [공통 타입 정의](#1-공통-타입-정의)
2. [공통 변경 사항](#2-공통-변경-사항)
3. [API 매핑 테이블](#3-api-매핑-테이블)
4. [조회/필터/KPI API](#4-조회필터kpi-api)
5. [상세/히스토리 API](#5-상세히스토리-api)
6. [상태변경/배치 API](#6-상태변경배치-api)
7. [단건 수정 API](#7-단건-수정-api)

---

## 1. 공통 타입 정의

### 1-1. RefundStatus (enum)

```typescript
// 기존 ClaimStatus → RefundStatus 변경
// RETURN_ prefix 제거

enum RefundStatus {
  REQUESTED    // 환불 요청됨 (기존: RETURN_REQUESTED)
  COLLECTING   // 수거 중 (기존: RETURN_COLLECTING)
  COLLECTED    // 수거 완료 (기존: COLLECT_COMPLETED)
  COMPLETED    // 환불 완료 (기존: RETURN_COMPLETED)
  REJECTED     // 환불 거부 (신규)
  CANCELLED    // 취소됨 (신규)
}
```

### 1-2. RefundReasonType (enum)

```typescript
// 기존 단순 문자열 → 구조화된 enum

enum RefundReasonType {
  CHANGE_OF_MIND      // 단순 변심 (기존: CHANGE_MIND)
  WRONG_PRODUCT       // 상품 오배송
  DEFECTIVE           // 상품 불량/파손
  DIFFERENT_FROM_DESC // 상품 설명과 다름
  DELAYED_DELIVERY    // 배송 지연
  OTHER               // 기타 (기존: ETC)
}
```

### 1-3. ShipmentMethodType (enum)

```typescript
enum ShipmentMethodType {
  COURIER            // 택배 (기존: AUTO 포함)
  QUICK              // 퀵서비스
  VISIT              // 직접 방문 전달
  AUTO_PICKUP        // 자동 수거 (신규)
  DESIGNATED_COURIER // 지정 택배
}
```

#### 배송 방식별 상세 설명

| 타입 | 설명 | 주요 특징 |
|------|------|-----------|
| `COURIER` | 일반 택배 | 고객이 직접 택배사에 접수하여 발송. 송장번호로 추적 가능 |
| `QUICK` | 퀵서비스 | 당일 수거/배송. 퀵 기사가 고객 위치에서 픽업 |
| `VISIT` | 직접 방문 전달 | **구매자가 판매자/물류센터에 직접 방문**하여 물건을 전달. 택배 없이 대면 인수 |
| `AUTO_PICKUP` | 자동 수거 | **지정 택배사가 자동으로 고객 위치에 방문**하여 수거. 고객이 별도 접수 불필요 |
| `DESIGNATED_COURIER` | 지정 택배 | 판매자가 지정한 택배사로 고객이 직접 접수하여 발송. 특정 계약 택배사만 이용 가능 |

#### 배송 방식별 필수/선택 필드

| 필드 | COURIER | QUICK | VISIT | AUTO_PICKUP | DESIGNATED_COURIER |
|------|---------|-------|-------|-------------|---------------------|
| `courierCode` | ✅ 필수 | ⚪ 선택 | ❌ null | ✅ 필수 | ✅ 필수 |
| `courierName` | ✅ 필수 | ⚪ 선택 | ❌ null | ✅ 필수 | ✅ 필수 |
| `trackingNumber` | ✅ 필수 | ⚪ 선택 | ❌ null | ✅ 필수 | ✅ 필수 |

#### 배송 방식별 sender/receiver 의미

| 타입 | sender (발송인) | receiver (수령인) |
|------|-----------------|-------------------|
| `COURIER` | 고객 (반품 물건 발송자) | 판매자 또는 물류센터 |
| `QUICK` | 고객 (픽업 대상) | 판매자 또는 물류센터 |
| `VISIT` | 고객 (물건을 가져가는 방문자) | 판매자 또는 물류센터 (방문 목적지) |
| `AUTO_PICKUP` | 고객 (수거 대상 위치) | 판매자 또는 물류센터 |
| `DESIGNATED_COURIER` | 고객 (반품 물건 발송자) | 판매자 또는 물류센터 |

#### 배송 방식별 시각(timestamp) 의미

| 타입 | shippedAt | receivedAt |
|------|-----------|------------|
| `COURIER` | 고객이 택배 발송 완료한 시점 (택배 접수) | 판매자/물류센터 입고 완료 시점 |
| `QUICK` | 퀵 기사가 고객 위치에서 픽업한 시점 | 판매자/물류센터 도착 시점 |
| `VISIT` | 고객이 판매자/물류센터에 물건을 전달한 시점 | 판매자/물류센터 입고 확인 시점 |
| `AUTO_PICKUP` | 택배 기사가 고객 위치에서 자동 수거한 시점 | 판매자/물류센터 입고 완료 시점 |
| `DESIGNATED_COURIER` | 고객이 지정 택배사에 발송 완료한 시점 | 판매자/물류센터 입고 완료 시점 |

### 1-4. FeePayer (enum)

```typescript
enum FeePayer {
  SELLER  // 판매자 귀책
  BUYER   // 구매자 귀책
}
```

### 1-5. ClaimReason (interface)

```typescript
interface ClaimReason {
  reasonType: RefundReasonType;
  reasonDetail: string;
}
```

### 1-6. ShipmentMethod (interface)

```typescript
interface ShipmentMethod {
  type: ShipmentMethodType;    // 배송 방식 (1-3 참조)
  courierCode: string | null;  // 택배사 코드 (VISIT인 경우 null)
  courierName: string | null;  // 택배사 이름 (VISIT인 경우 null)
}
```

#### ShipmentMethod 필드 상세

| 필드 | 타입 | 설명 |
|------|------|------|
| `type` | ShipmentMethodType | 배송 방식. 상세 설명은 1-3 참조 |
| `courierCode` | string \| null | 택배사 코드. VISIT인 경우 null |
| `courierName` | string \| null | 택배사 이름. VISIT인 경우 null |

### 1-7. ShippingFeeInfo (interface)

```typescript
interface ShippingFeeInfo {
  amount: number;           // 배송비 금액
  payer: FeePayer;          // 부담 주체
  includeInPackage: boolean; // 동봉 여부
}
```

### 1-8. RefundInfo (interface)

```typescript
interface RefundInfo {
  originalAmount: number;      // 원래 환불 금액
  finalAmount: number;         // 최종 환불 금액
  deductionAmount: number;     // 차감액
  deductionReason: string | null; // 차감 사유
  refundMethod: string;        // 환불 방식
  refundedAt: string | null;   // 환불 완료 시각
}
```

### 1-9. ClaimShipment (interface)

```typescript
interface ClaimShipment {
  shipmentId: string;                    // 배송 식별자
  status: string;                        // 배송 상태 (PENDING, IN_TRANSIT, DELIVERED 등)
  method: ShipmentMethod;                // 배송 방식 (1-6 참조)
  trackingNumber: string | null;         // 송장번호 (VISIT인 경우 null)
  feeInfo: ShippingFeeInfo;              // 배송비 정보 (1-7 참조)
  sender: {                              // 발송인 정보 (배송 방식별 의미 다름, 1-3 참조)
    name: string;                        // 발송인 이름
    phone: string;                       // 발송인 연락처
    address: Address;                    // 발송인 주소 (수거 위치)
  };
  receiver: {                            // 수령인 정보 (배송 방식별 의미 다름, 1-3 참조)
    name: string;                        // 수령인 이름
    phone: string;                       // 수령인 연락처
    address: Address;                    // 수령인 주소 (배송 목적지)
  };
  shippedAt: string | null;              // 발송/픽업 시각 (배송 방식별 의미 다름, 1-3 참조)
  receivedAt: string | null;             // 수령/입고 시각 (배송 방식별 의미 다름, 1-3 참조)
}
```

#### ClaimShipment 필드 상세

| 필드 | 타입 | 설명 |
|------|------|------|
| `shipmentId` | string | 배송 고유 식별자 (UUID v7) |
| `status` | string | 배송 상태: `PENDING` (대기), `IN_TRANSIT` (배송중), `DELIVERED` (배송완료), `FAILED` (실패) |
| `method` | ShipmentMethod | 배송 방식 정보. `type`, `courierCode`, `courierName` 포함 |
| `trackingNumber` | string \| null | 송장번호. VISIT 방식은 null |
| `feeInfo` | ShippingFeeInfo | 배송비 정보. 금액, 부담주체, 동봉여부 |
| `sender` | object | 발송인 정보. **배송 방식과 무관하게 항상 '고객'** |
| `receiver` | object | 수령인 정보. **VISIT: 수거담당자, 그 외: 판매자/물류센터** |
| `shippedAt` | string \| null | 발송/픽업 완료 시각. ISO 8601 형식 |
| `receivedAt` | string \| null | 수령/입고 완료 시각. ISO 8601 형식. 미완료시 null |

#### 배송 방식별 예시 데이터

**COURIER (일반 택배)**

```json
{
  "shipmentId": "shp-01234567-89ab-cdef",
  "status": "DELIVERED",
  "method": {
    "type": "COURIER",
    "courierCode": "CJ",
    "courierName": "CJ대한통운"
  },
  "trackingNumber": "123456789012",
  "sender": {
    "name": "김고객",
    "phone": "010-1234-5678",
    "address": { "zipCode": "12345", "address1": "서울시 강남구", "address2": "101호" }
  },
  "receiver": {
    "name": "물류센터",
    "phone": "02-1234-5678",
    "address": { "zipCode": "67890", "address1": "경기도 이천시", "address2": "물류센터" }
  },
  "shippedAt": "2024-01-15T10:00:00+09:00",
  "receivedAt": "2024-01-17T14:30:00+09:00"
}
```

**VISIT (직접 방문 전달)**

```json
{
  "shipmentId": "shp-11234567-89ab-cdef",
  "status": "DELIVERED",
  "method": {
    "type": "VISIT",
    "courierCode": null,
    "courierName": null
  },
  "trackingNumber": null,
  "sender": {
    "name": "김고객",
    "phone": "010-1234-5678",
    "address": { "zipCode": "12345", "address1": "서울시 강남구", "address2": "101호" }
  },
  "receiver": {
    "name": "물류센터 접수처",
    "phone": "02-1234-5678",
    "address": { "zipCode": "67890", "address1": "경기도 이천시", "address2": "물류센터 1층" }
  },
  "shippedAt": "2024-01-15T14:00:00+09:00",
  "receivedAt": "2024-01-15T14:05:00+09:00"
}
```

**QUICK (퀵서비스)**

```json
{
  "shipmentId": "shp-21234567-89ab-cdef",
  "status": "IN_TRANSIT",
  "method": {
    "type": "QUICK",
    "courierCode": "VROONG",
    "courierName": "부릉"
  },
  "trackingNumber": "QK20240115001",
  "sender": {
    "name": "김고객",
    "phone": "010-1234-5678",
    "address": { "zipCode": "12345", "address1": "서울시 강남구", "address2": "101호" }
  },
  "receiver": {
    "name": "판매자 창고",
    "phone": "02-1234-5678",
    "address": { "zipCode": "04567", "address1": "서울시 성동구", "address2": "창고동" }
  },
  "shippedAt": "2024-01-15T11:30:00+09:00",
  "receivedAt": null
}
```

**AUTO_PICKUP (자동 수거)**

```json
{
  "shipmentId": "shp-31234567-89ab-cdef",
  "status": "IN_TRANSIT",
  "method": {
    "type": "AUTO_PICKUP",
    "courierCode": "CJ",
    "courierName": "CJ대한통운"
  },
  "trackingNumber": "AP20240115001",
  "sender": {
    "name": "김고객",
    "phone": "010-1234-5678",
    "address": { "zipCode": "12345", "address1": "서울시 강남구", "address2": "101호" }
  },
  "receiver": {
    "name": "물류센터",
    "phone": "02-1234-5678",
    "address": { "zipCode": "67890", "address1": "경기도 이천시", "address2": "물류센터" }
  },
  "shippedAt": "2024-01-15T09:00:00+09:00",
  "receivedAt": null
}
```

### 1-10. ApiResponse (interface)

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

### 2-1. API 경로 변경

| 변경 전 | 변경 후 |
|---------|---------|
| `/order/returns/*` | `/refunds/*` |
| `/order/return/{claimId}/*` | `/refunds/{claimId}/*` |

**사유**: 도메인 분리 - 반품(Return)과 환불(Refund)을 명확히 구분하고, 교환(Exchange)은 별도 도메인으로 분리

### 2-2. 응답 래핑 구조

모든 응답은 아래 형식으로 래핑됩니다:

```json
{
  "data": { /* 실제 응답 데이터 */ },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-uuid-v7"
}
```

**사유**: 일관된 응답 구조, 디버깅/추적 용이성, 타임스탬프 표준화

### 2-3. claimId 타입 변경

| 변경 전 | 변경 후 |
|---------|---------|
| `number` (Long) | `string` (UUIDv7) |

```diff
- "claimId": 123
+ "claimId": "01234567-89ab-cdef-0123-456789abcdef"
```

**사유**: 분산 시스템 호환성, 시간순 정렬 가능, 예측 불가능한 ID

### 2-4. 시간 필드명 표준화

| 변경 전 | 변경 후 |
|---------|---------|
| `createdDate` | `createdAt` |
| `deliveryCompleteDate` | `receivedAt` |

**사유**: ISO 표준 네이밍 컨벤션 준수 (`*At` suffix)

### 2-5. DELETE 메서드 금지

| 변경 전 | 변경 후 |
|---------|---------|
| `DELETE /order/returns/refund-hold/batch` | `PATCH /refunds/hold/batch` (isHold: false) |

**사유**: RESTful 설계 원칙 - 리소스 삭제가 아닌 상태 변경

---

## 3. API 매핑 테이블

| 기존 API (RETURN_SPEC.md) | 새 API | 비고 |
|---------------------------|--------|------|
| `GET /order/returns/summary` | `GET /refunds/summary` | 응답 구조 변경 |
| `GET /order/returns` | `GET /refunds` | 페이지네이션 변경 |
| `GET /order/return/{claimId}` | `GET /refunds/{claimId}` | 구조 대폭 변경 |
| `POST /order/return/{claimId}/histories` | `POST /refunds/{claimId}/histories` | 응답 래핑 |
| `PATCH /order/return/{claimId}/status` | `PATCH /refunds/{claimId}/status` | 상태값 변경 |
| `POST /order/returns/status/batch` | `POST /refunds/status/batch` | 배치 응답 변경 |
| `POST /order/returns/collect-complete/batch` | `POST /refunds/collect/batch` | 경로 단순화 |
| `POST /order/returns/return-complete/batch` | `POST /refunds/complete/batch` | 경로 단순화 |
| `POST /order/returns/reject-or-withdraw/batch` | `POST /refunds/reject/batch` | 거부만 분리 |
| `POST /order/returns/convert-to-exchange/batch` | `POST /refunds/convert-to-exchange/batch` | 유지 |
| `POST /order/returns/refund-hold/batch` | `PATCH /refunds/hold/batch` | PATCH로 통합 |
| `DELETE /order/returns/refund-hold/batch` | `PATCH /refunds/hold/batch` | PATCH로 통합 |
| `PATCH /order/return/{claimId}/reason` | `PATCH /refunds/{claimId}/reason` | 구조 변경 |
| `PATCH /order/return/{claimId}/delivery` | `PATCH /refunds/{claimId}/shipment` | 이름 변경 |

---

## 4. 조회/필터/KPI API

### 4-1. KPI 요약 조회

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 3-1. KPI 요약 조회` |
| **기존 API** | `GET /order/returns/summary` |
| **새 API** | `GET /refunds/summary` |

#### ✅ 스펙

**`GET /refunds/summary`**

**Response 200**

```json
{
  "data": {
    "requested": 8,
    "collecting": 3,
    "collected": 2,
    "completed": 1,
    "rejected": 0,
    "cancelled": 0
  },
  "timestamp": "2024-01-15T10:30:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 4-2. 환불 목록 조회

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 3-2. 반품 목록 조회` |
| **기존 API** | `GET /order/returns` |
| **새 API** | `GET /refunds` |

#### ✅ 스펙

**`GET /refunds`**

**Request (Query)**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `dateType` | enum | X | `REQUESTED` \| `COMPLETED` (요청일, 환불완료일) |
| `startDate` | string | Y | 시작일 (YYYY-MM-DD) |
| `endDate` | string | Y | 종료일 (YYYY-MM-DD) |
| `status` | enum[] | X | `REQUESTED` \| `COLLECTING` \| `COLLECTED` \| `COMPLETED` \| `REJECTED` \| `CANCELLED` |
| `shopIds` | number[] | X | 샵 ID 목록 (Long) |
| `partnerIds` | number[] | X | 파트너 ID 목록 |
| `isHold` | boolean | X | 환불보류 여부 |
| `searchField` | enum | X | `CLAIM_NUMBER` \| `ORDER_NUMBER` \| `CUSTOMER_NAME` \| `CUSTOMER_PHONE` \| `PRODUCT_NAME` |
| `searchWord` | string | X | 검색어 |
| `page` | number | O | 페이지 번호 (0부터, 기본값: 0) |
| `size` | number | O | 페이지 크기 (기본값: 20) |
| `sort` | enum | N | `requestedAt` \| `completedAt` \| `totalAmount` |
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
        "paymentShipmentInfo": { /* PaymentShipmentInfo */ },
        "settlementInfo": { /* SettlementInfo */ },
        "claimInfo": {
          "claimId": "01234567-89ab-cdef-0123-456789abcdef",
          "claimNumber": "RFD-20240115-0001",
          "status": "REQUESTED",
          "isHold": false,
          "reason": {
            "reasonType": "CHANGE_OF_MIND",
            "reasonDetail": "사이즈가 큼"
          },
          "refundInfo": {
            "originalAmount": 50000,
            "finalAmount": 47000,
            "deductionAmount": 3000,
            "deductionReason": "반품 배송비"
          },
          "collectShipment": {
            "shipmentId": "ship-uuid",
            "status": "SHIPPED",
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
            "shippedAt": "2024-01-15T10:00:00+09:00",
            "receivedAt": null
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

### 5-1. 환불 상세 조회

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 4-1. 반품 상세 조회` |
| **기존 API** | `GET /order/return/{claimId}` |
| **새 API** | `GET /refunds/{claimId}` |

#### ✅ 스펙

**`GET /refunds/{claimId}`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 환불 클레임 ID |

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
      "claimNumber": "RFD-20240115-0001",
      "status": "COLLECTING",
      "isHold": false,
      "holdReason": null,
      "reason": {
        "reasonType": "CHANGE_OF_MIND",
        "reasonDetail": "사이즈가 큼"
      },
      "refundInfo": {
        "originalAmount": 50000,
        "finalAmount": 47000,
        "deductionAmount": 3000,
        "deductionReason": "반품 배송비",
        "refundMethod": "ORIGINAL_PAYMENT",
        "refundedAt": null
      },
      "collectShipment": {
        "shipmentId": "ship-01234567-89ab-cdef",
        "status": "SHIPPED",
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
          "name": "판매자",
          "phone": "02-1234-5678",
          "address": {
            "zipCode": "54321",
            "address1": "경기도 성남시",
            "address2": "분당구 판교로 456"
          }
        },
        "shippedAt": "2024-01-15T10:00:00+09:00",
        "receivedAt": null
      },
      "requestedAt": "2024-01-15T09:00:00+09:00",
      "createdAt": "2024-01-15T09:00:00+09:00"
    },
    "claimHistories": [
      {
        "historyId": "hst-uuid",
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
        "type": "MANUAL",
        "title": "CS 메모",
        "message": "고객과 통화 완료. 내일 수거 예정.",
        "actor": {
          "actorType": "ADMIN",
          "actorId": "admin_17",
          "actorName": "홍길동"
        },
        "createdAt": "2024-01-15T11:00:00+09:00"
      }
    ]
  },
  "timestamp": "2024-01-15T12:00:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 5-2. 수기 메모 등록

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 4-2. 수기 메모 등록` |
| **기존 API** | `POST /order/return/{claimId}/histories` |
| **새 API** | `POST /refunds/{claimId}/histories` |

#### ✅ 스펙

**`POST /refunds/{claimId}/histories`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 환불 클레임 ID |

**Request Body**

```json
{
  "message": "고객과 통화 완료. 내일 수거 예정."
}
```

**Response 201**

```json
{
  "data": {
    "historyId": "hst-01234567-89ab-cdef"
  },
  "timestamp": "2024-01-15T11:00:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

## 6. 상태변경/배치 API

### 6-1. 단건 상태 변경

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-1. 단건 상태 변경` |
| **기존 API** | `PATCH /order/return/{claimId}/status` |
| **새 API** | `PATCH /refunds/{claimId}/status` |

#### ✅ 스펙

**`PATCH /refunds/{claimId}/status`**

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| `claimId` | string (UUID) | 환불 클레임 ID |

**Request Body**

```json
{
  "targetStatus": "COLLECTING",
  "reason": {
    "reasonType": "CHANGE_OF_MIND",
    "reasonDetail": "사이즈가 커요"
  },
}
```

**Response 204 No Content**

---

### 6-2. 배치 상태 변경 (공통)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-2. 배치 상태 변경(공통)` |
| **기존 API** | `POST /order/returns/status/batch` |
| **새 API** | `POST /refunds/status/batch` |

#### ✅ 스펙

**`POST /refunds/status/batch`**

**Request Body**

```json
{
  "claimIds": [
    "01234567-89ab-cdef-0123-456789abcdef",
    "11234567-89ab-cdef-0123-456789abcdef",
    "21234567-89ab-cdef-0123-456789abcdef"
  ],
  "targetStatus": "COLLECTED",
  "reason": {
    "reasonType": "CHANGE_OF_MIND",
    "reasonDetail": "단순 변심"
  },
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
        "claimId": "01234567-89ab-cdef-0123-456789abcdef",
        "success": true,
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      },
      {
        "claimId": "11234567-89ab-cdef-0123-456789abcdef",
        "success": true,
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      },
      {
        "claimId": "21234567-89ab-cdef-0123-456789abcdef",
        "success": false,
        "errorCode": "INVALID_STATE",
        "errorMessage": "현재 상태에서 해당 상태로 변경할 수 없습니다."
      }
    ]
  },
  "timestamp": "2024-01-15T12:00:00+09:00",
  "requestId": "req-01234567-89ab-cdef"
}
```

---

### 6-3. 수거 완료 처리 (배치)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-3. 수거 완료처리(배치)` |
| **기존 API** | `POST /order/returns/collect-complete/batch` |
| **새 API** | `POST /refunds/collect/batch` |

#### ✅ 스펙

**`POST /refunds/collect/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  // "reason": {
  //   "reasonType": "CHANGE_OF_MIND",
  //   "reasonDetail": "단순 변심"
  // },
  // "memo": "물류 확인 완료"
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
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      },
      {
        "claimId": "uuid-2",
        "success": true,
        "beforeStatus": "COLLECTING",
        "afterStatus": "COLLECTED"
      }
    ]
  },
  "timestamp": "2024-01-15T12:00:00+09:00",
  "requestId": "req-xxx"
}
```

---

### 6-4. 환불 완료 처리 (배치)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-4. 반품완료(=환불완료) 처리(배치)` |
| **기존 API** | `POST /order/returns/return-complete/batch` |
| **새 API** | `POST /refunds/complete/batch` |

#### ✅ 스펙

**`POST /refunds/complete/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  // "memo": "검수/입고 완료"
}
```

**Response 200**: RefundBatchResult 구조 동일

---

### 6-5. 환불 거부 처리 (배치)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-5. 반품 거부/철회 처리(배치)` |
| **기존 API** | `POST /order/returns/reject-or-withdraw/batch` |
| **새 API** | `POST /refunds/reject/batch` |

#### ✅ 스펙

**`POST /refunds/reject/batch`**

**Request Body**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "reason": {
    "reasonType": "OTHER",
    "reasonDetail": "상품 훼손으로 반품 불가"
  },
  // "memo": "CS 확인"
}
```

**Response 200**: RefundBatchResult 구조 동일

---

### 6-6. 교환으로 변경 (배치)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-6. 교환으로 변경(배치)` |
| **기존 API** | `POST /order/returns/convert-to-exchange/batch` |
| **새 API** | `POST /refunds/convert-to-exchange/batch` |

#### ✅ 스펙

**`POST /refunds/convert-to-exchange/batch`**

**Request Body**

```json
{
  "items": [
    { "orderId": "20260115001", "claimId": "uuid-1" },
    { "orderId": "20260115002", "claimId": "uuid-2" }
  ],
}
```

**Response 200**

```json
{
  "data": {
    "requestedCount": 2,
    "successCount": 1,
    "failCount": 1,
    "results": [
      {
        "claimId": "uuid-1",
        "success": true,
        "exchangeClaimId": "exchange-uuid-1"
      },
      {
        "claimId": "uuid-2",
        "success": false,
        "errorCode": "INVALID_STATE",
        "errorMessage": "수거 완료 후에만 교환 전환이 가능합니다."
      }
    ]
  },
  "timestamp": "2024-01-15T12:00:00+09:00",
  "requestId": "req-xxx"
}
```

---

### 6-7 & 6-8. 환불 보류 설정/해제 (통합)

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 5-7. 환불보류 설정(배치)`, `## 5-8. 환불보류 해제(배치)` |
| **기존 API** | `POST /order/returns/refund-hold/batch` (설정) <br> `DELETE /order/returns/refund-hold/batch` (해제) |
| **새 API** | `PATCH /refunds/hold/batch` (설정/해제 통합) |

#### ✅ 스펙

**`PATCH /refunds/hold/batch`**

**Request Body (설정)**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "isHold": true,
  "memo": "검수 필요로 환불 보류"
}
```

**Request Body (해제)**

```json
{
  "claimIds": ["uuid-1", "uuid-2"],
  "isHold": false,
  "memo": "검수 완료"
}
```

**Response 200**: RefundBatchResult 구조 동일

---

## 7. 단건 수정 API

### 7-1. 환불 사유 수정

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 6-1. 반품 사유 수정(단건)` |
| **기존 API** | `PATCH /order/return/{claimId}/reason` |
| **새 API** | `PATCH /refunds/{claimId}/reason` |

#### ✅ 스펙

**`PATCH /refunds/{claimId}/reason`**

**Request Body**

```json
{
  "reason": {
    "reasonType": "DIFFERENT_FROM_DESC",
    "reasonDetail": "색상이 화면과 달라요"
  },
  "memo": "고객 응대 후 사유 정정"
}
```

**Response 204 No Content**

---

### 7-2. 수거 정보 수정

#### 📌 매핑 정보

| 항목 | 내용 |
|------|------|
| **기존 명세** | `RETURN_SPEC.md` - `## 6-2. 수거정보 수정(단건)` |
| **기존 API** | `PATCH /order/return/{claimId}/delivery` |
| **새 API** | `PATCH /refunds/{claimId}/shipment` |

#### ✅ 스펙

**`PATCH /refunds/{claimId}/shipment`**

**Request Body**

```json
{
  "method": {
    "type": "COURIER",
    "courierCode": "CJ",
    "courierName": "CJ대한통운"
  },
  "trackingNumber": "1234567890",
  "receivedAt": "2024-01-16T15:30:00+09:00",
  "memo": "송장번호 고객 재확인"
}
```

**Response 204 No Content**

---
