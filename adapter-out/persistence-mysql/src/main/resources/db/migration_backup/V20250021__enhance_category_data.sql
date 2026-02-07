-- ============================================
-- Category Enhancement - Part 5: 카테고리 확장
-- ============================================
-- 신규 대분류: 07 가구/인테리어, 08 키즈
-- 기존 패션의류/잡화 세분류 보강
-- ============================================

-- ============================================
-- 1. 대분류: 가구/인테리어 (07)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (7, '07', '가구/인테리어', 'Furniture/Interior', NULL, 0, '7', 7, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 중분류: 가구/인테리어
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (70, '0701', '가구', 'Furniture', 7, 1, '7/70', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (71, '0702', '침구/패브릭', 'Bedding/Fabric', 7, 1, '7/71', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (72, '0703', '조명', 'Lighting', 7, 1, '7/72', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (73, '0704', '수납/정리', 'Storage/Organization', 7, 1, '7/73', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (74, '0705', '홈데코', 'Home Decor', 7, 1, '7/74', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (75, '0706', '욕실용품', 'Bathroom', 7, 1, '7/75', 6, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (76, '0707', 'DIY/공구', 'DIY/Tools', 7, 1, '7/76', 7, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 가구 (0701XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (700, '070101', '거실가구', 'Living Room Furniture', 70, 2, '7/70/700', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (701, '070102', '침실가구', 'Bedroom Furniture', 70, 2, '7/70/701', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (702, '070103', '주방/식당가구', 'Kitchen/Dining Furniture', 70, 2, '7/70/702', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (703, '070104', '서재/사무가구', 'Office Furniture', 70, 2, '7/70/703', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (704, '070105', '현관/베란다', 'Entryway/Balcony', 70, 2, '7/70/704', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 거실가구
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7000, '07010101', '소파', 'Sofas', 700, 3, '7/70/700/7000', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7001, '07010102', '거실장/TV장', 'TV Stands', 700, 3, '7/70/700/7001', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7002, '07010103', '거실테이블', 'Coffee Tables', 700, 3, '7/70/700/7002', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7003, '07010104', '1인소파/안락의자', 'Armchairs', 700, 3, '7/70/700/7003', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7004, '07010105', '스툴/오토만', 'Stools/Ottomans', 700, 3, '7/70/700/7004', 5, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 침실가구
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7010, '07010201', '침대프레임', 'Bed Frames', 701, 3, '7/70/701/7010', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7011, '07010202', '매트리스', 'Mattresses', 701, 3, '7/70/701/7011', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7012, '07010203', '옷장', 'Wardrobes', 701, 3, '7/70/701/7012', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7013, '07010204', '서랍장', 'Dressers', 701, 3, '7/70/701/7013', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7014, '07010205', '화장대', 'Vanities', 701, 3, '7/70/701/7014', 5, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7015, '07010206', '협탁', 'Nightstands', 701, 3, '7/70/701/7015', 6, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 주방/식당가구
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7020, '07010301', '식탁', 'Dining Tables', 702, 3, '7/70/702/7020', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7021, '07010302', '식탁의자', 'Dining Chairs', 702, 3, '7/70/702/7021', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7022, '07010303', '주방수납장', 'Kitchen Cabinets', 702, 3, '7/70/702/7022', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7023, '07010304', '바/카운터', 'Bar/Counter', 702, 3, '7/70/702/7023', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 서재/사무가구
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7030, '07010401', '책상', 'Desks', 703, 3, '7/70/703/7030', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7031, '07010402', '사무용의자', 'Office Chairs', 703, 3, '7/70/703/7031', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7032, '07010403', '책장', 'Bookcases', 703, 3, '7/70/703/7032', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7033, '07010404', '파일캐비넷', 'File Cabinets', 703, 3, '7/70/703/7033', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 침구/패브릭 (0702XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (710, '070201', '침구세트', 'Bedding Sets', 71, 2, '7/71/710', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (711, '070202', '이불/베개', 'Quilts/Pillows', 71, 2, '7/71/711', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (712, '070203', '커튼', 'Curtains', 71, 2, '7/71/712', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (713, '070204', '카페트/러그', 'Carpets/Rugs', 71, 2, '7/71/713', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (714, '070205', '쿠션/방석', 'Cushions/Seat Pads', 71, 2, '7/71/714', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 침구세트
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7100, '07020101', '호텔침구', 'Hotel Bedding', 710, 3, '7/71/710/7100', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7101, '07020102', '사계절침구', 'All-Season Bedding', 710, 3, '7/71/710/7101', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7102, '07020103', '여름침구', 'Summer Bedding', 710, 3, '7/71/710/7102', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7103, '07020104', '겨울침구', 'Winter Bedding', 710, 3, '7/71/710/7103', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 이불/베개
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7110, '07020201', '이불솜', 'Quilt Filling', 711, 3, '7/71/711/7110', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7111, '07020202', '이불커버', 'Duvet Covers', 711, 3, '7/71/711/7111', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7112, '07020203', '베개솜', 'Pillow Filling', 711, 3, '7/71/711/7112', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7113, '07020204', '베개커버', 'Pillowcases', 711, 3, '7/71/711/7113', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7114, '07020205', '토퍼', 'Mattress Toppers', 711, 3, '7/71/711/7114', 5, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 조명 (0703XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (720, '070301', '천장조명', 'Ceiling Lights', 72, 2, '7/72/720', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (721, '070302', '스탠드조명', 'Floor/Table Lamps', 72, 2, '7/72/721', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (722, '070303', '벽조명', 'Wall Lights', 72, 2, '7/72/722', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (723, '070304', '무드등/간접조명', 'Mood Lighting', 72, 2, '7/72/723', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 천장조명
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7200, '07030101', '펜던트조명', 'Pendant Lights', 720, 3, '7/72/720/7200', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7201, '07030102', '샹들리에', 'Chandeliers', 720, 3, '7/72/720/7201', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7202, '07030103', 'LED등/형광등', 'LED/Fluorescent', 720, 3, '7/72/720/7202', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7203, '07030104', '매립조명', 'Recessed Lights', 720, 3, '7/72/720/7203', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 스탠드조명
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7210, '07030201', '플로어스탠드', 'Floor Lamps', 721, 3, '7/72/721/7210', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7211, '07030202', '테이블스탠드', 'Table Lamps', 721, 3, '7/72/721/7211', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7212, '07030203', '데스크스탠드', 'Desk Lamps', 721, 3, '7/72/721/7212', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 소분류: 홈데코 (0705XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (740, '070501', '액자/그림', 'Frames/Paintings', 74, 2, '7/74/740', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (741, '070502', '화병/조화', 'Vases/Artificial Flowers', 74, 2, '7/74/741', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (742, '070503', '시계', 'Clocks', 74, 2, '7/74/742', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (743, '070504', '거울', 'Mirrors', 74, 2, '7/74/743', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (744, '070505', '캔들/디퓨저', 'Candles/Diffusers', 74, 2, '7/74/744', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (745, '070506', '장식소품', 'Decorative Items', 74, 2, '7/74/745', 6, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 액자/그림
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7400, '07050101', '포스터/그림', 'Posters/Prints', 740, 3, '7/74/740/7400', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7401, '07050102', '액자', 'Frames', 740, 3, '7/74/740/7401', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7402, '07050103', '캔버스', 'Canvas Art', 740, 3, '7/74/740/7402', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- 세분류: 시계
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (7420, '07050301', '벽시계', 'Wall Clocks', 742, 3, '7/74/742/7420', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7421, '07050302', '탁상시계', 'Table Clocks', 742, 3, '7/74/742/7421', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE'),
    (7422, '07050303', '알람시계', 'Alarm Clocks', 742, 3, '7/74/742/7422', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'NONE');

-- ============================================
-- 2. 대분류: 키즈 (08)
-- ============================================
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES (8, '08', '키즈', 'Kids', NULL, 0, '8', 8, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'KIDS');

-- 중분류: 키즈
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (80, '0801', '유아동의류', 'Kids Clothing', 8, 1, '8/80', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'KIDS'),
    (81, '0802', '유아동신발', 'Kids Shoes', 8, 1, '8/81', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'KIDS'),
    (82, '0803', '유아동잡화', 'Kids Accessories', 8, 1, '8/82', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (83, '0804', '베이비', 'Baby', 8, 1, '8/83', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (84, '0805', '출산/육아용품', 'Maternity/Childcare', 8, 1, '8/84', 5, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (85, '0806', '유아동스포츠', 'Kids Sports', 8, 1, '8/85', 6, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'KIDS');

-- 소분류: 유아동의류 (0801XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (800, '080101', '남아의류', 'Boys Clothing', 80, 2, '8/80/800', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (801, '080102', '여아의류', 'Girls Clothing', 80, 2, '8/80/801', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (802, '080103', '공용의류', 'Unisex Kids Clothing', 80, 2, '8/80/802', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'KIDS'),
    (803, '080104', '교복/학용품', 'School Uniforms', 80, 2, '8/80/803', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'KIDS');

-- 세분류: 남아의류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8000, '08010101', '남아상의', 'Boys Tops', 800, 3, '8/80/800/8000', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (8001, '08010102', '남아하의', 'Boys Bottoms', 800, 3, '8/80/800/8001', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (8002, '08010103', '남아아우터', 'Boys Outerwear', 800, 3, '8/80/800/8002', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (8003, '08010104', '남아세트', 'Boys Sets', 800, 3, '8/80/800/8003', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (8004, '08010105', '남아언더웨어', 'Boys Underwear', 800, 3, '8/80/800/8004', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS'),
    (8005, '08010106', '남아잠옷', 'Boys Sleepwear', 800, 3, '8/80/800/8005', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'KIDS');

-- 세분류: 여아의류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8010, '08010201', '여아상의', 'Girls Tops', 801, 3, '8/80/801/8010', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8011, '08010202', '여아하의', 'Girls Bottoms', 801, 3, '8/80/801/8011', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8012, '08010203', '여아아우터', 'Girls Outerwear', 801, 3, '8/80/801/8012', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8013, '08010204', '여아원피스', 'Girls Dresses', 801, 3, '8/80/801/8013', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8014, '08010205', '여아세트', 'Girls Sets', 801, 3, '8/80/801/8014', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8015, '08010206', '여아언더웨어', 'Girls Underwear', 801, 3, '8/80/801/8015', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS'),
    (8016, '08010207', '여아잠옷', 'Girls Sleepwear', 801, 3, '8/80/801/8016', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'KIDS');

-- 세분류: 공용의류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8020, '08010301', '키즈티셔츠', 'Kids T-Shirts', 802, 3, '8/80/802/8020', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'KIDS'),
    (8021, '08010302', '키즈맨투맨', 'Kids Sweatshirts', 802, 3, '8/80/802/8021', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'KIDS'),
    (8022, '08010303', '키즈후드', 'Kids Hoodies', 802, 3, '8/80/802/8022', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'KIDS'),
    (8023, '08010304', '키즈트레이닝', 'Kids Training Sets', 802, 3, '8/80/802/8023', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'UNISEX', 'KIDS');

-- 소분류: 유아동신발 (0802XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (810, '080201', '남아신발', 'Boys Shoes', 81, 2, '8/81/810', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'KIDS'),
    (811, '080202', '여아신발', 'Girls Shoes', 81, 2, '8/81/811', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS'),
    (812, '080203', '공용신발', 'Unisex Kids Shoes', 81, 2, '8/81/812', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'UNISEX', 'KIDS');

-- 세분류: 남아신발
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8100, '08020101', '남아스니커즈', 'Boys Sneakers', 810, 3, '8/81/810/8100', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'KIDS'),
    (8101, '08020102', '남아구두', 'Boys Dress Shoes', 810, 3, '8/81/810/8101', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'KIDS'),
    (8102, '08020103', '남아샌들', 'Boys Sandals', 810, 3, '8/81/810/8102', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'KIDS'),
    (8103, '08020104', '남아부츠', 'Boys Boots', 810, 3, '8/81/810/8103', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'KIDS');

-- 세분류: 여아신발
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8110, '08020201', '여아스니커즈', 'Girls Sneakers', 811, 3, '8/81/811/8110', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS'),
    (8111, '08020202', '여아구두', 'Girls Dress Shoes', 811, 3, '8/81/811/8111', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS'),
    (8112, '08020203', '여아샌들', 'Girls Sandals', 811, 3, '8/81/811/8112', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS'),
    (8113, '08020204', '여아부츠', 'Girls Boots', 811, 3, '8/81/811/8113', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS'),
    (8114, '08020205', '여아플랫', 'Girls Flats', 811, 3, '8/81/811/8114', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'KIDS');

-- 소분류: 유아동잡화 (0803XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (820, '080301', '키즈가방', 'Kids Bags', 82, 2, '8/82/820', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'KIDS'),
    (821, '080302', '키즈모자', 'Kids Hats', 82, 2, '8/82/821', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (822, '080303', '키즈양말', 'Kids Socks', 82, 2, '8/82/822', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (823, '080304', '키즈액세서리', 'Kids Accessories', 82, 2, '8/82/823', 4, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS');

-- 세분류: 키즈가방
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8200, '08030101', '키즈백팩', 'Kids Backpacks', 820, 3, '8/82/820/8200', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'KIDS'),
    (8201, '08030102', '키즈크로스백', 'Kids Crossbody Bags', 820, 3, '8/82/820/8201', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'KIDS'),
    (8202, '08030103', '키즈보조가방', 'Kids Pouches', 820, 3, '8/82/820/8202', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'KIDS'),
    (8203, '08030104', '학생가방', 'School Bags', 820, 3, '8/82/820/8203', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'NONE', 'KIDS');

-- 세분류: 키즈모자
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8210, '08030201', '키즈캡', 'Kids Caps', 821, 3, '8/82/821/8210', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (8211, '08030202', '키즈비니', 'Kids Beanies', 821, 3, '8/82/821/8211', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (8212, '08030203', '키즈버킷햇', 'Kids Bucket Hats', 821, 3, '8/82/821/8212', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS'),
    (8213, '08030204', '키즈썬캡', 'Kids Sun Caps', 821, 3, '8/82/821/8213', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'KIDS');

-- 소분류: 베이비 (0804XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (830, '080401', '베이비의류', 'Baby Clothing', 83, 2, '8/83/830', 1, 0, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (831, '080402', '베이비신발', 'Baby Shoes', 83, 2, '8/83/831', 2, 0, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'NONE', 'BABY'),
    (832, '080403', '베이비잡화', 'Baby Accessories', 83, 2, '8/83/832', 3, 0, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'BABY');

-- 세분류: 베이비의류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8300, '08040101', '바디슈트', 'Bodysuits', 830, 3, '8/83/830/8300', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8301, '08040102', '베이비상의', 'Baby Tops', 830, 3, '8/83/830/8301', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8302, '08040103', '베이비하의', 'Baby Bottoms', 830, 3, '8/83/830/8302', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8303, '08040104', '베이비세트', 'Baby Sets', 830, 3, '8/83/830/8303', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8304, '08040105', '베이비우주복', 'Baby Rompers', 830, 3, '8/83/830/8304', 5, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8305, '08040106', '베이비잠옷', 'Baby Sleepwear', 830, 3, '8/83/830/8305', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY'),
    (8306, '08040107', '베이비아우터', 'Baby Outerwear', 830, 3, '8/83/830/8306', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'NONE', 'BABY');

-- 소분류: 출산/육아용품 (0805XX)
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (840, '080501', '수유용품', 'Feeding Supplies', 84, 2, '8/84/840', 1, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (841, '080502', '목욕/위생', 'Bathing/Hygiene', 84, 2, '8/84/841', 2, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (842, '080503', '외출용품', 'Strollers/Car Seats', 84, 2, '8/84/842', 3, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (843, '080504', '침구/인테리어', 'Baby Bedding/Room', 84, 2, '8/84/843', 4, 0, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY');

-- 세분류: 수유용품
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (8400, '08050101', '젖병/젖꼭지', 'Bottles/Nipples', 840, 3, '8/84/840/8400', 1, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (8401, '08050102', '분유케이스', 'Formula Cases', 840, 3, '8/84/840/8401', 2, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (8402, '08050103', '이유식용품', 'Weaning Supplies', 840, 3, '8/84/840/8402', 3, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY'),
    (8403, '08050104', '수유쿠션', 'Nursing Pillows', 840, 3, '8/84/840/8403', 4, 1, 'ACTIVE', 1, 1, 'LIVING', 'ETC', 'NONE', 'BABY');

-- ============================================
-- 3. 패션의류 세분류 보강 (기존 id 미사용 영역 활용)
-- ============================================

-- 남성 아우터 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1007, '01010108', '윈드브레이커', 'Windbreakers', 100, 3, '1/10/100/1007', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1008, '01010109', '플리스', 'Fleece Jackets', 100, 3, '1/10/100/1008', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1009, '01010110', '무스탕', 'Shearling Coats', 100, 3, '1/10/100/1009', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 남성 상의 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1016, '01010207', '탱크탑/슬리브리스', 'Tank Tops', 101, 3, '1/10/101/1016', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1017, '01010208', '헨리넥', 'Henley Shirts', 101, 3, '1/10/101/1017', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 남성 하의 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1026, '01010307', '린넨팬츠', 'Linen Pants', 102, 3, '1/10/102/1026', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT'),
    (1027, '01010308', '코듀로이', 'Corduroy Pants', 102, 3, '1/10/102/1027', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'MALE', 'ADULT');

-- 여성 아우터 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1108, '01020109', '윈드브레이커', 'Windbreakers', 110, 3, '1/11/110/1108', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1109, '01020110', '플리스', 'Fleece Jackets', 110, 3, '1/11/110/1109', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1110, '01020111', '케이프/숄', 'Capes/Shawls', 110, 3, '1/11/110/1110', 11, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 여성 상의 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1117, '01020208', '탱크탑', 'Tank Tops', 111, 3, '1/11/111/1117', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1118, '01020209', '오프숄더', 'Off-Shoulder Tops', 111, 3, '1/11/111/1118', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1119, '01020210', '페플럼', 'Peplum Tops', 111, 3, '1/11/111/1119', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 여성 하의 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1126, '01020307', '조거팬츠', 'Jogger Pants', 112, 3, '1/11/112/1126', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1127, '01020308', '큐롯', 'Culottes', 112, 3, '1/11/112/1127', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1128, '01020309', '부츠컷', 'Bootcut Pants', 112, 3, '1/11/112/1128', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- 여성 원피스/드레스 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (1135, '01020406', '셔츠원피스', 'Shirt Dresses', 113, 3, '1/11/113/1135', 6, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1136, '01020407', '니트원피스', 'Knit Dresses', 113, 3, '1/11/113/1136', 7, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1137, '01020408', '플리츠원피스', 'Pleated Dresses', 113, 3, '1/11/113/1137', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT'),
    (1138, '01020409', '랩원피스', 'Wrap Dresses', 113, 3, '1/11/113/1138', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'CLOTHING', 'FEMALE', 'ADULT');

-- ============================================
-- 4. 패션잡화 세분류 보강
-- ============================================

-- 남성신발 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2007, '02010108', '드레스슈즈', 'Dress Shoes', 200, 3, '2/20/200/2007', 8, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2008, '02010109', '데저트부츠', 'Desert Boots', 200, 3, '2/20/200/2008', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT'),
    (2009, '02010110', '덱슈즈', 'Deck Shoes', 200, 3, '2/20/200/2009', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'MALE', 'ADULT');

-- 여성신발 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2018, '02010209', '에스파드리유', 'Espadrilles', 201, 3, '2/20/201/2018', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2019, '02010210', '앵클부츠', 'Ankle Boots', 201, 3, '2/20/201/2019', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT'),
    (2024, '02010211', '슬링백', 'Slingbacks', 201, 3, '2/20/201/2024', 11, 1, 'ACTIVE', 1, 1, 'FASHION', 'SHOES', 'FEMALE', 'ADULT');

-- 여성가방 추가 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2118, '02020209', '새들백', 'Saddle Bags', 211, 3, '2/21/211/2118', 9, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2119, '02020210', '바게트백', 'Baguette Bags', 211, 3, '2/21/211/2119', 10, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2126, '02020211', '박스백', 'Box Bags', 211, 3, '2/21/211/2126', 11, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2127, '02020212', '체인백', 'Chain Bags', 211, 3, '2/21/211/2127', 12, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT'),
    (2128, '02020213', '파우치백', 'Pouch Bags', 211, 3, '2/21/211/2128', 13, 1, 'ACTIVE', 1, 1, 'FASHION', 'BAGS', 'FEMALE', 'ADULT');

-- 주얼리 추가 세분류: 팔찌/뱅글
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2430, '02050401', '체인팔찌', 'Chain Bracelets', 243, 3, '2/24/243/2430', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2431, '02050402', '가죽팔찌', 'Leather Bracelets', 243, 3, '2/24/243/2431', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2432, '02050403', '비즈팔찌', 'Beaded Bracelets', 243, 3, '2/24/243/2432', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2433, '02050404', '뱅글', 'Bangles', 243, 3, '2/24/243/2433', 4, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 스카프/머플러 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2800, '02090101', '실크스카프', 'Silk Scarves', 280, 3, '2/28/280/2800', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2801, '02090102', '트윌리스카프', 'Twilly Scarves', 280, 3, '2/28/280/2801', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2802, '02090103', '반다나', 'Bandanas', 280, 3, '2/28/280/2802', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2810, '02090201', '니트머플러', 'Knit Mufflers', 281, 3, '2/28/281/2810', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2811, '02090202', '캐시미어머플러', 'Cashmere Mufflers', 281, 3, '2/28/281/2811', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2820, '02090301', '울숄', 'Wool Shawls', 282, 3, '2/28/282/2820', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2821, '02090302', '캐시미어스톨', 'Cashmere Stoles', 282, 3, '2/28/282/2821', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE');

-- 기타 액세서리 세분류
INSERT INTO category (id, code, name_ko, name_en, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group, gender_scope, age_group)
VALUES
    (2900, '02100101', '가죽장갑', 'Leather Gloves', 290, 3, '2/29/290/2900', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2901, '02100102', '니트장갑', 'Knit Gloves', 290, 3, '2/29/290/2901', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2902, '02100103', '드라이빙장갑', 'Driving Gloves', 290, 3, '2/29/290/2902', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'NONE', 'NONE'),
    (2910, '02100201', '실크넥타이', 'Silk Ties', 291, 3, '2/29/291/2910', 1, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2911, '02100202', '니트타이', 'Knit Ties', 291, 3, '2/29/291/2911', 2, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT'),
    (2912, '02100203', '보타이', 'Bow Ties', 291, 3, '2/29/291/2912', 3, 1, 'ACTIVE', 1, 1, 'FASHION', 'ACCESSORIES', 'MALE', 'ADULT');

-- ============================================
-- 검증 쿼리 (주석 처리)
-- ============================================
-- SELECT COUNT(*) AS total_categories FROM category;
-- SELECT depth, COUNT(*) FROM category GROUP BY depth ORDER BY depth;
-- SELECT name_ko, COUNT(*) FROM category WHERE depth = 0 GROUP BY name_ko;
