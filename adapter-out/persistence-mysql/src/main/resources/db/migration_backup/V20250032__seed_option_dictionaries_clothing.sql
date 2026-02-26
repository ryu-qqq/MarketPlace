-- ================================================
-- V20250032: OptionDictionary 시드 데이터 (의류 및 악세서리)
-- 의류 사이즈 및 기타 옵션 값 마스터 데이터
-- ================================================

-- 옵션 그룹 ID를 변수로 저장
SET @og_clothing_int = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_INT');
SET @og_clothing_kr_top = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_KR_TOP');
SET @og_clothing_kr_bottom = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_KR_BOTTOM');
SET @og_clothing_eu = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_EU');
SET @og_clothing_us = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_US');
SET @og_clothing_uk = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_UK');
SET @og_clothing_it = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_IT');
SET @og_clothing_fr = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_FR');
SET @og_clothing_jp = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_JP');
SET @og_denim_waist = (SELECT id FROM option_group WHERE code = 'SIZE_DENIM_WAIST');
SET @og_denim_length = (SELECT id FROM option_group WHERE code = 'SIZE_DENIM_LENGTH');
SET @og_ring_kr = (SELECT id FROM option_group WHERE code = 'SIZE_RING_KR');
SET @og_ring_us = (SELECT id FROM option_group WHERE code = 'SIZE_RING_US');
SET @og_ring_eu = (SELECT id FROM option_group WHERE code = 'SIZE_RING_EU');
SET @og_belt_cm = (SELECT id FROM option_group WHERE code = 'SIZE_BELT_CM');
SET @og_color = (SELECT id FROM option_group WHERE code = 'COLOR');
SET @og_mattress = (SELECT id FROM option_group WHERE code = 'SIZE_MATTRESS');

-- ===========================================
-- 1. International 의류 사이즈 (XXS-4XL)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_int, 'XXS', 'XXS', 'XXS', 1),
(@og_clothing_int, 'XS', 'XS', 'XS', 2),
(@og_clothing_int, 'S', 'S', 'S', 3),
(@og_clothing_int, 'M', 'M', 'M', 4),
(@og_clothing_int, 'L', 'L', 'L', 5),
(@og_clothing_int, 'XL', 'XL', 'XL', 6),
(@og_clothing_int, 'XXL', 'XXL', 'XXL', 7),
(@og_clothing_int, '3XL', '3XL', '3XL', 8),
(@og_clothing_int, '4XL', '4XL', '4XL', 9),
(@og_clothing_int, 'FREE', '프리사이즈', 'FREE', 10);

-- ===========================================
-- 2. KR 상의 사이즈 (44-120)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_kr_top, '44', '44', '44', 1),
(@og_clothing_kr_top, '55', '55 (S)', '55 (S)', 2),
(@og_clothing_kr_top, '66', '66 (M)', '66 (M)', 3),
(@og_clothing_kr_top, '77', '77 (L)', '77 (L)', 4),
(@og_clothing_kr_top, '88', '88 (XL)', '88 (XL)', 5),
(@og_clothing_kr_top, '90', '90', '90', 6),
(@og_clothing_kr_top, '95', '95 (M)', '95 (M)', 7),
(@og_clothing_kr_top, '100', '100 (L)', '100 (L)', 8),
(@og_clothing_kr_top, '105', '105 (XL)', '105 (XL)', 9),
(@og_clothing_kr_top, '110', '110 (2XL)', '110 (2XL)', 10),
(@og_clothing_kr_top, '115', '115 (3XL)', '115 (3XL)', 11),
(@og_clothing_kr_top, '120', '120 (4XL)', '120 (4XL)', 12);

-- ===========================================
-- 3. KR 하의 사이즈 (인치 25-40)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_kr_bottom, '25', '25인치', '25 inch', 1),
(@og_clothing_kr_bottom, '26', '26인치', '26 inch', 2),
(@og_clothing_kr_bottom, '27', '27인치', '27 inch', 3),
(@og_clothing_kr_bottom, '28', '28인치', '28 inch', 4),
(@og_clothing_kr_bottom, '29', '29인치', '29 inch', 5),
(@og_clothing_kr_bottom, '30', '30인치', '30 inch', 6),
(@og_clothing_kr_bottom, '31', '31인치', '31 inch', 7),
(@og_clothing_kr_bottom, '32', '32인치', '32 inch', 8),
(@og_clothing_kr_bottom, '33', '33인치', '33 inch', 9),
(@og_clothing_kr_bottom, '34', '34인치', '34 inch', 10),
(@og_clothing_kr_bottom, '36', '36인치', '36 inch', 11),
(@og_clothing_kr_bottom, '38', '38인치', '38 inch', 12),
(@og_clothing_kr_bottom, '40', '40인치', '40 inch', 13);

-- ===========================================
-- 4. EU 의류 사이즈 (32-56)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_eu, '32', 'EU 32', 'EU 32', 1),
(@og_clothing_eu, '34', 'EU 34', 'EU 34', 2),
(@og_clothing_eu, '36', 'EU 36', 'EU 36', 3),
(@og_clothing_eu, '38', 'EU 38', 'EU 38', 4),
(@og_clothing_eu, '40', 'EU 40', 'EU 40', 5),
(@og_clothing_eu, '42', 'EU 42', 'EU 42', 6),
(@og_clothing_eu, '44', 'EU 44', 'EU 44', 7),
(@og_clothing_eu, '46', 'EU 46', 'EU 46', 8),
(@og_clothing_eu, '48', 'EU 48', 'EU 48', 9),
(@og_clothing_eu, '50', 'EU 50', 'EU 50', 10),
(@og_clothing_eu, '52', 'EU 52', 'EU 52', 11),
(@og_clothing_eu, '54', 'EU 54', 'EU 54', 12),
(@og_clothing_eu, '56', 'EU 56', 'EU 56', 13);

-- ===========================================
-- 5. US 의류 사이즈 (0-20)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_us, '0', 'US 0', 'US 0', 1),
(@og_clothing_us, '2', 'US 2', 'US 2', 2),
(@og_clothing_us, '4', 'US 4', 'US 4', 3),
(@og_clothing_us, '6', 'US 6', 'US 6', 4),
(@og_clothing_us, '8', 'US 8', 'US 8', 5),
(@og_clothing_us, '10', 'US 10', 'US 10', 6),
(@og_clothing_us, '12', 'US 12', 'US 12', 7),
(@og_clothing_us, '14', 'US 14', 'US 14', 8),
(@og_clothing_us, '16', 'US 16', 'US 16', 9),
(@og_clothing_us, '18', 'US 18', 'US 18', 10),
(@og_clothing_us, '20', 'US 20', 'US 20', 11);

-- ===========================================
-- 6. UK 의류 사이즈 (4-24)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_uk, '4', 'UK 4', 'UK 4', 1),
(@og_clothing_uk, '6', 'UK 6', 'UK 6', 2),
(@og_clothing_uk, '8', 'UK 8', 'UK 8', 3),
(@og_clothing_uk, '10', 'UK 10', 'UK 10', 4),
(@og_clothing_uk, '12', 'UK 12', 'UK 12', 5),
(@og_clothing_uk, '14', 'UK 14', 'UK 14', 6),
(@og_clothing_uk, '16', 'UK 16', 'UK 16', 7),
(@og_clothing_uk, '18', 'UK 18', 'UK 18', 8),
(@og_clothing_uk, '20', 'UK 20', 'UK 20', 9),
(@og_clothing_uk, '22', 'UK 22', 'UK 22', 10),
(@og_clothing_uk, '24', 'UK 24', 'UK 24', 11);

-- ===========================================
-- 7. IT 의류 사이즈 (36-54)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_it, '36', 'IT 36', 'IT 36', 1),
(@og_clothing_it, '38', 'IT 38', 'IT 38', 2),
(@og_clothing_it, '40', 'IT 40', 'IT 40', 3),
(@og_clothing_it, '42', 'IT 42', 'IT 42', 4),
(@og_clothing_it, '44', 'IT 44', 'IT 44', 5),
(@og_clothing_it, '46', 'IT 46', 'IT 46', 6),
(@og_clothing_it, '48', 'IT 48', 'IT 48', 7),
(@og_clothing_it, '50', 'IT 50', 'IT 50', 8),
(@og_clothing_it, '52', 'IT 52', 'IT 52', 9),
(@og_clothing_it, '54', 'IT 54', 'IT 54', 10);

-- ===========================================
-- 8. FR 의류 사이즈 (32-52)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_fr, '32', 'FR 32', 'FR 32', 1),
(@og_clothing_fr, '34', 'FR 34', 'FR 34', 2),
(@og_clothing_fr, '36', 'FR 36', 'FR 36', 3),
(@og_clothing_fr, '38', 'FR 38', 'FR 38', 4),
(@og_clothing_fr, '40', 'FR 40', 'FR 40', 5),
(@og_clothing_fr, '42', 'FR 42', 'FR 42', 6),
(@og_clothing_fr, '44', 'FR 44', 'FR 44', 7),
(@og_clothing_fr, '46', 'FR 46', 'FR 46', 8),
(@og_clothing_fr, '48', 'FR 48', 'FR 48', 9),
(@og_clothing_fr, '50', 'FR 50', 'FR 50', 10),
(@og_clothing_fr, '52', 'FR 52', 'FR 52', 11);

-- ===========================================
-- 9. JP 의류 사이즈 (5-23호)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_clothing_jp, '5', 'JP 5호', 'JP 5', 1),
(@og_clothing_jp, '7', 'JP 7호', 'JP 7', 2),
(@og_clothing_jp, '9', 'JP 9호', 'JP 9', 3),
(@og_clothing_jp, '11', 'JP 11호', 'JP 11', 4),
(@og_clothing_jp, '13', 'JP 13호', 'JP 13', 5),
(@og_clothing_jp, '15', 'JP 15호', 'JP 15', 6),
(@og_clothing_jp, '17', 'JP 17호', 'JP 17', 7),
(@og_clothing_jp, '19', 'JP 19호', 'JP 19', 8),
(@og_clothing_jp, '21', 'JP 21호', 'JP 21', 9),
(@og_clothing_jp, '23', 'JP 23호', 'JP 23', 10);

-- ===========================================
-- 10. 데님 허리 사이즈 (인치 24-42)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_denim_waist, '24', 'W24', 'W24', 1),
(@og_denim_waist, '25', 'W25', 'W25', 2),
(@og_denim_waist, '26', 'W26', 'W26', 3),
(@og_denim_waist, '27', 'W27', 'W27', 4),
(@og_denim_waist, '28', 'W28', 'W28', 5),
(@og_denim_waist, '29', 'W29', 'W29', 6),
(@og_denim_waist, '30', 'W30', 'W30', 7),
(@og_denim_waist, '31', 'W31', 'W31', 8),
(@og_denim_waist, '32', 'W32', 'W32', 9),
(@og_denim_waist, '33', 'W33', 'W33', 10),
(@og_denim_waist, '34', 'W34', 'W34', 11),
(@og_denim_waist, '36', 'W36', 'W36', 12),
(@og_denim_waist, '38', 'W38', 'W38', 13),
(@og_denim_waist, '40', 'W40', 'W40', 14),
(@og_denim_waist, '42', 'W42', 'W42', 15);

-- ===========================================
-- 11. 데님 기장 사이즈 (인치 28-36)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_denim_length, '28', 'L28', 'L28', 1),
(@og_denim_length, '29', 'L29', 'L29', 2),
(@og_denim_length, '30', 'L30', 'L30', 3),
(@og_denim_length, '31', 'L31', 'L31', 4),
(@og_denim_length, '32', 'L32', 'L32', 5),
(@og_denim_length, '33', 'L33', 'L33', 6),
(@og_denim_length, '34', 'L34', 'L34', 7),
(@og_denim_length, '36', 'L36', 'L36', 8);

-- ===========================================
-- 12. 반지 사이즈 (KR 1-30호)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_ring_kr, '1', '1호', 'KR 1', 1),
(@og_ring_kr, '3', '3호', 'KR 3', 2),
(@og_ring_kr, '5', '5호', 'KR 5', 3),
(@og_ring_kr, '7', '7호', 'KR 7', 4),
(@og_ring_kr, '9', '9호', 'KR 9', 5),
(@og_ring_kr, '10', '10호', 'KR 10', 6),
(@og_ring_kr, '11', '11호', 'KR 11', 7),
(@og_ring_kr, '12', '12호', 'KR 12', 8),
(@og_ring_kr, '13', '13호', 'KR 13', 9),
(@og_ring_kr, '14', '14호', 'KR 14', 10),
(@og_ring_kr, '15', '15호', 'KR 15', 11),
(@og_ring_kr, '16', '16호', 'KR 16', 12),
(@og_ring_kr, '17', '17호', 'KR 17', 13),
(@og_ring_kr, '18', '18호', 'KR 18', 14),
(@og_ring_kr, '19', '19호', 'KR 19', 15),
(@og_ring_kr, '20', '20호', 'KR 20', 16),
(@og_ring_kr, '21', '21호', 'KR 21', 17),
(@og_ring_kr, '22', '22호', 'KR 22', 18),
(@og_ring_kr, '23', '23호', 'KR 23', 19),
(@og_ring_kr, '25', '25호', 'KR 25', 20),
(@og_ring_kr, '27', '27호', 'KR 27', 21),
(@og_ring_kr, '30', '30호', 'KR 30', 22);

-- ===========================================
-- 13. 반지 사이즈 (US 3-15)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_ring_us, '3', 'US 3', 'US 3', 1),
(@og_ring_us, '3.5', 'US 3.5', 'US 3.5', 2),
(@og_ring_us, '4', 'US 4', 'US 4', 3),
(@og_ring_us, '4.5', 'US 4.5', 'US 4.5', 4),
(@og_ring_us, '5', 'US 5', 'US 5', 5),
(@og_ring_us, '5.5', 'US 5.5', 'US 5.5', 6),
(@og_ring_us, '6', 'US 6', 'US 6', 7),
(@og_ring_us, '6.5', 'US 6.5', 'US 6.5', 8),
(@og_ring_us, '7', 'US 7', 'US 7', 9),
(@og_ring_us, '7.5', 'US 7.5', 'US 7.5', 10),
(@og_ring_us, '8', 'US 8', 'US 8', 11),
(@og_ring_us, '8.5', 'US 8.5', 'US 8.5', 12),
(@og_ring_us, '9', 'US 9', 'US 9', 13),
(@og_ring_us, '9.5', 'US 9.5', 'US 9.5', 14),
(@og_ring_us, '10', 'US 10', 'US 10', 15),
(@og_ring_us, '10.5', 'US 10.5', 'US 10.5', 16),
(@og_ring_us, '11', 'US 11', 'US 11', 17),
(@og_ring_us, '11.5', 'US 11.5', 'US 11.5', 18),
(@og_ring_us, '12', 'US 12', 'US 12', 19),
(@og_ring_us, '13', 'US 13', 'US 13', 20),
(@og_ring_us, '14', 'US 14', 'US 14', 21),
(@og_ring_us, '15', 'US 15', 'US 15', 22);

-- ===========================================
-- 14. 반지 사이즈 (EU 44-70)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_ring_eu, '44', 'EU 44', 'EU 44', 1),
(@og_ring_eu, '45', 'EU 45', 'EU 45', 2),
(@og_ring_eu, '46', 'EU 46', 'EU 46', 3),
(@og_ring_eu, '47', 'EU 47', 'EU 47', 4),
(@og_ring_eu, '48', 'EU 48', 'EU 48', 5),
(@og_ring_eu, '49', 'EU 49', 'EU 49', 6),
(@og_ring_eu, '50', 'EU 50', 'EU 50', 7),
(@og_ring_eu, '51', 'EU 51', 'EU 51', 8),
(@og_ring_eu, '52', 'EU 52', 'EU 52', 9),
(@og_ring_eu, '53', 'EU 53', 'EU 53', 10),
(@og_ring_eu, '54', 'EU 54', 'EU 54', 11),
(@og_ring_eu, '55', 'EU 55', 'EU 55', 12),
(@og_ring_eu, '56', 'EU 56', 'EU 56', 13),
(@og_ring_eu, '57', 'EU 57', 'EU 57', 14),
(@og_ring_eu, '58', 'EU 58', 'EU 58', 15),
(@og_ring_eu, '59', 'EU 59', 'EU 59', 16),
(@og_ring_eu, '60', 'EU 60', 'EU 60', 17),
(@og_ring_eu, '62', 'EU 62', 'EU 62', 18),
(@og_ring_eu, '64', 'EU 64', 'EU 64', 19),
(@og_ring_eu, '66', 'EU 66', 'EU 66', 20),
(@og_ring_eu, '68', 'EU 68', 'EU 68', 21),
(@og_ring_eu, '70', 'EU 70', 'EU 70', 22);

-- ===========================================
-- 15. 벨트 사이즈 (cm 70-130)
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_belt_cm, '70', '70cm', '70cm', 1),
(@og_belt_cm, '75', '75cm', '75cm', 2),
(@og_belt_cm, '80', '80cm', '80cm', 3),
(@og_belt_cm, '85', '85cm', '85cm', 4),
(@og_belt_cm, '90', '90cm', '90cm', 5),
(@og_belt_cm, '95', '95cm', '95cm', 6),
(@og_belt_cm, '100', '100cm', '100cm', 7),
(@og_belt_cm, '105', '105cm', '105cm', 8),
(@og_belt_cm, '110', '110cm', '110cm', 9),
(@og_belt_cm, '115', '115cm', '115cm', 10),
(@og_belt_cm, '120', '120cm', '120cm', 11),
(@og_belt_cm, '125', '125cm', '125cm', 12),
(@og_belt_cm, '130', '130cm', '130cm', 13);

-- ===========================================
-- 16. 매트리스 사이즈
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_mattress, 'SS', '슈퍼싱글 (900x2000)', 'Super Single (900x2000)', 1),
(@og_mattress, 'S', '싱글 (1000x2000)', 'Single (1000x2000)', 2),
(@og_mattress, 'Q', '퀸 (1500x2000)', 'Queen (1500x2000)', 3),
(@og_mattress, 'K', '킹 (1600x2000)', 'King (1600x2000)', 4),
(@og_mattress, 'LK', '라지킹 (1800x2000)', 'Large King (1800x2000)', 5),
(@og_mattress, 'CK', '캘리포니아킹 (1800x2100)', 'California King (1800x2100)', 6);

-- ===========================================
-- 17. 기본 색상 옵션
-- ===========================================
INSERT INTO option_dictionary (option_group_id, canonical_value, display_name_ko, display_name_en, sort_order) VALUES
(@og_color, 'BLACK', '블랙', 'Black', 1),
(@og_color, 'WHITE', '화이트', 'White', 2),
(@og_color, 'GRAY', '그레이', 'Gray', 3),
(@og_color, 'NAVY', '네이비', 'Navy', 4),
(@og_color, 'BEIGE', '베이지', 'Beige', 5),
(@og_color, 'BROWN', '브라운', 'Brown', 6),
(@og_color, 'RED', '레드', 'Red', 7),
(@og_color, 'BLUE', '블루', 'Blue', 8),
(@og_color, 'GREEN', '그린', 'Green', 9),
(@og_color, 'PINK', '핑크', 'Pink', 10),
(@og_color, 'YELLOW', '옐로우', 'Yellow', 11),
(@og_color, 'ORANGE', '오렌지', 'Orange', 12),
(@og_color, 'PURPLE', '퍼플', 'Purple', 13),
(@og_color, 'IVORY', '아이보리', 'Ivory', 14),
(@og_color, 'KHAKI', '카키', 'Khaki', 15),
(@og_color, 'BURGUNDY', '버건디', 'Burgundy', 16),
(@og_color, 'CREAM', '크림', 'Cream', 17),
(@og_color, 'CAMEL', '카멜', 'Camel', 18),
(@og_color, 'SILVER', '실버', 'Silver', 19),
(@og_color, 'GOLD', '골드', 'Gold', 20);
