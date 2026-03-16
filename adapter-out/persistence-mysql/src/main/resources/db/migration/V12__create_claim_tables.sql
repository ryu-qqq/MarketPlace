-- V12: 취소/환불/교환 클레임 독립 테이블 생성
-- 도메인 Aggregate 구조와 1:1 매핑
-- 기존 order_cancels, order_claims, order_exchanges는 V4 API 조회용으로 유지 (CQRS)

-- ============================================================
-- 1. claim_shipments 테이블 (수거 배송 - Refund/Exchange 공유)
-- ============================================================
CREATE TABLE `claim_shipments` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '클레임 배송 ID (UUIDv7)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '배송 상태 (PENDING, IN_TRANSIT, DELIVERED, FAILED)',
  `method_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '배송 방식 (COURIER, QUICK, VISIT)',
  `courier_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '택배사 코드',
  `courier_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '택배사명',
  `tracking_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '송장번호',
  `fee_amount` int NOT NULL DEFAULT 0 COMMENT '배송비 금액',
  `fee_payer` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '배송비 부담 주체 (BUYER, SELLER)',
  `fee_include_in_package` tinyint(1) NOT NULL DEFAULT 0 COMMENT '착불 여부',
  `sender_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송인 이름',
  `sender_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송인 연락처',
  `sender_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송인 주소',
  `sender_address_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송인 상세주소',
  `sender_zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송인 우편번호',
  `receiver_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 이름',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 연락처',
  `receiver_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 주소',
  `receiver_address_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 상세주소',
  `receiver_zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 우편번호',
  `shipped_at` timestamp NULL DEFAULT NULL COMMENT '배송 시작 시각',
  `received_at` timestamp NULL DEFAULT NULL COMMENT '수령 완료 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_claim_shipments_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='클레임 수거 배송 정보';

-- ============================================================
-- 2. cancels 테이블 (취소 Aggregate)
-- ============================================================
CREATE TABLE `cancels` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 ID (UUIDv7)',
  `cancel_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 번호 (CAN-YYYYMMDD-XXXX)',
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `cancel_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 유형 (BUYER_CANCEL, SELLER_CANCEL)',
  `cancel_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REQUESTED' COMMENT '취소 상태 (REQUESTED, APPROVED, REJECTED, COMPLETED, CANCELLED)',
  `reason_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 사유 유형',
  `reason_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '취소 사유 상세',
  `refund_amount` int DEFAULT NULL COMMENT '환불 금액',
  `refund_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 방법',
  `refund_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 상태',
  `refunded_at` timestamp NULL DEFAULT NULL COMMENT '환불 완료 시각',
  `pg_refund_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'PG 환불 ID',
  `requested_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '요청자',
  `processed_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '처리자',
  `requested_at` timestamp NOT NULL COMMENT '요청 시각',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리 시각',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cancels_cancel_number` (`cancel_number`),
  KEY `idx_cancels_order_id` (`order_id`),
  KEY `idx_cancels_status` (`cancel_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='취소 클레임';

-- ============================================================
-- 3. cancel_items 테이블 (취소 대상 주문상품)
-- ============================================================
CREATE TABLE `cancel_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '취소 아이템 ID',
  `cancel_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '취소 ID (FK)',
  `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 상품 ID',
  `cancel_qty` int NOT NULL COMMENT '취소 수량',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  PRIMARY KEY (`id`),
  KEY `idx_cancel_items_cancel_id` (`cancel_id`),
  CONSTRAINT `fk_cancel_items_cancel` FOREIGN KEY (`cancel_id`) REFERENCES `cancels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='취소 대상 주문상품';

-- ============================================================
-- 4. refund_claims 테이블 (환불 클레임 Aggregate)
-- ============================================================
CREATE TABLE `refund_claims` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '환불 클레임 ID (UUIDv7)',
  `claim_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '환불 클레임 번호 (REF-YYYYMMDD-XXXX)',
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `refund_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REQUESTED' COMMENT '환불 상태 (REQUESTED, COLLECTING, COLLECTED, COMPLETED, REJECTED, CANCELLED)',
  `reason_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '환불 사유 유형',
  `reason_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 사유 상세',
  `original_amount` int DEFAULT NULL COMMENT '원래 금액',
  `final_amount` int DEFAULT NULL COMMENT '최종 환불 금액',
  `deduction_amount` int DEFAULT NULL COMMENT '차감 금액',
  `deduction_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '차감 사유',
  `refund_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불 방법',
  `refunded_at` timestamp NULL DEFAULT NULL COMMENT '환불 완료 시각',
  `claim_shipment_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수거 배송 ID (FK)',
  `hold_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '보류 사유',
  `hold_at` timestamp NULL DEFAULT NULL COMMENT '보류 시각',
  `requested_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '요청자',
  `processed_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '처리자',
  `requested_at` timestamp NOT NULL COMMENT '요청 시각',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리 시각',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_claims_claim_number` (`claim_number`),
  KEY `idx_refund_claims_order_id` (`order_id`),
  KEY `idx_refund_claims_status` (`refund_status`),
  KEY `idx_refund_claims_shipment_id` (`claim_shipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환불 클레임';

-- ============================================================
-- 5. refund_items 테이블 (환불 대상 주문상품)
-- ============================================================
CREATE TABLE `refund_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '환불 아이템 ID',
  `refund_claim_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '환불 클레임 ID (FK)',
  `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 상품 ID',
  `refund_qty` int NOT NULL COMMENT '환불 수량',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  PRIMARY KEY (`id`),
  KEY `idx_refund_items_claim_id` (`refund_claim_id`),
  CONSTRAINT `fk_refund_items_claim` FOREIGN KEY (`refund_claim_id`) REFERENCES `refund_claims` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환불 대상 주문상품';

-- ============================================================
-- 6. exchange_claims 테이블 (교환 클레임 Aggregate)
-- ============================================================
CREATE TABLE `exchange_claims` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '교환 클레임 ID (UUIDv7)',
  `claim_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '교환 클레임 번호 (EXC-YYYYMMDD-XXXX)',
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `exchange_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'REQUESTED' COMMENT '교환 상태 (REQUESTED, COLLECTING, COLLECTED, PREPARING, SHIPPING, COMPLETED, REJECTED, CANCELLED)',
  `reason_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '교환 사유 유형',
  `reason_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '교환 사유 상세',
  `target_product_group_id` bigint DEFAULT NULL COMMENT '교환 대상 상품그룹 ID',
  `target_product_id` bigint DEFAULT NULL COMMENT '교환 대상 상품 ID',
  `target_sku_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '교환 대상 SKU 코드',
  `target_quantity` int DEFAULT NULL COMMENT '교환 대상 수량',
  `original_price` int DEFAULT NULL COMMENT '원 상품 가격',
  `target_price` int DEFAULT NULL COMMENT '교환 상품 가격',
  `price_difference` int DEFAULT NULL COMMENT '가격 차이',
  `additional_payment_required` tinyint(1) NOT NULL DEFAULT 0 COMMENT '추가 결제 필요 여부',
  `partial_refund_required` tinyint(1) NOT NULL DEFAULT 0 COMMENT '부분 환불 필요 여부',
  `collect_shipping_fee` int NOT NULL DEFAULT 0 COMMENT '수거 배송비',
  `reship_shipping_fee` int NOT NULL DEFAULT 0 COMMENT '재배송 배송비',
  `total_shipping_fee` int NOT NULL DEFAULT 0 COMMENT '총 배송비',
  `shipping_fee_payer` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '배송비 부담 주체 (BUYER, SELLER)',
  `claim_shipment_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수거 배송 ID (FK)',
  `linked_order_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '재배송 신규 주문 ID',
  `requested_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '요청자',
  `processed_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '처리자',
  `requested_at` timestamp NOT NULL COMMENT '요청 시각',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리 시각',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료 시각',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exchange_claims_claim_number` (`claim_number`),
  KEY `idx_exchange_claims_order_id` (`order_id`),
  KEY `idx_exchange_claims_status` (`exchange_status`),
  KEY `idx_exchange_claims_shipment_id` (`claim_shipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='교환 클레임';

-- ============================================================
-- 7. exchange_items 테이블 (교환 대상 주문상품)
-- ============================================================
CREATE TABLE `exchange_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '교환 아이템 ID',
  `exchange_claim_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '교환 클레임 ID (FK)',
  `order_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 상품 ID',
  `exchange_qty` int NOT NULL COMMENT '교환 수량',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  PRIMARY KEY (`id`),
  KEY `idx_exchange_items_claim_id` (`exchange_claim_id`),
  CONSTRAINT `fk_exchange_items_claim` FOREIGN KEY (`exchange_claim_id`) REFERENCES `exchange_claims` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='교환 대상 주문상품';
