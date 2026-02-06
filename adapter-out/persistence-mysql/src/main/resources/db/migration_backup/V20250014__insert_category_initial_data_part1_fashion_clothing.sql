-- ============================================
-- Category Initial Data - Part 1: 패션의류 (01)
-- ============================================
-- 4단계 계층 구조: 대분류 > 중분류 > 소분류 > 세분류
-- 코드 체계: XX(대) > XXXX(중) > XXXXXX(소) > XXXXXXXX(세)
-- Path: 부모path/현재id 형태
-- ============================================

-- ============================================
-- 1. 대분류: 패션의류 (01)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (1, '01', '패션의류', 'Fashion Clothing', NULL, 0, '1', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE');

-- ============================================
-- 2. 중분류: 남성의류, 여성의류, 유니섹스
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (10, '0101', '남성의류', 'Men''s Clothing', 1, 1, '1/10', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (11, '0102', '여성의류', 'Women''s Clothing', 1, 1, '1/11', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (12, '0103', '유니섹스', 'Unisex', 1, 1, '1/12', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT');

-- ============================================
-- 3. 소분류: 남성의류 (0101XX)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (100, '010101', '아우터', 'Outerwear', 10, 2, '1/10/100', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (101, '010102', '상의', 'Tops', 10, 2, '1/10/101', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (102, '010103', '하의', 'Bottoms', 10, 2, '1/10/102', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (103, '010104', '정장/수트', 'Suits', 10, 2, '1/10/103', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (104, '010105', '언더웨어', 'Underwear', 10, 2, '1/10/104', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 소분류: 여성의류 (0102XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (110, '010201', '아우터', 'Outerwear', 11, 2, '1/11/110', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (111, '010202', '상의', 'Tops', 11, 2, '1/11/111', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (112, '010203', '하의', 'Bottoms', 11, 2, '1/11/112', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (113, '010204', '원피스/드레스', 'Dresses', 11, 2, '1/11/113', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (114, '010205', '정장/수트', 'Suits', 11, 2, '1/11/114', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (115, '010206', '언더웨어', 'Underwear', 11, 2, '1/11/115', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 소분류: 유니섹스 (0103XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (120, '010301', '캐주얼', 'Casual', 12, 2, '1/12/120', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (121, '010302', '스트릿', 'Street', 12, 2, '1/12/121', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (122, '010303', '스포츠웨어', 'Sportswear', 12, 2, '1/12/122', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT');

-- ============================================
-- 4. 세분류: 남성 아우터 (01010100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1000, '01010101', '코트', 'Coats', 100, 3, '1/10/100/1000', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1001, '01010102', '자켓', 'Jackets', 100, 3, '1/10/100/1001', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1002, '01010103', '패딩/다운', 'Padded/Down', 100, 3, '1/10/100/1002', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1003, '01010104', '점퍼/블루종', 'Bombers', 100, 3, '1/10/100/1003', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1004, '01010105', '가디건', 'Cardigans', 100, 3, '1/10/100/1004', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1005, '01010106', '조끼/베스트', 'Vests', 100, 3, '1/10/100/1005', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1006, '01010107', '레더/퍼', 'Leather/Fur', 100, 3, '1/10/100/1006', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 세분류: 남성 상의 (01010200~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1010, '01010201', '티셔츠', 'T-Shirts', 101, 3, '1/10/101/1010', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1011, '01010202', '셔츠', 'Shirts', 101, 3, '1/10/101/1011', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1012, '01010203', '니트/스웨터', 'Knitwear', 101, 3, '1/10/101/1012', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1013, '01010204', '후드', 'Hoodies', 101, 3, '1/10/101/1013', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1014, '01010205', '맨투맨', 'Sweatshirts', 101, 3, '1/10/101/1014', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1015, '01010206', '폴로', 'Polo Shirts', 101, 3, '1/10/101/1015', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 세분류: 남성 하의 (01010300~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1020, '01010301', '데님/청바지', 'Jeans', 102, 3, '1/10/102/1020', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1021, '01010302', '슬랙스', 'Slacks', 102, 3, '1/10/102/1021', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1022, '01010303', '면바지/치노', 'Chinos', 102, 3, '1/10/102/1022', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1023, '01010304', '조거/트레이닝', 'Joggers', 102, 3, '1/10/102/1023', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1024, '01010305', '반바지/쇼츠', 'Shorts', 102, 3, '1/10/102/1024', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1025, '01010306', '카고팬츠', 'Cargo Pants', 102, 3, '1/10/102/1025', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 세분류: 남성 정장/수트 (01010400~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1030, '01010401', '정장세트', 'Suit Sets', 103, 3, '1/10/103/1030', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1031, '01010402', '정장자켓/블레이저', 'Blazers', 103, 3, '1/10/103/1031', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1032, '01010403', '정장바지', 'Suit Pants', 103, 3, '1/10/103/1032', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1033, '01010404', '조끼/베스트', 'Suit Vests', 103, 3, '1/10/103/1033', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 세분류: 남성 언더웨어 (01010500~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1040, '01010501', '브리프', 'Briefs', 104, 3, '1/10/104/1040', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1041, '01010502', '트렁크', 'Trunks', 104, 3, '1/10/104/1041', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1042, '01010503', '드로즈', 'Boxer Briefs', 104, 3, '1/10/104/1042', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1043, '01010504', '런닝/이너웨어', 'Undershirts', 104, 3, '1/10/104/1043', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1044, '01010505', '양말', 'Socks', 104, 3, '1/10/104/1044', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- ============================================
-- 4. 세분류: 여성 아우터 (01020100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1100, '01020101', '코트', 'Coats', 110, 3, '1/11/110/1100', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1101, '01020102', '자켓', 'Jackets', 110, 3, '1/11/110/1101', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1102, '01020103', '패딩/다운', 'Padded/Down', 110, 3, '1/11/110/1102', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1103, '01020104', '점퍼/블루종', 'Bombers', 110, 3, '1/11/110/1103', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1104, '01020105', '가디건', 'Cardigans', 110, 3, '1/11/110/1104', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1105, '01020106', '조끼/베스트', 'Vests', 110, 3, '1/11/110/1105', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1106, '01020107', '레더/퍼', 'Leather/Fur', 110, 3, '1/11/110/1106', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1107, '01020108', '트렌치코트', 'Trench Coats', 110, 3, '1/11/110/1107', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 세분류: 여성 상의 (01020200~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1110, '01020201', '티셔츠', 'T-Shirts', 111, 3, '1/11/111/1110', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1111, '01020202', '블라우스', 'Blouses', 111, 3, '1/11/111/1111', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1112, '01020203', '셔츠', 'Shirts', 111, 3, '1/11/111/1112', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1113, '01020204', '니트/스웨터', 'Knitwear', 111, 3, '1/11/111/1113', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1114, '01020205', '후드', 'Hoodies', 111, 3, '1/11/111/1114', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1115, '01020206', '맨투맨', 'Sweatshirts', 111, 3, '1/11/111/1115', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1116, '01020207', '크롭탑', 'Crop Tops', 111, 3, '1/11/111/1116', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 세분류: 여성 하의 (01020300~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1120, '01020301', '데님/청바지', 'Jeans', 112, 3, '1/11/112/1120', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1121, '01020302', '슬랙스', 'Slacks', 112, 3, '1/11/112/1121', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1122, '01020303', '스커트', 'Skirts', 112, 3, '1/11/112/1122', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1123, '01020304', '레깅스', 'Leggings', 112, 3, '1/11/112/1123', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1124, '01020305', '반바지/쇼츠', 'Shorts', 112, 3, '1/11/112/1124', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1125, '01020306', '와이드팬츠', 'Wide Pants', 112, 3, '1/11/112/1125', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 세분류: 여성 원피스/드레스 (01020400~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1130, '01020401', '미니원피스', 'Mini Dresses', 113, 3, '1/11/113/1130', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1131, '01020402', '미디원피스', 'Midi Dresses', 113, 3, '1/11/113/1131', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1132, '01020403', '맥시원피스', 'Maxi Dresses', 113, 3, '1/11/113/1132', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1133, '01020404', '점프수트', 'Jumpsuits', 113, 3, '1/11/113/1133', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1134, '01020405', '이브닝드레스', 'Evening Dresses', 113, 3, '1/11/113/1134', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 세분류: 여성 정장/수트 (01020500~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1140, '01020501', '정장세트', 'Suit Sets', 114, 3, '1/11/114/1140', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1141, '01020502', '정장자켓/블레이저', 'Blazers', 114, 3, '1/11/114/1141', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1142, '01020503', '정장스커트', 'Suit Skirts', 114, 3, '1/11/114/1142', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1143, '01020504', '정장바지', 'Suit Pants', 114, 3, '1/11/114/1143', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 세분류: 여성 언더웨어 (01020600~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1150, '01020601', '브라', 'Bras', 115, 3, '1/11/115/1150', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1151, '01020602', '팬티', 'Panties', 115, 3, '1/11/115/1151', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1152, '01020603', '속옷세트', 'Lingerie Sets', 115, 3, '1/11/115/1152', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1153, '01020604', '보정속옷', 'Shapewear', 115, 3, '1/11/115/1153', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1154, '01020605', '양말/스타킹', 'Socks/Stockings', 115, 3, '1/11/115/1154', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- ============================================
-- 4. 세분류: 유니섹스 캐주얼 (01030100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1200, '01030101', '티셔츠', 'T-Shirts', 120, 3, '1/12/120/1200', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1201, '01030102', '후드/맨투맨', 'Hoodies/Sweatshirts', 120, 3, '1/12/120/1201', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1202, '01030103', '셔츠', 'Shirts', 120, 3, '1/12/120/1202', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1203, '01030104', '팬츠', 'Pants', 120, 3, '1/12/120/1203', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT');

-- 세분류: 유니섹스 스트릿 (01030200~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1210, '01030201', '그래픽티', 'Graphic Tees', 121, 3, '1/12/121/1210', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1211, '01030202', '오버핏후드', 'Oversized Hoodies', 121, 3, '1/12/121/1211', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1212, '01030203', '트랙수트', 'Track Suits', 121, 3, '1/12/121/1212', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1213, '01030204', '카고팬츠', 'Cargo Pants', 121, 3, '1/12/121/1213', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT');

-- 세분류: 유니섹스 스포츠웨어 (01030300~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1220, '01030301', '트레이닝세트', 'Training Sets', 122, 3, '1/12/122/1220', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1221, '01030302', '래쉬가드', 'Rash Guards', 122, 3, '1/12/122/1221', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1222, '01030303', '조거팬츠', 'Jogger Pants', 122, 3, '1/12/122/1222', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT'),
    (1223, '01030304', '스포츠브라/탑', 'Sports Bras/Tops', 122, 3, '1/12/122/1223', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'ADULT');
