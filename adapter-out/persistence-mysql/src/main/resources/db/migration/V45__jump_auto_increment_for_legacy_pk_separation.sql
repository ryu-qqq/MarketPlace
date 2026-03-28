-- ============================================================
-- V45: Market 스키마 auto_increment 점프
-- ============================================================
-- 목적: 레거시 PK 범위(luxurydb)와 market PK 범위를 분리하여
--       나중에 레거시를 완전히 끊었을 때 PK 충돌을 방지합니다.
--
-- 레거시 max PK → market 시작 PK (10배 여유)
--   product_group:     ~520K  → 10,000,001
--   product:           ~9M    → 100,000,001
--   product_group_image: ~7.5M → 100,000,001
--   product_option_mapping: ~12M → 100,000,001
--   seller_option_group: ~2.2M → 10,000,001
--   seller_option_value: ~8.3M → 100,000,001
--   product_notice:     (product_group_id FK, auto_increment 아님)
--   product_group_description: (product_group_id FK, auto_increment 아님)
--
-- 적용 대상: prod 환경만 (stage는 테스트 데이터이므로 불필요)
-- ============================================================

ALTER TABLE product_groups AUTO_INCREMENT = 10000001;
ALTER TABLE products AUTO_INCREMENT = 100000001;
ALTER TABLE product_group_images AUTO_INCREMENT = 100000001;
ALTER TABLE product_option_mappings AUTO_INCREMENT = 100000001;
ALTER TABLE seller_option_groups AUTO_INCREMENT = 10000001;
ALTER TABLE seller_option_values AUTO_INCREMENT = 100000001;
-- product_group_prices: 아직 테이블 미생성 — 생성 후 별도 처리
