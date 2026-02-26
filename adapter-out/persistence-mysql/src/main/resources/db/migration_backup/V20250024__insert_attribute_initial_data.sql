-- ============================================
-- Attribute 초기 데이터 마이그레이션
-- ============================================
-- 업계 표준 기반 속성 및 속성값 데이터
-- 패션/뷰티 이커머스 표준 기준
-- ============================================

-- ============================================
-- 1. attribute (속성 마스터)
-- ============================================
-- type: OPTION (옵션 선택) / SPEC (상품 스펙)
-- value_type: ENUM (선택형) / TEXT (입력형) / NUMBER (숫자형)
-- applies_level: SKU (SKU별 다름) / PRODUCT (상품 공통) / BOTH (둘 다)

INSERT INTO attribute (code, name_ko, name_en, type, value_type, applies_level, status) VALUES
-- 옵션 속성 (SKU 레벨)
('COLOR', '색상', 'Color', 'OPTION', 'ENUM', 'SKU', 'ACTIVE'),
('SIZE_CLOTHING', '의류 사이즈', 'Clothing Size', 'OPTION', 'ENUM', 'SKU', 'ACTIVE'),
('SIZE_SHOES', '신발 사이즈', 'Shoes Size', 'OPTION', 'ENUM', 'SKU', 'ACTIVE'),
('SIZE_RING', '반지 사이즈', 'Ring Size', 'OPTION', 'ENUM', 'SKU', 'ACTIVE'),
('SIZE_FREE', '프리 사이즈', 'Free Size', 'OPTION', 'ENUM', 'SKU', 'ACTIVE'),

-- 스펙 속성 (PRODUCT 레벨)
('MATERIAL', '소재', 'Material', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('PATTERN', '패턴', 'Pattern', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('SEASON', '시즌', 'Season', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('GENDER', '성별', 'Gender', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('FIT', '핏', 'Fit', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('NECKLINE', '넥라인', 'Neckline', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('SLEEVE', '소매', 'Sleeve', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('LENGTH', '기장', 'Length', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('HEEL_HEIGHT', '굽 높이', 'Heel Height', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('STRAP_TYPE', '스트랩 타입', 'Strap Type', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('CLOSURE', '클로저', 'Closure', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('WATER_RESISTANCE', '방수 등급', 'Water Resistance', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('SKIN_TYPE', '피부 타입', 'Skin Type', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE'),
('SKIN_CONCERN', '피부 고민', 'Skin Concern', 'SPEC', 'ENUM', 'PRODUCT', 'ACTIVE');

-- ============================================
-- 2. attribute_value (속성값)
-- ============================================

-- ----------------------------------------
-- 2-1. COLOR (색상) - 30개 표준 색상
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'BLACK' as code, '블랙' as name_ko, 'Black' as name_en, '{"hex":"#000000"}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'WHITE', '화이트', 'White', '{"hex":"#FFFFFF"}', 2 UNION ALL
    SELECT 'GRAY', '그레이', 'Gray', '{"hex":"#808080"}', 3 UNION ALL
    SELECT 'CHARCOAL', '차콜', 'Charcoal', '{"hex":"#36454F"}', 4 UNION ALL
    SELECT 'NAVY', '네이비', 'Navy', '{"hex":"#000080"}', 5 UNION ALL
    SELECT 'BLUE', '블루', 'Blue', '{"hex":"#0000FF"}', 6 UNION ALL
    SELECT 'SKY_BLUE', '스카이블루', 'Sky Blue', '{"hex":"#87CEEB"}', 7 UNION ALL
    SELECT 'RED', '레드', 'Red', '{"hex":"#FF0000"}', 8 UNION ALL
    SELECT 'BURGUNDY', '버건디', 'Burgundy', '{"hex":"#800020"}', 9 UNION ALL
    SELECT 'WINE', '와인', 'Wine', '{"hex":"#722F37"}', 10 UNION ALL
    SELECT 'PINK', '핑크', 'Pink', '{"hex":"#FFC0CB"}', 11 UNION ALL
    SELECT 'CORAL', '코랄', 'Coral', '{"hex":"#FF7F50"}', 12 UNION ALL
    SELECT 'ORANGE', '오렌지', 'Orange', '{"hex":"#FFA500"}', 13 UNION ALL
    SELECT 'YELLOW', '옐로우', 'Yellow', '{"hex":"#FFFF00"}', 14 UNION ALL
    SELECT 'MUSTARD', '머스타드', 'Mustard', '{"hex":"#FFDB58"}', 15 UNION ALL
    SELECT 'GREEN', '그린', 'Green', '{"hex":"#008000"}', 16 UNION ALL
    SELECT 'KHAKI', '카키', 'Khaki', '{"hex":"#C3B091"}', 17 UNION ALL
    SELECT 'OLIVE', '올리브', 'Olive', '{"hex":"#808000"}', 18 UNION ALL
    SELECT 'MINT', '민트', 'Mint', '{"hex":"#98FF98"}', 19 UNION ALL
    SELECT 'PURPLE', '퍼플', 'Purple', '{"hex":"#800080"}', 20 UNION ALL
    SELECT 'LAVENDER', '라벤더', 'Lavender', '{"hex":"#E6E6FA"}', 21 UNION ALL
    SELECT 'BROWN', '브라운', 'Brown', '{"hex":"#A52A2A"}', 22 UNION ALL
    SELECT 'CAMEL', '카멜', 'Camel', '{"hex":"#C19A6B"}', 23 UNION ALL
    SELECT 'BEIGE', '베이지', 'Beige', '{"hex":"#F5F5DC"}', 24 UNION ALL
    SELECT 'IVORY', '아이보리', 'Ivory', '{"hex":"#FFFFF0"}', 25 UNION ALL
    SELECT 'CREAM', '크림', 'Cream', '{"hex":"#FFFDD0"}', 26 UNION ALL
    SELECT 'GOLD', '골드', 'Gold', '{"hex":"#FFD700"}', 27 UNION ALL
    SELECT 'SILVER', '실버', 'Silver', '{"hex":"#C0C0C0"}', 28 UNION ALL
    SELECT 'ROSE_GOLD', '로즈골드', 'Rose Gold', '{"hex":"#B76E79"}', 29 UNION ALL
    SELECT 'MULTI', '멀티', 'Multi', '{"hex":"#GRADIENT"}', 30
) v
WHERE a.code = 'COLOR';

-- ----------------------------------------
-- 2-2. SIZE_CLOTHING (의류 사이즈)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    -- 알파벳 사이즈
    SELECT 'XXS' as code, 'XXS' as name_ko, 'XXS' as name_en, '{"type":"alpha","order":1}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'XS', 'XS', 'XS', '{"type":"alpha","order":2}', 2 UNION ALL
    SELECT 'S', 'S', 'S', '{"type":"alpha","order":3}', 3 UNION ALL
    SELECT 'M', 'M', 'M', '{"type":"alpha","order":4}', 4 UNION ALL
    SELECT 'L', 'L', 'L', '{"type":"alpha","order":5}', 5 UNION ALL
    SELECT 'XL', 'XL', 'XL', '{"type":"alpha","order":6}', 6 UNION ALL
    SELECT 'XXL', 'XXL', 'XXL', '{"type":"alpha","order":7}', 7 UNION ALL
    SELECT 'XXXL', 'XXXL', 'XXXL', '{"type":"alpha","order":8}', 8 UNION ALL
    -- 숫자 사이즈 (한국)
    SELECT 'KR_44', '44', '44', '{"type":"numeric_kr","cm":"85-88"}', 10 UNION ALL
    SELECT 'KR_55', '55', '55', '{"type":"numeric_kr","cm":"88-91"}', 11 UNION ALL
    SELECT 'KR_66', '66', '66', '{"type":"numeric_kr","cm":"91-94"}', 12 UNION ALL
    SELECT 'KR_77', '77', '77', '{"type":"numeric_kr","cm":"94-97"}', 13 UNION ALL
    SELECT 'KR_88', '88', '88', '{"type":"numeric_kr","cm":"97-100"}', 14 UNION ALL
    SELECT 'KR_99', '99', '99', '{"type":"numeric_kr","cm":"100-103"}', 15 UNION ALL
    SELECT 'KR_100', '100', '100', '{"type":"numeric_kr","cm":"103-106"}', 16 UNION ALL
    SELECT 'KR_105', '105', '105', '{"type":"numeric_kr","cm":"106-109"}', 17 UNION ALL
    SELECT 'KR_110', '110', '110', '{"type":"numeric_kr","cm":"109-112"}', 18 UNION ALL
    -- 유럽 사이즈
    SELECT 'EU_34', 'EU 34', 'EU 34', '{"type":"eu","us":"2"}', 20 UNION ALL
    SELECT 'EU_36', 'EU 36', 'EU 36', '{"type":"eu","us":"4"}', 21 UNION ALL
    SELECT 'EU_38', 'EU 38', 'EU 38', '{"type":"eu","us":"6"}', 22 UNION ALL
    SELECT 'EU_40', 'EU 40', 'EU 40', '{"type":"eu","us":"8"}', 23 UNION ALL
    SELECT 'EU_42', 'EU 42', 'EU 42', '{"type":"eu","us":"10"}', 24 UNION ALL
    SELECT 'EU_44', 'EU 44', 'EU 44', '{"type":"eu","us":"12"}', 25 UNION ALL
    SELECT 'EU_46', 'EU 46', 'EU 46', '{"type":"eu","us":"14"}', 26 UNION ALL
    SELECT 'EU_48', 'EU 48', 'EU 48', '{"type":"eu","us":"16"}', 27
) v
WHERE a.code = 'SIZE_CLOTHING';

-- ----------------------------------------
-- 2-3. SIZE_SHOES (신발 사이즈)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    -- 한국/mm 사이즈
    SELECT 'MM_220' as code, '220' as name_ko, '220mm' as name_en, '{"mm":220,"eu":35,"us_m":4,"us_w":5}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'MM_225', '225', '225mm', '{"mm":225,"eu":35.5,"us_m":4.5,"us_w":5.5}', 2 UNION ALL
    SELECT 'MM_230', '230', '230mm', '{"mm":230,"eu":36,"us_m":5,"us_w":6}', 3 UNION ALL
    SELECT 'MM_235', '235', '235mm', '{"mm":235,"eu":36.5,"us_m":5.5,"us_w":6.5}', 4 UNION ALL
    SELECT 'MM_240', '240', '240mm', '{"mm":240,"eu":37,"us_m":6,"us_w":7}', 5 UNION ALL
    SELECT 'MM_245', '245', '245mm', '{"mm":245,"eu":38,"us_m":6.5,"us_w":7.5}', 6 UNION ALL
    SELECT 'MM_250', '250', '250mm', '{"mm":250,"eu":39,"us_m":7,"us_w":8}', 7 UNION ALL
    SELECT 'MM_255', '255', '255mm', '{"mm":255,"eu":40,"us_m":7.5,"us_w":8.5}', 8 UNION ALL
    SELECT 'MM_260', '260', '260mm', '{"mm":260,"eu":41,"us_m":8,"us_w":9}', 9 UNION ALL
    SELECT 'MM_265', '265', '265mm', '{"mm":265,"eu":42,"us_m":8.5,"us_w":9.5}', 10 UNION ALL
    SELECT 'MM_270', '270', '270mm', '{"mm":270,"eu":43,"us_m":9,"us_w":10}', 11 UNION ALL
    SELECT 'MM_275', '275', '275mm', '{"mm":275,"eu":44,"us_m":10,"us_w":11}', 12 UNION ALL
    SELECT 'MM_280', '280', '280mm', '{"mm":280,"eu":45,"us_m":11,"us_w":12}', 13 UNION ALL
    SELECT 'MM_285', '285', '285mm', '{"mm":285,"eu":46,"us_m":12,"us_w":13}', 14 UNION ALL
    SELECT 'MM_290', '290', '290mm', '{"mm":290,"eu":47,"us_m":13,"us_w":14}', 15 UNION ALL
    SELECT 'MM_295', '295', '295mm', '{"mm":295,"eu":48,"us_m":14,"us_w":15}', 16 UNION ALL
    SELECT 'MM_300', '300', '300mm', '{"mm":300,"eu":49,"us_m":15,"us_w":16}', 17
) v
WHERE a.code = 'SIZE_SHOES';

-- ----------------------------------------
-- 2-4. SIZE_RING (반지 사이즈)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'RING_5' as code, '5호' as name_ko, 'Size 5' as name_en, '{"kr":5,"us":3,"diameter_mm":14.0}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'RING_7', '7호', 'Size 7', '{"kr":7,"us":4,"diameter_mm":14.8}', 2 UNION ALL
    SELECT 'RING_9', '9호', 'Size 9', '{"kr":9,"us":5,"diameter_mm":15.6}', 3 UNION ALL
    SELECT 'RING_11', '11호', 'Size 11', '{"kr":11,"us":6,"diameter_mm":16.4}', 4 UNION ALL
    SELECT 'RING_13', '13호', 'Size 13', '{"kr":13,"us":7,"diameter_mm":17.2}', 5 UNION ALL
    SELECT 'RING_15', '15호', 'Size 15', '{"kr":15,"us":8,"diameter_mm":18.0}', 6 UNION ALL
    SELECT 'RING_17', '17호', 'Size 17', '{"kr":17,"us":9,"diameter_mm":18.8}', 7 UNION ALL
    SELECT 'RING_19', '19호', 'Size 19', '{"kr":19,"us":10,"diameter_mm":19.6}', 8 UNION ALL
    SELECT 'RING_21', '21호', 'Size 21', '{"kr":21,"us":11,"diameter_mm":20.4}', 9 UNION ALL
    SELECT 'RING_23', '23호', 'Size 23', '{"kr":23,"us":12,"diameter_mm":21.2}', 10
) v
WHERE a.code = 'SIZE_RING';

-- ----------------------------------------
-- 2-5. SIZE_FREE (프리 사이즈)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'FREE' as code, 'FREE' as name_ko, 'Free Size' as name_en, '{"type":"free"}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'ONE_SIZE', 'ONE SIZE' as name_ko, 'One Size' as name_en, '{"type":"one"}', 2
) v
WHERE a.code = 'SIZE_FREE';

-- ----------------------------------------
-- 2-6. MATERIAL (소재)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    -- 천연 섬유
    SELECT 'COTTON' as code, '면' as name_ko, 'Cotton' as name_en, '{"category":"natural"}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'LINEN', '린넨', 'Linen', '{"category":"natural"}', 2 UNION ALL
    SELECT 'SILK', '실크', 'Silk', '{"category":"natural"}', 3 UNION ALL
    SELECT 'WOOL', '울', 'Wool', '{"category":"natural"}', 4 UNION ALL
    SELECT 'CASHMERE', '캐시미어', 'Cashmere', '{"category":"natural"}', 5 UNION ALL
    SELECT 'MOHAIR', '모헤어', 'Mohair', '{"category":"natural"}', 6 UNION ALL
    SELECT 'ANGORA', '앙고라', 'Angora', '{"category":"natural"}', 7 UNION ALL
    -- 합성 섬유
    SELECT 'POLYESTER', '폴리에스터', 'Polyester', '{"category":"synthetic"}', 10 UNION ALL
    SELECT 'NYLON', '나일론', 'Nylon', '{"category":"synthetic"}', 11 UNION ALL
    SELECT 'SPANDEX', '스판덱스', 'Spandex', '{"category":"synthetic"}', 12 UNION ALL
    SELECT 'RAYON', '레이온', 'Rayon', '{"category":"synthetic"}', 13 UNION ALL
    SELECT 'VISCOSE', '비스코스', 'Viscose', '{"category":"synthetic"}', 14 UNION ALL
    SELECT 'ACRYLIC', '아크릴', 'Acrylic', '{"category":"synthetic"}', 15 UNION ALL
    SELECT 'TENCEL', '텐셀', 'Tencel', '{"category":"synthetic"}', 16 UNION ALL
    SELECT 'MODAL', '모달', 'Modal', '{"category":"synthetic"}', 17 UNION ALL
    -- 가죽/기타
    SELECT 'LEATHER', '가죽', 'Leather', '{"category":"leather"}', 20 UNION ALL
    SELECT 'GENUINE_LEATHER', '천연가죽', 'Genuine Leather', '{"category":"leather"}', 21 UNION ALL
    SELECT 'SYNTHETIC_LEATHER', '합성가죽', 'Synthetic Leather', '{"category":"leather"}', 22 UNION ALL
    SELECT 'SUEDE', '스웨이드', 'Suede', '{"category":"leather"}', 23 UNION ALL
    SELECT 'PATENT', '에나멜', 'Patent', '{"category":"leather"}', 24 UNION ALL
    SELECT 'FUR', '퍼', 'Fur', '{"category":"other"}', 25 UNION ALL
    SELECT 'FAUX_FUR', '페이크퍼', 'Faux Fur', '{"category":"other"}', 26 UNION ALL
    SELECT 'DENIM', '데님', 'Denim', '{"category":"other"}', 27 UNION ALL
    SELECT 'VELVET', '벨벳', 'Velvet', '{"category":"other"}', 28 UNION ALL
    SELECT 'SATIN', '새틴', 'Satin', '{"category":"other"}', 29 UNION ALL
    SELECT 'CANVAS', '캔버스', 'Canvas', '{"category":"other"}', 30 UNION ALL
    SELECT 'MESH', '메쉬', 'Mesh', '{"category":"other"}', 31 UNION ALL
    SELECT 'RUBBER', '고무', 'Rubber', '{"category":"other"}', 32
) v
WHERE a.code = 'MATERIAL';

-- ----------------------------------------
-- 2-7. PATTERN (패턴)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'SOLID' as code, '무지' as name_ko, 'Solid' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'STRIPE', '스트라이프', 'Stripe', NULL, 2 UNION ALL
    SELECT 'CHECK', '체크', 'Check', NULL, 3 UNION ALL
    SELECT 'PLAID', '플레이드', 'Plaid', NULL, 4 UNION ALL
    SELECT 'GINGHAM', '깅엄', 'Gingham', NULL, 5 UNION ALL
    SELECT 'DOT', '도트', 'Dot', NULL, 6 UNION ALL
    SELECT 'FLORAL', '플로럴', 'Floral', NULL, 7 UNION ALL
    SELECT 'PAISLEY', '페이즐리', 'Paisley', NULL, 8 UNION ALL
    SELECT 'ANIMAL', '애니멀', 'Animal', NULL, 9 UNION ALL
    SELECT 'LEOPARD', '레오파드', 'Leopard', NULL, 10 UNION ALL
    SELECT 'ZEBRA', '지브라', 'Zebra', NULL, 11 UNION ALL
    SELECT 'CAMO', '카모', 'Camo', NULL, 12 UNION ALL
    SELECT 'GRAPHIC', '그래픽', 'Graphic', NULL, 13 UNION ALL
    SELECT 'LOGO', '로고', 'Logo', NULL, 14 UNION ALL
    SELECT 'EMBROIDERED', '자수', 'Embroidered', NULL, 15 UNION ALL
    SELECT 'QUILTED', '퀼팅', 'Quilted', NULL, 16
) v
WHERE a.code = 'PATTERN';

-- ----------------------------------------
-- 2-8. SEASON (시즌)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'SPRING' as code, '봄' as name_ko, 'Spring' as name_en, '{"months":[3,4,5]}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'SUMMER', '여름', 'Summer', '{"months":[6,7,8]}', 2 UNION ALL
    SELECT 'FALL', '가을', 'Fall', '{"months":[9,10,11]}', 3 UNION ALL
    SELECT 'WINTER', '겨울', 'Winter', '{"months":[12,1,2]}', 4 UNION ALL
    SELECT 'ALL_SEASON', '사계절', 'All Season', '{"months":[1,2,3,4,5,6,7,8,9,10,11,12]}', 5 UNION ALL
    SELECT 'SS', 'SS (봄/여름)', 'Spring/Summer', '{"months":[3,4,5,6,7,8]}', 6 UNION ALL
    SELECT 'FW', 'FW (가을/겨울)', 'Fall/Winter', '{"months":[9,10,11,12,1,2]}', 7
) v
WHERE a.code = 'SEASON';

-- ----------------------------------------
-- 2-9. GENDER (성별)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'WOMEN' as code, '여성' as name_ko, 'Women' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'MEN', '남성', 'Men', NULL, 2 UNION ALL
    SELECT 'UNISEX', '남녀공용', 'Unisex', NULL, 3 UNION ALL
    SELECT 'KIDS_GIRL', '여아', 'Girls', NULL, 4 UNION ALL
    SELECT 'KIDS_BOY', '남아', 'Boys', NULL, 5 UNION ALL
    SELECT 'KIDS_UNISEX', '남녀아공용', 'Kids Unisex', NULL, 6
) v
WHERE a.code = 'GENDER';

-- ----------------------------------------
-- 2-10. FIT (핏)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'SLIM' as code, '슬림핏' as name_ko, 'Slim Fit' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'REGULAR', '레귤러핏', 'Regular Fit', NULL, 2 UNION ALL
    SELECT 'RELAXED', '릴렉스드핏', 'Relaxed Fit', NULL, 3 UNION ALL
    SELECT 'LOOSE', '루즈핏', 'Loose Fit', NULL, 4 UNION ALL
    SELECT 'OVERSIZED', '오버사이즈', 'Oversized', NULL, 5 UNION ALL
    SELECT 'SKINNY', '스키니', 'Skinny', NULL, 6 UNION ALL
    SELECT 'STRAIGHT', '스트레이트', 'Straight', NULL, 7 UNION ALL
    SELECT 'WIDE', '와이드', 'Wide', NULL, 8 UNION ALL
    SELECT 'BOOTCUT', '부츠컷', 'Bootcut', NULL, 9 UNION ALL
    SELECT 'FLARE', '플레어', 'Flare', NULL, 10
) v
WHERE a.code = 'FIT';

-- ----------------------------------------
-- 2-11. NECKLINE (넥라인)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'ROUND' as code, '라운드넥' as name_ko, 'Round Neck' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'V_NECK', '브이넥', 'V-Neck', NULL, 2 UNION ALL
    SELECT 'U_NECK', '유넥', 'U-Neck', NULL, 3 UNION ALL
    SELECT 'BOAT', '보트넥', 'Boat Neck', NULL, 4 UNION ALL
    SELECT 'TURTLE', '터틀넥', 'Turtle Neck', NULL, 5 UNION ALL
    SELECT 'MOCK', '목넥', 'Mock Neck', NULL, 6 UNION ALL
    SELECT 'POLO', '폴로', 'Polo', NULL, 7 UNION ALL
    SELECT 'MANDARIN', '차이나', 'Mandarin', NULL, 8 UNION ALL
    SELECT 'HOODIE', '후드', 'Hoodie', NULL, 9 UNION ALL
    SELECT 'OFF_SHOULDER', '오프숄더', 'Off Shoulder', NULL, 10 UNION ALL
    SELECT 'HALTER', '홀터', 'Halter', NULL, 11 UNION ALL
    SELECT 'SQUARE', '스퀘어', 'Square', NULL, 12
) v
WHERE a.code = 'NECKLINE';

-- ----------------------------------------
-- 2-12. SLEEVE (소매)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'SLEEVELESS' as code, '민소매' as name_ko, 'Sleeveless' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'CAP', '캡소매', 'Cap Sleeve', NULL, 2 UNION ALL
    SELECT 'SHORT', '반팔', 'Short Sleeve', NULL, 3 UNION ALL
    SELECT 'THREE_QUARTER', '7부', '3/4 Sleeve', NULL, 4 UNION ALL
    SELECT 'LONG', '긴팔', 'Long Sleeve', NULL, 5 UNION ALL
    SELECT 'RAGLAN', '래글런', 'Raglan', NULL, 6 UNION ALL
    SELECT 'PUFF', '퍼프', 'Puff', NULL, 7 UNION ALL
    SELECT 'BELL', '벨', 'Bell', NULL, 8 UNION ALL
    SELECT 'DOLMAN', '돌먼', 'Dolman', NULL, 9
) v
WHERE a.code = 'SLEEVE';

-- ----------------------------------------
-- 2-13. LENGTH (기장)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'CROP' as code, '크롭' as name_ko, 'Crop' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'REGULAR_LENGTH', '레귤러' as name_ko, 'Regular' as name_en, NULL, 2 UNION ALL
    SELECT 'LONG_LENGTH', '롱' as name_ko, 'Long' as name_en, NULL, 3 UNION ALL
    SELECT 'MAXI', '맥시', 'Maxi', NULL, 4 UNION ALL
    SELECT 'MINI', '미니', 'Mini', NULL, 5 UNION ALL
    SELECT 'MIDI', '미디', 'Midi', NULL, 6 UNION ALL
    SELECT 'KNEE', '무릎', 'Knee', NULL, 7 UNION ALL
    SELECT 'ANKLE', '앵클', 'Ankle', NULL, 8 UNION ALL
    SELECT 'FULL_LENGTH', '풀', 'Full Length', NULL, 9
) v
WHERE a.code = 'LENGTH';

-- ----------------------------------------
-- 2-14. HEEL_HEIGHT (굽 높이)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'FLAT' as code, '플랫 (0-2cm)' as name_ko, 'Flat' as name_en, '{"min_cm":0,"max_cm":2}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'LOW', '로우힐 (2-5cm)', 'Low Heel', '{"min_cm":2,"max_cm":5}', 2 UNION ALL
    SELECT 'MID', '미드힐 (5-7cm)', 'Mid Heel', '{"min_cm":5,"max_cm":7}', 3 UNION ALL
    SELECT 'HIGH', '하이힐 (7-10cm)', 'High Heel', '{"min_cm":7,"max_cm":10}', 4 UNION ALL
    SELECT 'SUPER_HIGH', '슈퍼하이 (10cm+)', 'Super High', '{"min_cm":10,"max_cm":15}', 5 UNION ALL
    SELECT 'PLATFORM', '플랫폼', 'Platform', '{"type":"platform"}', 6 UNION ALL
    SELECT 'WEDGE', '웨지', 'Wedge', '{"type":"wedge"}', 7
) v
WHERE a.code = 'HEEL_HEIGHT';

-- ----------------------------------------
-- 2-15. STRAP_TYPE (스트랩 타입) - 시계/가방용
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'LEATHER_STRAP' as code, '가죽 스트랩' as name_ko, 'Leather Strap' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'METAL_BRACELET', '메탈 브레이슬릿', 'Metal Bracelet', NULL, 2 UNION ALL
    SELECT 'RUBBER_STRAP', '러버 스트랩', 'Rubber Strap', NULL, 3 UNION ALL
    SELECT 'NATO_STRAP', '나토 스트랩', 'NATO Strap', NULL, 4 UNION ALL
    SELECT 'FABRIC_STRAP', '패브릭 스트랩', 'Fabric Strap', NULL, 5 UNION ALL
    SELECT 'CHAIN_STRAP', '체인 스트랩', 'Chain Strap', NULL, 6 UNION ALL
    SELECT 'CROSSBODY', '크로스바디', 'Crossbody', NULL, 7 UNION ALL
    SELECT 'SHOULDER', '숄더', 'Shoulder', NULL, 8 UNION ALL
    SELECT 'HANDLE', '핸들', 'Handle', NULL, 9
) v
WHERE a.code = 'STRAP_TYPE';

-- ----------------------------------------
-- 2-16. CLOSURE (클로저)
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'BUTTON' as code, '버튼' as name_ko, 'Button' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'ZIPPER', '지퍼', 'Zipper', NULL, 2 UNION ALL
    SELECT 'SNAP', '스냅', 'Snap', NULL, 3 UNION ALL
    SELECT 'HOOK', '훅', 'Hook', NULL, 4 UNION ALL
    SELECT 'VELCRO', '벨크로', 'Velcro', NULL, 5 UNION ALL
    SELECT 'TIE', '끈', 'Tie', NULL, 6 UNION ALL
    SELECT 'BUCKLE', '버클', 'Buckle', NULL, 7 UNION ALL
    SELECT 'MAGNETIC', '마그네틱', 'Magnetic', NULL, 8 UNION ALL
    SELECT 'DRAWSTRING', '드로스트링', 'Drawstring', NULL, 9 UNION ALL
    SELECT 'SLIP_ON', '슬립온', 'Slip-on', NULL, 10 UNION ALL
    SELECT 'LACE_UP', '레이스업', 'Lace-up', NULL, 11
) v
WHERE a.code = 'CLOSURE';

-- ----------------------------------------
-- 2-17. WATER_RESISTANCE (방수 등급) - 시계용
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'WR_30M' as code, '30M (생활방수)' as name_ko, '30M' as name_en, '{"atm":3,"depth_m":30}' as meta_json, 1 as sort_order UNION ALL
    SELECT 'WR_50M', '50M', '50M', '{"atm":5,"depth_m":50}', 2 UNION ALL
    SELECT 'WR_100M', '100M', '100M', '{"atm":10,"depth_m":100}', 3 UNION ALL
    SELECT 'WR_200M', '200M (스쿠버)', '200M', '{"atm":20,"depth_m":200}', 4 UNION ALL
    SELECT 'WR_300M', '300M (다이버)', '300M', '{"atm":30,"depth_m":300}', 5 UNION ALL
    SELECT 'NOT_WR', '비방수', 'Not Water Resistant', '{"atm":0,"depth_m":0}', 6
) v
WHERE a.code = 'WATER_RESISTANCE';

-- ----------------------------------------
-- 2-18. SKIN_TYPE (피부 타입) - 화장품용
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'DRY' as code, '건성' as name_ko, 'Dry' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'OILY', '지성', 'Oily', NULL, 2 UNION ALL
    SELECT 'COMBINATION', '복합성', 'Combination', NULL, 3 UNION ALL
    SELECT 'NORMAL', '중성', 'Normal', NULL, 4 UNION ALL
    SELECT 'SENSITIVE', '민감성', 'Sensitive', NULL, 5 UNION ALL
    SELECT 'ALL_SKIN', '모든 피부', 'All Skin Types', NULL, 6
) v
WHERE a.code = 'SKIN_TYPE';

-- ----------------------------------------
-- 2-19. SKIN_CONCERN (피부 고민) - 화장품용
-- ----------------------------------------
INSERT INTO attribute_value (attribute_id, code, name_ko, name_en, meta_json, sort_order, status)
SELECT a.id, v.code, v.name_ko, v.name_en, v.meta_json, v.sort_order, 'ACTIVE'
FROM attribute a
CROSS JOIN (
    SELECT 'ACNE' as code, '여드름' as name_ko, 'Acne' as name_en, NULL as meta_json, 1 as sort_order UNION ALL
    SELECT 'WRINKLE', '주름', 'Wrinkle', NULL, 2 UNION ALL
    SELECT 'PORE', '모공', 'Pore', NULL, 3 UNION ALL
    SELECT 'DARK_SPOT', '잡티', 'Dark Spot', NULL, 4 UNION ALL
    SELECT 'DULLNESS', '칙칙함', 'Dullness', NULL, 5 UNION ALL
    SELECT 'REDNESS', '홍조', 'Redness', NULL, 6 UNION ALL
    SELECT 'DRYNESS', '건조함', 'Dryness', NULL, 7 UNION ALL
    SELECT 'ELASTICITY', '탄력', 'Elasticity', NULL, 8 UNION ALL
    SELECT 'DARK_CIRCLES', '다크서클', 'Dark Circles', NULL, 9 UNION ALL
    SELECT 'BRIGHTENING', '미백', 'Brightening', NULL, 10
) v
WHERE a.code = 'SKIN_CONCERN';


-- ============================================
-- 3. category_attribute_template (카테고리별 템플릿)
-- ============================================
-- category.product_group 기준으로 매핑

INSERT INTO category_attribute_template (category_group, active) VALUES
('CLOTHING', true),
('SHOES', true),
('BAGS', true),
('ACCESSORIES', true),
('COSMETICS', true),
('JEWELRY', true),
('WATCHES', true);


-- ============================================
-- 4. category_attribute_spec (템플릿별 속성 스펙)
-- ============================================

-- ----------------------------------------
-- 4-1. CLOTHING 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SIZE_CLOTHING', 1, 1, NULL, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 5, 3 UNION ALL
    SELECT 'PATTERN', 0, 1, 1, 4 UNION ALL
    SELECT 'FIT', 0, 1, 1, 5 UNION ALL
    SELECT 'NECKLINE', 0, 1, 1, 6 UNION ALL
    SELECT 'SLEEVE', 0, 1, 1, 7 UNION ALL
    SELECT 'LENGTH', 0, 1, 1, 8 UNION ALL
    SELECT 'SEASON', 0, 1, 4, 9 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 10
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'CLOTHING';

-- ----------------------------------------
-- 4-2. SHOES 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SIZE_SHOES', 1, 1, NULL, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 3, 3 UNION ALL
    SELECT 'HEEL_HEIGHT', 0, 1, 1, 4 UNION ALL
    SELECT 'CLOSURE', 0, 1, 1, 5 UNION ALL
    SELECT 'SEASON', 0, 1, 4, 6 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 7
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'SHOES';

-- ----------------------------------------
-- 4-3. BAGS 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SIZE_FREE', 0, 1, 1, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 3, 3 UNION ALL
    SELECT 'PATTERN', 0, 1, 1, 4 UNION ALL
    SELECT 'STRAP_TYPE', 0, 1, 3, 5 UNION ALL
    SELECT 'CLOSURE', 0, 1, 1, 6 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 7
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'BAGS';

-- ----------------------------------------
-- 4-4. ACCESSORIES 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SIZE_FREE', 0, 1, 1, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 3, 3 UNION ALL
    SELECT 'PATTERN', 0, 1, 1, 4 UNION ALL
    SELECT 'SEASON', 0, 1, 4, 5 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 6
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'ACCESSORIES';

-- ----------------------------------------
-- 4-5. COSMETICS 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 0 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SKIN_TYPE', 0, 1, 6, 2 UNION ALL
    SELECT 'SKIN_CONCERN', 0, 1, 10, 3
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'COSMETICS';

-- ----------------------------------------
-- 4-6. JEWELRY 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'SIZE_RING', 0, 1, NULL, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 3, 3 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 4
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'JEWELRY';

-- ----------------------------------------
-- 4-7. WATCHES 템플릿
-- ----------------------------------------
INSERT INTO category_attribute_spec (template_id, attribute_id, required, min_selection, max_selection, sort_order)
SELECT t.id, a.id, v.required, v.min_sel, v.max_sel, v.sort_order
FROM category_attribute_template t
CROSS JOIN (
    SELECT 'COLOR' as attr_code, 1 as required, 1 as min_sel, NULL as max_sel, 1 as sort_order UNION ALL
    SELECT 'STRAP_TYPE', 0, 1, 1, 2 UNION ALL
    SELECT 'MATERIAL', 0, 1, 3, 3 UNION ALL
    SELECT 'WATER_RESISTANCE', 0, 1, 1, 4 UNION ALL
    SELECT 'GENDER', 0, 1, 1, 5
) v
JOIN attribute a ON a.code = v.attr_code
WHERE t.category_group = 'WATCHES';


-- ============================================
-- 검증 쿼리 (주석 처리)
-- ============================================

-- 속성 개수 확인
-- SELECT COUNT(*) AS attribute_count FROM attribute;

-- 속성값 개수 확인
-- SELECT a.code, a.name_ko, COUNT(av.id) AS value_count
-- FROM attribute a
-- LEFT JOIN attribute_value av ON a.id = av.attribute_id
-- GROUP BY a.id ORDER BY a.id;

-- 템플릿별 스펙 확인
-- SELECT t.category_group, COUNT(s.id) AS spec_count
-- FROM category_attribute_template t
-- LEFT JOIN category_attribute_spec s ON t.id = s.template_id
-- GROUP BY t.id;
