-- V5: payments 테이블 ID를 UUIDv7(varchar)로 변경, payment_number 컬럼 추가

-- 1. 기존 데이터의 id를 UUID 형식 문자열로 변환하기 위해 임시 컬럼 사용
--    AUTO_INCREMENT → varchar 직접 변환 불가하므로 단계적 처리

-- 1-1. 임시 UUID 컬럼 추가
ALTER TABLE `payments`
    ADD COLUMN `new_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER `id`;

-- 1-2. 기존 id 값을 UUID 형식으로 변환하여 임시 컬럼에 저장
UPDATE `payments` SET `new_id` = UUID() WHERE `new_id` IS NULL;

-- 1-3. PK 제거 (AUTO_INCREMENT 해제)
ALTER TABLE `payments`
    MODIFY COLUMN `id` bigint NOT NULL,
    DROP PRIMARY KEY;

-- 1-4. 기존 id 컬럼 삭제
ALTER TABLE `payments`
    DROP COLUMN `id`;

-- 1-5. 임시 컬럼을 id로 리네임 + PK 설정
ALTER TABLE `payments`
    CHANGE COLUMN `new_id` `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '결제 ID (UUIDv7)',
    ADD PRIMARY KEY (`id`);

-- 2. payment_number 컬럼 추가 (order_id 다음)
ALTER TABLE `payments`
    ADD COLUMN `payment_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '결제 번호 (PAY-YYYYMMDD-XXXX)' AFTER `order_id`;

-- 3. payment_number 유니크 인덱스 추가
ALTER TABLE `payments`
    ADD UNIQUE KEY `uk_payments_payment_number` (`payment_number`);
