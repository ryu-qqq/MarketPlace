# 네이버 커머스 API - 문의(QnA) 관련 API 문서

> 출처: https://apicenter.commerce.naver.com/docs/commerce-api/current
> 버전: 2.74.0
> 수집일: 2026-03-20

---

## 개요

네이버 커머스 API의 문의 카테고리는 **두 가지 도메인**으로 나뉩니다:

1. **고객 문의 (Customer Inquiry)** - 주문 기반 1:1 문의 (상품, 배송, 반품, 교환, 환불, 기타)
2. **상품 문의 (Product QnA)** - 상품 상세 페이지 문의 (주문 없이 가능)

---

## 1. 고객 문의 조회

**Method**: `GET`
**URL**: `/v1/pay-user/inquiries`

주문 기반 고객 1:1 문의를 조회합니다.

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 | 기본값 |
|----------|------|------|------|--------|
| page | integer | - | 조회할 페이지 번호 (1~1000000) | 1 |
| size | integer | - | 페이지 크기 (10~200) | 10 |
| startSearchDate | string | **필수** | 문의 검색 시작일 (yyyy-MM-dd) | - |
| endSearchDate | string | **필수** | 문의 검색 종료일 (yyyy-MM-dd) | - |
| answered | string | - | 답변 완료 여부 (true/false). 생략 시 전체 조회 | - |

### Response (200) - content 배열 내 구조

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| inquiryNo | integer\<int64\> | **필수** | 문의 번호 |
| category | string | - | 문의 유형 (상품, 배송, 반품, 교환, 환불, 기타) |
| title | string | **필수** | 문의 제목 |
| inquiryContent | string | **필수** | 문의 내용 |
| inquiryRegistrationDateTime | string\<date-time\> | **필수** | 문의 등록 일시 |
| answerContentId | integer\<int64\> | - | 최근 답변 ID |
| answerContent | string | - | 최근 답변 내용 |
| answerTemplateNo | integer\<int64\> | - | 최근 답변 템플릿 번호 |
| answerRegistrationDateTime | string\<date-time\> | - | 최근 답변 등록 일시 |
| answered | boolean | **필수** | 답변 여부 |
| orderId | string | **필수** | 주문 ID |
| productNo | string | - | 상품번호 |
| productOrderIdList | string | - | 상품 주문 ID 목록 (쉼표 구분) |
| productName | string | - | 상품명 |
| productOrderOption | string | - | 상품 주문 옵션 |
| customerId | string | - | 구매자 ID |
| customerName | string | **필수** | 구매자 이름 |

### CURL 예시

```bash
curl -L 'https://api.commerce.naver.com/external/v1/pay-user/inquiries?startSearchDate=2026-03-01&endSearchDate=2026-03-20&answered=false&page=1&size=100' \
  -H 'Accept: application/json;charset=UTF-8' \
  -H 'Authorization: Bearer <token>'
```

---

## 2. 고객 문의 답변 등록

**Method**: `POST`
**URL**: `/v1/pay-merchant/inquiries/:inquiryNo/answer`

### Path Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| inquiryNo | integer\<int64\> | **필수** | 문의 번호 |

### Request Body (application/json)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| answerComment | string | **필수** | 답변 내용 |
| answerTemplateId | string | - | 답변 템플릿 ID |

### Response (200)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| code | string | 응답 코드 |
| message | string | 메시지 |
| data.inquiryNo | integer\<int64\> | 문의 번호 |
| data.inquiryCommentNo | integer\<int64\> | 답변 번호 |
| timestamp | string\<date-time\> | 응답 시간 |
| traceId | string | 추적 ID |

### CURL 예시

```bash
curl -L 'https://api.commerce.naver.com/external/v1/pay-merchant/inquiries/12345/answer' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"answerComment": "답변 내용입니다", "answerTemplateId": "12345"}'
```

---

## 3. 고객 문의 답변 수정

**Method**: `PUT`
**URL**: `/v1/pay-merchant/inquiries/:inquiryNo/answer/:answerContentId`

### Path Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| inquiryNo | integer\<int64\> | **필수** | 문의 번호 |
| answerContentId | integer\<int64\> | **필수** | 답변 번호 |

### Request Body (application/json)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| answerComment | string | **필수** | 답변 내용 |
| answerTemplateId | string | - | 답변 템플릿 ID |

### Response (200)

고객 문의 답변 등록과 동일한 구조.

---

## 4. 상품 문의 목록 조회

**Method**: `GET`
**URL**: `/v1/contents/qnas`

상품 상세 페이지에 등록된 문의를 조회합니다. (주문 기반이 아닌 상품 기반)

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 | 기본값 |
|----------|------|------|------|--------|
| page | integer\<int32\> | - | 페이지 번호 (첫 번째 = 1) | 1 |
| size | integer\<int32\> | - | 페이지 크기 (최대 100) | 100 |
| answered | boolean | - | 답변 여부 필터 | - |
| fromDate | string\<date-time\> | **필수** | 검색 시작 일시 (예: 2020-07-25T10:10:10.100Z) | - |
| toDate | string\<date-time\> | **필수** | 검색 종료 일시 (예: 2020-07-25T10:10:10.100Z) | - |

### Response (200) - contents 배열 내 구조

| 필드명 | 타입 | 설명 |
|--------|------|------|
| questionId | integer\<int64\> | 상품 문의 번호 |
| productId | integer\<int64\> | 채널 상품번호 |
| productName | string | 상품명 |
| question | string | 문의 내용 |
| answer | string | 판매자 답변 (여러 개면 최초 답변 반환) |
| answered | boolean | 판매자 답변 여부 |
| maskedWriterId | string | 마스킹된 작성자 ID |
| createDate | string\<date-time\> | 생성 일시 |

### CURL 예시

```bash
curl -L 'https://api.commerce.naver.com/external/v1/contents/qnas?fromDate=2026-03-01T00:00:00.000Z&toDate=2026-03-20T23:59:59.999Z&answered=false&page=1&size=100' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

---

## 5. 상품 문의 답변 등록/수정

**Method**: `PUT`
**URL**: `/v1/contents/qnas/:questionId`

상품 문의의 답변을 등록하거나 수정합니다.

### Path Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| questionId | integer\<int64\> | **필수** | 상품 문의 ID |

### Request Body (application/json)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| commentContent | string | **필수** | 상품 문의 답변 내용 |

### Response

- **204 No Content**: 성공

### CURL 예시

```bash
curl -L -X PUT 'https://api.commerce.naver.com/external/v1/contents/qnas/12345' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"commentContent": "답변 내용입니다"}'
```

---

## OMS 매핑 가이드

### 네이버 문의 유형 → QnaType 매핑

| 네이버 category | QnaType |
|----------------|---------|
| 상품 | PRODUCT |
| 배송 | SHIPPING |
| 반품 | REFUND |
| 교환 | EXCHANGE |
| 환불 | REFUND |
| 기타 | ETC |
| (상품 문의 API) | PRODUCT |

### 폴링 전략

1. **고객 문의** (`/v1/pay-user/inquiries`): 주문 기반 문의. `startSearchDate`/`endSearchDate` + `answered=false`로 미답변 문의 폴링
2. **상품 문의** (`/v1/contents/qnas`): 상품 기반 문의. `fromDate`/`toDate` + `answered=false`로 미답변 문의 폴링

### 답변 전략

1. **고객 문의 답변**: `POST /v1/pay-merchant/inquiries/{inquiryNo}/answer` → `answerComment` 필드
2. **상품 문의 답변**: `PUT /v1/contents/qnas/{questionId}` → `commentContent` 필드

> **주의**: 고객 문의와 상품 문의의 답변 필드명이 다릅니다 (`answerComment` vs `commentContent`).
> 고객 문의 답변은 POST(등록) / PUT(수정) 분리, 상품 문의는 PUT 하나로 등록/수정 모두 처리.
