# 주문 레거시 이관 계획

## 현황 요약

### 데이터 규모 (luxurydb → market)

| 구분 | 건수 | 비고 |
|------|------|------|
| 전체 주문 | 23,527 | delete_yn='N' |
| 유효 주문 (ORDER_FAILED 제외) | 22,716 | 이관 대상 |
| 외부몰 주문 (네이버/셀릭 경유) | 15,376 | SEWON + OCO + BUYMA + LF |
| 자사몰 주문 (세토프/OUR_MALL) | 7,340 | OUR_MALL + SEWON(자사) + SETOF |
| 배송 데이터 | 22,606 | shipment |
| 결제 데이터 | 23,461 | payment + payment_bill |
| 주문 이력 | 129,191 | orders_history |
| 상품 매핑 완료 | 3,741/4,017 (93%) | legacy_product_id_mappings |
| 상품 매핑 미완료 | 276개 상품 (1,341건 주문) | 매핑 필요 |
| market DB 기존 주문 | 0건 | 깨끗한 상태 |

### 핵심 발견: 주문번호 매핑

```
셀릭 ORDER_ID     = 네이버 orderId           (주문 단위)
셀릭 ORDER_SUB_ID = 네이버 productOrderId    (상품주문 단위)
luxurydb EXTERNAL_ORDER_PK_ID = 셀릭 ORDER_ID = 네이버 orderId ✅
```

셀릭이 네이버 주문번호를 그대로 사용하므로, luxurydb에서 바로 네이버 원본 주문번호 확보 가능.

### 레거시 주문 상태 → 내부 도메인 매핑

| 레거시 상태 | 건수 | → Order 상태 | → Delivery 상태 |
|------------|------|-------------|----------------|
| SETTLEMENT_COMPLETED | 16,916 | CONFIRMED | DELIVERED |
| SETTLEMENT_PROCESSING | 3,138 | DELIVERED | DELIVERED |
| SALE_CANCELLED_COMPLETED | 1,655 | CANCELLED | - |
| RETURN_REQUEST_COMPLETED | 353 | CLAIM_IN_PROGRESS → REFUNDED | DELIVERED |
| ORDER_PROCESSING | 304 | PREPARING | READY |
| CANCEL_REQUEST_COMPLETED | 181 | CANCELLED | - |
| DELIVERY_COMPLETED | 45 | DELIVERED | DELIVERED |
| SALE_CANCELLED | 39 | CANCELLED | - |
| DELIVERY_PROCESSING | 32 | SHIPPED | IN_TRANSIT |
| DELIVERY_PENDING | 22 | PREPARING | READY |
| ORDER_COMPLETED | 9 | ORDERED | READY |
| RETURN_REQUEST_REJECTED | 9 | DELIVERED | DELIVERED |
| RETURN_REQUEST | 8 | CLAIM_IN_PROGRESS | DELIVERED |
| CANCEL_REQUEST_CONFIRMED | 5 | CANCELLED | - |

---

## 이관 전략

### Phase 0: 사전 준비

#### 0-1. 상품 매핑 보완
- 매핑 안 된 276개 상품 (1,341건 주문) 처리
- legacy_product_id_mappings에 누락 상품 추가
- 이미 삭제된 상품이면 이관 제외 목록에 등록

#### 0-2. 도메인 Port/Adapter 완성
현재 Cancel/Refund/Exchange 도메인 Aggregate는 있지만 Application Port가 없음
- CancelCommandPort / CancelQueryPort
- RefundClaimCommandPort / RefundClaimQueryPort
- ExchangeClaimCommandPort / ExchangeClaimQueryPort
- 각 Persistence Adapter 구현

#### 0-3. InboundOrder 파이프라인 완성
- InboundOrder → Order 변환 서비스 구현
- 상품 매핑 (resolved_product_id 등) 자동화
- 셀러 할당 로직

---

### Phase 1: 완결 주문 이관 (벌크, 약 20,000건)

완결된 주문(SETTLEMENT_COMPLETED, SETTLEMENT_PROCESSING, DELIVERY_COMPLETED)부터 이관.
이 주문들은 상태 변경이 없으므로 안전.

#### 이관 소스 테이블 → 타겟 매핑

```
luxurydb.orders + payment + payment_bill + shipment + payment_snapshot_shipping_address
    ↓
market.orders              (주문 본체)
market.order_items         (주문 상품 - 1:1, luxurydb는 주문당 상품 1개)
market.order_histories     (주문 이력 - luxurydb.orders_history에서)
```

#### 필드 매핑

**orders → market.orders**
```
id                  = UUIDv7 신규 생성
order_number        = "ORD-" + INSERT_DATE 기반 생성
status              = 레거시 상태 → 내부 상태 변환
buyer_name          = payment_bill.BUYER_NAME
buyer_email         = payment_bill.BUYER_EMAIL
buyer_phone         = payment_bill.BUYER_PHONE_NUMBER
sales_channel_id    = payment.SITE_NAME 기반 (SEWON→2, OUR_MALL→1)
shop_id             = sales_channel 기반
external_order_no   = external_order.EXTERNAL_ORDER_PK_ID (= 네이버 orderId)
external_ordered_at = orders.INSERT_DATE
shop_code           = payment.SITE_NAME
```

**orders → market.order_items**
```
id                  = UUIDv7 신규 생성
order_id            = 위에서 생성한 orders.id
product_group_id    = legacy_product_id_mappings.internal_product_group_id
product_id          = legacy_product_id_mappings.internal_product_id
seller_id           = orders.SELLER_ID → seller 매핑 필요
unit_price          = orders.ORDER_AMOUNT / orders.QUANTITY
quantity            = orders.QUANTITY
total_amount        = orders.ORDER_AMOUNT
receiver_name       = payment_snapshot_shipping_address.RECEIVER_NAME
receiver_phone      = payment_snapshot_shipping_address.PHONE_NUMBER
receiver_address    = ADDRESS_LINE1
receiver_zipcode    = ZIP_CODE
delivery_status     = shipment.DELIVERY_STATUS → 내부 상태 변환
shipment_company_code = shipment.COMPANY_CODE
invoice             = shipment.INVOICE_NO
external_product_id = external_order.EXTERNAL_IDX (셀릭 상품 PK)
external_option_name = order_snapshot_product_option 참조
```

#### 실행 방식
- Spring Batch Job으로 구현
- chunk size: 500건
- 읽기: luxurydb (읽기 전용)
- 쓰기: market DB
- 멱등성: external_order_no + order_number 기준 중복 체크
- 트랜잭션: chunk 단위

---

### Phase 2: 취소/반품 주문 이관 (약 2,250건)

SALE_CANCELLED_COMPLETED, CANCEL_REQUEST_COMPLETED, RETURN_REQUEST_COMPLETED 등

#### 추가 매핑
```
luxurydb.orders (취소 상태)
    ↓
market.orders          (status = CANCELLED)
market.order_cancels   (Cancel Aggregate 저장)
market.order_items     (주문 상품)
```

- 취소 사유: orders_history.CHANGE_REASON 참조
- 환불 정보: payment_bill 기반으로 CancelRefundInfo 생성
- 반품: RETURN_REQUEST_COMPLETED → RefundClaim으로 변환

---

### Phase 3: 진행 중 주문 이관 (약 366건)

ORDER_PROCESSING, DELIVERY_PROCESSING, DELIVERY_PENDING, RETURN_REQUEST 등
상태가 변할 수 있는 주문이므로 신중하게 처리.

#### 전략
1. 이관 시점에 luxurydb에서 최신 상태 확인
2. 외부몰 주문은 네이버 API로 현재 상태 교차 검증
3. 이관 후 ShipmentOutbox 생성하여 외부 채널 동기화 유지

---

### Phase 4: 신규 주문 파이프라인 전환

레거시 이관 완료 후, 신규 주문 흐름을 전환:

#### 4-1. 외부몰 (네이버) 신규 주문
```
네이버 커머스 API → fetchNewOrders()
    ↓
InboundOrder (RECEIVED)
    ↓ 상품 매핑
InboundOrder (MAPPED)
    ↓ Order 변환
Order + OrderItem (ORDERED)
    ↓
ShipmentOutbox → 네이버 발주확인/발송 동기화
```

#### 4-2. 자사몰 (세토프) 신규 주문
```
세토프 주문 API → 직접 Order 생성
    ↓
Order + OrderItem (ORDERED)
```

#### 4-3. 셀릭 경유 중단
- 네이버 주문을 셀릭 거치지 않고 직접 폴링
- 셀릭 → luxurydb 경로 비활성화

---

## 우선순위 및 의존성

```
[Phase 0] 사전 준비
  ├─ 0-1. 상품 매핑 보완 ← 바로 시작 가능
  ├─ 0-2. Cancel/Refund/Exchange Port 완성 ← Phase 2 전에 필요
  └─ 0-3. InboundOrder 파이프라인 ← Phase 4 전에 필요

[Phase 1] 완결 주문 이관 (20,000건)
  └─ 의존: 상품 매핑 완료 (0-1)

[Phase 2] 취소/반품 이관 (2,250건)
  └─ 의존: Phase 1 + Cancel/Refund Port (0-2)

[Phase 3] 진행 중 주문 이관 (366건)
  └─ 의존: Phase 2 + 네이버 API 교차 검증

[Phase 4] 신규 주문 전환
  └─ 의존: Phase 3 + InboundOrder 파이프라인 (0-3)
```

---

## 리스크 및 대응

| 리스크 | 영향 | 대응 |
|--------|------|------|
| 상품 매핑 미완료 276건 | 1,341건 주문 이관 불가 | 매핑 보완 or 이관 제외 |
| 셀러 매핑 부재 | order_items.seller_id 설정 불가 | luxurydb seller → market seller 매핑 테이블 필요 |
| 진행 중 주문 상태 불일치 | 이관 중 상태 변경 시 충돌 | 네이버 API 교차 검증 + 이관 후 재동기화 |
| luxurydb 주문당 상품 1개 구조 | 네이버 1주문 N상품과 불일치 | external_order_no 기준 그룹핑으로 Order 생성 |
| payment_bill의 userId/mileage | market에서 제공 불가 | V4 API 간극 문서 대로 0/null 처리 |
