-- ============================================
-- 환불 정책 테이블
-- ============================================

CREATE TABLE IF NOT EXISTS refund_policies (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '정책명',
    is_default_policy TINYINT(1) NOT NULL DEFAULT 0 COMMENT '기본 정책 여부',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
    return_period_days INT NOT NULL COMMENT '반품 가능 기간 (일)',
    exchange_period_days INT NOT NULL COMMENT '교환 가능 기간 (일)',
    non_returnable_conditions VARCHAR(500) NULL COMMENT '반품 불가 조건',
    is_partial_refund_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '부분 환불 가능 여부',
    is_inspection_required TINYINT(1) NOT NULL DEFAULT 0 COMMENT '검수 필요 여부',
    inspection_period_days INT NULL COMMENT '검수 소요 기간 (일)',
    additional_info VARCHAR(2000) NULL COMMENT '추가 안내사항',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_refund_policies_seller_id (seller_id),
    INDEX idx_refund_policies_default (seller_id, is_default_policy),
    INDEX idx_refund_policies_active (is_active),
    INDEX idx_refund_policies_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환불 정책';
