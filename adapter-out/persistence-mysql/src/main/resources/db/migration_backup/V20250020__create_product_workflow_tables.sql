-- 상품 워크플로우 관련 테이블 생성
-- 상품 상태 이력 및 전이 기록 관리

-- ============================================================
-- product_workflow_history 테이블
-- 상품별 워크플로우 현재 상태 관리
-- ============================================================

CREATE TABLE product_workflow_history (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id              BIGINT UNSIGNED NOT NULL COMMENT '상품 ID',
    current_status          VARCHAR(30) NOT NULL COMMENT '현재 상태 (DRAFT, PENDING_REVIEW, APPROVED, REJECTED, LIVE, SUSPENDED)',
    domain_created_at       DATETIME(6) NOT NULL COMMENT '도메인 생성 시각',
    domain_updated_at       DATETIME(6) NOT NULL COMMENT '도메인 수정 시각',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_workflow_history_product UNIQUE (product_id),
    INDEX idx_workflow_history_status (current_status),
    INDEX idx_workflow_history_domain_created (domain_created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='상품 워크플로우 이력 테이블';


-- ============================================================
-- workflow_transition 테이블
-- 상태 전이 기록 (상태 변경 이력)
-- ============================================================

CREATE TABLE workflow_transition (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    workflow_history_id     BIGINT UNSIGNED NOT NULL COMMENT '워크플로우 이력 ID',
    from_status             VARCHAR(30) NOT NULL COMMENT '이전 상태',
    to_status               VARCHAR(30) NOT NULL COMMENT '변경 상태',
    reason                  VARCHAR(30) NOT NULL COMMENT '전이 사유 (INITIAL_CREATION, SUBMISSION, APPROVAL, REJECTION 등)',
    comment                 VARCHAR(1000) COMMENT '검토 코멘트',
    reviewer_id             BIGINT UNSIGNED NOT NULL COMMENT '검토자 ID',
    transitioned_at         DATETIME(6) NOT NULL COMMENT '전이 시각',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_workflow_transition_history (workflow_history_id),
    INDEX idx_workflow_transition_time (workflow_history_id, transitioned_at DESC),
    INDEX idx_workflow_transition_reviewer (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='워크플로우 전이 기록 테이블';
