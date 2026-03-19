# 네이버 커머스 주문 API 종합 분석 보고서

> 출처: https://apicenter.commerce.naver.com/docs/commerce-api/current
> 버전: 2.73.0 | 분석일: 2026-03-18
> 목적: 기존 클라이언트 코드 정합성 검증 + 시나리오별 사용 가이드

---

## 목차

1. [API 전체 맵](#1-api-전체-맵)
2. [주문 조회 API](#2-주문-조회-api)
3. [발주/발송 처리 API](#3-발주발송-처리-api)
4. [취소 API](#4-취소-api)
5. [반품 API](#5-반품-api)
6. [교환 API](#6-교환-api)
7. [공통 구조체](#7-공통-구조체)
8. [시나리오별 API 사용 가이드](#8-시나리오별-api-사용-가이드)
9. [기존 클라이언트 코드 대조 결과](#9-기존-클라이언트-코드-대조-결과)

---

## 1. API 전체 맵

| 카테고리 | API명 | 메서드 | 엔드포인트 | 단건/복수 |
|----------|--------|--------|-----------|----------|
| **주문조회** | 상품 주문 목록 조회 | GET | `/v1/pay-order/seller/orders/{orderId}/product-order-ids` | 단건(주문) |
| | 조건형 상품 주문 상세 조회 | GET | `/v1/pay-order/seller/product-orders` | 복수(최대300) |
| | 변경 상품 주문 내역 조회 | GET | `/v1/pay-order/seller/product-orders/last-changed-statuses` | 복수(최대300) |
| | 상품 주문 상세 내역 조회 | POST | `/v1/pay-order/seller/product-orders/query` | 복수(최대300) |
| **발주/발송** | 발주 확인 처리 | POST | `/v1/pay-order/seller/product-orders/confirm` | 복수(최대30) |
| | 발송 처리 | POST | `/v1/pay-order/seller/product-orders/dispatch` | 복수(최대30) |
| | 발송 지연 처리 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/delay` | 단건 |
| | 배송 희망일 변경 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/hope-delivery/change` | 단건 |
| **취소** | 취소 요청 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/request` | 단건 |
| | 취소 요청 승인 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/approve` | 단건 |
| **반품** | 반품 요청 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/request` | 단건 |
| | 반품 승인 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/approve` | 단건 |
| | 반품 거부(철회) | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/reject` | 단건 |
| | 반품 보류 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback` | 단건 |
| | 반품 보류 해제 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback/release` | 단건 |
| **교환** | 교환 수거 완료 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/collect/approve` | 단건 |
| | 교환 재배송 처리 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/dispatch` | 단건 |
| | 교환 거부(철회) | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/reject` | 단건 |
| | 교환 보류 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback` | 단건 |
| | 교환 보류 해제 | POST | `/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback/release` | 단건 |

> Base URL: `https://api.commerce.naver.com/external`
> 인증: 모든 API에 `Authorization: Bearer <token>` 헤더 필수

---

## 2. 주문 조회 API

### 2.1 상품 주문 목록 조회

주문번호(orderId)로 해당 주문에 속한 상품주문번호 목록을 조회한다.

```
GET /v1/pay-order/seller/orders/{orderId}/product-order-ids
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `orderId` | string | ✅ 필수 | 주문 번호. 예: `2021123115350911` |

**Response 200:**

| 필드 | 타입 | 설명 |
|------|------|------|
| `timestamp` | string\<date-time\> | 응답 시각 |
| `traceId` | string | 추적 ID (필수) |
| `data` | string[] | 상품 주문 번호 목록 |

**사용 시나리오:** 주문번호만 알 때, 해당 주문에 포함된 모든 상품주문번호를 먼저 조회한 후 상세 조회 API로 넘기는 용도.

---

### 2.2 조건형 상품 주문 상세 내역 조회

기간 + 조건 필터로 상품주문 상세 내역을 조회한다. 페이지네이션 지원.

```
GET /v1/pay-order/seller/product-orders
```

**Query Parameters:**

| 필드 | 타입 | 필수 | 설명 | 기본값 |
|------|------|------|------|--------|
| `from` | string\<date-time\> | ✅ 필수 | 조회 시작 일시(inclusive) | - |
| `to` | string\<date-time\> | 선택 | 조회 종료 일시. 생략 시 from+24h | from+24h |
| `rangeType` | string(enum) | 선택 | 조회 기준 유형 | `PAYED_DATETIME` |
| `productOrderStatuses` | string[](enum) | 선택 | 상품주문 상태 필터 | 전체 |
| `claimStatuses` | string[](enum) | 선택 | 클레임 상태 필터 | 전체 |
| `placeOrderStatusType` | string(enum) | 선택 | 발주 상태 필터 | 전체 |
| `fulfillment` | boolean | 선택 | 풀필먼트 배송 여부 필터 | null(전체) |
| `pageSize` | integer | 선택 | 페이지 크기 (1~300) | 300 |
| `page` | integer | 선택 | 페이지 번호 (1~) | 1 |
| `quantityClaimCompatibility` | boolean | 선택 | 수량클레임 대응 완료 시 true | - |

**rangeType enum:**

| 코드 | 설명 |
|------|------|
| `PAYED_DATETIME` | 결제일시 기준 |
| `ORDERED_DATETIME` | 주문일시 기준 |
| `DISPATCHED_DATETIME` | 발송처리일시 기준 |
| `PURCHASE_DECIDED_DATETIME` | 구매확정일시 기준 |
| `CLAIM_REQUESTED_DATETIME` | 클레임요청일시 기준 |
| `CLAIM_COMPLETED_DATETIME` | 클레임완료일시 기준 |
| `COLLECT_COMPLETED_DATETIME` | 수거완료일시 기준 |
| `GIFT_RECEIVED_DATETIME` | 선물수락일시 기준 |
| `HOPE_DELIVERY_INFO_CHANGED_DATETIME` | 배송희망일변경일시 기준 |

**productOrderStatuses enum:**

| 코드 | 설명 |
|------|------|
| `PAYMENT_WAITING` | 결제 대기 |
| `PAYED` | 결제 완료 |
| `DELIVERING` | 배송 중 |
| `DELIVERED` | 배송 완료 |
| `PURCHASE_DECIDED` | 구매 확정 |
| `EXCHANGED` | 교환 |
| `CANCELED` | 취소 |
| `RETURNED` | 반품 |
| `CANCELED_BY_NOPAYMENT` | 미결제 취소 |

**claimStatuses enum:**

| 코드 | 설명 |
|------|------|
| `CANCEL_REQUEST` | 취소 요청 |
| `CANCELING` | 취소 처리 중 |
| `CANCEL_DONE` | 취소 처리 완료 |
| `CANCEL_REJECT` | 취소 철회 |
| `RETURN_REQUEST` | 반품 요청 |
| `EXCHANGE_REQUEST` | 교환 요청 |
| `COLLECTING` | 수거 처리 중 |
| `COLLECT_DONE` | 수거 완료 |
| `EXCHANGE_REDELIVERING` | 교환 재배송 중 |
| `RETURN_DONE` | 반품 완료 |
| `EXCHANGE_DONE` | 교환 완료 |
| `RETURN_REJECT` | 반품 철회 |
| `EXCHANGE_REJECT` | 교환 철회 |
| `PURCHASE_DECISION_HOLDBACK` | 구매 확정 보류 |
| `PURCHASE_DECISION_REQUEST` | 구매 확정 요청 |
| `PURCHASE_DECISION_HOLDBACK_RELEASE` | 구매 확정 보류 해제 |
| `ADMIN_CANCELING` | 직권 취소 중 |
| `ADMIN_CANCEL_DONE` | 직권 취소 완료 |
| `ADMIN_CANCEL_REJECT` | 직권 취소 철회 |

**placeOrderStatusType enum:**

| 코드 | 설명 |
|------|------|
| `NOT_YET` | 발주 미확인 |
| `OK` | 발주 확인 |
| `CANCEL` | 발주 확인 해제 |

**Response:** 상품 주문 정보 구조체 배열 (섹션 7.2 참조)

**사용 시나리오:** 특정 기간의 특정 상태 주문만 필터링하여 조회할 때. 예) "오늘 결제 완료된 미발주 주문 목록 조회"

---

### 2.3 변경 상품 주문 내역 조회

지정 기간 내 상태가 변경된 상품주문 내역을 조회한다. **폴링(Polling)** 방식의 핵심 API.

```
GET /v1/pay-order/seller/product-orders/last-changed-statuses
```

**Query Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `lastChangedFrom` | string\<date-time\> | ✅ 필수 | 조회 시작 일시(inclusive) |
| `lastChangedTo` | string\<date-time\> | 선택 | 조회 종료 일시. 생략 시 from+24h |
| `lastChangedType` | string(enum) | 선택 | 변경 유형 필터 |
| `moreSequence` | string | 선택 | 페이징용 (이전 응답의 more.moreSequence 값) |
| `limitCount` | integer | 선택 | 조회 응답 개수 제한 (최대 300) |

**lastChangedType enum:**

| 코드 | 설명 |
|------|------|
| `PAY_WAITING` | 결제 대기 |
| `PAYED` | 결제 완료 |
| `EXCHANGE_OPTION` | 옵션 변경 (선물하기) |
| `DELIVERY_ADDRESS_CHANGED` | 배송지 변경 |
| `GIFT_RECEIVED` | 선물 수락 |
| `CLAIM_REJECTED` | 클레임 철회 |
| `DISPATCHED` | 발송 처리 |
| `CLAIM_REQUESTED` | 클레임 요청 |
| `COLLECT_DONE` | 수거 완료 |
| `CLAIM_COMPLETED` | 클레임 완료 |
| `PURCHASE_DECIDED` | 구매 확정 |
| `HOPE_DELIVERY_INFO_CHANGED` | 배송 희망일 변경 |
| `CLAIM_REDELIVERING` | 교환 재배송 처리 |

**Response 200:**

| 필드 | 타입 | 설명 |
|------|------|------|
| `data.lastChangeStatuses` | Array | 변경 상품 주문 정보 구조체 배열 (섹션 7.1 참조) |
| `data.more` | object | 다음 페이지 존재 시 제공 |
| `data.more.moreFrom` | string\<date-time\> | 다음 요청의 `lastChangedFrom`에 입력할 값 |
| `data.more.moreSequence` | string | 다음 요청의 `moreSequence`에 입력할 값 |

> **페이징 방식:** 300건 초과 시 `more` 객체가 반환됨. `moreFrom`과 `moreSequence`를 다음 요청에 넘겨서 이어서 조회.
> `more` 객체가 없으면 모든 데이터 조회 완료.

**사용 시나리오:** 주기적으로 변경된 주문을 감지하는 폴링. 기존 코드의 2-phase 폴링 1단계.

---

### 2.4 상품 주문 상세 내역 조회

상품주문번호 목록으로 상세 정보를 일괄 조회한다.

```
POST /v1/pay-order/seller/product-orders/query
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderIds` | string[] | ✅ 필수 | 상품 주문 번호 목록 (**최대 300개**) |
| `quantityClaimCompatibility` | boolean | 선택 | 수량클레임 대응 완료 시 true |

**Response:** 상품 주문 정보 구조체 배열 (섹션 7.2 참조)

**사용 시나리오:** 폴링 2단계 - `last-changed-statuses`에서 얻은 productOrderId 목록으로 상세 정보 일괄 조회.

---

## 3. 발주/발송 처리 API

### 3.1 발주 확인 처리

결제 완료된 상품주문의 발주를 확인 처리한다.

```
POST /v1/pay-order/seller/product-orders/confirm
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderIds` | string[] | ✅ 필수 | 상품 주문 번호 배열 (**최대 30개**) |

**Response 200 (발주 확인 전용 응답 - 공통 구조체와 다름!):**

| 필드 | 타입 | 설명 |
|------|------|------|
| `timestamp` | string\<date-time\> | 응답 시각 |
| `traceId` | string | 추적 ID (필수) |
| `data.successProductOrderInfos` | object[] | 성공한 상품주문 정보 |
| `data.successProductOrderInfos[].productOrderId` | string | 상품주문번호 |
| `data.successProductOrderInfos[].isReceiverAddressChanged` | boolean | **수취인 주소 변경 여부** |
| `data.failProductOrderInfos` | object[] | 실패한 상품주문 정보 |
| `data.failProductOrderInfos[].productOrderId` | string | 상품주문번호 |
| `data.failProductOrderInfos[].code` | string | 실패 코드 |
| `data.failProductOrderInfos[].message` | string | 실패 메시지 |

> ⚠️ **주의:** 발주 확인 API만 응답 구조가 다르다! `successProductOrderIds`(string[])가 아니라 `successProductOrderInfos`(object[])이며, `isReceiverAddressChanged` 필드가 추가로 포함된다.

**사용 시나리오:** 결제 완료 → 발주 확인 → 발송 처리 순서. 발주 확인 시 주소 변경 여부를 확인해야 한다.

---

### 3.2 발송 처리

발주 확인된 상품주문을 발송 처리한다. 운송장 번호 등록.

```
POST /v1/pay-order/seller/product-orders/dispatch
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `dispatchProductOrders` | object[] | ✅ 필수 | 발송 처리할 상품주문 목록 (**최대 30개**) |

**dispatchProductOrders 각 객체:**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 | `2022040521691281` |
| `deliveryMethod` | string(enum) | ✅ 필수 | 배송 방법 코드 | `DELIVERY` |
| `deliveryCompanyCode` | string(enum) | ✅ 필수 | 택배사 코드 | `CJGLS` |
| `trackingNumber` | string | ✅ 필수 | 송장 번호 | `1234567890` |
| `dispatchDate` | string\<date-time\> | ✅ 필수 | 배송일 | `2022-04-05T12:17:35.000+09:00` |

**deliveryMethod enum:**

| 코드 | 설명 |
|------|------|
| `DELIVERY` | 택배, 등기, 소포 |
| `GDFW_ISSUE_SVC` | 굿스플로 송장 출력 |
| `VISIT_RECEIPT` | 방문 수령 |
| `DIRECT_DELIVERY` | 직접 전달 |
| `QUICK_SVC` | 퀵서비스 |
| `NOTHING` | 배송 없음 |
| `RETURN_DESIGNATED` | 지정 반품 택배 |
| `RETURN_DELIVERY` | 일반 반품 택배 |
| `RETURN_INDIVIDUAL` | 직접 반송 |
| `RETURN_MERCHANT` | 판매자 직접 수거(장보기 전용) |
| `UNKNOWN` | 알 수 없음 |

**deliveryCompanyCode enum (주요 택배사):**

| 코드 | 설명 |
|------|------|
| `CJGLS` | CJ대한통운 |
| `HYUNDAI` | 롯데택배 |
| `HANJIN` | 한진택배 |
| `KGB` | 로젠택배 |
| `EPOST` | 우체국택배 |
| `KDEXP` | 경동택배 |
| `CHUNIL` | 천일택배 |
| `PANTOS` | LX판토스 |
| `HDEXP` | 합동택배 |
| `SEBANG` | 세방택배 |
| `ILYANG` | 일양로지스 |
| `CUPARCEL` | CU편의점택배 |
| `CVSNET` | GSPostbox택배 |
| `HYBRID` | HI택배 |
| `KGLNET` | KGL네트웍스 |
| `KOREXG` | CJ대한통운(국제택배) |
| `DHL` | DHL |
| `FEDEX` | FEDEX |
| `EMS` | EMS |
| `UPS` | UPS |
| `TNT` | TNT |
| `USPS` | USPS |
| `ARAMEX` | ARAMEX |
| `KURLY` | 컬리넥스트마일 |
| `TEAMFRESH` | 팀프레시 |
| `VROONG` | 부릉 |
| `TODAYPICKUP` | 카카오T당일배송 |
| `HOMEPICK` | 홈픽택배 |
| `SSG` | SSG |
| `CH1` | 기타택배 |

> 전체 택배사 코드는 약 200개 이상. 위는 주요 코드만 발췌.

**Response:** 공통 클레임 응답 구조체 (섹션 7.3 참조)

**사용 시나리오:** 발주 확인 후, 택배사 + 운송장번호로 발송 처리.

---

### 3.3 발송 지연 처리

특정 상품주문의 발송을 지연 처리한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/delay
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 |

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `dispatchDueDate` | string\<date-time\> | ✅ 필수 | 발송 기한 | `2022-06-05T12:17:35.000+09:00` |
| `delayedDispatchReason` | string(enum) | ✅ 필수 | 지연 사유 코드 | `PRODUCT_PREPARE` |
| `dispatchDelayedDetailedReason` | string | 선택 | 상세 사유 | `상품 준비중입니다.` |

**delayedDispatchReason enum:**

| 코드 | 설명 |
|------|------|
| `PRODUCT_PREPARE` | 상품 준비 중 |
| `CUSTOMER_REQUEST` | 고객 요청 |
| `CUSTOM_BUILD` | 주문 제작 |
| `RESERVED_DISPATCH` | 예약 발송 |
| `OVERSEA_DELIVERY` | 해외 배송 |
| `ETC` | 기타 |

**Response:** 공통 클레임 응답 구조체 (섹션 7.3 참조)

---

### 3.4 배송 희망일 변경 처리

배송 희망일 정보를 변경한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/hope-delivery/change
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 |

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `hopeDeliveryYmd` | string | ✅ 필수 | 배송 희망일 (yyyyMMdd) | `20221231` |
| `hopeDeliveryHm` | string | 선택 | 배송 희망 시간 (HHmm) | `1500` |
| `region` | string | 선택 | 지역 (1~30자) | - |
| `changeReason` | string | ✅ 필수 | 변경 사유 (1~300자) | `고객 요청` |

**Response:** 공통 클레임 응답 구조체 (섹션 7.3 참조)

---

## 4. 취소 API

### 4.1 취소 요청

판매자가 1건의 상품주문을 취소 요청한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/request
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 |

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `cancelReason` | string(enum) | ✅ 필수 | 취소 사유 코드 (250바이트) |
| `cancelDetailedReason` | string | 선택 | 상세 사유 (**500자 제한**) |
| `cancelQuantity` | integer | 선택 | 취소 수량 (**미입력 시 전체수량 취소**) |

**cancelReason enum:**

| 코드 | 설명 |
|------|------|
| `INTENT_CHANGED` | 구매 의사 취소 |
| `COLOR_AND_SIZE` | 색상 및 사이즈 변경 |
| `WRONG_ORDER` | 다른 상품 잘못 주문 |
| `PRODUCT_UNSATISFIED` | 서비스 불만족 |
| `DELAYED_DELIVERY` | 배송 지연 |
| `SOLD_OUT` | 상품 품절 |
| `INCORRECT_INFO` | 상품 정보 상이 |

**Response:** 공통 클레임 응답 구조체 (섹션 7.3 참조)

---

### 4.2 취소 요청 승인

구매자가 요청한 취소를 판매자가 승인한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/approve
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 |

**Request Body:** 없음

**Response:** 공통 클레임 응답 구조체 (섹션 7.3 참조)

---

## 5. 반품 API

### 5.1 반품 요청

판매자가 1건의 상품주문을 반품 요청한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/return/request
```

**Path Parameters:**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `productOrderId` | string | ✅ 필수 | 상품 주문 번호 |

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `returnReason` | string(enum) | ✅ 필수 | 반품 사유 코드 |
| `collectDeliveryMethod` | string(enum) | ✅ 필수 | 수거 배송 방법 |
| `collectDeliveryCompany` | string(enum) | 선택 | 수거 택배사 코드 |
| `collectTrackingNumber` | string | 선택 | 수거 운송장번호 |
| `returnQuantity` | integer | 선택 | 반품 수량 (**미입력 시 전체수량 반품**) |

**returnReason enum:**

| 코드 | 설명 |
|------|------|
| `INTENT_CHANGED` | 구매 의사 취소 |
| `COLOR_AND_SIZE` | 색상 및 사이즈 변경 |
| `WRONG_ORDER` | 다른 상품 잘못 주문 |
| `PRODUCT_UNSATISFIED` | 서비스 불만족 |
| `DELAYED_DELIVERY` | 배송 지연 |
| `SOLD_OUT` | 상품 품절 |
| `DROPPED_DELIVERY` | 배송 누락 |
| `BROKEN` | 상품 파손 |
| `INCORRECT_INFO` | 상품 정보 상이 |
| `WRONG_DELIVERY` | 오배송 |
| `WRONG_OPTION` | 색상 등 다른 상품 잘못 배송 |

> **참고:** 취소 사유 vs 반품 사유의 차이:
> - 반품에만 존재: `DROPPED_DELIVERY`, `BROKEN`, `WRONG_DELIVERY`, `WRONG_OPTION`
> - 배송 후 발생하는 사유들이 반품에 추가됨

---

### 5.2 반품 승인

반품 요청을 승인한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/return/approve
```

**Request Body:** 없음

---

### 5.3 반품 거부(철회)

반품 요청을 거부(철회)한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/return/reject
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `rejectReturnReason` | string | ✅ 필수 | 거부(철회) 사유 | `고객님께서 통화로 교환을 원하셨습니다.` |

---

### 5.4 반품 보류

반품 처리를 보류한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `holdbackClassType` | string(enum) | ✅ 필수 | 보류 유형 코드 | `RETURN_PRODUCT_NOT_DELIVERED` |
| `holdbackReturnDetailReason` | string | ✅ 필수 | 보류 상세 사유 | `미입고` |
| `extraReturnFeeAmount` | number | 선택 | 기타 반품 비용 | `0` |

**holdbackClassType enum:**

| 코드 | 설명 |
|------|------|
| `RETURN_DELIVERYFEE` | 반품 배송비 청구 |
| `EXTRAFEEE` | 추가 비용 청구 |
| `RETURN_DELIVERYFEE_AND_EXTRAFEEE` | 반품 배송비 + 추가 비용 청구 |
| `RETURN_PRODUCT_NOT_DELIVERED` | 반품 상품 미입고 |
| `ETC` | 기타 사유 |
| `EXCHANGE_DELIVERYFEE` | 교환 배송비 청구 |
| `EXCHANGE_EXTRAFEE` | 추가 교환 비용 청구 |
| `EXCHANGE_PRODUCT_READY` | 교환 상품 준비 중 |
| `EXCHANGE_PRODUCT_NOT_DELIVERED` | 교환 상품 미입고 |
| `EXCHANGE_HOLDBACK` | 교환 구매 확정 보류 |
| `SELLER_CONFIRM_NEED` | 판매자 확인 필요 |
| `PURCHASER_CONFIRM_NEED` | 구매자 확인 필요 |
| `SELLER_REMIT` | 판매자 직접 송금 |
| `ETC2` | 기타 |

---

### 5.5 반품 보류 해제

보류된 반품을 해제한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback/release
```

**Request Body:** 없음

---

## 6. 교환 API

### 6.1 교환 수거 완료

교환 건의 수거 완료를 처리한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/collect/approve
```

**Request Body:** 없음

> ⚠️ **URL 차이 주의:** 기존 코드에서 `approve-collected`로 되어있지만, 공식 문서는 `collect/approve`임.

---

### 6.2 교환 재배송 처리

교환 승인 건을 재배송 처리한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/dispatch
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `reDeliveryMethod` | string(enum) | 선택 | 배송 방법 코드 | `DELIVERY` |
| `reDeliveryCompany` | string(enum) | 선택 | 택배사 코드 | `CJGLS` |
| `reDeliveryTrackingNumber` | string | 선택 | 재배송 송장 번호 | `1111111115` |

> **참고:** 교환 재배송은 모든 필드가 선택이다. 하지만 실제 배송 처리를 위해서는 택배사+운송장번호가 필요.

---

### 6.3 교환 거부(철회)

교환 요청을 거부(철회)한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/reject
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `rejectExchangeReason` | string | ✅ 필수 | 교환 거부(철회) 사유 |

---

### 6.4 교환 보류

교환 처리를 보류한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback
```

**Request Body (application/json, 필수):**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `holdbackClassType` | string(enum) | ✅ 필수 | 보류 유형 코드 | `EXCHANGE_PRODUCT_NOT_DELIVERED` |
| `holdbackExchangeDetailReason` | string | ✅ 필수 | 보류 상세 사유 | `미입고 상태` |
| `extraExchangeFeeAmount` | number | 선택 | 기타 교환 비용 | `0` |

> **holdbackClassType enum:** 반품 보류와 동일한 enum 사용 (섹션 5.4 참조)

---

### 6.5 교환 보류 해제

보류된 교환을 해제한다.

```
POST /v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback/release
```

**Request Body:** 없음

---

## 7. 공통 구조체

### 7.1 변경 상품 주문 정보 구조체

`last-changed-statuses` API 응답에 사용되는 간소화된 주문 정보.

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | string | 주문 ID |
| `productOrderId` | string | 상품 주문 ID |
| `lastChangedType` | string(enum) | 최종 변경 구분 |
| `paymentDate` | string\<date-time\> | 결제 일시 |
| `lastChangedDate` | string\<date-time\> | 최종 변경 일시 |
| `productOrderStatus` | string(enum) | 상품 주문 상태 |
| `claimType` | string(enum) | 클레임 구분 (CANCEL/RETURN/EXCHANGE 등) |
| `claimStatus` | string(enum) | 클레임 상태 |
| `receiverAddressChanged` | boolean | 배송지 변경 여부 (기본값: false) |
| `giftReceivingStatus` | string(enum) | 선물 수락 상태 |

---

### 7.2 상품 주문 정보 구조체 (상세)

`product-orders/query` 및 `product-orders` API 응답에 사용되는 상세 주문 정보. 객체 1개 = 상품주문번호 1개.

#### data[].order (주문 레벨 정보)

| 필드 | 타입 | 설명 |
|------|------|------|
| `orderId` | string | 주문 ID |
| `orderDate` | string\<date-time\> | 주문 일시 |
| `paymentDate` | string\<date-time\> | 결제 일시 |
| `paymentDueDate` | string\<date-time\> | 결제 기한 일시 |
| `paymentMeans` | string | 결제 수단 |
| `ordererName` | string | 주문자명 |
| `ordererId` | string | 주문자 ID |
| `ordererTel` | string | 주문자 전화번호 |
| `ordererNo` | string | 주문자 번호 |
| `generalPaymentAmount` | integer | 일반 결제 금액 |
| `chargeAmountPaymentAmount` | integer | 충전금 결제 금액 |
| `checkoutAccumulationPaymentAmount` | integer | 체크아웃 적립금 결제 금액 |
| `naverMileagePaymentAmount` | integer | 네이버 마일리지 결제 금액 |
| `orderDiscountAmount` | integer | 주문 할인 금액 |
| `payLocationType` | string | 결제 위치 유형 |
| `payLaterPaymentAmount` | integer | 후불 결제 금액 |
| `isDeliveryMemoParticularInput` | string | 배송 메모 특이사항 입력 여부 |
| `isMembershipSubscribed` | boolean | 멤버십 가입 여부 |

#### data[].productOrder (상품주문 레벨 정보) - 핵심

| 필드 | 타입 | 설명 |
|------|------|------|
| `productOrderId` | string | 상품 주문 ID |
| `productOrderStatus` | string(enum) | 상품 주문 상태 |
| `claimType` | string(enum) | 클레임 구분 |
| `claimStatus` | string(enum) | 클레임 상태 |
| `claimId` | string | 클레임 ID |
| `productId` | string | 상품 ID |
| `originalProductId` | string | 원상품 ID |
| `groupProductId` | integer | 그룹 상품 ID |
| `productName` | string | 상품명 |
| `productOption` | string | 상품 옵션 |
| `optionCode` | string | 옵션 코드 |
| `optionPrice` | integer | 옵션 가격 |
| `optionManageCode` | string | 옵션 관리 코드 |
| `sellerProductCode` | string | 판매자 상품 코드 |
| `sellerCustomCode1` | string | 판매자 커스텀 코드 1 |
| `sellerCustomCode2` | string | 판매자 커스텀 코드 2 |
| `itemNo` | string | 아이템 번호 |
| `quantity` | integer | 수량 |
| `initialQuantity` | integer | 초기 수량 |
| `remainQuantity` | integer | 잔여 수량 |
| `unitPrice` | integer | 단가 |
| `totalPaymentAmount` | integer | 총 결제 금액 |
| `initialPaymentAmount` | integer | 초기 결제 금액 |
| `remainPaymentAmount` | integer | 잔여 결제 금액 |
| `totalProductAmount` | integer | 총 상품 금액 |
| `initialProductAmount` | integer | 초기 상품 금액 |
| `remainProductAmount` | integer | 잔여 상품 금액 |
| `productDiscountAmount` | integer | 상품 할인 금액 |
| `deliveryFeeAmount` | integer | 배송비 |
| `deliveryDiscountAmount` | integer | 배송비 할인 금액 |
| `sectionDeliveryFee` | integer | 구간 배송비 |
| `deliveryPolicyType` | string | 배송 정책 유형 |
| `deliveryFeeType` | string | 배송비 유형 |
| `expectedDeliveryMethod` | string(enum) | 예상 배송 방법 |
| `expectedDeliveryCompany` | string | 예상 배송 택배사 |
| `deliveryAttributeType` | string | 배송 속성 유형 |
| `deliveryTagType` | string | 배송 태그 유형 |
| `arrivalGuaranteeDate` | string\<date-time\> | 도착 보장일 |
| `shippingStartDate` | string\<date-time\> | 배송 시작일 |
| `shippingDueDate` | string\<date-time\> | 배송 기한일 |
| `shippingMemo` | string | 배송 메모 |
| `placeOrderDate` | string\<date-time\> | 발주 일시 |
| `placeOrderStatus` | string(enum) | 발주 상태 |
| `decisionDate` | string\<date-time\> | 구매 확정 일시 |
| `delayedDispatchReason` | string(enum) | 발송 지연 사유 |
| `delayedDispatchDetailedReason` | string | 발송 지연 상세 사유 |
| `freeGift` | string | 사은품 |
| `mallId` | string | 몰 ID |
| `merchantChannelId` | string | 판매자 채널 ID |
| `packageNumber` | string | 묶음 배송 번호 |
| `productClass` | string | 상품 유형 |
| `inflowPath` | string | 유입 경로 |
| `inflowPathAdd` | string | 유입 경로 추가 |
| `individualCustomUniqueCode` | string | 개인통관고유부호 |
| `taxType` | string | 과세 유형 |
| `storageType` | string | 보관 유형 |
| `commissionRatingType` | string | 수수료 등급 유형 |
| `commissionPrePayStatus` | string | 수수료 선결제 상태 |
| `paymentCommission` | integer | 결제 수수료 |
| `saleCommission` | integer | 판매 수수료 |
| `channelCommission` | integer | 채널 수수료 |
| `expectedSettlementAmount` | integer | 예상 정산 금액 |
| `sellerBurdenDiscountAmount` | integer | 판매자 부담 할인 금액 |
| `giftReceivingStatus` | string(enum) | 선물 수락 상태 |
| `logisticsCompanyId` | string | 물류사 ID |
| `logisticsCenterId` | string | 물류센터 ID |
| `logisticsDirectContracted` | boolean | 물류 직계약 여부 |
| `shippingAddress` | object | 배송지 주소 |
| `takingAddress` | object | 수취 주소 |
| `skuMappings` | object[] | SKU 매핑 정보 |
| `hopeDelivery` | object | 희망 배송 정보 |
| `appliedCoupons` | object[] | 적용 쿠폰 |
| `appliedCardPromotion` | object | 적용 카드 프로모션 |

#### data[].productOrder.shippingAddress (배송지)

| 필드 | 타입 | 설명 |
|------|------|------|
| `addressType` | string | 주소 유형 |
| `baseAddress` | string | 기본 주소 |
| `detailedAddress` | string | 상세 주소 |
| `city` | string | 시/도 |
| `state` | string | 주/도 |
| `country` | string | 국가 |
| `zipCode` | string | 우편번호 |
| `name` | string | 수령인명 |
| `tel1` | string | 전화번호 1 |
| `tel2` | string | 전화번호 2 |
| `isRoadNameAddress` | boolean | 도로명 주소 여부 |
| `pickupLocationType` | string(enum) | 수령 위치 유형 (`FRONT_OF_DOOR` 등) |
| `pickupLocationContent` | string | 수령 위치 내용 |
| `entryMethod` | string(enum) | 출입 방법 (`LOBBY_PW` 등) |
| `entryMethodContent` | string | 출입 방법 내용 |
| `buildingManagementNo` | string | 건물 관리 번호 |
| `longitude` | string | 경도 |
| `latitude` | string | 위도 |

#### data[].delivery (배송 정보)

| 필드 | 타입 | 설명 |
|------|------|------|
| `deliveryCompany` | string | 배송 택배사 |
| `deliveryMethod` | string(enum) | 배송 방법 |
| `deliveryStatus` | string(enum) | 배송 상태 |
| `trackingNumber` | string | 송장 번호 |
| `sendDate` | string\<date-time\> | 발송 일시 |
| `deliveredDate` | string\<date-time\> | 배송 완료 일시 |
| `pickupDate` | string\<date-time\> | 픽업 일시 |
| `isWrongTrackingNumber` | boolean | 송장 번호 오류 여부 |
| `wrongTrackingNumberRegisteredDate` | string\<date-time\> | 오류 등록 일시 |
| `wrongTrackingNumberType` | string | 오류 유형 |

#### data[].cancel / data[].return / data[].exchange

각 클레임 유형별 상세 정보. 섹션 7.2 상세 구조체 참조. 주요 필드:

**cancel:**
- `claimId`, `claimStatus`, `claimRequestDate`, `cancelReason`, `cancelDetailedReason`
- `cancelApprovalDate`, `cancelCompletedDate`, `requestQuantity`, `requestChannel`
- `refundExpectedDate`, `refundStandbyReason`, `refundStandbyStatus`

**return:**
- 위 cancel 필드 + 수거 관련 필드 (`collectDeliveryMethod/Company/TrackingNumber`, `collectStatus`, `collectCompletedDate`)
- 보류 관련 필드 (`holdbackStatus/Reason/DetailedReason`, `holdbackConfigDate/Configurer`, `holdbackReleaseDate/Releaser`)
- 반품 수령 주소 (`returnReceiveAddress`), 수거지 주소 (`collectAddress`)
- 배송비 관련 (`claimDeliveryFeeDemandAmount/PayMeans/PayMethod`, `etcFeeDemandAmount`)

**exchange:**
- return과 유사한 구조 + 재배송 관련 필드 (`reDeliveryMethod/Company/TrackingNumber/Status/Address`)

#### data[].currentClaim / data[].completedClaims[]

> ⚠️ **중요:** 최상위의 `cancel`, `return`, `exchange` 필드는 **2025년 상반기 중 제거 예정**.
> `currentClaim.cancel`, `currentClaim.return`, `currentClaim.exchange`를 사용 권장.
> `completedClaims[]`는 완료된 클레임 이력 배열.

---

### 7.3 주문-클레임 처리 반환 구조체 (공통 응답)

취소/반품/교환/발송 등 대부분의 처리 API에서 사용하는 공통 응답.

| 필드 | 타입 | 설명 |
|------|------|------|
| `timestamp` | string\<date-time\> | 응답 시각 |
| `traceId` | string | 추적 ID (필수) |
| `data.successProductOrderIds` | string[] | 성공한 상품주문번호 목록 |
| `data.failProductOrderInfos` | object[] | 실패한 상품주문 정보 |
| `data.failProductOrderInfos[].productOrderId` | string | 상품주문번호 |
| `data.failProductOrderInfos[].code` | string | 실패 코드 |
| `data.failProductOrderInfos[].message` | string | 실패 메시지 |

> ⚠️ **예외:** 발주 확인 API(`/confirm`)만 `successProductOrderInfos`(object[])를 사용하며, `isReceiverAddressChanged` 필드가 포함됨.

---

## 8. 시나리오별 API 사용 가이드

### 시나리오 1: 신규 주문 폴링 및 처리

```
1. GET  /last-changed-statuses?lastChangedType=PAYED&lastChangedFrom=...
   → productOrderId 목록 수집 (변경 건만)

2. POST /product-orders/query  {productOrderIds: [...]}
   → 상세 정보 일괄 조회

3. POST /product-orders/confirm  {productOrderIds: [...]}
   → 발주 확인 (최대 30건씩)
   → ⚠️ isReceiverAddressChanged 확인 필요!

4. POST /product-orders/dispatch  {dispatchProductOrders: [...]}
   → 발송 처리 (최대 30건씩, 운송장번호 등록)
```

### 시나리오 2: 구매자 취소 요청 → 판매자 승인

```
1. GET  /last-changed-statuses?lastChangedType=CLAIM_REQUESTED
   → claimType=CANCEL인 건 필터링

2. POST /{productOrderId}/claim/cancel/approve
   → 취소 승인 (Body 없음)
```

### 시나리오 3: 판매자 직접 취소

```
1. POST /{productOrderId}/claim/cancel/request
   {
     "cancelReason": "SOLD_OUT",
     "cancelDetailedReason": "재고 소진",
     "cancelQuantity": null  // 전체 취소
   }
   → 취소 요청 = 즉시 취소 처리됨 (판매자 직접 취소는 별도 승인 불필요)
```

### 시나리오 4: 반품 처리 (정상 흐름)

```
1. 구매자가 반품 요청 → CLAIM_REQUESTED 이벤트 발생

2. [선택] 판매자가 반품 보류:
   POST /{productOrderId}/claim/return/holdback
   {
     "holdbackClassType": "RETURN_PRODUCT_NOT_DELIVERED",
     "holdbackReturnDetailReason": "반품 상품 미입고 확인 중"
   }

3. [선택] 보류 해제:
   POST /{productOrderId}/claim/return/holdback/release

4. 수거 완료 후 반품 승인:
   POST /{productOrderId}/claim/return/approve (Body 없음)
```

### 시나리오 5: 반품 거부

```
POST /{productOrderId}/claim/return/reject
{
  "rejectReturnReason": "착용 흔적이 있어 반품 불가합니다."
}
```

### 시나리오 6: 판매자 직접 반품 요청

```
POST /{productOrderId}/claim/return/request
{
  "returnReason": "BROKEN",
  "collectDeliveryMethod": "RETURN_DESIGNATED",
  "collectDeliveryCompany": "CJGLS",
  "collectTrackingNumber": "D2485799470",
  "returnQuantity": 1
}
```

### 시나리오 7: 교환 처리 (정상 흐름)

```
1. 구매자가 교환 요청 → CLAIM_REQUESTED 이벤트

2. 수거 완료 확인:
   POST /{productOrderId}/claim/exchange/collect/approve (Body 없음)

3. 교환 재배송 처리:
   POST /{productOrderId}/claim/exchange/dispatch
   {
     "reDeliveryMethod": "DELIVERY",
     "reDeliveryCompany": "CJGLS",
     "reDeliveryTrackingNumber": "1111111115"
   }
```

### 시나리오 8: 교환 거부

```
POST /{productOrderId}/claim/exchange/reject
{
  "rejectExchangeReason": "착용한 상품은 교환할 수 없습니다."
}
```

### 시나리오 9: 교환/반품 보류 처리

```
POST /{productOrderId}/claim/exchange/holdback
{
  "holdbackClassType": "EXCHANGE_PRODUCT_NOT_DELIVERED",
  "holdbackExchangeDetailReason": "교환 상품 미입고 상태",
  "extraExchangeFeeAmount": 0
}

// 보류 해제
POST /{productOrderId}/claim/exchange/holdback/release (Body 없음)
```

### 시나리오 10: 발송 지연 처리

```
POST /{productOrderId}/delay
{
  "dispatchDueDate": "2022-06-05T12:17:35.000+09:00",
  "delayedDispatchReason": "PRODUCT_PREPARE",
  "dispatchDelayedDetailedReason": "맞춤 제작 상품으로 3일 소요"
}
```

---

## 9. 기존 클라이언트 코드 대조 결과

### ✅ 정합 (문제 없음)

| 항목 | 기존 코드 | 공식 문서 | 결과 |
|------|----------|----------|------|
| 취소 요청 URL | `/{productOrderId}/claim/cancel/request` | 동일 | ✅ |
| 취소 승인 URL | `/{productOrderId}/claim/cancel/approve` | 동일 | ✅ |
| 취소 Request Body | cancelReason, cancelDetailedReason, cancelQuantity | 동일 | ✅ |
| 반품 요청 URL | `/{productOrderId}/claim/return/request` | 동일 | ✅ |
| 반품 승인 URL | `/{productOrderId}/claim/return/approve` | 동일 | ✅ |
| 반품 거부 URL | `/{productOrderId}/claim/return/reject` | 동일 | ✅ |
| 반품 보류 URL | `/{productOrderId}/claim/return/holdback` | 동일 | ✅ |
| 반품 보류 해제 URL | `/{productOrderId}/claim/return/holdback/release` | 동일 | ✅ |
| 교환 재배송 URL | `/{productOrderId}/claim/exchange/dispatch` | 동일 | ✅ |
| 교환 거부 URL | `/{productOrderId}/claim/exchange/reject` | 동일 | ✅ |
| 교환 보류 URL | `/{productOrderId}/claim/exchange/holdback` | 동일 | ✅ |
| 교환 보류 해제 URL | `/{productOrderId}/claim/exchange/holdback/release` | 동일 | ✅ |
| 발주 확인 URL | `/product-orders/confirm` | 동일 | ✅ |
| 발송 처리 URL | `/product-orders/dispatch` | 동일 | ✅ |
| 발송 지연 URL | `/{productOrderId}/delay` | 동일 | ✅ |
| 배송 희망일 URL | `/{productOrderId}/hope-delivery/change` | 동일 | ✅ |
| 주문 목록 조회 URL | `/orders/{orderId}/product-order-ids` | 동일 | ✅ |
| 상품주문 상세 조회 URL | `/product-orders/query` | 동일 | ✅ |
| 변경 조회 URL | `/product-orders/last-changed-statuses` | 동일 | ✅ |
| NaverCancelRequest 필드 | cancelReason, cancelDetailedReason, cancelQuantity | 동일 | ✅ |
| NaverReturnRequest 필드 | returnReason, collectDeliveryMethod/Company/TrackingNumber, returnQuantity | 동일 | ✅ |
| NaverReturnRejectRequest 필드 | rejectReturnReason | 동일 | ✅ |
| NaverExchangeReDeliveryRequest 필드 | reDeliveryMethod, reDeliveryCompany, reDeliveryTrackingNumber | 동일 | ✅ |
| NaverExchangeRejectRequest 필드 | rejectExchangeReason | 동일 | ✅ |
| NaverOrderDispatchRequest 필드 | productOrderId, deliveryMethod, deliveryCompanyCode, trackingNumber, dispatchDate | 동일 | ✅ |
| NaverOrderDelayRequest 필드 | dispatchDueDate, delayedDispatchReason, dispatchDelayedDetailedReason | 동일 | ✅ |
| NaverWishedDeliveryDateRequest 필드 | hopeDeliveryYmd, hopeDeliveryHm, region, changeReason | 동일 | ✅ |
| NaverClaimResponse 구조 | successProductOrderIds, failProductOrderInfos[{productOrderId, code, message}] | 동일 | ✅ |
| 배치 크기 제한 | 확인 30건, 발송 30건, 조회 300건 | 동일 | ✅ |

### ⚠️ 차이점 / 개선 필요 사항

| # | 항목 | 기존 코드 | 공식 문서 | 심각도 | 설명 |
|---|------|----------|----------|--------|------|
| 1 | **교환 수거완료 URL** | `approve-collected` | `collect/approve` | 🔴 **검증 필요** | 기존 코드: `.../exchange/approve-collected`, 공식 문서: `.../exchange/collect/approve`. URL 경로가 다름. **실제 호출 테스트로 확인 필요** (네이버가 두 가지 모두 지원할 수 있음) |
| 2 | **반품 보류 Request Body 누락** | Body 없이 호출 | holdbackClassType(필수), holdbackReturnDetailReason(필수), extraReturnFeeAmount(선택) | 🔴 **버그** | 기존 `holdbackReturn()` 메서드가 Body 없이 POST만 호출. 공식 문서상 3개 필드(2개 필수)의 Request Body가 필요 |
| 3 | **교환 보류 Request Body 누락** | Body 없이 호출 | holdbackClassType(필수), holdbackExchangeDetailReason(필수), extraExchangeFeeAmount(선택) | 🔴 **버그** | 기존 `holdbackExchange()` 메서드가 Body 없이 POST만 호출. 공식 문서상 3개 필드(2개 필수)의 Request Body가 필요 |
| 4 | **발주 확인 응답 구조 차이** | `NaverClaimResponse`로 수신 (void 반환) | `successProductOrderInfos`(object[] + isReceiverAddressChanged) | 🟡 **개선** | 발주 확인 API만 응답 구조가 다름. `isReceiverAddressChanged` 필드를 활용하지 못하고 있음. 주소 변경 감지에 활용 가능 |
| 5 | **조건형 조회 API 파라미터 불완전** | productOrderStatus, lastChangedFrom/To만 사용 | rangeType, claimStatuses, placeOrderStatusType, fulfillment, page, pageSize, quantityClaimCompatibility | 🟡 **개선** | 기존 `getProductOrdersConditional()`이 공식 문서의 파라미터를 일부만 사용. 필터링 기능 확장 가능 |
| 6 | **quantityClaimCompatibility 미사용** | 미전송 | 수량클레임 대응 완료 시 true | 🟡 **개선** | 수량 부분 취소/반품을 정확히 처리하려면 이 플래그를 true로 보내야 함 |
| 7 | **currentClaim 마이그레이션** | 최상위 cancel/return/exchange 사용 | `currentClaim` 하위로 이동 권장, 최상위는 2025 상반기 제거 예정 | 🟡 **예정** | 응답 파싱 시 `currentClaim.cancel` 등으로 전환 필요 |

### 요약

- **전체적으로 기존 클라이언트 코드는 공식 문서와 높은 수준의 정합성을 보임**
- **🔴 반드시 수정 필요:** 반품/교환 보류 API의 Request Body 누락 (2건)
- **🔴 검증 필요:** 교환 수거완료 URL 경로 차이 (1건)
- **🟡 개선 권장:** 발주확인 응답 파싱, 조건형 조회 파라미터 확장, quantityClaimCompatibility 플래그, currentClaim 마이그레이션 (4건)
