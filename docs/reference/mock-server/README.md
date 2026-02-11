# OMS Mock Server

OMS(Order Management System) 프론트엔드 개발을 위한 Mock API 서버입니다.

---

## 🚀 빠른 시작

### 방법 1: 로컬 개발 환경과 함께 (권장)

```bash
# 로컬 개발 환경 + Mock Server 함께 시작
cd local-dev
./scripts/local-start.sh --mock

# 종료
./scripts/local-stop.sh --mock
```

### 방법 2: Mock Server만 단독 실행

```bash
# 1. Mock 서버 실행
cd mock-server
docker-compose up -d

# 2. 서버 상태 확인
curl http://localhost:8089/__admin/mappings

# 3. API 테스트
curl http://localhost:8089/api/oms/presets

# 4. 서버 중지
docker-compose down
```

### 방법 3: 완전 독립 로컬 환경에서 사용

```bash
# MySQL + Redis + Mock Server 함께 시작
cd local-dev
docker-compose -f docker-compose.dev.yml --profile mock up -d

# 종료
docker-compose -f docker-compose.dev.yml --profile mock down
```

**Base URL**: `http://localhost:8089`
**API Prefix**: `/api/oms`

---

## 📋 응답 구조

### 성공 응답 (ApiResponse)

모든 성공 API 응답은 `ApiResponse<T>` 구조를 따릅니다:

```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-001"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 항상 `true` |
| `data` | T | 실제 응답 데이터 |
| `timestamp` | string | 응답 시각 (ISO 8601) |
| `requestId` | string | 요청 추적 ID |

### 페이지 응답 (PageApiResponse)

페이징이 필요한 목록 조회는 `PageApiResponse<T>` 구조를 사용합니다:

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 156,
    "totalPages": 16,
    "first": true,
    "last": false
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-001"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `content` | T[] | 현재 페이지 데이터 |
| `page` | number | 현재 페이지 (0부터 시작) |
| `size` | number | 페이지 크기 |
| `totalElements` | number | 전체 데이터 개수 |
| `totalPages` | number | 전체 페이지 수 |
| `first` | boolean | 첫 페이지 여부 |
| `last` | boolean | 마지막 페이지 여부 |

---

## 🚨 에러 응답 (RFC 7807 Problem Details)

모든 에러 응답은 [RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807) 표준을 따릅니다:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for request",
  "instance": "/api/oms/products",
  "timestamp": "2025-12-22T10:30:00.000Z",
  "code": "VALIDATION_FAILED"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `type` | string | 에러 타입 URI (기본: `about:blank`) |
| `title` | string | HTTP 상태 텍스트 |
| `status` | number | HTTP 상태 코드 |
| `detail` | string | 에러 상세 메시지 |
| `instance` | string | 에러 발생 경로 |
| `timestamp` | string | 에러 발생 시각 (ISO 8601) |
| `code` | string | 애플리케이션 에러 코드 |

### 검증 에러 (errors 필드 포함)

`@Valid` 검증 실패 시 `errors` 필드가 추가됩니다:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for request",
  "instance": "/api/oms/products",
  "timestamp": "2025-12-22T10:30:00.000Z",
  "code": "VALIDATION_FAILED",
  "errors": {
    "productName": "상품명은 필수입니다",
    "price": "가격은 0보다 커야 합니다"
  }
}
```

> **Note**: `errors` 필드는 검증 에러(VALIDATION_FAILED, BINDING_FAILED, CONSTRAINT_VIOLATION)에서만 포함됩니다.

### 에러 코드 목록

| 코드 | HTTP 상태 | 설명 |
|------|----------|------|
| `VALIDATION_FAILED` | 400 | @RequestBody 검증 실패 |
| `BINDING_FAILED` | 400 | @ModelAttribute 바인딩 실패 |
| `CONSTRAINT_VIOLATION` | 400 | 메서드 레벨 검증 실패 |
| `INVALID_ARGUMENT` | 400 | 잘못된 인자 |
| `INVALID_FORMAT` | 400 | JSON 파싱 실패 |
| `TYPE_MISMATCH` | 400 | 파라미터 타입 불일치 |
| `MISSING_PARAMETER` | 400 | 필수 파라미터 누락 |
| `UNAUTHORIZED` | 401 | 인증 필요 |
| `RESOURCE_NOT_FOUND` | 404 | 리소스 없음 |
| `PRODUCT_NOT_FOUND` | 404 | 상품 없음 |
| `METHOD_NOT_ALLOWED` | 405 | 지원하지 않는 HTTP 메서드 |
| `STATE_CONFLICT` | 409 | 상태 충돌 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |

### 에러 응답 Content-Type

에러 응답의 Content-Type은 `application/problem+json`입니다.

---

## 📺 화면별 API 매핑

### 화면 1: 연동 정보 등록 (Preset)

```
┌─────────────────────────────────────────────────────────────┐
│  [검색 조건] [검색 버튼]                                      │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 연동 리스트 테이블                      [선택삭제] [신규등록] │
│  │ NO | 쇼핑몰 | 설정제목 | 카테고리 | 등록일 | 관리     │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘

[사용자 액션 → API]
• 화면 진입 / 검색 버튼 클릭 → GET /api/oms/presets
• [선택 삭제] 버튼 클릭      → DELETE /api/oms/presets
• [신규 등록] 버튼 클릭      → GET /api/oms/shops (드롭다운 데이터)
• 모달에서 카테고리 선택     → GET /api/oms/categories
• 모달 [저장] 버튼 클릭      → POST /api/oms/presets
• [수정] 버튼 클릭           → PUT /api/oms/presets/{id}
```

---

### 화면 2: 상품 조회 및 전송

```
┌─────────────────────────────────────────────────────────────┐
│  [검색 조건: 기간, 상태, 파트너, 연동상태, 상품코드]            │
│  [상품 조회 버튼]                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 검색 결과 테이블               [엑셀다운로드] [선택상품연동] │
│  │ ☑ | NO | 이미지 | 상품코드 | 상품명 | 가격 | 재고 | ...  │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘

[사용자 액션 → API]
• [상품 조회] 버튼 클릭           → GET /api/oms/products
• 상품 행 클릭                   → GET /api/oms/products/{id}
• [선택 상품 연동] 버튼 클릭      → GET /api/oms/presets (모달용)
• 모달 [전송 시작] 버튼 클릭      → POST /api/oms/products/sync
```

---

### 화면 3: 상품 상세 (연동 이력)

```
┌─────────────────────────────────────────────────────────────┐
│  상품 정보 영역                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 이미지 | 상품명, 가격, 재고, 옵션 등                    │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│  연동 이력 [상태 필터: 전체/성공/실패/대기중]                   │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ NO | 쇼핑몰 | 프리셋 | 상태 | 요청일시 | 외부상품ID | 재처리 │    │
│  └─────────────────────────────────────────────────────┘    │
│  [페이지네이션]                                               │
└─────────────────────────────────────────────────────────────┘

[사용자 액션 → API]
• 화면 진입                → GET /api/oms/products/{id}
• 화면 진입 / 필터 변경     → GET /api/oms/products/{id}/sync-history
• [재처리] 버튼 클릭        → POST /api/oms/sync-history/{historyId}/retry
```

---

## 📡 API 상세 스펙

---

### 1. 프리셋 목록 조회

```
GET /api/oms/presets
```

> 화면: 연동 정보 등록 (리스트), 상품 전송 모달

**Query Parameters:**

```
startDate   (string, 선택)  등록일 시작 YYYY-MM-DD
endDate     (string, 선택)  등록일 종료 YYYY-MM-DD
shopName    (string, 선택)  쇼핑몰명 필터
keyword     (string, 선택)  설정 제목 검색
```

**Request:**

```bash
curl "http://localhost:8089/api/oms/presets?startDate=2025-12-01&endDate=2025-12-15"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "shopName": "스마트스토어",
        "accountId": "trexi001",
        "presetName": "식품 - 과자류 전송용",
        "categoryPath": "식품 > 과자 > 스낵 > 젤리",
        "categoryCode": "50000123",
        "createdAt": "2025-12-15"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 4,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-005"
}
```

---

### 2. 프리셋 신규 등록

```
POST /api/oms/presets
```

> 화면: 연동 정보 등록 → [신규 등록] 모달 → [저장]

**Request Body:**

```
shopId        (number, 필수)  쇼핑몰 ID - GET /api/oms/shops 에서 조회
presetName    (string, 필수)  설정 제목
categoryCode  (string, 필수)  세분류 카테고리 코드
```

**Request:**

```bash
curl -X POST http://localhost:8089/api/oms/presets \
  -H "Content-Type: application/json" \
  -d '{
    "shopId": 1,
    "presetName": "스마트스토어 - 겨울시즌 의류 전송용",
    "categoryCode": "50000123"
  }'
```

**Response (성공):**

```json
{
  "success": true,
  "data": {
    "id": 1005,
    "createdAt": "2025-12-17"
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-007"
}
```

**Response (검증 실패):**

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed for request",
  "instance": "/api/oms/presets",
  "timestamp": "2025-12-22T10:30:00.000Z",
  "code": "VALIDATION_FAILED",
  "errors": {
    "presetName": "설정 제목은 필수입니다",
    "categoryCode": "카테고리 코드는 필수입니다"
  }
}
```

---

### 3. 프리셋 수정

```
PUT /api/oms/presets/{id}
```

> 화면: 연동 정보 등록 → [수정] 버튼

**Path Parameters:**

```
id  (number, 필수)  프리셋 ID
```

**Request Body:**

```
presetName    (string, 선택)  설정 제목
categoryCode  (string, 선택)  세분류 카테고리 코드
```

**Request:**

```bash
curl -X PUT http://localhost:8089/api/oms/presets/1001 \
  -H "Content-Type: application/json" \
  -d '{
    "presetName": "수정된 프리셋 이름",
    "categoryCode": "50000456"
  }'
```

**Response:**

```json
{
  "success": true,
  "data": null,
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-008"
}
```

---

### 4. 프리셋 삭제

```
DELETE /api/oms/presets
```

> 화면: 연동 정보 등록 → 체크박스 선택 → [선택 삭제]

**Request Body:**

```
ids  (number[], 필수)  삭제할 프리셋 ID 배열
```

**Request:**

```bash
curl -X DELETE http://localhost:8089/api/oms/presets \
  -H "Content-Type: application/json" \
  -d '{ "ids": [1001, 1002] }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "deletedCount": 2
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-009"
}
```

---

### 5. 쇼핑몰 계정 목록

```
GET /api/oms/shops
```

> 화면: 연동 정보 등록 → [신규 등록] 모달 → 쇼핑몰/계정 드롭다운

**Query Parameters:**

```
keyword  (string, 선택)  쇼핑몰명 또는 계정 ID 검색
status   (string, 선택)  ACTIVE | INACTIVE
page     (number, 선택)  페이지 번호 (기본: 0)
size     (number, 선택)  페이지 크기 (기본: 10)
```

**Request:**

```bash
curl "http://localhost:8089/api/oms/shops?page=0&size=10"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      { "id": 1, "shopName": "스마트스토어", "accountId": "trexi001", "status": "ACTIVE" },
      { "id": 2, "shopName": "쿠팡", "accountId": "my_coupang_shop", "status": "ACTIVE" },
      { "id": 3, "shopName": "11번가", "accountId": "st_11_main", "status": "ACTIVE" },
      { "id": 4, "shopName": "G마켓", "accountId": "gmkt_global", "status": "ACTIVE" }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 4,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-006"
}
```

**드롭다운 표시 형식:** `스마트스토어 (trexi001)`

---

### 6. 파트너사 목록

```
GET /api/oms/partners
```

> 화면: 상품 조회 및 전송 → 파트너사 드롭다운/검색

**Query Parameters:**

```
keyword  (string, 선택)  파트너사명 또는 코드 검색
status   (string, 선택)  ACTIVE | INACTIVE
page     (number, 선택)  페이지 번호 (기본: 0)
size     (number, 선택)  페이지 크기 (기본: 10)
```

**Request:**

```bash
curl "http://localhost:8089/api/oms/partners?keyword=나이키&page=0&size=10"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      { "id": 1001, "partnerName": "나이키코리아", "partnerCode": "NIKE-KR", "status": "ACTIVE" },
      { "id": 1002, "partnerName": "아디다스코리아", "partnerCode": "ADIDAS-KR", "status": "ACTIVE" },
      { "id": 1003, "partnerName": "뉴발란스코리아", "partnerCode": "NB-KR", "status": "ACTIVE" },
      { "id": 1004, "partnerName": "푸마코리아", "partnerCode": "PUMA-KR", "status": "ACTIVE" },
      { "id": 1005, "partnerName": "컨버스코리아", "partnerCode": "CONVERSE-KR", "status": "INACTIVE" }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-012"
}
```

**드롭다운 표시 형식:** `나이키코리아 (NIKE-KR)`

---

### 7. 카테고리 목록 (트리 구조)

```
GET /api/oms/categories
```

> 화면: 연동 정보 등록 → [신규 등록] 모달 → 카테고리 매핑

**Request:**

```bash
curl http://localhost:8089/api/oms/categories/{shopId}
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "code": "100000000",
      "name": "식품",
      "depth": 1,
      "children": [
        {
          "code": "100100000",
          "name": "과자/베이커리",
          "depth": 2,
          "children": [
            {
              "code": "100101000",
              "name": "스낵",
              "depth": 3,
              "children": [
                { "code": "100101001", "name": "젤리", "depth": 4, "children": [] },
                { "code": "100101002", "name": "사탕", "depth": 4, "children": [] }
              ]
            }
          ]
        }
      ]
    },
    {
      "code": "200000000",
      "name": "여성패션",
      "depth": 1,
      "children": [...]
    }
  ],
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-003"
}
```

**트리 구조 특징:**

```
• children: []      → leaf 노드 (최하위 카테고리, 이 코드를 저장)
• children: [...]   → 하위 존재, 드릴다운 가능
• depth             → 현재 레벨 (UI 스타일링용)
• 사이트마다 레벨 깊이가 다를 수 있음 (3~5레벨)
```

**프론트 처리 예시:**

```javascript
// 재귀적 렌더링
function renderCategory(category) {
  const hasChildren = category.children.length > 0;
  return (
    <div>
      <span onClick={() => hasChildren ? expand() : select(category.code)}>
        {category.name}
      </span>
      {hasChildren && category.children.map(child => renderCategory(child))}
    </div>
  );
}
```

---

### 8. 상품 목록 조회

```
GET /api/oms/products
```

> 화면: 상품 조회 및 전송 → [상품 조회]

**Query Parameters:**

```
dateType      (string, 선택)  createdAt | updatedAt
startDate     (string, 선택)  시작일 YYYY-MM-DD
endDate       (string, 선택)  종료일 YYYY-MM-DD
status        (string, 선택)  ON_SALE | SOLD_OUT
syncStatus    (string, 선택)  SUCCESS | FAILED | PENDING
keyword       (string, 선택)  통합 검색 (상품명, 상품코드, 파트너사명)
shopIds       (string, 선택)  쇼핑몰 ID 리스트 (콤마 구분, 예: 1,2,3)
partnerIds    (string, 선택)  파트너사 ID 리스트 (콤마 구분, 예: 1001,1002)
productCodes  (string, 선택)  상품코드 (콤마 구분, 숫자)
page          (number, 선택)  페이지 번호 (기본: 0)
size          (number, 선택)  페이지 크기 (기본: 10)
```

**Request:**

```bash
curl "http://localhost:8089/api/oms/products?status=ON_SALE&syncStatus=SUCCESS&page=0"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 15,
        "productCode": "125694305",
        "productName": "나이키 에어포스 1 '07 화이트",
        "imageUrl": "https://via.placeholder.com/100",
        "price": 129000,
        "stock": 50,
        "status": "ON_SALE",
        "statusLabel": "판매중",
        "partnerName": "나이키코리아",
        "createdAt": "2025-12-15",
        "syncStatus": "SUCCESS",
        "syncStatusLabel": "연동완료",
        "lastSyncAt": "2025-12-16 14:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 156,
    "totalPages": 16,
    "first": true,
    "last": false
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-001"
}
```

**상태값:**

```
status:
  ON_SALE   → 판매중 (초록)
  SOLD_OUT  → 품절   (빨강)

syncStatus:
  SUCCESS  → 연동완료 (초록)
  FAILED   → 연동실패 (빨강)
  PENDING  → 연동대기 (노랑)
```

---

### 9. 상품 상세 조회

```
GET /api/oms/products/{id}
```

> 화면: 상품 상세 페이지 상단 정보 영역

**Path Parameters:**

```
id  (number, 필수)  상품 ID (productGroupId)
```

**Request:**

```bash
curl http://localhost:8089/api/oms/products/125694305
```

**Response:**

```json
{
  "success": true,
  "data": {
    "productGroup": {
      "productGroupId": 125694305,
      "productGroupName": "나이키 에어포스 1 '07 화이트",
      "sellerId": 1001,
      "sellerName": "나이키코리아",
      "categoryId": 200101001,
      "optionType": "OPTION_ONE",
      "managementType": "STOCK",
      "brand": {
        "brandId": 501,
        "brandName": "Nike",
        "brandNameKo": "나이키"
      },
      "price": {
        "regularPrice": 159000,
        "currentPrice": 129000,
        "salePrice": 129000,
        "directDiscountPrice": 0,
        "directDiscountRate": 0,
        "discountRate": 19
      },
      "productGroupMainImageUrl": "https://via.placeholder.com/400",
      "categoryFullName": "여성패션 > 아우터 > 패딩 > 롱패딩",
      "productStatus": {
        "soldOutYn": "N",
        "displayYn": "Y"
      },
      "insertDate": "2025-12-15 10:30:00",
      "updateDate": "2025-12-16 14:30:00",
      "insertOperator": "admin",
      "updateOperator": "admin"
    },
    "products": [
      {
        "productId": 1001,
        "stockQuantity": 10,
        "productStatus": {
          "soldOutYn": "N",
          "displayYn": "Y"
        },
        "option": "250 / 화이트",
        "options": [
          {
            "optionGroupId": 1,
            "optionDetailId": 101,
            "optionName": "SIZE",
            "optionValue": "250"
          },
          {
            "optionGroupId": 2,
            "optionDetailId": 201,
            "optionName": "COLOR",
            "optionValue": "화이트"
          }
        ],
        "additionalPrice": 0
      }
    ],
    "syncSummary": {
      "totalSyncCount": 5,
      "successCount": 3,
      "failCount": 1,
      "pendingCount": 1,
      "lastSyncAt": "2025-12-16 14:30:00"
    }
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-002"
}
```

**Response (상품 없음):**

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "상품을 찾을 수 없습니다",
  "instance": "/api/oms/products/999999",
  "timestamp": "2025-12-22T10:30:00.000Z",
  "code": "PRODUCT_NOT_FOUND"
}
```

---

### 10. 연동 이력 조회

```
GET /api/oms/products/{id}/sync-history
```

> 화면: 상품 상세 → 연동 이력 테이블 (필터 + 페이징)

**Path Parameters:**

```
id  (number, 필수)  상품 ID
```

**Query Parameters:**

```
status  (string, 선택)  SUCCESS | FAILED | PENDING
page    (number, 선택)  페이지 번호 (기본: 0)
size    (number, 선택)  페이지 크기 (기본: 10)
```

**Request:**

```bash
curl "http://localhost:8089/api/oms/products/15/sync-history?status=FAILED&page=0&size=10"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "jobId": "SYNC-20251216-001",
        "shopName": "스마트스토어",
        "accountId": "trexi001",
        "presetName": "식품 - 과자류 전송용",
        "status": "SUCCESS",
        "statusLabel": "성공",
        "requestedAt": "2025-12-16 14:30:00",
        "completedAt": "2025-12-16 14:30:45",
        "externalProductId": "NAVER-12345678",
        "errorMessage": null,
        "retryCount": 0
      },
      {
        "id": 1003,
        "jobId": "SYNC-20251216-003",
        "shopName": "11번가",
        "accountId": "st_11_main",
        "presetName": "기본 잡화 세팅",
        "status": "FAILED",
        "statusLabel": "실패",
        "requestedAt": "2025-12-16 14:32:00",
        "completedAt": "2025-12-16 14:32:10",
        "externalProductId": null,
        "errorMessage": "카테고리 매핑 오류: 해당 카테고리가 존재하지 않습니다.",
        "retryCount": 2
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-004"
}
```

**상태값:**

```
SUCCESS  → 성공    (외부몰 등록 완료)
FAILED   → 실패    (오류 발생, 재처리 가능)
PENDING  → 대기중  (처리 대기 중)
```

---

### 11. 연동 재처리

```
POST /api/oms/sync-history/{historyId}/retry
```

> 화면: 상품 상세 → 연동 이력 → [재처리] 버튼

**Path Parameters:**

```
historyId  (number, 필수)  연동 이력 ID
```

**Request:**

```bash
curl -X POST http://localhost:8089/api/oms/sync-history/1003/retry
```

**Response:**

```json
{
  "success": true,
  "data": {
    "historyId": 1003,
    "newJobId": "SYNC-20251217-RETRY-001",
    "status": "PENDING"
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-010"
}
```

---

### 12. 상품 외부몰 전송

```
POST /api/oms/products/sync
```

> 화면: 상품 조회 및 전송 → [선택 상품 연동] 모달 → [전송 시작]

**Request Body:**

```
productIds  (number[], 필수)  전송할 상품 ID 배열
presetIds   (number[], 필수)  전송 대상 프리셋 ID 배열
```

**Request:**

```bash
curl -X POST http://localhost:8089/api/oms/products/sync \
  -H "Content-Type: application/json" \
  -d '{
    "productIds": [15, 14, 13],
    "presetIds": [1001, 1002]
  }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "jobId": "SYNC-20251217-001",
    "requestedCount": 3,
    "targetPresetCount": 2,
    "status": "PROCESSING"
  },
  "timestamp": "2025-12-22T10:30:00",
  "requestId": "req-mock-011"
}
```

---

## 📁 파일 구조

```
mock-server/
├── docker-compose.yml
├── README.md
├── mappings/                         # API 매핑 (13개)
│   ├── oms-presets-get.json
│   ├── oms-presets-post.json
│   ├── oms-presets-put.json
│   ├── oms-presets-delete.json
│   ├── oms-products-list.json
│   ├── oms-products-detail.json
│   ├── oms-products-sync.json
│   ├── oms-sync-history-get.json
│   ├── oms-sync-history-retry.json
│   ├── oms-shops-get.json
│   ├── oms-partners-get.json
│   ├── oms-categories-get.json
│   └── cors-options.json
└── __files/responses/                # Mock 데이터
    ├── presets-list.json
    ├── products-list.json
    ├── product-detail.json
    ├── sync-history.json
    ├── shops-list.json
    ├── partners-list.json
    ├── categories.json
    └── errors/                       # RFC 7807 에러 응답
        ├── validation-failed.json
        ├── unauthorized.json
        ├── not-found.json
        ├── product-not-found.json
        ├── category-mapping-failed.json
        └── internal-error.json
```

---

## ⚠️ 주의사항

1. 이 Mock 서버는 **개발 환경 전용**입니다
2. 프로덕션 배포와 **완전히 독립적**으로 운영됩니다
3. Query Parameter는 현재 Mock에서 **무시**됩니다 (항상 동일한 응답)
4. 백엔드 개발 완료 후 Base URL만 교체하면 됩니다
5. **상품 코드는 숫자형**입니다 (예: "125694305")
6. **카테고리는 트리 구조**이며, 사이트마다 레벨 깊이가 다를 수 있습니다
7. **성공 응답**: `ApiResponse` 구조 (`success`, `data`, `timestamp`, `requestId`)
8. **에러 응답**: RFC 7807 Problem Details 구조 (`type`, `title`, `status`, `detail`, `code`)
9. **에러 Content-Type**: `application/problem+json`

---

## 🔄 실제 API 전환

```javascript
// 개발 중 (Mock)
const API_BASE = 'http://localhost:8089/api/oms';

// 백엔드 완료 후 (실제)
const API_BASE = 'https://api.example.com/api/oms';
```

---

## 📞 문의

API 스펙 관련 문의: 백엔드 개발팀
