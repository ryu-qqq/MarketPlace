-- V1: 전체 테이블 DDL (엔티티 기반 재구성)
-- Generated from Stage DB schema

-- ============================================================
-- 1. 독립 테이블 (FK 없음)
-- ============================================================

CREATE TABLE `admin_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL,
  `title` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  `required_role_level` int NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_admin_menus_parent_order` (`parent_id`,`display_order`),
  KEY `idx_admin_menus_active_role` (`active`,`required_role_level`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `brand` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '브랜드 코드',
  `name_ko` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '브랜드 한글명',
  `name_en` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '브랜드 영문명',
  `short_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '브랜드 약칭',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE 등)',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '로고 URL',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_brand_code` (`code`),
  KEY `idx_brand_status` (`status`),
  KEY `idx_brand_updated` (`updated_at`),
  KEY `idx_brand_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='표준 브랜드 마스터 테이블';

CREATE TABLE `canonical_option_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name_ko` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name_en` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_canonical_option_group_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `common_code_types` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드 타입 식별자',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드 타입명',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '설명',
  `display_order` int NOT NULL COMMENT '표시 순서',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_common_code_types_code` (`code`),
  KEY `idx_common_code_types_active` (`is_active`),
  KEY `idx_common_code_types_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 코드 타입';

CREATE TABLE `inbound_source` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 소스 고유 코드 (e.g. SETOF, COUPANG_CRAWL)',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 소스 표시명',
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CRAWLING, LEGACY, PARTNER',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '설명',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_es_code` (`code`),
  KEY `idx_es_type` (`type`),
  KEY `idx_es_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 데이터 소스 (크롤링, 레거시, 파트너)';

CREATE TABLE `notice_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '고시정보 카테고리 코드 (CategoryGroup enum 값과 동일)',
  `name_ko` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '한국어 이름',
  `name_en` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '영어 이름',
  `target_category_group` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '적용 대상 카테고리 그룹 (CLOTHING, SHOES, COSMETICS 등)',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 상태',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_category_code` (`code`),
  UNIQUE KEY `uk_notice_category_group` (`target_category_group`),
  KEY `idx_notice_category_active` (`active`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고시정보 카테고리 테이블';

CREATE TABLE `orders` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PK (UUID)',
  `order_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 번호',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 상태',
  `buyer_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '구매자 이름',
  `buyer_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '구매자 이메일',
  `buyer_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '구매자 전화번호',
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `shop_id` bigint NOT NULL COMMENT '샵 ID',
  `external_order_no` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 주문번호',
  `external_ordered_at` timestamp NOT NULL COMMENT '외부 주문일시',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_orders_order_number` (`order_number`),
  KEY `idx_orders_status` (`status`),
  KEY `idx_orders_sales_channel_id` (`sales_channel_id`),
  KEY `idx_orders_external_order_no` (`sales_channel_id`,`external_order_no`),
  KEY `idx_orders_deleted` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sales_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '판매채널명',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_channel_name` (`channel_name`),
  KEY `idx_sc_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='판매채널 마스터 테이블';

CREATE TABLE `sellers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '셀러명',
  `display_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '표시명',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '로고 URL',
  `description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '셀러 설명',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 여부',
  `auth_tenant_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '인증 테넌트 ID',
  `auth_organization_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '인증 조직 ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sellers_seller_name` (`seller_name`),
  KEY `idx_sellers_active` (`is_active`),
  KEY `idx_sellers_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러';

CREATE TABLE `seller_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '셀러명',
  `display_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '표시명',
  `logo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '로고 URL',
  `description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '셀러 설명',
  `registration_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사업자등록번호',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '회사명',
  `representative` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '대표자명',
  `sale_report_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '통신판매업신고번호',
  `business_zip_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사업장 우편번호',
  `business_base_address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사업장 기본주소',
  `business_detail_address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업장 상세주소',
  `cs_phone_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CS 전화번호',
  `cs_email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CS 이메일',
  `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '담당자명',
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '담당자 연락처',
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '담당자 이메일',
  `bank_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '은행 코드',
  `bank_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '은행명',
  `account_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '계좌번호',
  `account_holder_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '예금주명',
  `settlement_cycle` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)',
  `settlement_day` int NOT NULL COMMENT '정산일',
  `agreed_at` timestamp NOT NULL COMMENT '약관 동의 일시',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '신청 상태 (PENDING, APPROVED, REJECTED)',
  `applied_at` timestamp NOT NULL COMMENT '신청일시',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리일시',
  `processed_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '처리자',
  `rejection_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '거절 사유',
  `approved_seller_id` bigint DEFAULT NULL COMMENT '승인된 셀러 ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_seller_applications_status` (`status`),
  KEY `idx_seller_applications_reg_num` (`registration_number`),
  KEY `idx_seller_applications_applied_at` (`applied_at`),
  KEY `idx_seller_applications_status_reg` (`status`,`registration_number`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='입점 신청';

-- ============================================================
-- 2. 1차 의존 테이블 (독립 테이블 참조)
-- ============================================================

CREATE TABLE `canonical_option_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canonical_option_group_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name_ko` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name_en` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_canonical_value_group_code` (`canonical_option_group_id`,`code`),
  CONSTRAINT `fk_canonical_value_group` FOREIGN KEY (`canonical_option_group_id`) REFERENCES `canonical_option_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '카테고리 고유 코드 (예: CAT001)',
  `name_ko` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '한글 카테고리명',
  `name_en` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '영문 카테고리명',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '부모 카테고리 ID (루트는 NULL)',
  `depth` int NOT NULL DEFAULT '0' COMMENT '계층 깊이 (루트=0)',
  `path` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Path Enumeration (예: 1/2/3)',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `leaf` tinyint(1) NOT NULL DEFAULT '0' COMMENT '리프 노드 여부',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '카테고리 상태 (ACTIVE, HIDDEN, DEPRECATED)',
  `department` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '부서/부문 (MEN, WOMEN, UNISEX 등)',
  `category_group` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ETC' COMMENT '카테고리 그룹 (고시정보 연결용)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  `display_path` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '표시용 이름 경로 (예: 패션의류 > 상의 > 티셔츠)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`code`),
  KEY `idx_category_parent` (`parent_id`),
  KEY `idx_category_parent_sort` (`parent_id`,`sort_order`),
  KEY `idx_category_status` (`status`),
  KEY `idx_category_business` (`department`,`category_group`),
  KEY `idx_category_path` (`path`(255)),
  KEY `idx_category_updated` (`updated_at`),
  KEY `idx_category_group` (`category_group`),
  KEY `idx_category_deleted` (`deleted_at`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='카테고리 계층 구조 테이블 (Path Enumeration 패턴)';

CREATE TABLE `common_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `common_code_type_id` bigint NOT NULL COMMENT '코드 타입 ID',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드 값',
  `display_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '표시명',
  `display_order` int NOT NULL COMMENT '표시 순서',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_common_codes_type_code` (`common_code_type_id`,`code`),
  KEY `idx_common_codes_type_id` (`common_code_type_id`),
  KEY `idx_common_codes_active` (`is_active`),
  KEY `idx_common_codes_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공통 코드';

CREATE TABLE `notice_field` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `notice_category_id` bigint unsigned NOT NULL COMMENT '고시정보 카테고리 ID',
  `field_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '필드 코드',
  `field_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '필드 이름 (표시명)',
  `required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '필수 여부',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_field_category_code` (`notice_category_id`,`field_code`),
  KEY `idx_notice_field_category` (`notice_category_id`),
  KEY `idx_notice_field_sort` (`notice_category_id`,`sort_order`),
  CONSTRAINT `fk_notice_field_category` FOREIGN KEY (`notice_category_id`) REFERENCES `notice_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고시정보 필드 테이블';

CREATE TABLE `channel_option_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `sales_channel_id` bigint NOT NULL COMMENT '판매 채널 ID',
  `canonical_option_value_id` bigint NOT NULL COMMENT '표준 옵션값 ID',
  `external_option_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 옵션 코드',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_channel_option_mapping` (`sales_channel_id`,`canonical_option_value_id`),
  KEY `idx_channel_option_mapping_channel_id` (`sales_channel_id`),
  KEY `idx_channel_option_mapping_canonical_id` (`canonical_option_value_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='판매 채널 옵션 매핑';

CREATE TABLE `inbound_brand_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inbound_source_id` bigint NOT NULL COMMENT '인바운드 소스 ID',
  `external_brand_code` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 소스의 브랜드 코드',
  `external_brand_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 소스의 브랜드명',
  `internal_brand_id` bigint NOT NULL COMMENT '내부 Brand ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_ebm_source_code` (`inbound_source_id`,`external_brand_code`),
  KEY `idx_ebm_internal` (`internal_brand_id`),
  KEY `idx_ebm_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 소스 → 내부 브랜드 매핑';

CREATE TABLE `inbound_category_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inbound_source_id` bigint NOT NULL COMMENT '인바운드 소스 ID',
  `external_category_code` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 소스의 카테고리 코드',
  `external_category_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 소스의 카테고리명',
  `internal_category_id` bigint NOT NULL COMMENT '내부 Category ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_ecm_source_code` (`inbound_source_id`,`external_category_code`),
  KEY `idx_ecm_internal` (`internal_category_id`),
  KEY `idx_ecm_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 소스 → 내부 카테고리 매핑';

CREATE TABLE `inbound_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inbound_source_id` bigint NOT NULL COMMENT '인바운드 소스 ID',
  `external_product_code` varchar(255) NOT NULL,
  `external_brand_code` varchar(255) DEFAULT NULL,
  `external_category_code` varchar(255) DEFAULT NULL,
  `internal_brand_id` bigint DEFAULT NULL,
  `internal_category_id` bigint DEFAULT NULL,
  `internal_product_group_id` bigint DEFAULT NULL,
  `seller_id` bigint NOT NULL,
  `status` varchar(50) NOT NULL,
  `raw_payload` json DEFAULT NULL,
  `resolved_shipping_policy_id` bigint DEFAULT NULL,
  `resolved_refund_policy_id` bigint DEFAULT NULL,
  `resolved_notice_category_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source_product` (`inbound_source_id`,`external_product_code`),
  KEY `idx_status` (`status`),
  KEY `idx_internal_product_group` (`internal_product_group_id`),
  KEY `idx_seller` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `inbound_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sales_channel_id` bigint NOT NULL,
  `shop_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `external_order_no` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `external_ordered_at` timestamp NOT NULL,
  `buyer_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `buyer_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `buyer_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결제수단 (CARD, BANK_TRANSFER 등)',
  `total_payment_amount` int NOT NULL DEFAULT '0' COMMENT '주문 총 결제금액',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT '결제 완료 시점',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'RECEIVED',
  `internal_order_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '변환된 내부 Order ID',
  `failure_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inbound_orders_channel_ext` (`sales_channel_id`,`external_order_no`),
  KEY `idx_inbound_orders_status` (`status`),
  KEY `idx_inbound_orders_seller` (`seller_id`),
  KEY `idx_inbound_orders_external_ordered` (`sales_channel_id`,`external_ordered_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sales_channel_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `external_brand_code` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 브랜드 코드',
  `external_brand_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 브랜드명',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_scb_sc_code` (`sales_channel_id`,`external_brand_code`),
  KEY `idx_scb_sc` (`sales_channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 채널 브랜드 테이블';

CREATE TABLE `sales_channel_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `external_category_code` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 카테고리 코드',
  `external_category_name` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 카테고리명',
  `parent_id` bigint DEFAULT NULL COMMENT '부모 카테고리 ID (self-ref)',
  `depth` int NOT NULL DEFAULT '0',
  `path` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `leaf` tinyint(1) NOT NULL DEFAULT '0',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `display_path` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '표시용 이름 경로 (예: 식품 > 과자 > 스낵 > 젤리)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_scc_sc_code` (`sales_channel_id`,`external_category_code`),
  KEY `idx_scc_sc_parent` (`sales_channel_id`,`parent_id`),
  KEY `idx_scc_sc_depth` (`sales_channel_id`,`depth`),
  KEY `idx_scc_path` (`path`(255))
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 채널 카테고리 매핑 테이블';

CREATE TABLE `seller_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `address_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주소 유형 (SHIPPING, RETURN 등)',
  `address_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주소명 (예: 본사 출고지)',
  `zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '우편번호',
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '기본주소',
  `address_detail` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '상세주소',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '기본 주소 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_seller_addresses_seller_id` (`seller_id`),
  KEY `idx_seller_addresses_type` (`seller_id`,`address_type`),
  KEY `idx_seller_addresses_default` (`seller_id`,`is_default`),
  KEY `idx_seller_addresses_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 주소 (출고지/반품지)';

CREATE TABLE `seller_admins` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUIDv7 PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `auth_user_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '인증 서버 사용자 ID',
  `login_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '로그인 ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '관리자명',
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '연락처',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (PENDING_APPROVAL, ACTIVE, SUSPENDED, DEACTIVATED)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_admins_login_id` (`login_id`),
  KEY `idx_seller_admins_seller_id` (`seller_id`),
  KEY `idx_seller_admins_status` (`status`),
  KEY `idx_seller_admins_deleted` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 관리자';

CREATE TABLE `seller_business_infos` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `registration_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사업자등록번호',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '회사명',
  `representative` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '대표자명',
  `sale_report_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '통신판매업신고번호',
  `business_zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업장 우편번호',
  `business_address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업장 기본주소',
  `business_address_detail` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업장 상세주소',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_business_infos_reg_num` (`registration_number`),
  KEY `idx_seller_business_infos_seller_id` (`seller_id`),
  KEY `idx_seller_business_infos_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 사업자 정보';

CREATE TABLE `seller_contracts` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `commission_rate` decimal(5,2) NOT NULL COMMENT '수수료율 (%)',
  `contract_start_date` date NOT NULL COMMENT '계약 시작일',
  `contract_end_date` date DEFAULT NULL COMMENT '계약 종료일',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '계약 상태 (ACTIVE, EXPIRED, TERMINATED)',
  `special_terms` text COLLATE utf8mb4_unicode_ci COMMENT '특약 사항',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_seller_contracts_seller_id` (`seller_id`),
  KEY `idx_seller_contracts_status` (`status`),
  KEY `idx_seller_contracts_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 계약 정보';

CREATE TABLE `seller_cs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `cs_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CS 전화번호',
  `cs_mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CS 휴대폰번호',
  `cs_email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CS 이메일',
  `operating_start_time` time DEFAULT NULL COMMENT '운영 시작 시간',
  `operating_end_time` time DEFAULT NULL COMMENT '운영 종료 시간',
  `operating_days` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '운영 요일 (MON,TUE,...)',
  `kakao_channel_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '카카오 채널 URL',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_seller_cs_seller_id` (`seller_id`),
  KEY `idx_seller_cs_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 CS 정보';

CREATE TABLE `seller_sales_channels` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '채널 코드 (NAVER_COMMERCE, SETOF, BUYMA, LFMALL)',
  `connection_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '연동 상태 (CONNECTED, DISCONNECTED, SUSPENDED)',
  `api_key` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'API Key',
  `api_secret` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'API Secret',
  `access_token` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Access Token',
  `vendor_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 벤더 ID',
  `display_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '표시명',
  `shop_id` bigint NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_sales_channels_seller_channel` (`seller_id`,`sales_channel_id`),
  KEY `idx_seller_sales_channels_shop_id` (`shop_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 판매채널 연동';

CREATE TABLE `seller_settlements` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `bank_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '은행 코드',
  `bank_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '은행명',
  `account_number` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '계좌번호',
  `account_holder_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '예금주명',
  `settlement_cycle` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '정산 주기 (WEEKLY, BIWEEKLY, MONTHLY)',
  `settlement_day` int NOT NULL COMMENT '정산일',
  `is_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '계좌 인증 여부',
  `verified_at` timestamp NULL DEFAULT NULL COMMENT '계좌 인증일시',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_seller_settlements_seller_id` (`seller_id`),
  KEY `idx_seller_settlements_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 정산 정보';

CREATE TABLE `shipping_policies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `policy_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '정책명',
  `is_default_policy` tinyint(1) NOT NULL DEFAULT '0' COMMENT '기본 정책 여부',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 여부',
  `shipping_fee_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '배송비 유형 (FREE, FIXED, CONDITIONAL_FREE 등)',
  `base_fee` int DEFAULT NULL COMMENT '기본 배송비',
  `free_threshold` int DEFAULT NULL COMMENT '무료배송 기준 금액',
  `jeju_extra_fee` int DEFAULT NULL COMMENT '제주 추가 배송비',
  `island_extra_fee` int DEFAULT NULL COMMENT '도서산간 추가 배송비',
  `return_fee` int DEFAULT NULL COMMENT '반품 배송비',
  `exchange_fee` int DEFAULT NULL COMMENT '교환 배송비',
  `lead_time_min_days` int DEFAULT NULL COMMENT '최소 배송 소요일',
  `lead_time_max_days` int DEFAULT NULL COMMENT '최대 배송 소요일',
  `lead_time_cutoff_time` time DEFAULT NULL COMMENT '당일 출고 마감 시간',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_shipping_policies_seller_id` (`seller_id`),
  KEY `idx_shipping_policies_default` (`seller_id`,`is_default_policy`),
  KEY `idx_shipping_policies_active` (`is_active`),
  KEY `idx_shipping_policies_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송 정책';

CREATE TABLE `refund_policies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `policy_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '정책명',
  `is_default_policy` tinyint(1) NOT NULL DEFAULT '0' COMMENT '기본 정책 여부',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성 여부',
  `return_period_days` int NOT NULL COMMENT '반품 가능 기간 (일)',
  `exchange_period_days` int NOT NULL COMMENT '교환 가능 기간 (일)',
  `non_returnable_conditions` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '반품 불가 조건',
  `is_partial_refund_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '부분 환불 가능 여부',
  `is_inspection_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '검수 필요 여부',
  `inspection_period_days` int DEFAULT NULL COMMENT '검수 소요 기간 (일)',
  `additional_info` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '추가 안내사항',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_refund_policies_seller_id` (`seller_id`),
  KEY `idx_refund_policies_default` (`seller_id`,`is_default_policy`),
  KEY `idx_refund_policies_active` (`is_active`),
  KEY `idx_refund_policies_deleted` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='환불 정책';

CREATE TABLE `shop` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `shop_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부몰명',
  `account_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '계정 ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
  `channel_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `api_key` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `api_secret` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `access_token` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendor_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sc_account` (`sales_channel_id`,`account_id`),
  KEY `idx_shop_status` (`status`),
  KEY `idx_shop_updated` (`updated_at`),
  KEY `idx_shop_sc` (`sales_channel_id`),
  KEY `idx_shop_channel_code` (`channel_code`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부몰 마스터 테이블';

-- ============================================================
-- 3. 2차 의존 테이블 (1차 의존 테이블 참조)
-- ============================================================

CREATE TABLE `product_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `brand_id` bigint NOT NULL COMMENT '브랜드 ID',
  `category_id` bigint NOT NULL COMMENT '카테고리 ID',
  `shipping_policy_id` bigint NOT NULL COMMENT '배송 정책 ID',
  `refund_policy_id` bigint NOT NULL COMMENT '환불 정책 ID',
  `product_group_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품 그룹명',
  `option_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '옵션 유형 (SINGLE, COMBINATION)',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (DRAFT, ACTIVE, INACTIVE, DELETED)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_product_groups_seller_id` (`seller_id`),
  KEY `idx_product_groups_brand_id` (`brand_id`),
  KEY `idx_product_groups_category_id` (`category_id`),
  KEY `idx_product_groups_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹';

CREATE TABLE `brand_preset` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'Shop FK',
  `sales_channel_brand_id` bigint NOT NULL COMMENT 'SalesChannelBrand FK',
  `preset_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '프리셋 이름',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_bp_shop` (`shop_id`),
  KEY `idx_bp_scb` (`sales_channel_brand_id`),
  KEY `idx_bp_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='브랜드 프리셋 테이블';

CREATE TABLE `category_preset` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT 'Shop FK',
  `sales_channel_category_id` bigint NOT NULL COMMENT 'SalesChannelCategory FK',
  `preset_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '프리셋 이름',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_cp_shop` (`shop_id`),
  KEY `idx_cp_scc` (`sales_channel_category_id`),
  KEY `idx_cp_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='카테고리 프리셋 테이블';

CREATE TABLE `inbound_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inbound_order_id` bigint NOT NULL,
  `external_product_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `external_option_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `external_product_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `external_option_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `external_image_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_price` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '1',
  `total_amount` int NOT NULL DEFAULT '0',
  `discount_amount` int NOT NULL DEFAULT '0',
  `payment_amount` int NOT NULL DEFAULT '0',
  `receiver_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `receiver_zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `receiver_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `receiver_address_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delivery_request` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resolved_product_group_id` bigint DEFAULT NULL,
  `resolved_product_id` bigint DEFAULT NULL,
  `resolved_seller_id` bigint DEFAULT NULL,
  `resolved_brand_id` bigint DEFAULT NULL,
  `resolved_sku_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mapped` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inbound_order_items_order` (`inbound_order_id`),
  KEY `idx_inbound_order_items_ext_product` (`external_product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `from_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이전 상태',
  `to_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변경 상태',
  `changed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변경자',
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '변경 사유',
  `changed_at` timestamp NOT NULL COMMENT '변경 일시',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_histories_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (FK)',
  `product_group_id` bigint NOT NULL COMMENT '상품그룹 ID',
  `product_id` bigint NOT NULL COMMENT '상품 ID',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `brand_id` bigint NOT NULL COMMENT '브랜드 ID',
  `sku_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SKU 코드',
  `external_product_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '외부 상품 ID',
  `external_option_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 옵션 ID',
  `external_product_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 상품명',
  `external_option_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 옵션명',
  `external_image_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '외부 상품 이미지 URL',
  `unit_price` int NOT NULL DEFAULT '0' COMMENT '단가',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '수량',
  `total_amount` int NOT NULL DEFAULT '0' COMMENT '합계 금액',
  `discount_amount` int NOT NULL DEFAULT '0' COMMENT '할인 금액',
  `payment_amount` int NOT NULL DEFAULT '0' COMMENT '실결제 금액',
  `receiver_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '수령인 이름',
  `receiver_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 전화번호',
  `receiver_zipcode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 우편번호',
  `receiver_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 주소',
  `receiver_address_detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수령인 상세주소',
  `delivery_request` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '배송 요청사항',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_items_order_id` (`order_id`),
  KEY `idx_order_items_seller_id` (`seller_id`),
  KEY `idx_order_items_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `shipments` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PK (UUID)',
  `shipment_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '배송 번호',
  `order_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 ID (UUID)',
  `order_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문 번호',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '배송 상태 (READY, PREPARING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED)',
  `shipment_method_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '배송 방식 (COURIER, QUICK, VISIT, DESIGNATED_COURIER)',
  `courier_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '택배사 코드',
  `courier_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '택배사명',
  `tracking_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '운송장 번호',
  `order_confirmed_at` timestamp NULL DEFAULT NULL COMMENT '주문 확인일시',
  `shipped_at` timestamp NULL DEFAULT NULL COMMENT '출고일시',
  `delivered_at` timestamp NULL DEFAULT NULL COMMENT '배송 완료일시',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시 (soft delete)',
  PRIMARY KEY (`id`),
  KEY `idx_shipments_order_id` (`order_id`),
  KEY `idx_shipments_order_number` (`order_number`),
  KEY `idx_shipments_status` (`status`),
  KEY `idx_shipments_shipment_number` (`shipment_number`),
  KEY `idx_shipments_deleted` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='배송';

-- ============================================================
-- 4. 3차 의존 테이블 (product_groups 등 참조)
-- ============================================================

CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품 그룹 ID',
  `sku_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SKU 코드',
  `regular_price` int NOT NULL COMMENT '정상가',
  `current_price` int NOT NULL COMMENT '현재 판매가',
  `sale_price` int DEFAULT NULL COMMENT '할인가',
  `discount_rate` int NOT NULL COMMENT '할인율 (%)',
  `stock_quantity` int NOT NULL COMMENT '재고 수량',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (ACTIVE, INACTIVE, SOLD_OUT)',
  `sort_order` int NOT NULL COMMENT '정렬 순서',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_products_product_group_id` (`product_group_id`),
  KEY `idx_products_status` (`status`),
  KEY `idx_products_sku_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품';

CREATE TABLE `product_group_descriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품 그룹 ID',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '상세설명 HTML 콘텐츠',
  `cdn_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CDN 경로',
  `publish_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '발행 상태',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_product_group_descriptions_product_group_id` (`product_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹 상세설명';

CREATE TABLE `product_group_images` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품 그룹 ID',
  `origin_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '원본 이미지 URL',
  `uploaded_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '업로드된 이미지 URL',
  `image_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이미지 유형 (MAIN, SUB, DETAIL)',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시',
  PRIMARY KEY (`id`),
  KEY `idx_product_group_images_product_group_id` (`product_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 그룹 이미지';

CREATE TABLE `product_notices` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품 그룹 ID',
  `notice_category_id` bigint NOT NULL COMMENT '고시정보 카테고리 ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  PRIMARY KEY (`id`),
  KEY `idx_product_notices_product_group_id` (`product_group_id`),
  KEY `idx_product_notices_notice_category_id` (`notice_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 고시정보';

CREATE TABLE `seller_option_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품 그룹 ID',
  `option_group_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '옵션 그룹명',
  `canonical_option_group_id` bigint DEFAULT NULL COMMENT '표준 옵션 그룹 ID',
  `input_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PREDEFINED' COMMENT '입력 유형 (PREDEFINED: 사전 정의, FREE_INPUT: 자유 입력)',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_seller_option_groups_product_group_id` (`product_group_id`),
  KEY `idx_seller_option_groups_canonical_id` (`canonical_option_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 옵션 그룹';

CREATE TABLE `product_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_group_id` bigint NOT NULL,
  `previous_profile_id` bigint DEFAULT NULL,
  `profile_version` int NOT NULL DEFAULT '1',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `expected_analysis_count` int NOT NULL DEFAULT '3',
  `completed_analysis_count` int NOT NULL DEFAULT '0',
  `completed_analysis_types` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CSV: DESCRIPTION,OPTION,NOTICE',
  `extracted_attributes_json` text COLLATE utf8mb4_unicode_ci,
  `option_suggestions_json` text COLLATE utf8mb4_unicode_ci,
  `notice_suggestions_json` text COLLATE utf8mb4_unicode_ci,
  `decision_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `overall_confidence` double DEFAULT NULL,
  `decision_reasons_json` text COLLATE utf8mb4_unicode_ci,
  `decision_at` datetime(6) DEFAULT NULL,
  `raw_analysis_json` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `analyzed_at` datetime(6) DEFAULT NULL,
  `expired_at` datetime(6) DEFAULT NULL,
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description_content_hash` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_pp_product_group` (`product_group_id`),
  KEY `idx_pp_status_created` (`status`,`created_at`),
  KEY `idx_pp_product_group_version` (`product_group_id`,`profile_version`),
  KEY `idx_pp_expired` (`expired_at`),
  KEY `idx_pp_previous_profile` (`previous_profile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `outbound_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_group_id` bigint NOT NULL,
  `sales_channel_id` bigint NOT NULL,
  `external_product_id` varchar(255) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pg_channel` (`product_group_id`,`sales_channel_id`),
  KEY `idx_status` (`status`),
  KEY `idx_external_product` (`external_product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `brand_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `preset_id` bigint DEFAULT NULL COMMENT '브랜드 프리셋 ID (FK)',
  `sales_channel_brand_id` bigint NOT NULL COMMENT '외부 채널 브랜드 ID',
  `internal_brand_id` bigint NOT NULL COMMENT '내부 브랜드 ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_bm_preset_internal` (`preset_id`,`internal_brand_id`),
  KEY `idx_bm_internal` (`internal_brand_id`),
  KEY `idx_bm_status` (`status`),
  KEY `idx_bm_preset` (`preset_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부<->내부 브랜드 매핑 테이블';

CREATE TABLE `category_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `preset_id` bigint DEFAULT NULL COMMENT '카테고리 프리셋 ID (FK)',
  `sales_channel_category_id` bigint NOT NULL COMMENT '외부 채널 카테고리 ID',
  `internal_category_id` bigint NOT NULL COMMENT '내부 카테고리 ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_cm_preset_internal` (`preset_id`,`internal_category_id`),
  KEY `idx_cm_internal` (`internal_category_id`),
  KEY `idx_cm_status` (`status`),
  KEY `idx_cm_preset` (`preset_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부<->내부 카테고리 매핑 테이블';

-- ============================================================
-- 5. 4차 의존 테이블 (products, product_group_descriptions 등 참조)
-- ============================================================

CREATE TABLE `description_images` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_description_id` bigint NOT NULL COMMENT '상품 그룹 상세설명 ID',
  `origin_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '원본 이미지 URL',
  `uploaded_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '업로드된 이미지 URL',
  `sort_order` int NOT NULL COMMENT '정렬 순서',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '삭제일시',
  PRIMARY KEY (`id`),
  KEY `idx_description_images_description_id` (`product_group_description_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상세설명 이미지';

CREATE TABLE `product_notice_entries` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_notice_id` bigint NOT NULL COMMENT '상품 고시정보 ID',
  `notice_field_id` bigint NOT NULL COMMENT '고시정보 항목 ID',
  `field_value` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '항목 값',
  PRIMARY KEY (`id`),
  KEY `idx_product_notice_entries_notice_id` (`product_notice_id`),
  KEY `idx_product_notice_entries_field_id` (`notice_field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 고시정보 항목';

CREATE TABLE `product_option_mappings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_id` bigint NOT NULL COMMENT '상품 ID',
  `seller_option_value_id` bigint NOT NULL COMMENT '셀러 옵션값 ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_product_option_mappings_product_id` (`product_id`),
  KEY `idx_product_option_mappings_option_value_id` (`seller_option_value_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품-옵션 매핑';

CREATE TABLE `seller_option_values` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_option_group_id` bigint NOT NULL COMMENT '셀러 옵션 그룹 ID',
  `option_value_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '옵션값 이름',
  `canonical_option_value_id` bigint DEFAULT NULL COMMENT '표준 옵션값 ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_seller_option_values_group_id` (`seller_option_group_id`),
  KEY `idx_seller_option_values_canonical_id` (`canonical_option_value_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 옵션값';

CREATE TABLE `legacy_product_id_mappings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `legacy_product_id` bigint NOT NULL COMMENT '레거시 Product(SKU) ID (luxurydb)',
  `internal_product_id` bigint NOT NULL COMMENT '내부 Product ID',
  `legacy_product_group_id` bigint NOT NULL COMMENT '레거시 상품그룹 ID (그룹 참조)',
  `internal_product_group_id` bigint NOT NULL COMMENT '내부 상품그룹 ID',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_legacy_product_id_mapping_legacy` (`legacy_product_id`),
  KEY `idx_legacy_product_id_mapping_internal` (`internal_product_id`),
  KEY `idx_legacy_product_id_mapping_group` (`legacy_product_group_id`),
  KEY `idx_legacy_product_id_mapping_internal_group` (`internal_product_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='레거시 <-> 내부 Product(SKU) ID 매핑';

-- ============================================================
-- 6. Outbox / 비동기 처리 테이블
-- ============================================================

CREATE TABLE `image_upload_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `source_id` bigint NOT NULL COMMENT '소스 엔티티 ID',
  `source_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '소스 유형 (PRODUCT_GROUP_IMAGE, DESCRIPTION_IMAGE)',
  `origin_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '원본 이미지 URL',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '처리 상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리 완료일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 락 버전 (@Version)',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등성 키',
  `download_task_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_image_upload_outboxes_idempotency_key` (`idempotency_key`),
  KEY `idx_image_upload_outboxes_status` (`status`),
  KEY `idx_image_upload_outboxes_source` (`source_type`,`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='이미지 업로드 Outbox';

CREATE TABLE `image_transform_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_image_id` bigint NOT NULL COMMENT '소스 이미지 ID',
  `source_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PRODUCT_GROUP_IMAGE, DESCRIPTION_IMAGE',
  `uploaded_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '업로드된 CDN URL',
  `variant_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SMALL_WEBP, MEDIUM_WEBP, LARGE_WEBP, ORIGINAL_WEBP',
  `file_asset_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `transform_request_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'FileFlow 변환 요청 ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `retry_count` int NOT NULL DEFAULT '0',
  `max_retry` int NOT NULL DEFAULT '3',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `processed_at` timestamp NULL DEFAULT NULL,
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint NOT NULL DEFAULT '0',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ito_idempotency_key` (`idempotency_key`),
  KEY `idx_ito_status` (`status`),
  KEY `idx_ito_source` (`source_type`,`source_image_id`),
  KEY `idx_ito_transform_request` (`transform_request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `image_variants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source_image_id` bigint NOT NULL COMMENT '원본 이미지 ID',
  `source_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PRODUCT_GROUP_IMAGE, DESCRIPTION_IMAGE',
  `variant_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SMALL_WEBP, MEDIUM_WEBP, LARGE_WEBP, ORIGINAL_WEBP',
  `result_asset_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'FileFlow 변환 에셋 ID',
  `variant_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변환된 이미지 CDN URL',
  `width` int DEFAULT NULL,
  `height` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_iv_source_variant` (`source_image_id`,`source_type`,`variant_type`),
  KEY `idx_iv_source` (`source_type`,`source_image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `intelligence_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_group_id` bigint NOT NULL,
  `profile_id` bigint DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `retry_count` int NOT NULL DEFAULT '0',
  `max_retry` int NOT NULL DEFAULT '3',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint NOT NULL DEFAULT '0',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_io_idempotency` (`idempotency_key`),
  KEY `idx_io_status_created` (`status`,`created_at`),
  KEY `idx_io_status_updated` (`status`,`updated_at`),
  KEY `idx_io_product_group` (`product_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `legacy_conversion_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `legacy_product_group_id` bigint NOT NULL COMMENT '레거시 상품그룹 ID (luxurydb)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PROCESSING, COMPLETED, FAILED',
  `retry_count` int NOT NULL DEFAULT '0',
  `max_retry` int NOT NULL DEFAULT '3',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `processed_at` datetime(6) DEFAULT NULL,
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_legacy_conversion_outbox_status_created` (`status`,`created_at`),
  KEY `idx_legacy_conversion_outbox_status_updated` (`status`,`updated_at`),
  KEY `idx_legacy_conversion_outbox_legacy_group_id` (`legacy_product_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='레거시 상품 -> 내부 상품 변환 Outbox';

CREATE TABLE `outbound_sync_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `product_group_id` bigint NOT NULL COMMENT '상품그룹 ID',
  `sales_channel_id` bigint NOT NULL COMMENT '판매채널 ID',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `sync_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '연동 타입 (CREATE, UPDATE, DELETE)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 페이로드',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',
  `processed_at` datetime(6) DEFAULT NULL COMMENT '처리일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 잠금 버전',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등성 키',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_external_product_sync_outboxes_idempotency` (`idempotency_key`),
  KEY `idx_external_product_sync_outboxes_product_group_id` (`product_group_id`),
  KEY `idx_external_product_sync_outboxes_status_retry` (`status`,`retry_count`),
  KEY `idx_external_product_sync_outboxes_seller_id` (`seller_id`),
  KEY `idx_external_product_sync_outboxes_product_group_status` (`product_group_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='외부 상품 연동 Outbox';

CREATE TABLE `outbound_seller_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seller_id` bigint NOT NULL,
  `entity_id` bigint NOT NULL,
  `entity_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `operation_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `retry_count` int NOT NULL DEFAULT '0',
  `max_retry` int NOT NULL DEFAULT '3',
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  `processed_at` timestamp(6) NULL DEFAULT NULL,
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` bigint NOT NULL DEFAULT '0',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_idempotency` (`idempotency_key`),
  KEY `idx_status_created` (`status`,`created_at`),
  KEY `idx_status_updated` (`status`,`updated_at`),
  KEY `idx_seller_entity` (`seller_id`,`entity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `seller_auth_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 페이로드',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 잠금 버전',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등성 키',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_auth_outboxes_idempotency` (`idempotency_key`),
  KEY `idx_seller_auth_outboxes_seller_id` (`seller_id`),
  KEY `idx_seller_auth_outboxes_status` (`status`),
  KEY `idx_seller_auth_outboxes_status_retry` (`status`,`retry_count`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 인증 Outbox';

CREATE TABLE `seller_admin_auth_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_admin_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '셀러 관리자 ID (UUIDv7)',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 페이로드',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 잠금 버전',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등성 키',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_admin_auth_outboxes_idempotency` (`idempotency_key`),
  KEY `idx_seller_admin_auth_outboxes_seller_admin_id` (`seller_admin_id`),
  KEY `idx_seller_admin_auth_outboxes_status` (`status`),
  KEY `idx_seller_admin_auth_outboxes_status_retry` (`status`,`retry_count`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 관리자 인증 Outbox';

CREATE TABLE `seller_admin_email_outboxes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `seller_id` bigint NOT NULL COMMENT '셀러 ID',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'JSON 페이로드 (recipientEmail, sellerName, companyName, tenantId, organizationId)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상태 (PENDING, PROCESSING, COMPLETED, FAILED)',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `max_retry` int NOT NULL DEFAULT '3' COMMENT '최대 재시도 횟수',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '처리일시',
  `error_message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `version` bigint NOT NULL DEFAULT '0' COMMENT '낙관적 잠금 버전',
  `idempotency_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '멱등성 키',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seller_admin_email_outboxes_idempotency` (`idempotency_key`),
  KEY `idx_seller_admin_email_outboxes_seller_id` (`seller_id`),
  KEY `idx_seller_admin_email_outboxes_status` (`status`),
  KEY `idx_seller_admin_email_outboxes_status_retry` (`status`,`retry_count`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='셀러 관리자 이메일 Outbox';
