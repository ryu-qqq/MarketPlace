-- V4: 주문 하위 테이블 생성 및 order_items 컬럼 추가
-- payments, order_cancels, order_claims, order_exchanges 테이블 생성
-- order_items에 배송/정산 컬럼 추가

-- ============================================================
-- 1. payments 테이블 (orders 1:1)
-- ============================================================
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `payment_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '결제 상태 (PENDING, COMPLETED, PARTIALLY_REFUNDED, FULLY_REFUNDED, CANCELLED)',
  `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결제 수단',
  `payment_agency_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'PG사 거래 ID',
  `payment_amount` int NOT NULL DEFAULT 0 COMMENT '결제 금액',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT '결제 완료 시각',
  `canceled_at` timestamp NULL DEFAULT NULL COMMENT '결제 취소 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payments_order_id` (`order_id`),
  KEY `idx_payments_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제 정보 (주문 1:1)';

-- ============================================================
-- 2. orders 테이블에서 결제 관련 컬럼 제거 (payments 테이블로 이관)
-- ============================================================
ALTER TABLE `orders`
    DROP COLUMN `payment_method`,
    DROP COLUMN `total_payment_amount`,
    DROP COLUMN `paid_at`;

-- ============================================================
-- 3. order_items 컬럼 추가: 배송 + 정산
-- ============================================================
ALTER TABLE `order_items`
    ADD COLUMN `delivery_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'READY' COMMENT '배송 상태 (READY, SHIPPED, IN_TRANSIT, DELIVERED, FAILED)' AFTER `delivery_request`,
    ADD COLUMN `shipment_company_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '택배사 코드' AFTER `delivery_status`,
    ADD COLUMN `invoice` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '운송장 번호' AFTER `shipment_company_code`,
    ADD COLUMN `shipment_completed_date` timestamp NULL DEFAULT NULL COMMENT '배송 완료일' AFTER `invoice`,
    ADD COLUMN `commission_rate` int NOT NULL DEFAULT 0 COMMENT '수수료율 (basis point, 100 = 1%)' AFTER `shipment_completed_date`,
    ADD COLUMN `fee` int NOT NULL DEFAULT 0 COMMENT '수수료 금액' AFTER `commission_rate`,
    ADD COLUMN `expectation_settlement_amount` int NOT NULL DEFAULT 0 COMMENT '예상 정산 금액' AFTER `fee`,
    ADD COLUMN `settlement_amount` int NOT NULL DEFAULT 0 COMMENT '실정산 금액' AFTER `expectation_settlement_amount`,
    ADD COLUMN `share_ratio` int NOT NULL DEFAULT 0 COMMENT '분배 비율 (basis point)' AFTER `settlement_amount`,
    ADD COLUMN `expected_settlement_day` timestamp NULL DEFAULT NULL COMMENT '예상 정산일' AFTER `share_ratio`,
    ADD COLUMN `settlement_day` timestamp NULL DEFAULT NULL COMMENT '실정산일' AFTER `expected_settlement_day`;

-- ============================================================
-- 3. order_cancels 테이블
-- ============================================================
CREATE TABLE `order_cancels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `order_item_id` bigint NOT NULL COMMENT '주문 아이템 ID (FK)',
  `cancel_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 번호',
  `cancel_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 상태 (REQUESTED, APPROVED, COMPLETED, REJECTED)',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '취소 수량',
  `reason_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '취소 사유 유형',
  `reason_detail` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '취소 사유 상세',
  `original_amount` int NOT NULL DEFAULT 0 COMMENT '원래 금액',
  `refund_amount` int NOT NULL DEFAULT 0 COMMENT '환불 금액',
  `refund_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 수단',
  `refunded_at` timestamp NULL DEFAULT NULL COMMENT '환불 완료 시각',
  `requested_at` timestamp NOT NULL COMMENT '취소 요청 시각',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '취소 완료 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_cancels_order_id` (`order_id`),
  KEY `idx_order_cancels_order_item_id` (`order_item_id`),
  KEY `idx_order_cancels_cancel_number` (`cancel_number`),
  KEY `idx_order_cancels_status` (`cancel_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 취소';

-- ============================================================
-- 4. order_claims 테이블
-- ============================================================
CREATE TABLE `order_claims` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `order_item_id` bigint NOT NULL COMMENT '주문 아이템 ID (FK)',
  `claim_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '클레임 번호',
  `claim_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '클레임 유형 (REFUND, EXCHANGE)',
  `claim_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '클레임 상태 (REQUESTED, COLLECTING, COLLECTED, APPROVED, COMPLETED, REJECTED)',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '클레임 수량',
  `reason_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '클레임 사유 유형',
  `reason_detail` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '클레임 사유 상세',
  `collect_method` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수거 방법 (COURIER_PICKUP, CUSTOMER_SHIP)',
  `original_amount` int NOT NULL DEFAULT 0 COMMENT '원래 금액',
  `deduction_amount` int NOT NULL DEFAULT 0 COMMENT '차감 금액 (반품 배송비 등)',
  `deduction_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '차감 사유',
  `refund_amount` int NOT NULL DEFAULT 0 COMMENT '환불 금액',
  `refund_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 수단',
  `refunded_at` timestamp NULL DEFAULT NULL COMMENT '환불 완료 시각',
  `requested_at` timestamp NOT NULL COMMENT '클레임 요청 시각',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '클레임 완료 시각',
  `rejected_at` timestamp NULL DEFAULT NULL COMMENT '클레임 거절 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_claims_order_id` (`order_id`),
  KEY `idx_order_claims_order_item_id` (`order_item_id`),
  KEY `idx_order_claims_claim_number` (`claim_number`),
  KEY `idx_order_claims_type_status` (`claim_type`, `claim_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 클레임 (반품/교환)';

-- ============================================================
-- 5. order_exchanges 테이블 (order_claims의 자식)
-- ============================================================
CREATE TABLE `order_exchanges` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `claim_id` bigint NOT NULL COMMENT '클레임 ID (FK)',
  `new_product_group_id` bigint DEFAULT NULL COMMENT '교환 상품그룹 ID',
  `new_product_id` bigint DEFAULT NULL COMMENT '교환 상품 ID',
  `new_option_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '교환 옵션명',
  `price_difference` int NOT NULL DEFAULT 0 COMMENT '가격 차이 (양수: 추가결제, 음수: 환불)',
  `new_order_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '교환으로 생성된 신규 주문 ID',
  `new_order_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '교환으로 생성된 신규 주문번호',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_exchanges_claim_id` (`claim_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='교환 상세 (클레임의 자식)';
