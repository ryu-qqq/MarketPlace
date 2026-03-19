# OMS 도메인 로직 검증 분석서

## 1. 도메인 상태 전이 맵 (State Machine)

### 1.1 Order (주문 Aggregate)
```
ORDERED → PREPARING → SHIPPED → DELIVERED → CONFIRMED
                                          ↘ CLAIM_IN_PROGRESS → REFUNDED
                                                              → EXCHANGED
ORDERED → CANCELLED (배송 전 취소)
PREPARING → CANCELLED (배송 전 취소)
SHIPPED → CLAIM_IN_PROGRESS (배송 중 클레임)
DELIVERED → CLAIM_IN_PROGRESS (배송 완료 후 클레임)
DELIVERED → CONFIRMED (구매확정)
```

**허용 전이 규칙:**
| From | To |
|------|-----|
| ORDERED | PREPARING, CANCELLED |
| PREPARING | SHIPPED, CANCELLED |
| SHIPPED | DELIVERED, CLAIM_IN_PROGRESS |
| DELIVERED | CONFIRMED, CLAIM_IN_PROGRESS |
| CLAIM_IN_PROGRESS | REFUNDED, EXCHANGED |

### 1.2 OrderItem (주문상품)
```
READY → CONFIRMED
READY → CANCELLED
CONFIRMED → CANCELLED
CONFIRMED → RETURN_REQUESTED → RETURNED
```

### 1.3 Cancel (취소)
```
REQUESTED → APPROVED → COMPLETED
REQUESTED → REJECTED
REQUESTED → CANCELLED (구매자 철회)
```
- **BUYER_CANCEL**: REQUESTED 상태로 생성
- **SELLER_CANCEL**: APPROVED 상태로 즉시 생성 (승인 절차 스킵)

### 1.4 RefundClaim (환불)
```
REQUESTED → COLLECTING → COLLECTED → COMPLETED
REQUESTED → REJECTED
REQUESTED → CANCELLED
COLLECTING → REJECTED
COLLECTING → CANCELLED
COLLECTED → REJECTED
```

### 1.5 ExchangeClaim (교환)
```
REQUESTED → COLLECTING → COLLECTED → PREPARING → SHIPPING → COMPLETED
REQUESTED → REJECTED
REQUESTED → CANCELLED
COLLECTING → CANCELLED
COLLECTED → REJECTED
PREPARING → REJECTED
```

---

## 2. OMS 시나리오 전체 나열 + 도메인 매핑

### 카테고리 A: 정상 주문 처리 흐름

| # | 시나리오 | Order | OrderItem | Claim | 처리 주체 |
|---|---------|-------|-----------|-------|-----------|
| A-1 | 신규 주문 접수 | ORDERED | READY | - | CreateOrderService |
| A-2 | 상품 준비 시작 | PREPARING | - | - | PrepareOrderService |
| A-3 | 배송 출발 | SHIPPED | - | - | ShipOrderService |
| A-4 | 배송 완료 | DELIVERED | - | - | DeliverOrderService |
| A-5 | 구매 확정 | CONFIRMED | CONFIRMED | - | ConfirmOrderService |

### 카테고리 B: 취소 시나리오

| # | 시나리오 | Order | OrderItem | Cancel | 처리 주체 |
|---|---------|-------|-----------|--------|-----------|
| B-1 | 구매자 배송 전 취소 요청 (ORDERED) | CANCELLED | CANCELLED | REQUESTED | CancelOrderService → Cancel 생성 |
| B-2 | 구매자 배송 전 취소 요청 (PREPARING) | CANCELLED | CANCELLED | REQUESTED | CancelOrderService → Cancel 생성 |
| B-3 | 판매자 취소 (재고 부족 등) | - | - | APPROVED(직접) | SellerCancelBatchService |
| B-4 | 관리자 취소 승인 | - | - | REQUESTED→APPROVED | ApproveCancelBatchService |
| B-5 | 관리자 취소 거절 | - | - | REQUESTED→REJECTED | RejectCancelBatchService |
| B-6 | 취소 완료 (환불 처리) | - | - | APPROVED→COMPLETED | Outbox→SQS→ExecuteCancelOutboxService |
| B-7 | 구매자 취소 철회 | - | - | REQUESTED→CANCELLED | Cancel.withdraw() |

### 카테고리 C: 환불 시나리오

| # | 시나리오 | Order | RefundClaim | 처리 주체 |
|---|---------|-------|-------------|-----------|
| C-1 | 배송 완료 후 환불 요청 | CLAIM_IN_PROGRESS | REQUESTED | StartClaimService + RequestRefundBatchService |
| C-2 | 수거 시작 (승인) | - | COLLECTING | ApproveRefundBatchService |
| C-3 | 수거 완료 | - | COLLECTED | (미구현?) |
| C-4 | 환불 완료 | REFUNDED | COMPLETED | CompleteRefundService |
| C-5 | 환불 거절 | - | REJECTED | RejectRefundBatchService |
| C-6 | 환불 취소 (구매자 철회) | - | CANCELLED | RefundClaim.cancel() |
| C-7 | 환불 보류 | - | holdInfo 설정 | RefundClaim.hold() |
| C-8 | 환불 보류 해제 | - | holdInfo 제거 | RefundClaim.releaseHold() |

### 카테고리 D: 교환 시나리오

| # | 시나리오 | Order | ExchangeClaim | 처리 주체 |
|---|---------|-------|---------------|-----------|
| D-1 | 배송 완료 후 교환 요청 | CLAIM_IN_PROGRESS | REQUESTED | StartClaimService + RequestExchangeBatchService |
| D-2 | 수거 시작 (승인) | - | COLLECTING | ApproveExchangeBatchService |
| D-3 | 수거 완료 | - | COLLECTED | CollectExchangeBatchService |
| D-4 | 교환상품 준비 | - | PREPARING | PrepareExchangeBatchService |
| D-5 | 교환상품 발송 | - | SHIPPING | ShipExchangeBatchService |
| D-6 | 교환 완료 | EXCHANGED | COMPLETED | CompleteExchangeBatchService |
| D-7 | 교환 거절 | - | REJECTED | RejectExchangeBatchService |
| D-8 | 교환 취소 | - | CANCELLED | ExchangeClaim.cancel() |
| D-9 | 교환→환불 전환 | - | CANCELLED + Refund REQUESTED | ConvertToRefundBatchService |

### 카테고리 E: 외부 채널 동기화 (ClaimSync)

| # | 시나리오 | 처리 주체 |
|---|---------|-----------|
| E-1 | 외부 취소 요청 수신 → Cancel 생성 | CancelClaimSyncHandler |
| E-2 | 외부 취소 진행 수신 → Cancel 승인 | CancelClaimSyncHandler |
| E-3 | 외부 취소 완료 수신 → Cancel 완료 (중간상태 건너뜀) | CancelClaimSyncHandler |
| E-4 | 외부 취소 거절 수신 → Cancel 철회 | CancelClaimSyncHandler |
| E-5 | 외부 환불 수신 → RefundClaim 동기화 | RefundClaimSyncHandler |
| E-6 | 외부 교환 수신 → ExchangeClaim 동기화 | ExchangeClaimSyncHandler |

### 카테고리 F: Outbox 패턴 (외부 동기화 발행)

| # | 시나리오 | 처리 주체 |
|---|---------|-----------|
| F-1 | 취소 Outbox PENDING → PROCESSING → SQS 발행 | CancelOutboxRelayProcessor |
| F-2 | 환불 Outbox PENDING → PROCESSING → SQS 발행 | RefundOutboxRelayProcessor |
| F-3 | 교환 Outbox PENDING → PROCESSING → SQS 발행 | ExchangeOutboxRelayProcessor |
| F-4 | Outbox 실패 → 재시도 (3회 max) → FAILED | CancelOutbox.failAndRetry() |
| F-5 | Outbox 타임아웃 복구 | RecoverTimeoutCancelOutboxService 등 |
| F-6 | Outbox 수동 재처리 | CancelOutbox.retry() |

---

## 3. 발견된 논리적 이슈 / 잠재 버그

### 🔴 Critical

#### ISSUE-1: Order↔Claim 상태 동기화 갭
**문제**: Order.cancel()은 OrderStatus를 CANCELLED로 바꾸지만, Cancel Aggregate는 별도로 생성됨.
`CancelOrderService`는 Order만 취소하고, Cancel을 생성하지 않음.
`SellerCancelBatchService`는 Cancel만 생성하고, Order 상태를 변경하지 않음.

**영향**:
- Order는 CANCELLED인데 Cancel 기록이 없는 경우 발생 가능
- Cancel이 COMPLETED인데 Order가 여전히 ORDERED/PREPARING인 경우 발생 가능

**시나리오**:
```
1. 관리자가 CancelOrderService로 주문 취소 → Order.CANCELLED, Cancel 기록 없음
2. 판매자가 SellerCancelBatchService로 취소 → Cancel.APPROVED 생성, Order는 그대로
```

#### ISSUE-2: Refund/Exchange 완료 시 Order 상태 전환 트리거 불명확
**문제**: `CompleteRefundService`와 `CompleteExchangeService`는 Order의 상태를 REFUNDED/EXCHANGED로 바꾸지만, 이 서비스가 언제 호출되는지 명확한 트리거가 없음.
- RefundClaim.complete()와 Order.completeRefund()가 별도 서비스에서 독립적으로 호출됨
- 누가, 언제 `CompleteRefundUseCase`를 호출하는가?

**영향**: RefundClaim은 COMPLETED인데 Order가 여전히 CLAIM_IN_PROGRESS에 머물 수 있음

#### ISSUE-3: 교환→환불 전환 시 Order 상태 미변경
**문제**: `ConvertToRefundBatchService`가 ExchangeClaim을 CANCELLED 처리하고 새 RefundClaim을 생성하지만, Order 상태는 CLAIM_IN_PROGRESS에서 변경되지 않음. 이건 의도된 것일 수 있지만, 새 RefundClaim이 완료되었을 때 Order가 REFUNDED로 전환되는 흐름이 보장되는지 확인 필요.

### 🟡 Warning

#### ISSUE-4: 배송 중 클레임과 취소의 중복 가능성
**문제**: SHIPPED 상태의 Order에서:
- `CLAIMABLE = EnumSet.of(DELIVERED, SHIPPED)` → startClaim() 가능
- `CANCELLABLE = EnumSet.of(ORDERED, PREPARING)` → cancel() 불가

하지만 외부 채널에서는 배송 중에도 취소가 가능할 수 있음. CancelClaimSyncHandler에서 외부 취소를 수신할 때 Order 상태를 변경하지 않으므로, 외부에서 SHIPPED 주문 취소가 들어오면 Cancel은 생성되지만 Order 상태는 변경 안 됨.

#### ISSUE-5: OrderItem과 Claim 간 1:1 제약 미검증
**문제**: Cancel/Refund/Exchange 모두 OrderItemId로 1건을 처리하는 구조. 하지만:
- 같은 OrderItem에 대해 Cancel → 거절 → 다시 Refund 요청이 가능한가?
- 같은 OrderItem에 대해 Cancel과 Refund가 동시에 존재할 수 있는가?
- 이를 방지하는 검증이 보이지 않음 (RequestRefundBatchService에 OrderItem 상태 검증 없음)

**영향**: 같은 주문상품에 대해 취소와 환불이 동시에 진행될 수 있음

#### ISSUE-6: OrderItem 상태가 Claim 처리와 연동되지 않음
**문제**: OrderItem에는 cancel(), requestReturn(), completeReturn() 메서드가 있지만, 실제 Cancel/Refund/Exchange 서비스에서 OrderItem 상태를 변경하는 코드가 없음.
- SellerCancelBatchService: Cancel만 생성, OrderItem.cancel() 미호출
- RequestRefundBatchService: RefundClaim만 생성, OrderItem.requestReturn() 미호출

**영향**: OrderItem은 항상 READY 상태에 머물 수 있음

#### ISSUE-7: 환불 수거 완료(COLLECTED) 전환 서비스 미확인
**문제**: RefundStatus에는 COLLECTING → COLLECTED 전이가 정의되어 있고, RefundClaim.completeCollection()도 존재하지만, 이를 호출하는 배치 서비스가 보이지 않음 (ApproveRefundBatch는 COLLECTING으로 전환하는데, COLLECTED로 전환하는 서비스는?). Exchange는 CollectExchangeBatchService가 있지만 Refund에는 대응하는 서비스가 없음.

### 🟢 Minor / 설계 의도 확인 필요

#### ISSUE-8: CancelClaimSyncHandler에서 completeCancel 시 이벤트 중복
**문제**: 외부 채널에서 CANCEL_DONE 수신 시 Cancel이 없으면 forBuyerCancel(REQUESTED) → approve() → complete() 연쇄 호출. 이 과정에서 CancelCreatedEvent, CancelStatusChangedEvent, CancelApprovedEvent, CancelCompletedEvent 등 대량의 이벤트가 발생하지만, persist() 시 이벤트가 실제로 소비되는지 확인 필요.

#### ISSUE-9: Outbox Relay에서 Instant.now() 직접 사용
**문제**: `CancelOutboxRelayProcessor.relay()` 메서드에서 `Instant.now()`를 직접 사용 (line 53). APP-TIM-001 규칙 위반 가능성 (TimeProvider 사용 의무).

#### ISSUE-10: 다건 OrderItem 주문의 부분 클레임 처리
**문제**: Order Aggregate가 여러 OrderItem을 포함하지만, Order 상태 전이는 주문 단위. 3개 상품 중 1개만 환불 요청 시:
- Order는 CLAIM_IN_PROGRESS로 전환
- 나머지 2개 상품의 처리가 블로킹됨 (CONFIRMED 전환 불가: CLAIM_IN_PROGRESS에서 CONFIRMED 전이 미허용)

---

## 4. 시나리오별 도메인 흐름 상세 (크로스 도메인)

### 전체 취소 흐름 (구매자 취소)
```
1. [Order] CancelOrderService → Order.cancel() → CANCELLED
   ⚠️ Cancel Aggregate 미생성 (ISSUE-1)

   또는

1. [Cancel] SellerCancelBatchService → Cancel.forBuyerCancel() → REQUESTED + Outbox
2. [Cancel] ApproveCancelBatchService → Cancel.approve() → APPROVED + Outbox
3. [Outbox] CancelOutboxRelayProcessor → SQS 발행
4. [SQS] ExecuteCancelOutboxService → 외부 채널 동기화
5. [Cancel] (완료 처리?) → Cancel.complete() → COMPLETED
   ⚠️ Order 상태 CANCELLED 전환 미연동 (ISSUE-1)
```

### 전체 환불 흐름
```
1. [Order] StartClaimService → Order.startClaim() → CLAIM_IN_PROGRESS
2. [Refund] RequestRefundBatchService → RefundClaim.forNew() → REQUESTED + Outbox
3. [Refund] ApproveRefundBatchService → RefundClaim.startCollecting() → COLLECTING + Outbox
4. [Refund] (수거 완료 서비스?) → RefundClaim.completeCollection() → COLLECTED ⚠️ ISSUE-7
5. [Refund] (환불 완료) → RefundClaim.complete() → COMPLETED
6. [Order] CompleteRefundService → Order.completeRefund() → REFUNDED ⚠️ ISSUE-2
```

### 전체 교환 흐름
```
1. [Order] StartClaimService → Order.startClaim() → CLAIM_IN_PROGRESS
2. [Exchange] RequestExchangeBatchService → ExchangeClaim.forNew() → REQUESTED + Outbox
3. [Exchange] ApproveExchangeBatchService → ExchangeClaim.startCollecting() → COLLECTING + Outbox
4. [Exchange] CollectExchangeBatchService → ExchangeClaim.completeCollection() → COLLECTED
5. [Exchange] PrepareExchangeBatchService → ExchangeClaim.startPreparing() → PREPARING
6. [Exchange] ShipExchangeBatchService → ExchangeClaim.startShipping() → SHIPPING
7. [Exchange] CompleteExchangeBatchService → ExchangeClaim.complete() → COMPLETED
8. [Order] CompleteExchangeService → Order.completeExchange() → EXCHANGED ⚠️ ISSUE-2
```

### 교환→환불 전환 흐름
```
1. [Exchange] ConvertToRefundBatchService
   → ExchangeClaim.cancel() → CANCELLED
   → RequestRefundBatchUseCase.execute() → new RefundClaim REQUESTED
2. [Refund] 이후 일반 환불 흐름 진행
   ⚠️ Order 상태는 CLAIM_IN_PROGRESS 유지 (ISSUE-3)
```

---

## 5. 검증 우선순위 권장

| 우선순위 | 이슈 | 검증 방법 |
|---------|------|----------|
| P0 | ISSUE-1: Order↔Cancel 동기화 갭 | 통합 테스트: 전체 취소 흐름에서 Order+Cancel 양쪽 상태 확인 |
| P0 | ISSUE-5: 동일 OrderItem 중복 클레임 | 단위 테스트: 같은 OrderItem에 Cancel+Refund 동시 생성 시나리오 |
| P0 | ISSUE-6: OrderItem 상태 미연동 | 코드 리뷰: 클레임 서비스에서 OrderItem 상태 변경 의도 확인 |
| P1 | ISSUE-2: Refund/Exchange 완료 → Order 전환 트리거 | 통합 테스트: Claim 완료 → Order 상태 최종 확인 |
| P1 | ISSUE-7: Refund COLLECTED 전환 서비스 누락 | 코드 검색: 해당 서비스 존재 여부 확인 |
| P1 | ISSUE-10: 부분 클레임 시 Order 블로킹 | 단위 테스트: 다건 주문의 1건 클레임 → 나머지 처리 가능 여부 |
| P2 | ISSUE-4: 외부 채널 배송 중 취소 | 시나리오 테스트: SHIPPED Order의 외부 취소 수신 |
| P2 | ISSUE-9: Instant.now() 직접 사용 | 코드 리뷰 |
