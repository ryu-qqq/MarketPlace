-- orders 테이블
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) NOT NULL COMMENT 'PK (UUID)',
    order_number VARCHAR(50) NOT NULL COMMENT '주문 번호',
    status VARCHAR(30) NOT NULL COMMENT '주문 상태',
    buyer_name VARCHAR(100) NOT NULL COMMENT '구매자 이름',
    buyer_email VARCHAR(255) NULL COMMENT '구매자 이메일',
    buyer_phone VARCHAR(20) NULL COMMENT '구매자 전화번호',
    sales_channel_id BIGINT NOT NULL COMMENT '판매채널 ID',
    shop_id BIGINT NOT NULL COMMENT '샵 ID',
    external_order_no VARCHAR(100) NOT NULL COMMENT '외부 주문번호',
    external_ordered_at TIMESTAMP NOT NULL COMMENT '외부 주문일시',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
    PRIMARY KEY (id),
    INDEX idx_orders_order_number (order_number),
    INDEX idx_orders_status (status),
    INDEX idx_orders_sales_channel_id (sales_channel_id),
    INDEX idx_orders_external_order_no (sales_channel_id, external_order_no),
    INDEX idx_orders_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- order_items 테이블
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id VARCHAR(36) NOT NULL COMMENT '주문 ID (FK)',
    product_group_id BIGINT NOT NULL COMMENT '상품그룹 ID',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    brand_id BIGINT NOT NULL COMMENT '브랜드 ID',
    sku_code VARCHAR(50) NULL COMMENT 'SKU 코드',
    external_product_id VARCHAR(100) NOT NULL COMMENT '외부 상품 ID',
    external_option_id VARCHAR(100) NULL COMMENT '외부 옵션 ID',
    external_product_name VARCHAR(500) NULL COMMENT '외부 상품명',
    external_option_name VARCHAR(500) NULL COMMENT '외부 옵션명',
    external_image_url VARCHAR(1000) NULL COMMENT '외부 상품 이미지 URL',
    unit_price INT NOT NULL DEFAULT 0 COMMENT '단가',
    quantity INT NOT NULL DEFAULT 1 COMMENT '수량',
    total_amount INT NOT NULL DEFAULT 0 COMMENT '합계 금액',
    discount_amount INT NOT NULL DEFAULT 0 COMMENT '할인 금액',
    payment_amount INT NOT NULL DEFAULT 0 COMMENT '실결제 금액',
    receiver_name VARCHAR(100) NOT NULL COMMENT '수령인 이름',
    receiver_phone VARCHAR(20) NULL COMMENT '수령인 전화번호',
    receiver_zipcode VARCHAR(10) NULL COMMENT '수령인 우편번호',
    receiver_address VARCHAR(500) NULL COMMENT '수령인 주소',
    receiver_address_detail VARCHAR(500) NULL COMMENT '수령인 상세주소',
    delivery_request VARCHAR(500) NULL COMMENT '배송 요청사항',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_seller_id (seller_id),
    INDEX idx_order_items_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- order_histories 테이블
CREATE TABLE IF NOT EXISTS order_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id VARCHAR(36) NOT NULL COMMENT '주문 ID (FK)',
    from_status VARCHAR(30) NULL COMMENT '이전 상태',
    to_status VARCHAR(30) NOT NULL COMMENT '변경 상태',
    changed_by VARCHAR(100) NOT NULL COMMENT '변경자',
    reason VARCHAR(500) NULL COMMENT '변경 사유',
    changed_at TIMESTAMP NOT NULL COMMENT '변경 일시',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_order_histories_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
