-- ============================================
-- 배송 테이블
-- shipments
-- ============================================

CREATE TABLE IF NOT EXISTS shipments (
    id VARCHAR(36) NOT NULL COMMENT 'PK (UUID)',
    shipment_number VARCHAR(50) NOT NULL COMMENT '배송 번호',
    order_id VARCHAR(36) NOT NULL COMMENT '주문 ID (UUID)',
    order_number VARCHAR(50) NOT NULL COMMENT '주문 번호',
    status VARCHAR(20) NOT NULL COMMENT '배송 상태 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED)',
    shipment_method_type VARCHAR(30) NULL COMMENT '배송 방식 (COURIER, QUICK, VISIT, DESIGNATED_COURIER)',
    courier_code VARCHAR(50) NULL COMMENT '택배사 코드',
    courier_name VARCHAR(100) NULL COMMENT '택배사명',
    tracking_number VARCHAR(100) NULL COMMENT '운송장 번호',
    order_confirmed_at TIMESTAMP NULL DEFAULT NULL COMMENT '주문 확인일시',
    shipped_at TIMESTAMP NULL DEFAULT NULL COMMENT '출고일시',
    delivered_at TIMESTAMP NULL DEFAULT NULL COMMENT '배송 완료일시',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_shipments_order_id (order_id),
    INDEX idx_shipments_order_number (order_number),
    INDEX idx_shipments_status (status),
    INDEX idx_shipments_shipment_number (shipment_number),
    INDEX idx_shipments_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송';
