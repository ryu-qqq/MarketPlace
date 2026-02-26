-- ============================================
-- Category Initial Data - Part 3: 뷰티 (03)
-- ============================================
-- 4단계 계층 구조: 대분류 > 중분류 > 소분류 > 세분류
-- 코드 체계: XX(대) > XXXX(중) > XXXXXX(소) > XXXXXXXX(세)
-- ============================================

-- ============================================
-- 1. 대분류: 뷰티 (03)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (3, '03', '뷰티', 'Beauty', NULL, 0, '3', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- ============================================
-- 2. 중분류: 스킨케어, 메이크업, 헤어케어, 바디케어, 향수, 남성그루밍
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (30, '0301', '스킨케어', 'Skincare', 3, 1, '3/30', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (31, '0302', '메이크업', 'Makeup', 3, 1, '3/31', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (32, '0303', '헤어케어', 'Hair Care', 3, 1, '3/32', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (33, '0304', '바디케어', 'Body Care', 3, 1, '3/33', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (34, '0305', '향수/디퓨저', 'Fragrance/Diffusers', 3, 1, '3/34', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (35, '0306', '남성그루밍', 'Men''s Grooming', 3, 1, '3/35', 6, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (36, '0307', '뷰티기기', 'Beauty Devices', 3, 1, '3/36', 7, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (37, '0308', '네일', 'Nail', 3, 1, '3/37', 8, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT');

-- ============================================
-- 3. 소분류: 스킨케어 (0301XX)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (300, '030101', '클렌저', 'Cleansers', 30, 2, '3/30/300', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (301, '030102', '토너/미스트', 'Toners/Mists', 30, 2, '3/30/301', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (302, '030103', '에센스/세럼', 'Essences/Serums', 30, 2, '3/30/302', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (303, '030104', '크림/로션', 'Creams/Lotions', 30, 2, '3/30/303', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (304, '030105', '아이케어', 'Eye Care', 30, 2, '3/30/304', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (305, '030106', '선케어', 'Sun Care', 30, 2, '3/30/305', 6, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (306, '030107', '마스크팩', 'Masks', 30, 2, '3/30/306', 7, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (307, '030108', '스킨케어세트', 'Skincare Sets', 30, 2, '3/30/307', 8, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- 소분류: 메이크업 (0302XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (310, '030201', '베이스메이크업', 'Base Makeup', 31, 2, '3/31/310', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (311, '030202', '아이메이크업', 'Eye Makeup', 31, 2, '3/31/311', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (312, '030203', '립메이크업', 'Lip Makeup', 31, 2, '3/31/312', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (313, '030204', '치크메이크업', 'Cheek Makeup', 31, 2, '3/31/313', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (314, '030205', '메이크업툴', 'Makeup Tools', 31, 2, '3/31/314', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (315, '030206', '메이크업세트', 'Makeup Sets', 31, 2, '3/31/315', 6, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT');

-- 소분류: 헤어케어 (0303XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (320, '030301', '샴푸/린스', 'Shampoo/Conditioner', 32, 2, '3/32/320', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (321, '030302', '헤어트리트먼트', 'Hair Treatments', 32, 2, '3/32/321', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (322, '030303', '헤어스타일링', 'Hair Styling', 32, 2, '3/32/322', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (323, '030304', '헤어컬러', 'Hair Color', 32, 2, '3/32/323', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (324, '030305', '두피케어', 'Scalp Care', 32, 2, '3/32/324', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- 소분류: 바디케어 (0304XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (330, '030401', '바디클렌저', 'Body Cleansers', 33, 2, '3/33/330', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (331, '030402', '바디로션/크림', 'Body Lotions/Creams', 33, 2, '3/33/331', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (332, '030403', '바디오일', 'Body Oils', 33, 2, '3/33/332', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (333, '030404', '핸드케어', 'Hand Care', 33, 2, '3/33/333', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (334, '030405', '풋케어', 'Foot Care', 33, 2, '3/33/334', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (335, '030406', '데오드란트', 'Deodorants', 33, 2, '3/33/335', 6, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- 소분류: 향수/디퓨저 (0305XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (340, '030501', '여성향수', 'Women''s Perfumes', 34, 2, '3/34/340', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (341, '030502', '남성향수', 'Men''s Perfumes', 34, 2, '3/34/341', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (342, '030503', '유니섹스향수', 'Unisex Perfumes', 34, 2, '3/34/342', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'UNISEX', 'ADULT'),
    (343, '030504', '홈프레그런스', 'Home Fragrance', 34, 2, '3/34/343', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (344, '030505', '바디미스트', 'Body Mists', 34, 2, '3/34/344', 5, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- 소분류: 남성그루밍 (0306XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (350, '030601', '쉐이빙', 'Shaving', 35, 2, '3/35/350', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (351, '030602', '남성스킨케어', 'Men''s Skincare', 35, 2, '3/35/351', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (352, '030603', '남성헤어케어', 'Men''s Hair Care', 35, 2, '3/35/352', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (353, '030604', '남성올인원', 'Men''s All-in-One', 35, 2, '3/35/353', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT');

-- 소분류: 뷰티기기 (0307XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (360, '030701', '피부관리기기', 'Skin Devices', 36, 2, '3/36/360', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (361, '030702', '헤어기기', 'Hair Devices', 36, 2, '3/36/361', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (362, '030703', '제모기', 'Hair Removal', 36, 2, '3/36/362', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- 소분류: 네일 (0308XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (370, '030801', '네일컬러', 'Nail Colors', 37, 2, '3/37/370', 1, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (371, '030802', '네일케어', 'Nail Care', 37, 2, '3/37/371', 2, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (372, '030803', '네일아트', 'Nail Art', 37, 2, '3/37/372', 3, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (373, '030804', '네일도구', 'Nail Tools', 37, 2, '3/37/373', 4, 0, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT');

-- ============================================
-- 4. 세분류: 스킨케어 (03010100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 클렌저 세분류
    (3000, '03010101', '폼클렌저', 'Foam Cleansers', 300, 3, '3/30/300/3000', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3001, '03010102', '클렌징오일', 'Cleansing Oils', 300, 3, '3/30/300/3001', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3002, '03010103', '클렌징워터', 'Cleansing Water', 300, 3, '3/30/300/3002', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3003, '03010104', '클렌징밤', 'Cleansing Balms', 300, 3, '3/30/300/3003', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3004, '03010105', '클렌징젤', 'Cleansing Gels', 300, 3, '3/30/300/3004', 5, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3005, '03010106', '스크럽/필링', 'Scrubs/Peeling', 300, 3, '3/30/300/3005', 6, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 토너/미스트 세분류
    (3010, '03010201', '토너/스킨', 'Toners', 301, 3, '3/30/301/3010', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3011, '03010202', '미스트', 'Mists', 301, 3, '3/30/301/3011', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3012, '03010203', '토너패드', 'Toner Pads', 301, 3, '3/30/301/3012', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 에센스/세럼 세분류
    (3020, '03010301', '에센스', 'Essences', 302, 3, '3/30/302/3020', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3021, '03010302', '세럼', 'Serums', 302, 3, '3/30/302/3021', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3022, '03010303', '앰플', 'Ampoules', 302, 3, '3/30/302/3022', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3023, '03010304', '부스터', 'Boosters', 302, 3, '3/30/302/3023', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 크림/로션 세분류
    (3030, '03010401', '수분크림', 'Moisturizing Creams', 303, 3, '3/30/303/3030', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3031, '03010402', '영양크림', 'Nourishing Creams', 303, 3, '3/30/303/3031', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3032, '03010403', '로션/에멀전', 'Lotions/Emulsions', 303, 3, '3/30/303/3032', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3033, '03010404', '나이트크림', 'Night Creams', 303, 3, '3/30/303/3033', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 아이케어 세분류
    (3040, '03010501', '아이크림', 'Eye Creams', 304, 3, '3/30/304/3040', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3041, '03010502', '아이세럼', 'Eye Serums', 304, 3, '3/30/304/3041', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3042, '03010503', '아이패치', 'Eye Patches', 304, 3, '3/30/304/3042', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 선케어 세분류
    (3050, '03010601', '선크림', 'Sunscreens', 305, 3, '3/30/305/3050', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3051, '03010602', '선스틱', 'Sun Sticks', 305, 3, '3/30/305/3051', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3052, '03010603', '선스프레이', 'Sun Sprays', 305, 3, '3/30/305/3052', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3053, '03010604', '애프터선', 'After Sun', 305, 3, '3/30/305/3053', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 마스크팩 세분류
    (3060, '03010701', '시트마스크', 'Sheet Masks', 306, 3, '3/30/306/3060', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3061, '03010702', '워시오프팩', 'Wash-off Masks', 306, 3, '3/30/306/3061', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3062, '03010703', '필오프팩', 'Peel-off Masks', 306, 3, '3/30/306/3062', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3063, '03010704', '슬리핑팩', 'Sleeping Masks', 306, 3, '3/30/306/3063', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE');

-- ============================================
-- 4. 세분류: 메이크업 (03020100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 베이스메이크업 세분류
    (3100, '03020101', '프라이머', 'Primers', 310, 3, '3/31/310/3100', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3101, '03020102', '파운데이션', 'Foundations', 310, 3, '3/31/310/3101', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3102, '03020103', '쿠션', 'Cushions', 310, 3, '3/31/310/3102', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3103, '03020104', 'BB/CC크림', 'BB/CC Creams', 310, 3, '3/31/310/3103', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3104, '03020105', '컨실러', 'Concealers', 310, 3, '3/31/310/3104', 5, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3105, '03020106', '파우더', 'Powders', 310, 3, '3/31/310/3105', 6, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3106, '03020107', '세팅스프레이', 'Setting Sprays', 310, 3, '3/31/310/3106', 7, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    -- 아이메이크업 세분류
    (3110, '03020201', '아이섀도', 'Eye Shadows', 311, 3, '3/31/311/3110', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3111, '03020202', '아이라이너', 'Eye Liners', 311, 3, '3/31/311/3111', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3112, '03020203', '마스카라', 'Mascaras', 311, 3, '3/31/311/3112', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3113, '03020204', '아이브로우', 'Eyebrows', 311, 3, '3/31/311/3113', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3114, '03020205', '속눈썹', 'False Lashes', 311, 3, '3/31/311/3114', 5, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    -- 립메이크업 세분류
    (3120, '03020301', '립스틱', 'Lipsticks', 312, 3, '3/31/312/3120', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3121, '03020302', '립틴트', 'Lip Tints', 312, 3, '3/31/312/3121', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3122, '03020303', '립글로스', 'Lip Glosses', 312, 3, '3/31/312/3122', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3123, '03020304', '립라이너', 'Lip Liners', 312, 3, '3/31/312/3123', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3124, '03020305', '립밤', 'Lip Balms', 312, 3, '3/31/312/3124', 5, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 치크메이크업 세분류
    (3130, '03020401', '블러셔', 'Blushers', 313, 3, '3/31/313/3130', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3131, '03020402', '하이라이터', 'Highlighters', 313, 3, '3/31/313/3131', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3132, '03020403', '쉐딩/컨투어', 'Shading/Contour', 313, 3, '3/31/313/3132', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    -- 메이크업툴 세분류
    (3140, '03020501', '브러쉬', 'Brushes', 314, 3, '3/31/314/3140', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3141, '03020502', '스펀지/퍼프', 'Sponges/Puffs', 314, 3, '3/31/314/3141', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3142, '03020503', '뷰러', 'Eyelash Curlers', 314, 3, '3/31/314/3142', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3143, '03020504', '거울', 'Mirrors', 314, 3, '3/31/314/3143', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT');

-- ============================================
-- 4. 세분류: 헤어케어, 바디케어, 향수 등 (간략히)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 샴푸/린스 세분류
    (3200, '03030101', '샴푸', 'Shampoos', 320, 3, '3/32/320/3200', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3201, '03030102', '린스/컨디셔너', 'Conditioners', 320, 3, '3/32/320/3201', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3202, '03030103', '드라이샴푸', 'Dry Shampoos', 320, 3, '3/32/320/3202', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 헤어스타일링 세분류
    (3220, '03030301', '헤어왁스', 'Hair Wax', 322, 3, '3/32/322/3220', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3221, '03030302', '헤어스프레이', 'Hair Sprays', 322, 3, '3/32/322/3221', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3222, '03030303', '헤어젤', 'Hair Gels', 322, 3, '3/32/322/3222', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3223, '03030304', '헤어무스', 'Hair Mousse', 322, 3, '3/32/322/3223', 4, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 바디클렌저 세분류
    (3300, '03040101', '바디워시', 'Body Wash', 330, 3, '3/33/330/3300', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3301, '03040102', '비누', 'Soaps', 330, 3, '3/33/330/3301', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3302, '03040103', '바디스크럽', 'Body Scrubs', 330, 3, '3/33/330/3302', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 여성향수 세분류
    (3400, '03050101', '오드퍼퓸', 'Eau de Parfum', 340, 3, '3/34/340/3400', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3401, '03050102', '오드뚜왈렛', 'Eau de Toilette', 340, 3, '3/34/340/3401', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3402, '03050103', '오드코롱', 'Eau de Cologne', 340, 3, '3/34/340/3402', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    -- 남성향수 세분류
    (3410, '03050201', '오드퍼퓸', 'Eau de Parfum', 341, 3, '3/34/341/3410', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (3411, '03050202', '오드뚜왈렛', 'Eau de Toilette', 341, 3, '3/34/341/3411', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (3412, '03050203', '오드코롱', 'Eau de Cologne', 341, 3, '3/34/341/3412', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    -- 홈프레그런스 세분류
    (3430, '03050401', '디퓨저', 'Diffusers', 343, 3, '3/34/343/3430', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3431, '03050402', '캔들', 'Candles', 343, 3, '3/34/343/3431', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3432, '03050403', '룸스프레이', 'Room Sprays', 343, 3, '3/34/343/3432', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 쉐이빙 세분류
    (3500, '03060101', '면도기', 'Razors', 350, 3, '3/35/350/3500', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (3501, '03060102', '쉐이빙폼/젤', 'Shaving Foam/Gel', 350, 3, '3/35/350/3501', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    (3502, '03060103', '애프터쉐이브', 'Aftershave', 350, 3, '3/35/350/3502', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'MALE', 'ADULT'),
    -- 뷰티기기 세분류
    (3600, '03070101', 'LED마스크', 'LED Masks', 360, 3, '3/36/360/3600', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3601, '03070102', '클렌징기기', 'Cleansing Devices', 360, 3, '3/36/360/3601', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3602, '03070103', '갈바닉기기', 'Galvanic Devices', 360, 3, '3/36/360/3602', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 헤어기기 세분류
    (3610, '03070201', '드라이어', 'Hair Dryers', 361, 3, '3/36/361/3610', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3611, '03070202', '고데기', 'Hair Straighteners', 361, 3, '3/36/361/3611', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    (3612, '03070203', '컬링아이론', 'Curling Irons', 361, 3, '3/36/361/3612', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'NONE', 'NONE'),
    -- 네일컬러 세분류
    (3700, '03080101', '네일폴리쉬', 'Nail Polishes', 370, 3, '3/37/370/3700', 1, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3701, '03080102', '젤네일', 'Gel Nails', 370, 3, '3/37/370/3701', 2, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT'),
    (3702, '03080103', '탑/베이스코트', 'Top/Base Coats', 370, 3, '3/37/370/3702', 3, 1, 'ACTIVE', 1, 1, 'BEAUTY', 'ETC', 'FEMALE', 'ADULT');
