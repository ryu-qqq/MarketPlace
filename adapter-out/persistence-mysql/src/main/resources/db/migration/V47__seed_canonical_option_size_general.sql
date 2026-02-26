-- ============================================
-- 캐노니컬 옵션 시드 데이터 - SIZE_GENERAL
-- ============================================
-- 범용 사이즈: 의류/신발 외 일반 상품용 레터 사이즈
-- 가방, 펫용품, 잡화 등에서 사용
-- ============================================

-- ============================================
-- 1. 캐노니컬 옵션 그룹: SIZE_GENERAL
-- ============================================
INSERT INTO canonical_option_group (id, code, name_ko, name_en, active) VALUES
(10, 'SIZE_GENERAL', '범용 사이즈', 'General Size', TRUE);

-- ============================================
-- 2. 캐노니컬 옵션 값: SIZE_GENERAL (9개)
-- ============================================
INSERT INTO canonical_option_value (canonical_option_group_id, code, name_ko, name_en, sort_order) VALUES
(10, 'FREE',  '프리',  'Free',  1),
(10, 'XS',    'XS',    'XS',    2),
(10, 'S',     'S',     'S',     3),
(10, 'M',     'M',     'M',     4),
(10, 'L',     'L',     'L',     5),
(10, 'XL',    'XL',    'XL',    6),
(10, 'XXL',   'XXL',   'XXL',   7),
(10, 'XXXL',  'XXXL',  'XXXL',  8),
(10, 'XXXXL', 'XXXXL', 'XXXXL', 9);
