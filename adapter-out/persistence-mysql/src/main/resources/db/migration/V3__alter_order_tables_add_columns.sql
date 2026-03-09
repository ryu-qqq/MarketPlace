-- V3: orders 테이블에 결제/샵 정보 컬럼 추가, order_items 테이블에 스냅샷 컬럼 추가

-- 1. orders 테이블: 샵 코드/이름, 결제 정보
ALTER TABLE `orders`
    ADD COLUMN `shop_code` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '샵 코드 (예: NAVER, COUPANG)' AFTER `external_ordered_at`,
    ADD COLUMN `shop_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '샵 이름' AFTER `shop_code`,
    ADD COLUMN `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결제 수단' AFTER `shop_name`,
    ADD COLUMN `total_payment_amount` int NOT NULL DEFAULT 0 COMMENT '총 결제 금액' AFTER `payment_method`,
    ADD COLUMN `paid_at` timestamp NULL DEFAULT NULL COMMENT '결제 완료 시각' AFTER `total_payment_amount`;

-- 2. order_items 테이블: 내부 상품 스냅샷 필드
ALTER TABLE `order_items`
    ADD COLUMN `product_group_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '상품그룹 이름 스냅샷' AFTER `sku_code`,
    ADD COLUMN `brand_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '브랜드 이름 스냅샷' AFTER `product_group_name`,
    ADD COLUMN `seller_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '셀러 이름 스냅샷' AFTER `brand_name`,
    ADD COLUMN `main_image_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '대표 이미지 URL 스냅샷' AFTER `seller_name`;

-- 3. inbound_order_items 테이블: 매핑 시 캡처한 스냅샷 필드
ALTER TABLE `inbound_order_items`
    ADD COLUMN `resolved_product_group_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '매핑된 상품그룹 이름' AFTER `resolved_sku_code`;
