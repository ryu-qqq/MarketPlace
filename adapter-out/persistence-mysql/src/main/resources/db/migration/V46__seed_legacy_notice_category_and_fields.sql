-- ============================================================
-- V46: 레거시 전용 고시정보 카테고리 + 필드 시딩
-- ============================================================
-- 레거시 OMS에서 사용하는 고정 9개 고시정보 필드를
-- 표준 notice_category + notice_field 체계로 관리합니다.
--
-- 레거시 API로 들어오는 상품은 이 카테고리(LEGACY_DEFAULT)를 사용하고,
-- 레거시가 완전히 끊기면 이 카테고리를 비활성화하면 됩니다.
--
-- field_code는 레거시 Request DTO의 필드명과 1:1 매핑합니다:
--   material, color, size, maker, origin,
--   washingMethod, yearMonth, assuranceStandard, asPhone
-- ============================================================

INSERT INTO notice_category (id, code, name_ko, name_en, target_category_group, active)
VALUES (100, 'LEGACY_DEFAULT', '레거시 기본 고시정보', 'Legacy Default Notice', 'LEGACY_DEFAULT', 1);

INSERT INTO notice_field (id, notice_category_id, field_code, field_name, required, sort_order) VALUES
(100, 100, 'material',           '제품 소재',        0, 1),
(101, 100, 'color',              '색상',             0, 2),
(102, 100, 'size',               '치수',             0, 3),
(103, 100, 'maker',              '제조자',           0, 4),
(104, 100, 'origin',             '원산지',           0, 5),
(105, 100, 'washingMethod',      '세탁방법',         0, 6),
(106, 100, 'yearMonth',          '제조년월',         0, 7),
(107, 100, 'assuranceStandard',  '품질보증기준',     0, 8),
(108, 100, 'asPhone',            'A/S 연락처',       0, 9);
