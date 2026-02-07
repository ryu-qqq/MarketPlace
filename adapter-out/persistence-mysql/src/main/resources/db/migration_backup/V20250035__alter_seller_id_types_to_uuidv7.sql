-- V20250035: Seller ID 타입 변경 (BINARY(16)/BIGINT → VARCHAR(36) UUIDv7)
--
-- 변경 사항:
--   1. seller.id: BINARY(16) → VARCHAR(36)
--   2. seller.company_id: BIGINT → VARCHAR(36)
--   3. seller_sales_channel.id: BINARY(16) → VARCHAR(36)
--   4. seller_sales_channel.seller_id: BINARY(16) → VARCHAR(36)
--
-- 주의: 기존 데이터가 없는 상태에서 실행하는 것을 권장합니다.
--       기존 데이터가 있는 경우 데이터 마이그레이션 스크립트가 추가로 필요합니다.

-- ========================================
-- 1. seller_sales_channel 테이블 수정 (seller FK 참조하므로 먼저 수정)
-- ========================================

-- 기존 인덱스 삭제
ALTER TABLE seller_sales_channel DROP INDEX uk_seller_sales_channel;
ALTER TABLE seller_sales_channel DROP INDEX idx_seller_sales_channel_seller;

-- 컬럼 타입 변경
ALTER TABLE seller_sales_channel
    MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    MODIFY COLUMN seller_id VARCHAR(36) NOT NULL COMMENT '셀러 ID (FK, UUIDv7)';

-- 인덱스 재생성
ALTER TABLE seller_sales_channel
    ADD UNIQUE KEY uk_seller_sales_channel (seller_id, sales_channel_id),
    ADD INDEX idx_seller_sales_channel_seller (seller_id);

-- ========================================
-- 2. seller 테이블 수정
-- ========================================

-- 기존 인덱스 삭제
ALTER TABLE seller DROP INDEX idx_seller_company_id;

-- 컬럼 타입 변경
ALTER TABLE seller
    MODIFY COLUMN id VARCHAR(36) NOT NULL COMMENT 'UUIDv7',
    MODIFY COLUMN company_id VARCHAR(36) NOT NULL COMMENT '회사(입점사) ID (UUIDv7)';

-- 인덱스 재생성
ALTER TABLE seller ADD INDEX idx_seller_company_id (company_id);

-- ========================================
-- 3. 관련 Policy 테이블의 seller_id 타입 변경
--    (현재 BIGINT로 정의되어 있으나, UUIDv7 String으로 변경 필요)
-- ========================================

-- shipping_policy
ALTER TABLE shipping_policy DROP INDEX idx_shipping_policy_seller;
ALTER TABLE shipping_policy DROP INDEX idx_shipping_policy_default;
ALTER TABLE shipping_policy
    MODIFY COLUMN seller_id VARCHAR(36) NOT NULL COMMENT '셀러 ID (UUIDv7)';
ALTER TABLE shipping_policy
    ADD INDEX idx_shipping_policy_seller (seller_id),
    ADD INDEX idx_shipping_policy_default (seller_id, is_default);

-- return_policy
ALTER TABLE return_policy DROP INDEX idx_return_policy_seller;
ALTER TABLE return_policy DROP INDEX idx_return_policy_default;
ALTER TABLE return_policy
    MODIFY COLUMN seller_id VARCHAR(36) NOT NULL COMMENT '셀러 ID (UUIDv7)';
ALTER TABLE return_policy
    ADD INDEX idx_return_policy_seller (seller_id),
    ADD INDEX idx_return_policy_default (seller_id, is_default);

-- refund_policy
ALTER TABLE refund_policy DROP INDEX idx_refund_policy_seller;
ALTER TABLE refund_policy DROP INDEX idx_refund_policy_default;
ALTER TABLE refund_policy
    MODIFY COLUMN seller_id VARCHAR(36) NOT NULL COMMENT '셀러 ID (UUIDv7)';
ALTER TABLE refund_policy
    ADD INDEX idx_refund_policy_seller (seller_id),
    ADD INDEX idx_refund_policy_default (seller_id, is_default);
