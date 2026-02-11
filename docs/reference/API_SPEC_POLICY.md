# 배송정책 & 환불정책 API 스펙

> **Version**: 1.0.0
> **Last Updated**: 2025-01-26
> **Base URL**: `/api/v2`

---

## 공통 응답 형식

### 성공 응답 (데이터 있음)
```json
{
  "data": { ... },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 성공 응답 (데이터 없음)
```
HTTP/1.1 204 No Content
```

### 페이징 응답
```json
{
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 에러 응답 (RFC 7807)
```json
{
  "type": "https://api.setof.com/errors/SHP-001",
  "title": "Shipping Policy Not Found",
  "status": 404,
  "detail": "배송 정책을 찾을 수 없습니다",
  "code": "SHP-001",
  "timestamp": "2025-01-26T10:30:00.000Z",
  "instance": "/api/v2/sellers/1/shipping-policies/999"
}
```

---

# 배송정책 (Shipping Policy) API

## 1. 배송정책 등록

### Request
```
POST /api/v2/sellers/{sellerId}/shipping-policies
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Request Body
```json
{
  "policyName": "기본 배송정책",
  "defaultPolicy": true,
  "shippingFeeType": "CONDITIONAL_FREE",
  "baseFee": 3000,
  "freeThreshold": 50000,
  "jejuExtraFee": 3000,
  "islandExtraFee": 5000,
  "returnFee": 3000,
  "exchangeFee": 6000,
  "leadTime": {
    "minDays": 1,
    "maxDays": 3,
    "cutoffTime": "14:00"
  }
}
```

> **Note**: 출고지/반품지 주소는 셀러 레벨에서 기본 주소로 관리됩니다.

#### Request Body Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| policyName | String | Y | 정책명 (1~100자) |
| defaultPolicy | Boolean | Y | 기본 정책 여부 |
| shippingFeeType | String | Y | 배송비 유형 (아래 Enum 참조) |
| baseFee | Long | N | 기본 배송비 (원) |
| freeThreshold | Long | N | 무료배송 기준금액 (원) - CONDITIONAL_FREE 시 필수 |
| jejuExtraFee | Long | N | 제주 추가 배송비 (원) |
| islandExtraFee | Long | N | 도서산간 추가 배송비 (원) |
| returnFee | Long | N | 반품 배송비 (원) |
| exchangeFee | Long | N | 교환 배송비 (원) |
| leadTime | Object | N | 발송 소요일 정보 |
| leadTime.minDays | Integer | N | 최소 발송일 |
| leadTime.maxDays | Integer | N | 최대 발송일 |
| leadTime.cutoffTime | String | N | 당일발송 마감시간 (HH:mm) |

#### ShippingFeeType Enum
| Value | Description |
|-------|-------------|
| FREE | 무료배송 |
| PAID | 유료배송 |
| CONDITIONAL_FREE | 조건부 무료배송 |
| QUANTITY_BASED | 수량별 배송비 |

### Response
```
HTTP/1.1 201 Created
```
```json
{
  "data": {
    "policyId": 1
  },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 2. 배송정책 수정

### Request
```
PUT /api/v2/sellers/{sellerId}/shipping-policies/{policyId}
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |
| policyId | Long | Y | 배송정책 ID |

#### Request Body
등록과 동일

### Response
```
HTTP/1.1 204 No Content
```

---

## 3. 배송정책 목록 조회

### Request
```
GET /api/v2/sellers/{sellerId}/shipping-policies?page=0&size=20&sort=createdAt,desc
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | Integer | N | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | N | 20 | 페이지 크기 |
| sort | String | N | createdAt,desc | 정렬 기준 |

### Response
```
HTTP/1.1 200 OK
```
```json
{
  "data": {
    "content": [
      {
        "policyId": 1,
        "policyName": "기본 배송정책",
        "defaultPolicy": true,
        "active": true,
        "shippingFeeType": "CONDITIONAL_FREE",
        "shippingFeeTypeDisplayName": "조건부 무료배송",
        "baseFee": 3000,
        "freeThreshold": 50000,
        "createdAt": "2025-01-26T10:30:00+09:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Response Content Fields
| Field | Type | Description |
|-------|------|-------------|
| policyId | Long | 정책 ID |
| policyName | String | 정책명 |
| defaultPolicy | Boolean | 기본 정책 여부 |
| active | Boolean | 활성화 상태 |
| shippingFeeType | String | 배송비 유형 코드 |
| shippingFeeTypeDisplayName | String | 배송비 유형 표시명 |
| baseFee | Long | 기본 배송비 |
| freeThreshold | Long | 무료배송 기준금액 |
| createdAt | String | 생성일시 (ISO 8601) |

---

## 4. 배송정책 다건 상태 변경

### Request
```
PATCH /api/v2/sellers/{sellerId}/shipping-policies/status
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Request Body
```json
{
  "policyIds": [1, 2, 3],
  "active": false
}
```

#### Request Body Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| policyIds | Long[] | Y | 상태 변경할 정책 ID 목록 |
| active | Boolean | Y | 변경할 활성화 상태 |

### Response
```
HTTP/1.1 204 No Content
```

---

# 환불정책 (Refund Policy) API

## 1. 환불정책 등록

### Request
```
POST /api/v2/sellers/{sellerId}/refund-policies
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Request Body
```json
{
  "policyName": "기본 환불정책",
  "defaultPolicy": true,
  "returnPeriodDays": 7,
  "exchangePeriodDays": 7,
  "nonReturnableConditions": [
    "OPENED_PACKAGING",
    "USED_PRODUCT",
    "MISSING_TAG"
  ],
  "partialRefundEnabled": true,
  "inspectionRequired": true,
  "inspectionPeriodDays": 3,
  "additionalInfo": "교환/반품 시 상품 택이 제거되지 않은 상태여야 합니다."
}
```

#### Request Body Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| policyName | String | Y | 정책명 (1~100자) |
| defaultPolicy | Boolean | Y | 기본 정책 여부 |
| returnPeriodDays | Integer | Y | 반품 가능 기간 (1~90일) |
| exchangePeriodDays | Integer | Y | 교환 가능 기간 (1~90일) |
| nonReturnableConditions | String[] | N | 반품 불가 조건 목록 |
| partialRefundEnabled | Boolean | N | 부분 환불 허용 여부 |
| inspectionRequired | Boolean | N | 검수 필요 여부 |
| inspectionPeriodDays | Integer | N | 검수 소요 기간 (일) |
| additionalInfo | String | N | 추가 안내 문구 |

#### NonReturnableCondition Enum
| Value | DisplayName | Description |
|-------|-------------|-------------|
| OPENED_PACKAGING | 포장 개봉 | 고객 변심으로 인한 포장 개봉 시 |
| USED_PRODUCT | 사용 흔적 | 사용 흔적이 있는 경우 |
| TIME_EXPIRED | 시간 경과 | 시간 경과로 재판매가 불가한 경우 |
| DIGITAL_CONTENT | 디지털 콘텐츠 | 복제 가능한 디지털 콘텐츠 |
| CUSTOM_MADE | 주문 제작 | 고객 요청에 따른 주문 제작 상품 |
| HYGIENE_PRODUCT | 위생 상품 | 식품/화장품 등 위생상 문제가 있는 상품 |
| PARTIAL_SET | 세트 일부 | 세트 상품의 일부만 반품하는 경우 |
| MISSING_TAG | 택/라벨 제거 | 상품 택이나 라벨이 제거된 경우 |
| DAMAGED_BY_CUSTOMER | 고객 과실 파손 | 고객 과실로 인한 상품 파손 |

### Response
```
HTTP/1.1 201 Created
```
```json
{
  "data": {
    "policyId": 1
  },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 2. 환불정책 수정

### Request
```
PUT /api/v2/sellers/{sellerId}/refund-policies/{policyId}
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |
| policyId | Long | Y | 환불정책 ID |

#### Request Body
등록과 동일

### Response
```
HTTP/1.1 204 No Content
```

---

## 3. 환불정책 목록 조회

### Request
```
GET /api/v2/sellers/{sellerId}/refund-policies?page=0&size=20&sort=createdAt,desc
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Query Parameters
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | Integer | N | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | N | 20 | 페이지 크기 |
| sort | String | N | createdAt,desc | 정렬 기준 |

### Response
```
HTTP/1.1 200 OK
```
```json
{
  "data": {
    "content": [
      {
        "policyId": 1,
        "policyName": "기본 환불정책",
        "defaultPolicy": true,
        "active": true,
        "returnPeriodDays": 7,
        "exchangePeriodDays": 7,
        "nonReturnableConditions": [
          {
            "code": "OPENED_PACKAGING",
            "displayName": "포장 개봉"
          },
          {
            "code": "USED_PRODUCT",
            "displayName": "사용 흔적"
          }
        ],
        "createdAt": "2025-01-26T10:30:00+09:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-01-26T10:30:00+09:00",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Response Content Fields
| Field | Type | Description |
|-------|------|-------------|
| policyId | Long | 정책 ID |
| policyName | String | 정책명 |
| defaultPolicy | Boolean | 기본 정책 여부 |
| active | Boolean | 활성화 상태 |
| returnPeriodDays | Integer | 반품 가능 기간 |
| exchangePeriodDays | Integer | 교환 가능 기간 |
| nonReturnableConditions | Object[] | 반품 불가 조건 목록 |
| createdAt | String | 생성일시 (ISO 8601) |

---

## 4. 환불정책 다건 상태 변경

### Request
```
PATCH /api/v2/sellers/{sellerId}/refund-policies/status
Content-Type: application/json
```

#### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sellerId | Long | Y | 셀러 ID |

#### Request Body
```json
{
  "policyIds": [1, 2, 3],
  "active": false
}
```

#### Request Body Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| policyIds | Long[] | Y | 상태 변경할 정책 ID 목록 |
| active | Boolean | Y | 변경할 활성화 상태 |

### Response
```
HTTP/1.1 204 No Content
```

---

# 에러 코드

## 배송정책 (SHP)
| Code | HTTP Status | Message |
|------|-------------|---------|
| SHP-001 | 404 | 배송 정책을 찾을 수 없습니다 |
| SHP-002 | 400 | 비활성화된 배송 정책입니다 |
| SHP-003 | 400 | 이미 기본 배송 정책으로 설정되어 있습니다 |
| SHP-004 | 400 | 기본 배송 정책은 삭제할 수 없습니다 |
| SHP-005 | 400 | 무료배송 기준금액이 유효하지 않습니다 |

## 환불정책 (RFP)
| Code | HTTP Status | Message |
|------|-------------|---------|
| RFP-001 | 404 | 환불 정책을 찾을 수 없습니다 |
| RFP-002 | 400 | 비활성화된 환불 정책입니다 |
| RFP-003 | 400 | 이미 기본 환불 정책으로 설정되어 있습니다 |
| RFP-004 | 400 | 기본 환불 정책은 삭제할 수 없습니다 |
| RFP-005 | 400 | 반품 기간이 유효하지 않습니다 |
| RFP-006 | 400 | 교환 기간이 유효하지 않습니다 |
| RFP-007 | 400 | 반품 가능 기간이 만료되었습니다 |
| RFP-008 | 400 | 교환 가능 기간이 만료되었습니다 |
