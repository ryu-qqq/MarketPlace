-- ============================================
-- Application Status History Table
-- ============================================
-- 입점 신청 상태 변경 이력 테이블
-- 상태 변경 추적: PENDING → APPROVED/REJECTED → COMPLETED/EXPIRED
-- ID: UUIDv7 (CHAR(36))
-- ============================================

CREATE TABLE application_status_history (
    id CHAR(36) NOT NULL COMMENT 'UUIDv7 기반 PK',
    company_application_id CHAR(36) NOT NULL COMMENT '입점 신청 ID (FK 역할)',

    -- 상태 변경 정보
    from_status VARCHAR(20) NOT NULL COMMENT '이전 상태 (PENDING, APPROVED, REJECTED, COMPLETED, EXPIRED)',
    to_status VARCHAR(20) NOT NULL COMMENT '변경된 상태 (PENDING, APPROVED, REJECTED, COMPLETED, EXPIRED)',
    changed_by VARCHAR(100) NOT NULL COMMENT '변경자 (이메일 또는 SYSTEM)',
    reason VARCHAR(500) NULL COMMENT '변경 사유 (거절 사유 등)',
    changed_at TIMESTAMP(6) NOT NULL COMMENT '상태 변경 시간',

    -- 감사 정보
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 시간',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 시간',

    PRIMARY KEY (id),

    -- 인덱스
    INDEX idx_app_status_history_application_id (company_application_id),
    INDEX idx_app_status_history_changed_at (changed_at),
    INDEX idx_app_status_history_to_status (to_status),
    INDEX idx_app_status_history_app_id_changed_at (company_application_id, changed_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입점 신청 상태 변경 이력';
