-- ============================================
-- Category Initial Data - Part 4: 디지털/가전(04), 생활/건강(05), 스포츠/레저(06)
-- ============================================
-- 4단계 계층 구조: 대분류 > 중분류 > 소분류 > 세분류
-- 코드 체계: XX(대) > XXXX(중) > XXXXXX(소) > XXXXXXXX(세)
-- ============================================

-- ============================================
-- 1. 대분류: 디지털/가전 (04)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (4, '04', '디지털/가전', 'Digital/Electronics', NULL, 0, '4', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 중분류: 디지털/가전
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (40, '0401', '스마트기기', 'Smart Devices', 4, 1, '4/40', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (41, '0402', '오디오/영상', 'Audio/Video', 4, 1, '4/41', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (42, '0403', '컴퓨터/주변기기', 'Computers/Peripherals', 4, 1, '4/42', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (43, '0404', '생활가전', 'Home Appliances', 4, 1, '4/43', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (44, '0405', '카메라/영상기기', 'Cameras/Video', 4, 1, '4/44', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 스마트기기 (0401XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (400, '040101', '스마트폰/태블릿', 'Smartphones/Tablets', 40, 2, '4/40/400', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (401, '040102', '웨어러블', 'Wearables', 40, 2, '4/40/401', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (402, '040103', '스마트폰액세서리', 'Phone Accessories', 40, 2, '4/40/402', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 스마트기기
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (4000, '04010101', '스마트폰', 'Smartphones', 400, 3, '4/40/400/4000', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4001, '04010102', '태블릿', 'Tablets', 400, 3, '4/40/400/4001', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4002, '04010103', 'e-리더', 'E-Readers', 400, 3, '4/40/400/4002', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4010, '04010201', '스마트워치', 'Smart Watches', 401, 3, '4/40/401/4010', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4011, '04010202', '스마트밴드', 'Smart Bands', 401, 3, '4/40/401/4011', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4012, '04010203', 'VR/AR기기', 'VR/AR Devices', 401, 3, '4/40/401/4012', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4020, '04010301', '폰케이스', 'Phone Cases', 402, 3, '4/40/402/4020', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4021, '04010302', '충전기/케이블', 'Chargers/Cables', 402, 3, '4/40/402/4021', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4022, '04010303', '보조배터리', 'Power Banks', 402, 3, '4/40/402/4022', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4023, '04010304', '보호필름', 'Screen Protectors', 402, 3, '4/40/402/4023', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 오디오/영상 (0402XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (410, '040201', '이어폰/헤드폰', 'Earphones/Headphones', 41, 2, '4/41/410', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (411, '040202', '스피커', 'Speakers', 41, 2, '4/41/411', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (412, '040203', 'TV/모니터', 'TVs/Monitors', 41, 2, '4/41/412', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 오디오/영상
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (4100, '04020101', '유선이어폰', 'Wired Earphones', 410, 3, '4/41/410/4100', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4101, '04020102', '무선이어폰', 'Wireless Earphones', 410, 3, '4/41/410/4101', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4102, '04020103', '오버이어헤드폰', 'Over-ear Headphones', 410, 3, '4/41/410/4102', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4103, '04020104', '노이즈캔슬링', 'Noise Canceling', 410, 3, '4/41/410/4103', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4110, '04020201', '블루투스스피커', 'Bluetooth Speakers', 411, 3, '4/41/411/4110', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4111, '04020202', '사운드바', 'Soundbars', 411, 3, '4/41/411/4111', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (4112, '04020203', '스마트스피커', 'Smart Speakers', 411, 3, '4/41/411/4112', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- ============================================
-- 2. 대분류: 생활/건강 (05)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (5, '05', '생활/건강', 'Living/Health', NULL, 0, '5', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 중분류: 생활/건강
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (50, '0501', '홈인테리어', 'Home Interior', 5, 1, '5/50', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (51, '0502', '주방용품', 'Kitchen', 5, 1, '5/51', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (52, '0503', '생활용품', 'Daily Essentials', 5, 1, '5/52', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (53, '0504', '건강/의료', 'Health/Medical', 5, 1, '5/53', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (54, '0505', '반려동물', 'Pets', 5, 1, '5/54', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (55, '0506', '문구/오피스', 'Stationery/Office', 5, 1, '5/55', 6, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 홈인테리어 (0501XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (500, '050101', '가구', 'Furniture', 50, 2, '5/50/500', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (501, '050102', '침구/커튼', 'Bedding/Curtains', 50, 2, '5/50/501', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (502, '050103', '조명', 'Lighting', 50, 2, '5/50/502', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (503, '050104', '수납/정리', 'Storage', 50, 2, '5/50/503', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (504, '050105', '홈데코', 'Home Decor', 50, 2, '5/50/504', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 홈인테리어
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (5000, '05010101', '소파', 'Sofas', 500, 3, '5/50/500/5000', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5001, '05010102', '테이블/책상', 'Tables/Desks', 500, 3, '5/50/500/5001', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5002, '05010103', '의자', 'Chairs', 500, 3, '5/50/500/5002', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5003, '05010104', '침대', 'Beds', 500, 3, '5/50/500/5003', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5004, '05010105', '옷장/서랍장', 'Wardrobes/Drawers', 500, 3, '5/50/500/5004', 5, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5010, '05010201', '이불/베개', 'Bedding/Pillows', 501, 3, '5/50/501/5010', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5011, '05010202', '매트리스', 'Mattresses', 501, 3, '5/50/501/5011', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5012, '05010203', '커튼/블라인드', 'Curtains/Blinds', 501, 3, '5/50/501/5012', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5013, '05010204', '카페트/러그', 'Carpets/Rugs', 501, 3, '5/50/501/5013', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5040, '05010501', '액자/그림', 'Frames/Paintings', 504, 3, '5/50/504/5040', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5041, '05010502', '화병/조화', 'Vases/Flowers', 504, 3, '5/50/504/5041', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5042, '05010503', '시계', 'Clocks', 504, 3, '5/50/504/5042', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5043, '05010504', '거울', 'Mirrors', 504, 3, '5/50/504/5043', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 주방용품 (0502XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (510, '050201', '조리도구', 'Cookware', 51, 2, '5/51/510', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (511, '050202', '식기', 'Tableware', 51, 2, '5/51/511', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (512, '050203', '컵/텀블러', 'Cups/Tumblers', 51, 2, '5/51/512', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (513, '050204', '보관용기', 'Storage Containers', 51, 2, '5/51/513', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 주방용품
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (5100, '05020101', '냄비/팬', 'Pots/Pans', 510, 3, '5/51/510/5100', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5101, '05020102', '칼/도마', 'Knives/Boards', 510, 3, '5/51/510/5101', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5102, '05020103', '조리도구세트', 'Cooking Tool Sets', 510, 3, '5/51/510/5102', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5110, '05020201', '접시/볼', 'Plates/Bowls', 511, 3, '5/51/511/5110', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5111, '05020202', '수저/커트러리', 'Cutlery', 511, 3, '5/51/511/5111', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5112, '05020203', '식기세트', 'Dinnerware Sets', 511, 3, '5/51/511/5112', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5120, '05020301', '머그컵', 'Mugs', 512, 3, '5/51/512/5120', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5121, '05020302', '보온텀블러', 'Insulated Tumblers', 512, 3, '5/51/512/5121', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5122, '05020303', '물병/보틀', 'Water Bottles', 512, 3, '5/51/512/5122', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 건강/의료 (0504XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (530, '050401', '건강관리', 'Health Care', 53, 2, '5/53/530', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (531, '050402', '의료/재활', 'Medical/Rehabilitation', 53, 2, '5/53/531', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (532, '050403', '헬스/다이어트', 'Fitness/Diet', 53, 2, '5/53/532', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 건강/의료
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (5300, '05040101', '혈압계/체온계', 'BP/Thermometers', 530, 3, '5/53/530/5300', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5301, '05040102', '체중계', 'Scales', 530, 3, '5/53/530/5301', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5302, '05040103', '안마기', 'Massagers', 530, 3, '5/53/530/5302', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5320, '05040301', '헬스보충제', 'Supplements', 532, 3, '5/53/532/5320', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5321, '05040302', '홈트레이닝', 'Home Training', 532, 3, '5/53/532/5321', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (5322, '05040303', '다이어트보조', 'Diet Aids', 532, 3, '5/53/532/5322', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- ============================================
-- 3. 대분류: 스포츠/레저 (06)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (6, '06', '스포츠/레저', 'Sports/Leisure', NULL, 0, '6', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 중분류: 스포츠/레저
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (60, '0601', '스포츠의류', 'Sports Clothing', 6, 1, '6/60', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (61, '0602', '스포츠용품', 'Sports Equipment', 6, 1, '6/61', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (62, '0603', '아웃도어', 'Outdoor', 6, 1, '6/62', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (63, '0604', '골프', 'Golf', 6, 1, '6/63', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (64, '0605', '수영/수상스포츠', 'Swimming/Water Sports', 6, 1, '6/64', 5, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (65, '0606', '겨울스포츠', 'Winter Sports', 6, 1, '6/65', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (66, '0607', '자전거', 'Cycling', 6, 1, '6/66', 7, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 소분류: 스포츠의류 (0601XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (600, '060101', '러닝웨어', 'Running Wear', 60, 2, '6/60/600', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (601, '060102', '요가/필라테스', 'Yoga/Pilates', 60, 2, '6/60/601', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (602, '060103', '트레이닝웨어', 'Training Wear', 60, 2, '6/60/602', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (603, '060104', '스포츠언더웨어', 'Sports Underwear', 60, 2, '6/60/603', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE');

-- 세분류: 스포츠의류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6000, '06010101', '러닝탑', 'Running Tops', 600, 3, '6/60/600/6000', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6001, '06010102', '러닝팬츠', 'Running Pants', 600, 3, '6/60/600/6001', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6002, '06010103', '러닝자켓', 'Running Jackets', 600, 3, '6/60/600/6002', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6010, '06010201', '요가레깅스', 'Yoga Leggings', 601, 3, '6/60/601/6010', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (6011, '06010202', '요가탑', 'Yoga Tops', 601, 3, '6/60/601/6011', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (6012, '06010203', '요가매트', 'Yoga Mats', 601, 3, '6/60/601/6012', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6020, '06010301', '트레이닝상의', 'Training Tops', 602, 3, '6/60/602/6020', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6021, '06010302', '트레이닝하의', 'Training Bottoms', 602, 3, '6/60/602/6021', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6022, '06010303', '트레이닝세트', 'Training Sets', 602, 3, '6/60/602/6022', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6030, '06010401', '스포츠브라', 'Sports Bras', 603, 3, '6/60/603/6030', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (6031, '06010402', '컴프레션웨어', 'Compression Wear', 603, 3, '6/60/603/6031', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE');

-- 소분류: 아웃도어 (0603XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (620, '060301', '등산/트레킹', 'Hiking/Trekking', 62, 2, '6/62/620', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (621, '060302', '캠핑', 'Camping', 62, 2, '6/62/621', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (622, '060303', '낚시', 'Fishing', 62, 2, '6/62/622', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 세분류: 아웃도어
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6200, '06030101', '등산화', 'Hiking Boots', 620, 3, '6/62/620/6200', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'NONE'),
    (6201, '06030102', '등산배낭', 'Hiking Backpacks', 620, 3, '6/62/620/6201', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'NONE'),
    (6202, '06030103', '등산스틱', 'Trekking Poles', 620, 3, '6/62/620/6202', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6203, '06030104', '등산의류', 'Hiking Clothing', 620, 3, '6/62/620/6203', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6210, '06030201', '텐트', 'Tents', 621, 3, '6/62/621/6210', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6211, '06030202', '침낭/매트', 'Sleeping Bags/Mats', 621, 3, '6/62/621/6211', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6212, '06030203', '캠핑테이블/의자', 'Camping Tables/Chairs', 621, 3, '6/62/621/6212', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6213, '06030204', '캠핑조명', 'Camping Lights', 621, 3, '6/62/621/6213', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6214, '06030205', '캠핑쿠커/식기', 'Camping Cookware', 621, 3, '6/62/621/6214', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 소분류: 골프 (0604XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (630, '060401', '골프클럽', 'Golf Clubs', 63, 2, '6/63/630', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (631, '060402', '골프의류', 'Golf Clothing', 63, 2, '6/63/631', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (632, '060403', '골프용품', 'Golf Accessories', 63, 2, '6/63/632', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 세분류: 골프
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6300, '06040101', '드라이버', 'Drivers', 630, 3, '6/63/630/6300', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6301, '06040102', '아이언', 'Irons', 630, 3, '6/63/630/6301', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6302, '06040103', '퍼터', 'Putters', 630, 3, '6/63/630/6302', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6303, '06040104', '골프백', 'Golf Bags', 630, 3, '6/63/630/6303', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'NONE'),
    (6310, '06040201', '골프상의', 'Golf Tops', 631, 3, '6/63/631/6310', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6311, '06040202', '골프하의', 'Golf Bottoms', 631, 3, '6/63/631/6311', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6312, '06040203', '골프신발', 'Golf Shoes', 631, 3, '6/63/631/6312', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'NONE'),
    (6313, '06040204', '골프모자', 'Golf Hats', 631, 3, '6/63/631/6313', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (6320, '06040301', '골프공', 'Golf Balls', 632, 3, '6/63/632/6320', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6321, '06040302', '골프장갑', 'Golf Gloves', 632, 3, '6/63/632/6321', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (6322, '06040303', '골프티/마커', 'Golf Tees/Markers', 632, 3, '6/63/632/6322', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 소분류: 수영/수상스포츠 (0605XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (640, '060501', '수영복', 'Swimwear', 64, 2, '6/64/640', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (641, '060502', '수영용품', 'Swimming Gear', 64, 2, '6/64/641', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (642, '060503', '서핑/보드', 'Surfing/Board', 64, 2, '6/64/642', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 세분류: 수영/수상스포츠
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6400, '06050101', '남성수영복', 'Men''s Swimwear', 640, 3, '6/64/640/6400', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (6401, '06050102', '여성수영복', 'Women''s Swimwear', 640, 3, '6/64/640/6401', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (6402, '06050103', '래쉬가드', 'Rash Guards', 640, 3, '6/64/640/6402', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6403, '06050104', '비치웨어', 'Beachwear', 640, 3, '6/64/640/6403', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6410, '06050201', '수경', 'Goggles', 641, 3, '6/64/641/6410', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6411, '06050202', '수영모', 'Swim Caps', 641, 3, '6/64/641/6411', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (6412, '06050203', '오리발', 'Fins', 641, 3, '6/64/641/6412', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 소분류: 겨울스포츠 (0606XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (650, '060601', '스키/스노보드', 'Ski/Snowboard', 65, 2, '6/65/650', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (651, '060602', '스키/보드의류', 'Ski/Board Clothing', 65, 2, '6/65/651', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE');

-- 세분류: 겨울스포츠
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6500, '06060101', '스키', 'Skis', 650, 3, '6/65/650/6500', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6501, '06060102', '스노보드', 'Snowboards', 650, 3, '6/65/650/6501', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6502, '06060103', '스키부츠/바인딩', 'Ski Boots/Bindings', 650, 3, '6/65/650/6502', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'NONE'),
    (6503, '06060104', '헬멧/고글', 'Helmets/Goggles', 650, 3, '6/65/650/6503', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (6510, '06060201', '스키자켓', 'Ski Jackets', 651, 3, '6/65/651/6510', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6511, '06060202', '스키팬츠', 'Ski Pants', 651, 3, '6/65/651/6511', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6512, '06060203', '스키장갑', 'Ski Gloves', 651, 3, '6/65/651/6512', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 소분류: 자전거 (0607XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (660, '060701', '자전거', 'Bicycles', 66, 2, '6/66/660', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (661, '060702', '자전거의류', 'Cycling Clothing', 66, 2, '6/66/661', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (662, '060703', '자전거용품', 'Cycling Accessories', 66, 2, '6/66/662', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');

-- 세분류: 자전거
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (6600, '06070101', '로드바이크', 'Road Bikes', 660, 3, '6/66/660/6600', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6601, '06070102', 'MTB', 'Mountain Bikes', 660, 3, '6/66/660/6601', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6602, '06070103', '하이브리드', 'Hybrid Bikes', 660, 3, '6/66/660/6602', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6603, '06070104', '전기자전거', 'E-Bikes', 660, 3, '6/66/660/6603', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6610, '06070201', '저지', 'Jerseys', 661, 3, '6/66/661/6610', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6611, '06070202', '빕숏', 'Bib Shorts', 661, 3, '6/66/661/6611', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'NONE'),
    (6612, '06070203', '사이클화', 'Cycling Shoes', 661, 3, '6/66/661/6612', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'NONE'),
    (6620, '06070301', '헬멧', 'Helmets', 662, 3, '6/66/662/6620', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (6621, '06070302', '자전거잠금장치', 'Bike Locks', 662, 3, '6/66/662/6621', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6622, '06070303', '자전거라이트', 'Bike Lights', 662, 3, '6/66/662/6622', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE'),
    (6623, '06070304', '사이클컴퓨터', 'Bike Computers', 662, 3, '6/66/662/6623', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ETC', 'NONE', 'NONE');
