-- ============================================
-- 셀러 관리자 이메일 Outbox 테이블
-- Transactional Outbox 패턴
-- @Version 낙관적 잠금 적용
-- ============================================

CREATE TABLE IF NOT EXISTS seller_admin_email_outboxes (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    payload TEXT NOT NULL COMMENT 'JSON 페이로드 (recipientEmail, sellerName, companyName, tenantId, organizationId)',
    status VARCHAR(20) NOT NULL COMMENT '상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '재시도 횟수',
    max_retry INT NOT NULL DEFAULT 3 COMMENT '최대 재시도 횟수',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    processed_at TIMESTAMP NULL DEFAULT NULL COMMENT '처리일시',
    error_message VARCHAR(1000) NULL COMMENT '에러 메시지',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 잠금 버전',
    idempotency_key VARCHAR(100) NOT NULL COMMENT '멱등성 키',
    PRIMARY KEY (id),
    UNIQUE KEY uk_seller_admin_email_outboxes_idempotency (idempotency_key),
    INDEX idx_seller_admin_email_outboxes_seller_id (seller_id),
    INDEX idx_seller_admin_email_outboxes_status (status),
    INDEX idx_seller_admin_email_outboxes_status_retry (status, retry_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 관리자 이메일 Outbox';
