-- ============================================
-- 상품 카탈로그 코어 테이블
-- product_groups, products, product_group_images,
-- seller_option_groups, seller_option_values,
-- product_option_mappings
-- ============================================

-- ========================================
-- product_groups 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_groups (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_id BIGINT NOT NULL COMMENT '셀러 ID',
    brand_id BIGINT NOT NULL COMMENT '브랜드 ID',
    category_id BIGINT NOT NULL COMMENT '카테고리 ID',
    shipping_policy_id BIGINT NOT NULL COMMENT '배송 정책 ID',
    refund_policy_id BIGINT NOT NULL COMMENT '환불 정책 ID',
    product_group_name VARCHAR(200) NOT NULL COMMENT '상품 그룹명',
    option_type VARCHAR(50) NOT NULL COMMENT '옵션 유형 (SINGLE, COMBINATION)',
    status VARCHAR(50) NOT NULL COMMENT '상태 (DRAFT, ACTIVE, INACTIVE, DELETED)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_product_groups_seller_id (seller_id),
    INDEX idx_product_groups_brand_id (brand_id),
    INDEX idx_product_groups_category_id (category_id),
    INDEX idx_product_groups_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹';

-- ========================================
-- products 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품 그룹 ID',
    sku_code VARCHAR(100) NOT NULL COMMENT 'SKU 코드',
    regular_price INT NOT NULL COMMENT '정상가',
    current_price INT NOT NULL COMMENT '현재 판매가',
    sale_price INT NULL COMMENT '할인가',
    discount_rate INT NOT NULL COMMENT '할인율 (%)',
    stock_quantity INT NOT NULL COMMENT '재고 수량',
    status VARCHAR(50) NOT NULL COMMENT '상태 (ACTIVE, INACTIVE, SOLD_OUT)',
    sort_order INT NOT NULL COMMENT '정렬 순서',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (id),
    INDEX idx_products_product_group_id (product_group_id),
    INDEX idx_products_status (status),
    INDEX idx_products_sku_code (sku_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품';

-- ========================================
-- product_group_images 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_group_images (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품 그룹 ID',
    origin_url VARCHAR(500) NOT NULL COMMENT '원본 이미지 URL',
    uploaded_url VARCHAR(500) NULL COMMENT '업로드된 이미지 URL',
    image_type VARCHAR(50) NOT NULL COMMENT '이미지 유형 (MAIN, SUB, DETAIL)',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    PRIMARY KEY (id),
    INDEX idx_product_group_images_product_group_id (product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹 이미지';

-- ========================================
-- seller_option_groups 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_option_groups (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_group_id BIGINT NOT NULL COMMENT '상품 그룹 ID',
    option_group_name VARCHAR(100) NOT NULL COMMENT '옵션 그룹명',
    canonical_option_group_id BIGINT NULL COMMENT '표준 옵션 그룹 ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    PRIMARY KEY (id),
    INDEX idx_seller_option_groups_product_group_id (product_group_id),
    INDEX idx_seller_option_groups_canonical_id (canonical_option_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 옵션 그룹';

-- ========================================
-- seller_option_values 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS seller_option_values (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    seller_option_group_id BIGINT NOT NULL COMMENT '셀러 옵션 그룹 ID',
    option_value_name VARCHAR(100) NOT NULL COMMENT '옵션값 이름',
    canonical_option_value_id BIGINT NULL COMMENT '표준 옵션값 ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    PRIMARY KEY (id),
    INDEX idx_seller_option_values_group_id (seller_option_group_id),
    INDEX idx_seller_option_values_canonical_id (canonical_option_value_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 옵션값';

-- ========================================
-- product_option_mappings 테이블
-- ========================================

CREATE TABLE IF NOT EXISTS product_option_mappings (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    seller_option_value_id BIGINT NOT NULL COMMENT '셀러 옵션값 ID',
    PRIMARY KEY (id),
    INDEX idx_product_option_mappings_product_id (product_id),
    INDEX idx_product_option_mappings_option_value_id (seller_option_value_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품-옵션 매핑';
