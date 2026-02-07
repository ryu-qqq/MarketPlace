-- ================================================
-- V20250033: SizeConversion 시드 데이터
-- 국제 사이즈 체계 간 환산 규칙
-- ================================================

-- 옵션 그룹 ID 변수
SET @og_shoes_eu = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_EU');
SET @og_shoes_us_men = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_US_MEN');
SET @og_shoes_uk_men = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_UK_MEN');
SET @og_shoes_kr = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_KR');
SET @og_shoes_jp = (SELECT id FROM option_group WHERE code = 'SIZE_SHOES_JP');

SET @og_clothing_int = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_INT');
SET @og_clothing_eu = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_EU');
SET @og_clothing_us = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_US');
SET @og_clothing_uk = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_UK');
SET @og_clothing_it = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_IT');
SET @og_clothing_fr = (SELECT id FROM option_group WHERE code = 'SIZE_CLOTHING_FR');

-- ===========================================
-- 1. 신발 사이즈 환산 (EU → US Men → UK Men → KR)
-- 표준 환산 테이블 기반
-- ===========================================

-- EU 39 환산 체인
SET @eu_39 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '39');
SET @us_men_6 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '6');
SET @uk_men_5_5 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '5.5');
SET @kr_245 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '245');
SET @jp_24_5 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '24.5');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_39, @us_men_6, 'SHOES', 1.00, 'EU 39 = US Men 6 표준 환산'),
(@eu_39, @uk_men_5_5, 'SHOES', 1.00, 'EU 39 = UK Men 5.5 표준 환산'),
(@eu_39, @kr_245, 'SHOES', 1.00, 'EU 39 = KR 245mm 표준 환산'),
(@eu_39, @jp_24_5, 'SHOES', 1.00, 'EU 39 = JP 24.5cm 표준 환산');

-- EU 40 환산 체인
SET @eu_40 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '40');
SET @us_men_7 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '7');
SET @uk_men_6 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '6');
SET @kr_250 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '250');
SET @jp_25_0 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '25.0');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_40, @us_men_7, 'SHOES', 1.00, 'EU 40 = US Men 7 표준 환산'),
(@eu_40, @uk_men_6, 'SHOES', 1.00, 'EU 40 = UK Men 6 표준 환산'),
(@eu_40, @kr_250, 'SHOES', 1.00, 'EU 40 = KR 250mm 표준 환산'),
(@eu_40, @jp_25_0, 'SHOES', 1.00, 'EU 40 = JP 25.0cm 표준 환산');

-- EU 41 환산 체인
SET @eu_41 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '41');
SET @us_men_8 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '8');
SET @uk_men_7 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '7');
SET @kr_260 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '260');
SET @jp_26_0 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '26.0');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_41, @us_men_8, 'SHOES', 1.00, 'EU 41 = US Men 8 표준 환산'),
(@eu_41, @uk_men_7, 'SHOES', 1.00, 'EU 41 = UK Men 7 표준 환산'),
(@eu_41, @kr_260, 'SHOES', 1.00, 'EU 41 = KR 260mm 표준 환산'),
(@eu_41, @jp_26_0, 'SHOES', 1.00, 'EU 41 = JP 26.0cm 표준 환산');

-- EU 42 환산 체인
SET @eu_42 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '42');
SET @us_men_9 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '9');
SET @uk_men_8 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '8');
SET @kr_265 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '265');
SET @jp_26_5 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '26.5');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_42, @us_men_9, 'SHOES', 1.00, 'EU 42 = US Men 9 표준 환산'),
(@eu_42, @uk_men_8, 'SHOES', 1.00, 'EU 42 = UK Men 8 표준 환산'),
(@eu_42, @kr_265, 'SHOES', 1.00, 'EU 42 = KR 265mm 표준 환산'),
(@eu_42, @jp_26_5, 'SHOES', 1.00, 'EU 42 = JP 26.5cm 표준 환산');

-- EU 43 환산 체인
SET @eu_43 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '43');
SET @us_men_10 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '10');
SET @uk_men_9 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '9');
SET @kr_270 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '270');
SET @jp_27_0 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '27.0');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_43, @us_men_10, 'SHOES', 1.00, 'EU 43 = US Men 10 표준 환산'),
(@eu_43, @uk_men_9, 'SHOES', 1.00, 'EU 43 = UK Men 9 표준 환산'),
(@eu_43, @kr_270, 'SHOES', 1.00, 'EU 43 = KR 270mm 표준 환산'),
(@eu_43, @jp_27_0, 'SHOES', 1.00, 'EU 43 = JP 27.0cm 표준 환산');

-- EU 44 환산 체인
SET @eu_44 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '44');
SET @us_men_11 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '11');
SET @uk_men_10 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '10');
SET @kr_280 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '280');
SET @jp_28_0 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '28.0');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_44, @us_men_11, 'SHOES', 1.00, 'EU 44 = US Men 11 표준 환산'),
(@eu_44, @uk_men_10, 'SHOES', 1.00, 'EU 44 = UK Men 10 표준 환산'),
(@eu_44, @kr_280, 'SHOES', 1.00, 'EU 44 = KR 280mm 표준 환산'),
(@eu_44, @jp_28_0, 'SHOES', 1.00, 'EU 44 = JP 28.0cm 표준 환산');

-- EU 45 환산 체인
SET @eu_45 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_eu AND canonical_value = '45');
SET @us_men_12 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_us_men AND canonical_value = '12');
SET @uk_men_11 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_uk_men AND canonical_value = '11');
SET @kr_285 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_kr AND canonical_value = '285');
SET @jp_28_5 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_shoes_jp AND canonical_value = '28.5');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@eu_45, @us_men_12, 'SHOES', 1.00, 'EU 45 = US Men 12 표준 환산'),
(@eu_45, @uk_men_11, 'SHOES', 1.00, 'EU 45 = UK Men 11 표준 환산'),
(@eu_45, @kr_285, 'SHOES', 1.00, 'EU 45 = KR 285mm 표준 환산'),
(@eu_45, @jp_28_5, 'SHOES', 1.00, 'EU 45 = JP 28.5cm 표준 환산');

-- ===========================================
-- 2. 의류 사이즈 환산 (International ↔ EU ↔ US ↔ UK ↔ IT ↔ FR)
-- ===========================================

-- XS 환산 체인
SET @int_xs = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'XS');
SET @eu_32 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '32');
SET @us_0 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '0');
SET @uk_4 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '4');
SET @it_36 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '36');
SET @fr_32 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '32');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_xs, @eu_32, 'CLOTHING', 1.00, 'XS = EU 32 표준 환산'),
(@int_xs, @us_0, 'CLOTHING', 1.00, 'XS = US 0 표준 환산'),
(@int_xs, @uk_4, 'CLOTHING', 1.00, 'XS = UK 4 표준 환산'),
(@int_xs, @it_36, 'CLOTHING', 1.00, 'XS = IT 36 표준 환산'),
(@int_xs, @fr_32, 'CLOTHING', 1.00, 'XS = FR 32 표준 환산');

-- S 환산 체인
SET @int_s = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'S');
SET @eu_36 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '36');
SET @us_4 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '4');
SET @uk_8 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '8');
SET @it_40 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '40');
SET @fr_36 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '36');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_s, @eu_36, 'CLOTHING', 1.00, 'S = EU 36 표준 환산'),
(@int_s, @us_4, 'CLOTHING', 1.00, 'S = US 4 표준 환산'),
(@int_s, @uk_8, 'CLOTHING', 1.00, 'S = UK 8 표준 환산'),
(@int_s, @it_40, 'CLOTHING', 1.00, 'S = IT 40 표준 환산'),
(@int_s, @fr_36, 'CLOTHING', 1.00, 'S = FR 36 표준 환산');

-- M 환산 체인
SET @int_m = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'M');
SET @eu_38 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '38');
SET @us_6 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '6');
SET @uk_10 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '10');
SET @it_42 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '42');
SET @fr_38 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '38');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_m, @eu_38, 'CLOTHING', 1.00, 'M = EU 38 표준 환산'),
(@int_m, @us_6, 'CLOTHING', 1.00, 'M = US 6 표준 환산'),
(@int_m, @uk_10, 'CLOTHING', 1.00, 'M = UK 10 표준 환산'),
(@int_m, @it_42, 'CLOTHING', 1.00, 'M = IT 42 표준 환산'),
(@int_m, @fr_38, 'CLOTHING', 1.00, 'M = FR 38 표준 환산');

-- L 환산 체인
SET @int_l = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'L');
SET @eu_40 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '40');
SET @us_8 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '8');
SET @uk_12 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '12');
SET @it_44 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '44');
SET @fr_40 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '40');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_l, @eu_40, 'CLOTHING', 1.00, 'L = EU 40 표준 환산'),
(@int_l, @us_8, 'CLOTHING', 1.00, 'L = US 8 표준 환산'),
(@int_l, @uk_12, 'CLOTHING', 1.00, 'L = UK 12 표준 환산'),
(@int_l, @it_44, 'CLOTHING', 1.00, 'L = IT 44 표준 환산'),
(@int_l, @fr_40, 'CLOTHING', 1.00, 'L = FR 40 표준 환산');

-- XL 환산 체인
SET @int_xl = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'XL');
SET @eu_42 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '42');
SET @us_10 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '10');
SET @uk_14 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '14');
SET @it_46 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '46');
SET @fr_42 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '42');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_xl, @eu_42, 'CLOTHING', 1.00, 'XL = EU 42 표준 환산'),
(@int_xl, @us_10, 'CLOTHING', 1.00, 'XL = US 10 표준 환산'),
(@int_xl, @uk_14, 'CLOTHING', 1.00, 'XL = UK 14 표준 환산'),
(@int_xl, @it_46, 'CLOTHING', 1.00, 'XL = IT 46 표준 환산'),
(@int_xl, @fr_42, 'CLOTHING', 1.00, 'XL = FR 42 표준 환산');

-- XXL 환산 체인
SET @int_xxl = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_int AND canonical_value = 'XXL');
SET @eu_44 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_eu AND canonical_value = '44');
SET @us_12 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_us AND canonical_value = '12');
SET @uk_16 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_uk AND canonical_value = '16');
SET @it_48 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_it AND canonical_value = '48');
SET @fr_44 = (SELECT id FROM option_dictionary WHERE option_group_id = @og_clothing_fr AND canonical_value = '44');

INSERT INTO size_conversion (from_option_dictionary_id, to_option_dictionary_id, category_type, confidence, description) VALUES
(@int_xxl, @eu_44, 'CLOTHING', 1.00, 'XXL = EU 44 표준 환산'),
(@int_xxl, @us_12, 'CLOTHING', 1.00, 'XXL = US 12 표준 환산'),
(@int_xxl, @uk_16, 'CLOTHING', 1.00, 'XXL = UK 16 표준 환산'),
(@int_xxl, @it_48, 'CLOTHING', 1.00, 'XXL = IT 48 표준 환산'),
(@int_xxl, @fr_44, 'CLOTHING', 1.00, 'XXL = FR 44 표준 환산');
