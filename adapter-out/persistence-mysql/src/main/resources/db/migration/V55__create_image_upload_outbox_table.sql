-- ============================================
-- 이미지 업로드 Outbox 테이블
-- image_upload_outboxes
-- ============================================

CREATE TABLE IF NOT EXISTS image_upload_outboxes (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    source_id BIGINT NOT NULL COMMENT '소스 엔티티 ID',
    source_type VARCHAR(30) NOT NULL COMMENT '소스 유형 (PRODUCT_GROUP_IMAGE, DESCRIPTION_IMAGE)',
    origin_url VARCHAR(500) NOT NULL COMMENT '원본 이미지 URL',
    status VARCHAR(20) NOT NULL COMMENT '처리 상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '재시도 횟수',
    max_retry INT NOT NULL DEFAULT 3 COMMENT '최대 재시도 횟수',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    processed_at TIMESTAMP NULL DEFAULT NULL COMMENT '처리 완료일시',
    error_message VARCHAR(1000) NULL COMMENT '에러 메시지',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전 (@Version)',
    idempotency_key VARCHAR(100) NOT NULL COMMENT '멱등성 키',
    PRIMARY KEY (id),
    UNIQUE KEY uk_image_upload_outboxes_idempotency_key (idempotency_key),
    INDEX idx_image_upload_outboxes_status (status),
    INDEX idx_image_upload_outboxes_source (source_type, source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='이미지 업로드 Outbox';
