-- V5: payments 테이블 ID를 UUIDv7(varchar)로 변경, payment_number 컬럼 추가

-- 1. 기존 AUTO_INCREMENT id 컬럼을 varchar(36)으로 변경
ALTER TABLE `payments`
    MODIFY COLUMN `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '결제 ID (UUIDv7)';

-- 2. payment_number 컬럼 추가 (order_id 다음)
ALTER TABLE `payments`
    ADD COLUMN `payment_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '결제 번호 (PAY-YYYYMMDD-XXXX)' AFTER `order_id`;

-- 3. payment_number 유니크 인덱스 추가
ALTER TABLE `payments`
    ADD UNIQUE KEY `uk_payments_payment_number` (`payment_number`);
