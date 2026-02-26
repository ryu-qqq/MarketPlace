-- ============================================
-- category.depth 컬럼 타입 변경
-- TINYINT UNSIGNED → INT (Hibernate 스키마 검증 호환)
-- ============================================

ALTER TABLE category MODIFY COLUMN depth INT NOT NULL DEFAULT 0 COMMENT '계층 깊이 (루트=0)';
