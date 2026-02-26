-- ============================================
-- 배송 정책 테이블
-- ============================================

CREATE TABLE IF NOT EXISTS shipping_policies (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '정책명',
    is_default_policy TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 정책 여부',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
    shipping_fee_type VARCHAR(30) NOT NULL COMMENT '배송비 유형 (FREE, FIXED, CONDITIONAL_FREE 등)',
    base_fee INT NULL COMMENT '기본 배송비',
    free_threshold INT NULL COMMENT '무료배송 기준 금액',
    jeju_extra_fee INT NULL COMMENT '제주 추가 배송비',
    island_extra_fee INT NULL COMMENT '도서산간 추가 배송비',
    return_fee INT NULL COMMENT '반품 배송비',
    exchange_fee INT NULL COMMENT '교환 배송비',
    lead_time_min_days INT NULL COMMENT '최소 배송 소요일',
    lead_time_max_days INT NULL COMMENT '최대 배송 소요일',
    lead_time_cutoff_time TIME NULL COMMENT '당일 출고 마감 시간',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_shipping_policies_seller_id (seller_id),
    INDEX idx_shipping_policies_default (seller_id, is_default_policy),
    INDEX idx_shipping_policies_active (is_active),
    INDEX idx_shipping_policies_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송 정책';
