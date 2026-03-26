# Cancel / Refund / Exchange 기능 구현 계획

## 개요

도메인 Aggregate는 완전히 설계되어 있으나, Application/Persistence/API 레이어가 미구현.
새 테이블을 생성하여 도메인 구조와 1:1 매핑하는 A안으로 진행.

---

## Step 1: DB 스키마 (Flyway 마이그레이션)

### 1-1. cancels + cancel_items

```sql
CREATE TABLE cancels (
    id              VARCHAR(36) NOT NULL PRIMARY KEY,
    cancel_number   VARCHAR(50) NOT NULL UNIQUE,
    order_id        VARCHAR(36) NOT NULL,
    type            VARCHAR(20) NOT NULL,          -- BUYER_CANCEL, SELLER_CANCEL
    status          VARCHAR(20) NOT NULL,          -- REQUESTED, APPROVED, REJECTED, COMPLETED, CANCELLED
    reason_type     VARCHAR(50) NOT NULL,
    reason_detail   VARCHAR(500),
    refund_amount       INT,
    refund_method       VARCHAR(50),
    refund_status       VARCHAR(30),
    refunded_at         TIMESTAMP NULL,
    pg_refund_id        VARCHAR(100),
    requested_by    VARCHAR(100) NOT NULL,
    processed_by    VARCHAR(100),
    requested_at    TIMESTAMP NOT NULL,
    processed_at    TIMESTAMP NULL,
    completed_at    TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cancels_order_id (order_id),
    INDEX idx_cancels_status (status)
);

CREATE TABLE cancel_items (
    id              VARCHAR(36) NOT NULL PRIMARY KEY,
    cancel_id       VARCHAR(36) NOT NULL,
    order_item_id   VARCHAR(36) NOT NULL,
    cancel_qty      INT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_cancel_items_cancel_id (cancel_id),
    CONSTRAINT fk_cancel_items_cancel FOREIGN KEY (cancel_id) REFERENCES cancels(id)
);
```

### 1-2. refund_claims + refund_items

```sql
CREATE TABLE refund_claims (
    id                  VARCHAR(36) NOT NULL PRIMARY KEY,
    claim_number        VARCHAR(50) NOT NULL UNIQUE,
    order_id            VARCHAR(36) NOT NULL,
    status              VARCHAR(20) NOT NULL,      -- REQUESTED, COLLECTING, COLLECTED, COMPLETED, REJECTED, CANCELLED
    reason_type         VARCHAR(50) NOT NULL,
    reason_detail       VARCHAR(500),
    original_amount     INT,
    final_amount        INT,
    deduction_amount    INT,
    deduction_reason    VARCHAR(500),
    refund_method       VARCHAR(50),
    refunded_at         TIMESTAMP NULL,
    claim_shipment_id   VARCHAR(36),
    hold_reason         VARCHAR(500),
    hold_at             TIMESTAMP NULL,
    requested_by        VARCHAR(100) NOT NULL,
    processed_by        VARCHAR(100),
    requested_at        TIMESTAMP NOT NULL,
    processed_at        TIMESTAMP NULL,
    completed_at        TIMESTAMP NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_refund_claims_order_id (order_id),
    INDEX idx_refund_claims_status (status)
);

CREATE TABLE refund_items (
    id              VARCHAR(36) NOT NULL PRIMARY KEY,
    refund_claim_id VARCHAR(36) NOT NULL,
    order_item_id   VARCHAR(36) NOT NULL,
    refund_qty      INT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_refund_items_claim_id (refund_claim_id),
    CONSTRAINT fk_refund_items_claim FOREIGN KEY (refund_claim_id) REFERENCES refund_claims(id)
);
```

### 1-3. exchange_claims + exchange_items

```sql
CREATE TABLE exchange_claims (
    id                      VARCHAR(36) NOT NULL PRIMARY KEY,
    claim_number            VARCHAR(50) NOT NULL UNIQUE,
    order_id                VARCHAR(36) NOT NULL,
    status                  VARCHAR(20) NOT NULL,  -- REQUESTED, COLLECTING, COLLECTED, PREPARING, SHIPPING, COMPLETED, REJECTED, CANCELLED
    reason_type             VARCHAR(50) NOT NULL,
    reason_detail           VARCHAR(500),
    target_product_group_id BIGINT,
    target_product_id       BIGINT,
    target_sku_code         VARCHAR(50),
    target_quantity         INT,
    original_price          INT,
    target_price            INT,
    price_difference        INT,
    additional_payment_required TINYINT(1) DEFAULT 0,
    partial_refund_required     TINYINT(1) DEFAULT 0,
    collect_shipping_fee    INT DEFAULT 0,
    reship_shipping_fee     INT DEFAULT 0,
    total_shipping_fee      INT DEFAULT 0,
    shipping_fee_payer      VARCHAR(10),           -- BUYER, SELLER
    claim_shipment_id       VARCHAR(36),
    linked_order_id         VARCHAR(36),
    requested_by            VARCHAR(100) NOT NULL,
    processed_by            VARCHAR(100),
    requested_at            TIMESTAMP NOT NULL,
    processed_at            TIMESTAMP NULL,
    completed_at            TIMESTAMP NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_exchange_claims_order_id (order_id),
    INDEX idx_exchange_claims_status (status)
);

CREATE TABLE exchange_items (
    id                  VARCHAR(36) NOT NULL PRIMARY KEY,
    exchange_claim_id   VARCHAR(36) NOT NULL,
    order_item_id       VARCHAR(36) NOT NULL,
    exchange_qty        INT NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_exchange_items_claim_id (exchange_claim_id),
    CONSTRAINT fk_exchange_items_claim FOREIGN KEY (exchange_claim_id) REFERENCES exchange_claims(id)
);
```

### 1-4. claim_shipments

```sql
CREATE TABLE claim_shipments (
    id                  VARCHAR(36) NOT NULL PRIMARY KEY,
    status              VARCHAR(20) NOT NULL,      -- PENDING, IN_TRANSIT, DELIVERED, FAILED
    method_type         VARCHAR(20),               -- COURIER, QUICK, VISIT
    courier_code        VARCHAR(50),
    courier_name        VARCHAR(100),
    tracking_number     VARCHAR(100),
    fee_amount          INT DEFAULT 0,
    fee_payer           VARCHAR(10),                -- BUYER, SELLER
    fee_include_in_package TINYINT(1) DEFAULT 0,
    sender_name         VARCHAR(100),
    sender_phone        VARCHAR(20),
    sender_address      VARCHAR(500),
    sender_address_detail VARCHAR(500),
    sender_zipcode      VARCHAR(10),
    receiver_name       VARCHAR(100),
    receiver_phone      VARCHAR(20),
    receiver_address    VARCHAR(500),
    receiver_address_detail VARCHAR(500),
    receiver_zipcode    VARCHAR(10),
    shipped_at          TIMESTAMP NULL,
    received_at         TIMESTAMP NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## Step 2: Persistence 레이어

각 Aggregate별 동일한 구조:

### 2-1. Cancel Persistence
```
adapter-out/persistence-mysql/src/main/java/.../cancel/
├── entity/
│   ├── CancelJpaEntity.java
│   └── CancelItemJpaEntity.java
├── repository/
│   ├── CancelJpaRepository.java          (Spring Data JPA)
│   ├── CancelItemJpaRepository.java
│   └── CancelQueryDslRepository.java     (검색/조건 조회)
├── mapper/
│   └── CancelPersistenceMapper.java      (Domain ↔ Entity 변환)
└── adapter/
    ├── CancelCommandAdapter.java         (implements CancelCommandPort)
    └── CancelQueryAdapter.java           (implements CancelQueryPort)
```

### 2-2. Refund Persistence
```
adapter-out/persistence-mysql/src/main/java/.../refund/
├── entity/
│   ├── RefundClaimJpaEntity.java
│   └── RefundItemJpaEntity.java
├── repository/
│   ├── RefundClaimJpaRepository.java
│   ├── RefundItemJpaRepository.java
│   └── RefundClaimQueryDslRepository.java
├── mapper/
│   └── RefundPersistenceMapper.java
└── adapter/
    ├── RefundCommandAdapter.java         (implements RefundCommandPort)
    └── RefundQueryAdapter.java           (implements RefundQueryPort)
```

### 2-3. Exchange Persistence
```
adapter-out/persistence-mysql/src/main/java/.../exchange/
├── entity/
│   ├── ExchangeClaimJpaEntity.java
│   └── ExchangeItemJpaEntity.java
├── repository/
│   ├── ExchangeClaimJpaRepository.java
│   ├── ExchangeItemJpaRepository.java
│   └── ExchangeClaimQueryDslRepository.java
├── mapper/
│   └── ExchangePersistenceMapper.java
└── adapter/
    ├── ExchangeCommandAdapter.java       (implements ExchangeCommandPort)
    └── ExchangeQueryAdapter.java         (implements ExchangeQueryPort)
```

### 2-4. ClaimShipment Persistence
```
adapter-out/persistence-mysql/src/main/java/.../claim/
├── entity/
│   └── ClaimShipmentJpaEntity.java
├── repository/
│   └── ClaimShipmentJpaRepository.java
├── mapper/
│   └── ClaimShipmentPersistenceMapper.java
└── adapter/
    ├── ClaimShipmentCommandAdapter.java  (implements ClaimShipmentCommandPort)
    └── ClaimShipmentQueryAdapter.java    (implements ClaimShipmentQueryPort)
```

---

## Step 3: Application 레이어

### 3-1. Cancel Application
```
application/src/main/java/.../cancel/
├── port/
│   ├── in/                              (UseCase)
│   │   ├── CreateCancelUseCase.java
│   │   ├── ApproveCancelUseCase.java
│   │   ├── RejectCancelUseCase.java
│   │   ├── CompleteCancelUseCase.java
│   │   ├── WithdrawCancelUseCase.java
│   │   ├── GetCancelDetailUseCase.java
│   │   └── SearchCancelsUseCase.java
│   └── out/
│       ├── command/
│       │   └── CancelCommandPort.java    (persist, delete)
│       └── query/
│           └── CancelQueryPort.java      (findById, findByOrderId, search)
├── manager/
│   ├── CancelCommandManager.java         (Port 위임)
│   └── CancelReadManager.java
├── service/
│   ├── command/
│   │   ├── CreateCancelService.java      (Cancel 생성 + Order 상태 변경)
│   │   ├── ApproveCancelService.java
│   │   ├── RejectCancelService.java
│   │   ├── CompleteCancelService.java
│   │   └── WithdrawCancelService.java
│   └── query/
│       ├── GetCancelDetailService.java
│       └── SearchCancelsService.java
├── dto/
│   ├── command/
│   │   ├── CreateCancelCommand.java
│   │   ├── ApproveCancelCommand.java
│   │   └── CompleteCancelCommand.java
│   └── response/
│       ├── CancelDetailResult.java
│       └── CancelSearchResult.java
└── internal/
    └── CancelCoordinator.java            (Cancel + Order 상태 동기화)
```

### 3-2. Refund Application (동일 구조)
```
application/src/main/java/.../refund/
├── port/in/
│   ├── CreateRefundClaimUseCase.java
│   ├── StartCollectingUseCase.java
│   ├── CompleteCollectionUseCase.java
│   ├── CompleteRefundUseCase.java
│   ├── RejectRefundUseCase.java
│   ├── CancelRefundUseCase.java
│   ├── HoldRefundUseCase.java
│   ├── ReleaseHoldUseCase.java
│   ├── GetRefundDetailUseCase.java
│   └── SearchRefundsUseCase.java
├── port/out/
│   ├── command/RefundCommandPort.java
│   └── query/RefundQueryPort.java
├── manager/
├── service/command/ & query/
├── dto/command/ & response/
└── internal/
    └── RefundCoordinator.java            (Refund + Order + ClaimShipment 동기화)
```

### 3-3. Exchange Application (동일 구조)
```
application/src/main/java/.../exchange/
├── port/in/
│   ├── CreateExchangeClaimUseCase.java
│   ├── StartCollectingUseCase.java
│   ├── CompleteCollectionUseCase.java
│   ├── StartPreparingUseCase.java
│   ├── StartShippingUseCase.java
│   ├── CompleteExchangeUseCase.java
│   ├── RejectExchangeUseCase.java
│   ├── CancelExchangeUseCase.java
│   ├── UpdateExchangeTargetUseCase.java
│   ├── GetExchangeDetailUseCase.java
│   └── SearchExchangesUseCase.java
├── port/out/
│   ├── command/ExchangeCommandPort.java
│   └── query/ExchangeQueryPort.java
├── manager/
├── service/command/ & query/
├── dto/command/ & response/
└── internal/
    └── ExchangeCoordinator.java          (Exchange + Order + ClaimShipment + 신규주문 동기화)
```

### 3-4. ClaimShipment Application
```
application/src/main/java/.../claim/
├── port/out/
│   ├── command/ClaimShipmentCommandPort.java
│   └── query/ClaimShipmentQueryPort.java
└── manager/
    ├── ClaimShipmentCommandManager.java
    └── ClaimShipmentReadManager.java
```

---

## Step 4: REST API 레이어

### 4-1. Cancel API
```
POST   /admin/v1/cancels                  → 취소 생성 (구매자/판매자)
GET    /admin/v1/cancels/{cancelId}        → 취소 상세
GET    /admin/v1/cancels                   → 취소 목록 검색
PATCH  /admin/v1/cancels/{cancelId}/approve   → 승인
PATCH  /admin/v1/cancels/{cancelId}/reject    → 거절
PATCH  /admin/v1/cancels/{cancelId}/complete  → 완료 (환불 정보 포함)
DELETE /admin/v1/cancels/{cancelId}           → 철회
```

### 4-2. Refund API
```
POST   /admin/v1/refunds                     → 환불 클레임 생성
GET    /admin/v1/refunds/{refundId}           → 환불 상세
GET    /admin/v1/refunds                      → 환불 목록 검색
PATCH  /admin/v1/refunds/{refundId}/collect   → 수거 시작
PATCH  /admin/v1/refunds/{refundId}/collected → 수거 완료
PATCH  /admin/v1/refunds/{refundId}/complete  → 환불 완료
PATCH  /admin/v1/refunds/{refundId}/reject    → 거절
PATCH  /admin/v1/refunds/{refundId}/hold      → 보류
PATCH  /admin/v1/refunds/{refundId}/release   → 보류 해제
DELETE /admin/v1/refunds/{refundId}           → 취소
```

### 4-3. Exchange API
```
POST   /admin/v1/exchanges                        → 교환 클레임 생성
GET    /admin/v1/exchanges/{exchangeId}            → 교환 상세
GET    /admin/v1/exchanges                         → 교환 목록 검색
PATCH  /admin/v1/exchanges/{exchangeId}/collect    → 수거 시작
PATCH  /admin/v1/exchanges/{exchangeId}/collected  → 수거 완료
PATCH  /admin/v1/exchanges/{exchangeId}/prepare    → 재배송 준비
PATCH  /admin/v1/exchanges/{exchangeId}/ship       → 재배송 출발
PATCH  /admin/v1/exchanges/{exchangeId}/complete   → 교환 완료
PATCH  /admin/v1/exchanges/{exchangeId}/reject     → 거절
PATCH  /admin/v1/exchanges/{exchangeId}/target     → 교환 대상 변경
DELETE /admin/v1/exchanges/{exchangeId}            → 취소
```

---

## Step 5: Order 연동 수정

기존 Order의 Cancel/Refund/Exchange 관련 서비스를 새 모듈의 Coordinator로 전환:

```
기존: CancelOrderService → Order.cancel() 직접 호출
변경: CreateCancelService → CancelCoordinator
      → Cancel Aggregate 생성 + 저장
      → Order.cancel() 호출 + 저장
      → 이벤트 발행
```

---

## 구현 순서 (의존성 기반)

```
Week 1: DB 스키마 + Persistence
  ├─ Flyway 마이그레이션 작성
  ├─ JPA Entity 생성 (4개 Aggregate)
  ├─ Repository + QueryDSL
  └─ Mapper + Adapter

Week 2: Application 레이어 - Cancel
  ├─ Port (in/out) 정의
  ├─ Manager 구현
  ├─ Service 구현 (CRUD + 상태 전이)
  └─ CancelCoordinator (Order 연동)

Week 3: Application 레이어 - Refund + Exchange
  ├─ Refund Port + Manager + Service
  ├─ Exchange Port + Manager + Service
  └─ Coordinator (Order + ClaimShipment 연동)

Week 4: REST API + 테스트
  ├─ Controller + DTO
  ├─ Persistence 테스트
  ├─ Application 테스트
  └─ API 테스트 (RestDocs)
```

---

## 기존 코드와의 관계

### 유지
- `order_cancels`, `order_claims`, `order_exchanges` 테이블 → V4 API 조회용 비정규화 뷰로 유지
- `OrderCompositionQueryAdapter` → V4 주문 상세 조회 시 기존 테이블에서 읽기 유지
- V4 API 응답 구조 변경 없음

### 수정
- `CancelOrderService` → `CancelCoordinator`로 리다이렉트
- `StartClaimService` → `RefundCoordinator` / `ExchangeCoordinator`로 분기
- `CompleteRefundService` / `CompleteExchangeService` → 각 Coordinator에서 호출

### 추가
- 새 테이블 (cancels, cancel_items, refund_claims, ..., claim_shipments)
- 새 Persistence 모듈 (cancel, refund, exchange, claim)
- 새 Application 모듈 (cancel, refund, exchange)
- 새 REST API 엔드포인트
