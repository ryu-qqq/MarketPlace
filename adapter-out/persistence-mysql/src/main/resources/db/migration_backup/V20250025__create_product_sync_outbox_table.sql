-- ============================================================================
-- V20250025: Create Product Sync Outbox Table
-- ============================================================================
-- Description: Transactional Outbox Pattern을 위한 상품 동기화 발신함 테이블
-- Author: development-team
-- Created: 2025-01-25
-- ============================================================================

-- ----------------------------------------------------------------------------
-- product_sync_outbox: 상품 동기화 발신함
-- Transactional Outbox Pattern 구현
-- 상품 변경 시 동일 트랜잭션 내에서 Outbox에 메시지 저장
-- Worker가 주기적으로 폴링하여 SQS로 발송
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_sync_outbox (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Outbox ID',
    product_id          BIGINT          NOT NULL COMMENT '상품 ID',
    sales_channel_id    BIGINT          NOT NULL COMMENT '판매채널 ID',
    sync_type           VARCHAR(20)     NOT NULL COMMENT '동기화 유형 (CREATE, UPDATE, DELETE)',
    idempotency_key     VARCHAR(100)    NOT NULL COMMENT '멱등성 키 (중복 처리 방지)',
    payload             TEXT            NOT NULL COMMENT '동기화 페이로드 (JSON)',
    status              VARCHAR(20)     NOT NULL COMMENT '상태 (PENDING, SENT, FAILED)',
    retry_count         INT             NOT NULL DEFAULT 0 COMMENT '재시도 횟수',
    error_message       VARCHAR(2000)   NULL COMMENT '에러 메시지',
    processed_at        DATETIME(6)     NULL COMMENT '처리 일시',
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    PRIMARY KEY (id),

    -- 멱등성 키 유니크 제약조건 (중복 메시지 방지)
    CONSTRAINT uk_product_sync_outbox_idempotency
        UNIQUE (idempotency_key),

    -- 상태별 조회 인덱스 (Worker 폴링용)
    INDEX idx_product_sync_outbox_status (status),

    -- 상품+판매채널 조회 인덱스 (중복 요청 확인용)
    INDEX idx_product_sync_outbox_product_channel (product_id, sales_channel_id),

    -- 생성일시 조회 인덱스 (오래된 메시지 우선 처리용)
    INDEX idx_product_sync_outbox_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='상품 동기화 발신함 (Transactional Outbox Pattern)';
