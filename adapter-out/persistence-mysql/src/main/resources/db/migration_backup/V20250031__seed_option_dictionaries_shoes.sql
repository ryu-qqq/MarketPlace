-- ================================================
-- V20250031: OptionDictionary 시드 데이터 (신발)
-- 신발 사이즈 값 마스터 데이터
-- ================================================

-- 옵션 그룹 ID를 변수로 저장
SET @og_shoes_eu = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_EU');
SET @og_shoes_us_men = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_US_MEN');
SET @og_shoes_us_women = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_US_WOMEN');
SET @og_shoes_us_kids = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_US_KIDS');
SET @og_shoes_uk_men = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_UK_MEN');
SET @og_shoes_uk_women = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_UK_WOMEN');
SET @og_shoes_kr = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_KR');
SET @og_shoes_it = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_IT');
SET @og_shoes_fr = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_FR');
SET @og_shoes_jp = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_JP');

-- ===========================================
-- 1. EU 신발 사이즈 (35-48, 0.5 단위 포함)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_eu, '35', 'EU 35', 'EU 35', 1),
(@og_shoes_eu, '35.5', 'EU 35.5', 'EU 35.5', 2),
(@og_shoes_eu, '36', 'EU 36', 'EU 36', 3),
(@og_shoes_eu, '36.5', 'EU 36.5', 'EU 36.5', 4),
(@og_shoes_eu, '37', 'EU 37', 'EU 37', 5),
(@og_shoes_eu, '37.5', 'EU 37.5', 'EU 37.5', 6),
(@og_shoes_eu, '38', 'EU 38', 'EU 38', 7),
(@og_shoes_eu, '38.5', 'EU 38.5', 'EU 38.5', 8),
(@og_shoes_eu, '39', 'EU 39', 'EU 39', 9),
(@og_shoes_eu, '39.5', 'EU 39.5', 'EU 39.5', 10),
(@og_shoes_eu, '40', 'EU 40', 'EU 40', 11),
(@og_shoes_eu, '40.5', 'EU 40.5', 'EU 40.5', 12),
(@og_shoes_eu, '41', 'EU 41', 'EU 41', 13),
(@og_shoes_eu, '41.5', 'EU 41.5', 'EU 41.5', 14),
(@og_shoes_eu, '42', 'EU 42', 'EU 42', 15),
(@og_shoes_eu, '42.5', 'EU 42.5', 'EU 42.5', 16),
(@og_shoes_eu, '43', 'EU 43', 'EU 43', 17),
(@og_shoes_eu, '43.5', 'EU 43.5', 'EU 43.5', 18),
(@og_shoes_eu, '44', 'EU 44', 'EU 44', 19),
(@og_shoes_eu, '44.5', 'EU 44.5', 'EU 44.5', 20),
(@og_shoes_eu, '45', 'EU 45', 'EU 45', 21),
(@og_shoes_eu, '45.5', 'EU 45.5', 'EU 45.5', 22),
(@og_shoes_eu, '46', 'EU 46', 'EU 46', 23),
(@og_shoes_eu, '46.5', 'EU 46.5', 'EU 46.5', 24),
(@og_shoes_eu, '47', 'EU 47', 'EU 47', 25),
(@og_shoes_eu, '47.5', 'EU 47.5', 'EU 47.5', 26),
(@og_shoes_eu, '48', 'EU 48', 'EU 48', 27);

-- ===========================================
-- 2. US 남성 신발 사이즈 (5-15, 0.5 단위 포함)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_us_men, '5', 'US 5', 'US 5', 1),
(@og_shoes_us_men, '5.5', 'US 5.5', 'US 5.5', 2),
(@og_shoes_us_men, '6', 'US 6', 'US 6', 3),
(@og_shoes_us_men, '6.5', 'US 6.5', 'US 6.5', 4),
(@og_shoes_us_men, '7', 'US 7', 'US 7', 5),
(@og_shoes_us_men, '7.5', 'US 7.5', 'US 7.5', 6),
(@og_shoes_us_men, '8', 'US 8', 'US 8', 7),
(@og_shoes_us_men, '8.5', 'US 8.5', 'US 8.5', 8),
(@og_shoes_us_men, '9', 'US 9', 'US 9', 9),
(@og_shoes_us_men, '9.5', 'US 9.5', 'US 9.5', 10),
(@og_shoes_us_men, '10', 'US 10', 'US 10', 11),
(@og_shoes_us_men, '10.5', 'US 10.5', 'US 10.5', 12),
(@og_shoes_us_men, '11', 'US 11', 'US 11', 13),
(@og_shoes_us_men, '11.5', 'US 11.5', 'US 11.5', 14),
(@og_shoes_us_men, '12', 'US 12', 'US 12', 15),
(@og_shoes_us_men, '12.5', 'US 12.5', 'US 12.5', 16),
(@og_shoes_us_men, '13', 'US 13', 'US 13', 17),
(@og_shoes_us_men, '13.5', 'US 13.5', 'US 13.5', 18),
(@og_shoes_us_men, '14', 'US 14', 'US 14', 19),
(@og_shoes_us_men, '15', 'US 15', 'US 15', 20);

-- ===========================================
-- 3. US 여성 신발 사이즈 (4-12, 0.5 단위 포함)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_us_women, '4', 'US 4', 'US 4', 1),
(@og_shoes_us_women, '4.5', 'US 4.5', 'US 4.5', 2),
(@og_shoes_us_women, '5', 'US 5', 'US 5', 3),
(@og_shoes_us_women, '5.5', 'US 5.5', 'US 5.5', 4),
(@og_shoes_us_women, '6', 'US 6', 'US 6', 5),
(@og_shoes_us_women, '6.5', 'US 6.5', 'US 6.5', 6),
(@og_shoes_us_women, '7', 'US 7', 'US 7', 7),
(@og_shoes_us_women, '7.5', 'US 7.5', 'US 7.5', 8),
(@og_shoes_us_women, '8', 'US 8', 'US 8', 9),
(@og_shoes_us_women, '8.5', 'US 8.5', 'US 8.5', 10),
(@og_shoes_us_women, '9', 'US 9', 'US 9', 11),
(@og_shoes_us_women, '9.5', 'US 9.5', 'US 9.5', 12),
(@og_shoes_us_women, '10', 'US 10', 'US 10', 13),
(@og_shoes_us_women, '10.5', 'US 10.5', 'US 10.5', 14),
(@og_shoes_us_women, '11', 'US 11', 'US 11', 15),
(@og_shoes_us_women, '11.5', 'US 11.5', 'US 11.5', 16),
(@og_shoes_us_women, '12', 'US 12', 'US 12', 17);

-- ===========================================
-- 4. UK 남성 신발 사이즈 (4-14, 0.5 단위 포함)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_uk_men, '4', 'UK 4', 'UK 4', 1),
(@og_shoes_uk_men, '4.5', 'UK 4.5', 'UK 4.5', 2),
(@og_shoes_uk_men, '5', 'UK 5', 'UK 5', 3),
(@og_shoes_uk_men, '5.5', 'UK 5.5', 'UK 5.5', 4),
(@og_shoes_uk_men, '6', 'UK 6', 'UK 6', 5),
(@og_shoes_uk_men, '6.5', 'UK 6.5', 'UK 6.5', 6),
(@og_shoes_uk_men, '7', 'UK 7', 'UK 7', 7),
(@og_shoes_uk_men, '7.5', 'UK 7.5', 'UK 7.5', 8),
(@og_shoes_uk_men, '8', 'UK 8', 'UK 8', 9),
(@og_shoes_uk_men, '8.5', 'UK 8.5', 'UK 8.5', 10),
(@og_shoes_uk_men, '9', 'UK 9', 'UK 9', 11),
(@og_shoes_uk_men, '9.5', 'UK 9.5', 'UK 9.5', 12),
(@og_shoes_uk_men, '10', 'UK 10', 'UK 10', 13),
(@og_shoes_uk_men, '10.5', 'UK 10.5', 'UK 10.5', 14),
(@og_shoes_uk_men, '11', 'UK 11', 'UK 11', 15),
(@og_shoes_uk_men, '11.5', 'UK 11.5', 'UK 11.5', 16),
(@og_shoes_uk_men, '12', 'UK 12', 'UK 12', 17),
(@og_shoes_uk_men, '13', 'UK 13', 'UK 13', 18),
(@og_shoes_uk_men, '14', 'UK 14', 'UK 14', 19);

-- ===========================================
-- 5. UK 여성 신발 사이즈 (2-9, 0.5 단위 포함)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_uk_women, '2', 'UK 2', 'UK 2', 1),
(@og_shoes_uk_women, '2.5', 'UK 2.5', 'UK 2.5', 2),
(@og_shoes_uk_women, '3', 'UK 3', 'UK 3', 3),
(@og_shoes_uk_women, '3.5', 'UK 3.5', 'UK 3.5', 4),
(@og_shoes_uk_women, '4', 'UK 4', 'UK 4', 5),
(@og_shoes_uk_women, '4.5', 'UK 4.5', 'UK 4.5', 6),
(@og_shoes_uk_women, '5', 'UK 5', 'UK 5', 7),
(@og_shoes_uk_women, '5.5', 'UK 5.5', 'UK 5.5', 8),
(@og_shoes_uk_women, '6', 'UK 6', 'UK 6', 9),
(@og_shoes_uk_women, '6.5', 'UK 6.5', 'UK 6.5', 10),
(@og_shoes_uk_women, '7', 'UK 7', 'UK 7', 11),
(@og_shoes_uk_women, '7.5', 'UK 7.5', 'UK 7.5', 12),
(@og_shoes_uk_women, '8', 'UK 8', 'UK 8', 13),
(@og_shoes_uk_women, '8.5', 'UK 8.5', 'UK 8.5', 14),
(@og_shoes_uk_women, '9', 'UK 9', 'UK 9', 15);

-- ===========================================
-- 6. KR 신발 사이즈 (220-300mm, 5mm 단위)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_kr, '220', '220mm', '220mm', 1),
(@og_shoes_kr, '225', '225mm', '225mm', 2),
(@og_shoes_kr, '230', '230mm', '230mm', 3),
(@og_shoes_kr, '235', '235mm', '235mm', 4),
(@og_shoes_kr, '240', '240mm', '240mm', 5),
(@og_shoes_kr, '245', '245mm', '245mm', 6),
(@og_shoes_kr, '250', '250mm', '250mm', 7),
(@og_shoes_kr, '255', '255mm', '255mm', 8),
(@og_shoes_kr, '260', '260mm', '260mm', 9),
(@og_shoes_kr, '265', '265mm', '265mm', 10),
(@og_shoes_kr, '270', '270mm', '270mm', 11),
(@og_shoes_kr, '275', '275mm', '275mm', 12),
(@og_shoes_kr, '280', '280mm', '280mm', 13),
(@og_shoes_kr, '285', '285mm', '285mm', 14),
(@og_shoes_kr, '290', '290mm', '290mm', 15),
(@og_shoes_kr, '295', '295mm', '295mm', 16),
(@og_shoes_kr, '300', '300mm', '300mm', 17);

-- ===========================================
-- 7. IT 신발 사이즈 (이탈리아 34-47)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_it, '34', 'IT 34', 'IT 34', 1),
(@og_shoes_it, '34.5', 'IT 34.5', 'IT 34.5', 2),
(@og_shoes_it, '35', 'IT 35', 'IT 35', 3),
(@og_shoes_it, '35.5', 'IT 35.5', 'IT 35.5', 4),
(@og_shoes_it, '36', 'IT 36', 'IT 36', 5),
(@og_shoes_it, '36.5', 'IT 36.5', 'IT 36.5', 6),
(@og_shoes_it, '37', 'IT 37', 'IT 37', 7),
(@og_shoes_it, '37.5', 'IT 37.5', 'IT 37.5', 8),
(@og_shoes_it, '38', 'IT 38', 'IT 38', 9),
(@og_shoes_it, '38.5', 'IT 38.5', 'IT 38.5', 10),
(@og_shoes_it, '39', 'IT 39', 'IT 39', 11),
(@og_shoes_it, '39.5', 'IT 39.5', 'IT 39.5', 12),
(@og_shoes_it, '40', 'IT 40', 'IT 40', 13),
(@og_shoes_it, '40.5', 'IT 40.5', 'IT 40.5', 14),
(@og_shoes_it, '41', 'IT 41', 'IT 41', 15),
(@og_shoes_it, '41.5', 'IT 41.5', 'IT 41.5', 16),
(@og_shoes_it, '42', 'IT 42', 'IT 42', 17),
(@og_shoes_it, '42.5', 'IT 42.5', 'IT 42.5', 18),
(@og_shoes_it, '43', 'IT 43', 'IT 43', 19),
(@og_shoes_it, '43.5', 'IT 43.5', 'IT 43.5', 20),
(@og_shoes_it, '44', 'IT 44', 'IT 44', 21),
(@og_shoes_it, '44.5', 'IT 44.5', 'IT 44.5', 22),
(@og_shoes_it, '45', 'IT 45', 'IT 45', 23),
(@og_shoes_it, '46', 'IT 46', 'IT 46', 24),
(@og_shoes_it, '47', 'IT 47', 'IT 47', 25);

-- ===========================================
-- 8. JP 신발 사이즈 (22.0-30.0cm, 0.5 단위)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_shoes_jp, '22.0', '22.0cm', '22.0cm', 1),
(@og_shoes_jp, '22.5', '22.5cm', '22.5cm', 2),
(@og_shoes_jp, '23.0', '23.0cm', '23.0cm', 3),
(@og_shoes_jp, '23.5', '23.5cm', '23.5cm', 4),
(@og_shoes_jp, '24.0', '24.0cm', '24.0cm', 5),
(@og_shoes_jp, '24.5', '24.5cm', '24.5cm', 6),
(@og_shoes_jp, '25.0', '25.0cm', '25.0cm', 7),
(@og_shoes_jp, '25.5', '25.5cm', '25.5cm', 8),
(@og_shoes_jp, '26.0', '26.0cm', '26.0cm', 9),
(@og_shoes_jp, '26.5', '26.5cm', '26.5cm', 10),
(@og_shoes_jp, '27.0', '27.0cm', '27.0cm', 11),
(@og_shoes_jp, '27.5', '27.5cm', '27.5cm', 12),
(@og_shoes_jp, '28.0', '28.0cm', '28.0cm', 13),
(@og_shoes_jp, '28.5', '28.5cm', '28.5cm', 14),
(@og_shoes_jp, '29.0', '29.0cm', '29.0cm', 15),
(@og_shoes_jp, '29.5', '29.5cm', '29.5cm', 16),
(@og_shoes_jp, '30.0', '30.0cm', '30.0cm', 17);
