-- ============================================
-- Category Initial Data - Part 2: 패션잡화 (02)
-- ============================================
-- 4단계 계층 구조: 대분류 > 중분류 > 소분류 > 세분류
-- 코드 체계: XX(대) > XXXX(중) > XXXXXX(소) > XXXXXXXX(세)
-- ============================================

-- ============================================
-- 1. 대분류: 패션잡화 (02)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (2, '02', '패션잡화', 'Fashion Accessories', NULL, 0, '2', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- ============================================
-- 2. 중분류: 신발, 가방, 지갑, 시계, 주얼리, 아이웨어, 모자, 벨트
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (20, '0201', '신발', 'Shoes', 2, 1, '2/20', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'NONE'),
    (21, '0202', '가방', 'Bags', 2, 1, '2/21', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'NONE'),
    (22, '0203', '지갑/카드케이스', 'Wallets/Card Cases', 2, 1, '2/22', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (23, '0204', '시계', 'Watches', 2, 1, '2/23', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (24, '0205', '주얼리', 'Jewelry', 2, 1, '2/24', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (25, '0206', '아이웨어', 'Eyewear', 2, 1, '2/25', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (26, '0207', '모자', 'Hats', 2, 1, '2/26', 7, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (27, '0208', '벨트/서스펜더', 'Belts/Suspenders', 2, 1, '2/27', 8, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (28, '0209', '스카프/머플러', 'Scarves/Mufflers', 2, 1, '2/28', 9, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (29, '0210', '기타 액세서리', 'Other Accessories', 2, 1, '2/29', 10, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- ============================================
-- 3. 소분류: 신발 (0201XX)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (200, '020101', '남성신발', 'Men''s Shoes', 20, 2, '2/20/200', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (201, '020102', '여성신발', 'Women''s Shoes', 20, 2, '2/20/201', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (202, '020103', '유니섹스신발', 'Unisex Shoes', 20, 2, '2/20/202', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'ADULT');

-- 소분류: 가방 (0202XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (210, '020201', '남성가방', 'Men''s Bags', 21, 2, '2/21/210', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (211, '020202', '여성가방', 'Women''s Bags', 21, 2, '2/21/211', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (212, '020203', '유니섹스가방', 'Unisex Bags', 21, 2, '2/21/212', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT');

-- 소분류: 지갑/카드케이스 (0203XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (220, '020301', '남성지갑', 'Men''s Wallets', 22, 2, '2/22/220', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (221, '020302', '여성지갑', 'Women''s Wallets', 22, 2, '2/22/221', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (222, '020303', '카드케이스', 'Card Cases', 22, 2, '2/22/222', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (223, '020304', '머니클립', 'Money Clips', 22, 2, '2/22/223', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (224, '020305', '여권케이스', 'Passport Cases', 22, 2, '2/22/224', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- 소분류: 시계 (0204XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (230, '020401', '남성시계', 'Men''s Watches', 23, 2, '2/23/230', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (231, '020402', '여성시계', 'Women''s Watches', 23, 2, '2/23/231', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (232, '020403', '유니섹스시계', 'Unisex Watches', 23, 2, '2/23/232', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (233, '020404', '스마트워치', 'Smart Watches', 23, 2, '2/23/233', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (234, '020405', '시계줄/액세서리', 'Watch Straps/Accessories', 23, 2, '2/23/234', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- 소분류: 주얼리 (0205XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (240, '020501', '목걸이/펜던트', 'Necklaces/Pendants', 24, 2, '2/24/240', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (241, '020502', '귀걸이', 'Earrings', 24, 2, '2/24/241', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (242, '020503', '반지', 'Rings', 24, 2, '2/24/242', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (243, '020504', '팔찌/뱅글', 'Bracelets/Bangles', 24, 2, '2/24/243', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (244, '020505', '브로치/핀', 'Brooches/Pins', 24, 2, '2/24/244', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (245, '020506', '커플링/웨딩', 'Couple/Wedding Rings', 24, 2, '2/24/245', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 소분류: 아이웨어 (0206XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (250, '020601', '선글라스', 'Sunglasses', 25, 2, '2/25/250', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (251, '020602', '안경테', 'Eyeglass Frames', 25, 2, '2/25/251', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (252, '020603', '블루라이트차단', 'Blue Light Glasses', 25, 2, '2/25/252', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (253, '020604', '안경액세서리', 'Eyewear Accessories', 25, 2, '2/25/253', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 소분류: 모자 (0207XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (260, '020701', '캡/볼캡', 'Caps/Ball Caps', 26, 2, '2/26/260', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (261, '020702', '비니', 'Beanies', 26, 2, '2/26/261', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (262, '020703', '버킷햇', 'Bucket Hats', 26, 2, '2/26/262', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (263, '020704', '페도라/중절모', 'Fedoras', 26, 2, '2/26/263', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (264, '020705', '베레모', 'Berets', 26, 2, '2/26/264', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- 소분류: 벨트/서스펜더 (0208XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (270, '020801', '남성벨트', 'Men''s Belts', 27, 2, '2/27/270', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (271, '020802', '여성벨트', 'Women''s Belts', 27, 2, '2/27/271', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (272, '020803', '서스펜더', 'Suspenders', 27, 2, '2/27/272', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- 소분류: 스카프/머플러 (0209XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (280, '020901', '스카프', 'Scarves', 28, 2, '2/28/280', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (281, '020902', '머플러', 'Mufflers', 28, 2, '2/28/281', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (282, '020903', '숄/스톨', 'Shawls/Stoles', 28, 2, '2/28/282', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 소분류: 기타 액세서리 (0210XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (290, '021001', '장갑', 'Gloves', 29, 2, '2/29/290', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (291, '021002', '넥타이/보타이', 'Ties/Bow Ties', 29, 2, '2/29/291', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (292, '021003', '포켓스퀘어', 'Pocket Squares', 29, 2, '2/29/292', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (293, '021004', '키홀더/키링', 'Key Holders/Key Rings', 29, 2, '2/29/293', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (294, '021005', '우산', 'Umbrellas', 29, 2, '2/29/294', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- ============================================
-- 4. 세분류: 남성신발 (02010100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2000, '02010101', '스니커즈', 'Sneakers', 200, 3, '2/20/200/2000', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2001, '02010102', '로퍼', 'Loafers', 200, 3, '2/20/200/2001', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2002, '02010103', '구두/옥스포드', 'Oxfords', 200, 3, '2/20/200/2002', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2003, '02010104', '부츠', 'Boots', 200, 3, '2/20/200/2003', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2004, '02010105', '샌들/슬리퍼', 'Sandals/Slippers', 200, 3, '2/20/200/2004', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2005, '02010106', '러닝화', 'Running Shoes', 200, 3, '2/20/200/2005', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2006, '02010107', '캔버스화', 'Canvas Shoes', 200, 3, '2/20/200/2006', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT');

-- 세분류: 여성신발 (02010200~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2010, '02010201', '스니커즈', 'Sneakers', 201, 3, '2/20/201/2010', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2011, '02010202', '힐/펌프스', 'Heels/Pumps', 201, 3, '2/20/201/2011', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2012, '02010203', '플랫슈즈', 'Flats', 201, 3, '2/20/201/2012', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2013, '02010204', '로퍼', 'Loafers', 201, 3, '2/20/201/2013', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2014, '02010205', '부츠', 'Boots', 201, 3, '2/20/201/2014', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2015, '02010206', '샌들', 'Sandals', 201, 3, '2/20/201/2015', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2016, '02010207', '뮬/슬라이드', 'Mules/Slides', 201, 3, '2/20/201/2016', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2017, '02010208', '웨지힐', 'Wedge Heels', 201, 3, '2/20/201/2017', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT');

-- 세분류: 유니섹스신발 (02010300~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2020, '02010301', '스니커즈', 'Sneakers', 202, 3, '2/20/202/2020', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'ADULT'),
    (2021, '02010302', '슬리퍼', 'Slippers', 202, 3, '2/20/202/2021', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'ADULT'),
    (2022, '02010303', '레인부츠', 'Rain Boots', 202, 3, '2/20/202/2022', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'ADULT'),
    (2023, '02010304', '아쿠아슈즈', 'Aqua Shoes', 202, 3, '2/20/202/2023', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'ADULT');

-- ============================================
-- 4. 세분류: 남성가방 (02020100~)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2100, '02020101', '백팩', 'Backpacks', 210, 3, '2/21/210/2100', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (2101, '02020102', '크로스백', 'Crossbody Bags', 210, 3, '2/21/210/2101', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (2102, '02020103', '메신저백', 'Messenger Bags', 210, 3, '2/21/210/2102', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (2103, '02020104', '브리프케이스', 'Briefcases', 210, 3, '2/21/210/2103', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (2104, '02020105', '토트백', 'Tote Bags', 210, 3, '2/21/210/2104', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT'),
    (2105, '02020106', '클러치/파우치', 'Clutches/Pouches', 210, 3, '2/21/210/2105', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'MALE', 'ADULT');

-- 세분류: 여성가방 (02020200~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2110, '02020201', '토트백', 'Tote Bags', 211, 3, '2/21/211/2110', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2111, '02020202', '숄더백', 'Shoulder Bags', 211, 3, '2/21/211/2111', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2112, '02020203', '크로스백', 'Crossbody Bags', 211, 3, '2/21/211/2112', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2113, '02020204', '핸드백', 'Handbags', 211, 3, '2/21/211/2113', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2114, '02020205', '클러치', 'Clutches', 211, 3, '2/21/211/2114', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2115, '02020206', '미니백', 'Mini Bags', 211, 3, '2/21/211/2115', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2116, '02020207', '버킷백', 'Bucket Bags', 211, 3, '2/21/211/2116', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2117, '02020208', '호보백', 'Hobo Bags', 211, 3, '2/21/211/2117', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT');

-- 세분류: 유니섹스가방 (02020300~)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2120, '02020301', '백팩', 'Backpacks', 212, 3, '2/21/212/2120', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT'),
    (2121, '02020302', '에코백', 'Eco Bags', 212, 3, '2/21/212/2121', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT'),
    (2122, '02020303', '여행가방', 'Travel Bags', 212, 3, '2/21/212/2122', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT'),
    (2123, '02020304', '노트북가방', 'Laptop Bags', 212, 3, '2/21/212/2123', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT'),
    (2124, '02020305', '캐리어', 'Luggage', 212, 3, '2/21/212/2124', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT'),
    (2125, '02020306', '스포츠/짐백', 'Sports/Gym Bags', 212, 3, '2/21/212/2125', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'UNISEX', 'ADULT');

-- ============================================
-- 4. 세분류: 지갑 (020301~020305 -> 세분류)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 남성지갑 세분류
    (2200, '02030101', '반지갑', 'Bifold Wallets', 220, 3, '2/22/220/2200', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2201, '02030102', '장지갑', 'Long Wallets', 220, 3, '2/22/220/2201', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2202, '02030103', '3단지갑', 'Trifold Wallets', 220, 3, '2/22/220/2202', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    -- 여성지갑 세분류
    (2210, '02030201', '장지갑', 'Long Wallets', 221, 3, '2/22/221/2210', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (2211, '02030202', '반지갑', 'Bifold Wallets', 221, 3, '2/22/221/2211', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (2212, '02030203', '지퍼장지갑', 'Zip Wallets', 221, 3, '2/22/221/2212', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    -- 카드케이스 세분류
    (2220, '02030301', '카드홀더', 'Card Holders', 222, 3, '2/22/222/2220', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (2221, '02030302', '명함케이스', 'Business Card Cases', 222, 3, '2/22/222/2221', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT');

-- ============================================
-- 4. 세분류: 시계 (020401~020404 -> 세분류)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 남성시계 세분류
    (2300, '02040101', '드레스워치', 'Dress Watches', 230, 3, '2/23/230/2300', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2301, '02040102', '스포츠워치', 'Sports Watches', 230, 3, '2/23/230/2301', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2302, '02040103', '크로노그래프', 'Chronographs', 230, 3, '2/23/230/2302', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2303, '02040104', '다이버워치', 'Diver Watches', 230, 3, '2/23/230/2303', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    -- 여성시계 세분류
    (2310, '02040201', '드레스워치', 'Dress Watches', 231, 3, '2/23/231/2310', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (2311, '02040202', '주얼리워치', 'Jewelry Watches', 231, 3, '2/23/231/2311', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    (2312, '02040203', '패션워치', 'Fashion Watches', 231, 3, '2/23/231/2312', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT');

-- ============================================
-- 4. 세분류: 주얼리 (020501~020506 -> 세분류)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 목걸이/펜던트 세분류
    (2400, '02050101', '체인목걸이', 'Chain Necklaces', 240, 3, '2/24/240/2400', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2401, '02050102', '펜던트', 'Pendants', 240, 3, '2/24/240/2401', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2402, '02050103', '초커', 'Chokers', 240, 3, '2/24/240/2402', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    -- 귀걸이 세분류
    (2410, '02050201', '드롭귀걸이', 'Drop Earrings', 241, 3, '2/24/241/2410', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2411, '02050202', '스터드', 'Studs', 241, 3, '2/24/241/2411', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2412, '02050203', '후프귀걸이', 'Hoop Earrings', 241, 3, '2/24/241/2412', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    -- 반지 세분류
    (2420, '02050301', '일반반지', 'Rings', 242, 3, '2/24/242/2420', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2421, '02050302', '시그넷링', 'Signet Rings', 242, 3, '2/24/242/2421', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2422, '02050303', '스태킹링', 'Stacking Rings', 242, 3, '2/24/242/2422', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- ============================================
-- 4. 세분류: 아이웨어, 모자, 벨트 등 (간략히)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    -- 선글라스 세분류
    (2500, '02060101', '아비에이터', 'Aviators', 250, 3, '2/25/250/2500', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2501, '02060102', '웨이페어러', 'Wayfarers', 250, 3, '2/25/250/2501', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2502, '02060103', '라운드', 'Round', 250, 3, '2/25/250/2502', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2503, '02060104', '스퀘어', 'Square', 250, 3, '2/25/250/2503', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2504, '02060105', '캣아이', 'Cat Eye', 250, 3, '2/25/250/2504', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'FEMALE', 'ADULT'),
    -- 안경테 세분류
    (2510, '02060201', '풀프레임', 'Full Frame', 251, 3, '2/25/251/2510', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2511, '02060202', '하프프레임', 'Half Frame', 251, 3, '2/25/251/2511', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2512, '02060203', '무테', 'Rimless', 251, 3, '2/25/251/2512', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    -- 캡/볼캡 세분류
    (2600, '02070101', '스냅백', 'Snapbacks', 260, 3, '2/26/260/2600', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (2601, '02070102', '플렉스핏', 'Flexfit', 260, 3, '2/26/260/2601', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (2602, '02070103', '5패널', '5 Panel', 260, 3, '2/26/260/2602', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    (2603, '02070104', '트러커캡', 'Trucker Caps', 260, 3, '2/26/260/2603', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'UNISEX', 'ADULT'),
    -- 남성벨트 세분류
    (2700, '02080101', '가죽벨트', 'Leather Belts', 270, 3, '2/27/270/2700', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2701, '02080102', '캔버스벨트', 'Canvas Belts', 270, 3, '2/27/270/2701', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2702, '02080103', '정장벨트', 'Dress Belts', 270, 3, '2/27/270/2702', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT');
