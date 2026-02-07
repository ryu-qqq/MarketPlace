-- ============================================
-- 고시정보 카테고리 및 필드 데이터
-- ============================================
-- 기준: 전자상거래 등에서의 상품 등의 정보제공에 관한 고시
-- 공정거래위원회 고시 제2023-18호
-- ============================================

-- ============================================
-- 1. 카테고리 product_group 업데이트
-- ============================================

-- 뷰티 → COSMETICS
UPDATE category
SET product_group = 'COSMETICS', updated_at = NOW()
WHERE path LIKE '3%' AND product_group != 'COSMETICS';

-- 시계 → WATCHES
UPDATE category
SET product_group = 'WATCHES', updated_at = NOW()
WHERE path LIKE '2/23%' AND product_group != 'WATCHES';

-- 주얼리 → JEWELRY
UPDATE category
SET product_group = 'JEWELRY', updated_at = NOW()
WHERE path LIKE '2/24%' AND product_group != 'JEWELRY';

-- ============================================
-- 2. 고시정보 카테고리 (notice_category)
-- ============================================

INSERT INTO notice_category (id, code, name_ko, name_en, target_category_group, active, version) VALUES
(1, 'CLOTHING', '의류', 'Clothing', 'CLOTHING', 1, 0),
(2, 'SHOES', '구두/신발', 'Shoes', 'SHOES', 1, 0),
(3, 'BAGS', '가방', 'Bags', 'BAGS', 1, 0),
(4, 'ACCESSORIES', '패션잡화', 'Fashion Accessories', 'ACCESSORIES', 1, 0),
(5, 'COSMETICS', '화장품', 'Cosmetics', 'COSMETICS', 1, 0),
(6, 'JEWELRY', '귀금속/보석', 'Jewelry', 'JEWELRY', 1, 0),
(7, 'WATCHES', '시계', 'Watches', 'WATCHES', 1, 0),
(8, 'FURNITURE', '가구', 'Furniture', 'FURNITURE', 1, 0),
(9, 'BABY_KIDS', '영유아용품', 'Baby/Kids Products', 'BABY_KIDS', 1, 0),
(10, 'SPORTS', '스포츠용품', 'Sports Equipment', 'SPORTS', 1, 0),
(11, 'DIGITAL', '디지털/가전', 'Digital/Electronics', 'DIGITAL', 1, 0),
(12, 'ETC', '기타 재화', 'Other Products', 'ETC', 1, 0);

-- ============================================
-- 3. 고시정보 필드 (notice_field)
-- ============================================

-- ----------------------------------------
-- 3-1. 의류 (CLOTHING) - 8개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(1, 'material', '제품 소재 (섬유의 조성 또는 혼용률)', 'TEXT', 1, 1),
(1, 'color', '색상', 'TEXT', 1, 2),
(1, 'size', '치수', 'TEXT', 1, 3),
(1, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 4),
(1, 'made_in', '제조국', 'TEXT', 1, 5),
(1, 'wash_care', '세탁방법 및 취급시 주의사항', 'TEXT', 1, 6),
(1, 'release_date', '제조년월', 'TEXT', 0, 7),
(1, 'quality_assurance', '품질보증기준', 'TEXT', 1, 8);

-- ----------------------------------------
-- 3-2. 구두/신발 (SHOES) - 7개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(2, 'material_upper', '제품 주소재 (갑피/겉감)', 'TEXT', 1, 1),
(2, 'material_sole', '제품 주소재 (밑창)', 'TEXT', 1, 2),
(2, 'color', '색상', 'TEXT', 1, 3),
(2, 'size', '치수 (굽 높이 포함)', 'TEXT', 1, 4),
(2, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 5),
(2, 'made_in', '제조국', 'TEXT', 1, 6),
(2, 'quality_assurance', '품질보증기준', 'TEXT', 1, 7);

-- ----------------------------------------
-- 3-3. 가방 (BAGS) - 7개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(3, 'type', '종류', 'TEXT', 1, 1),
(3, 'material', '소재', 'TEXT', 1, 2),
(3, 'color', '색상', 'TEXT', 1, 3),
(3, 'size', '크기', 'TEXT', 1, 4),
(3, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 5),
(3, 'made_in', '제조국', 'TEXT', 1, 6),
(3, 'quality_assurance', '품질보증기준', 'TEXT', 1, 7);

-- ----------------------------------------
-- 3-4. 패션잡화 (ACCESSORIES) - 7개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(4, 'type', '종류', 'TEXT', 1, 1),
(4, 'material', '소재', 'TEXT', 1, 2),
(4, 'size', '치수', 'TEXT', 1, 3),
(4, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 4),
(4, 'made_in', '제조국', 'TEXT', 1, 5),
(4, 'care_instructions', '취급시 주의사항', 'TEXT', 0, 6),
(4, 'quality_assurance', '품질보증기준', 'TEXT', 1, 7);

-- ----------------------------------------
-- 3-5. 화장품 (COSMETICS) - 11개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(5, 'capacity', '용량 또는 중량', 'TEXT', 1, 1),
(5, 'product_type', '제품 주요 사양 (피부타입, 색상 등)', 'TEXT', 1, 2),
(5, 'usage', '사용기한 또는 개봉 후 사용기간', 'TEXT', 1, 3),
(5, 'how_to_use', '사용방법', 'TEXT', 1, 4),
(5, 'manufacturer', '화장품제조업자/책임판매업자', 'TEXT', 1, 5),
(5, 'made_in', '제조국', 'TEXT', 1, 6),
(5, 'ingredients', '화장품법에 따라 기재·표시하여야 하는 모든 성분', 'TEXT', 1, 7),
(5, 'functional_cosmetic', '기능성 화장품 심사 필 유무', 'TEXT', 0, 8),
(5, 'caution', '사용할 때의 주의사항', 'TEXT', 1, 9),
(5, 'quality_assurance', '품질보증기준', 'TEXT', 1, 10),
(5, 'cs_info', '소비자상담 관련 전화번호', 'TEXT', 1, 11);

-- ----------------------------------------
-- 3-6. 귀금속/보석 (JEWELRY) - 9개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(6, 'material', '소재/순도/밴드재질', 'TEXT', 1, 1),
(6, 'weight', '중량', 'TEXT', 1, 2),
(6, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 3),
(6, 'made_in', '제조국', 'TEXT', 1, 4),
(6, 'size', '치수', 'TEXT', 0, 5),
(6, 'gemstone_info', '착용 시 주의사항 (금속 알레르기 등)', 'TEXT', 1, 6),
(6, 'certification', '귀금속, 보석류 - Loss등 인증 여부', 'TEXT', 0, 7),
(6, 'quality_assurance', '품질보증기준', 'TEXT', 1, 8),
(6, 'cs_info', 'A/S 책임자와 전화번호', 'TEXT', 1, 9);

-- ----------------------------------------
-- 3-7. 시계 (WATCHES) - 9개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(7, 'type', '종류 (기계식/석영식/전자식 등)', 'TEXT', 1, 1),
(7, 'movement', '무브먼트 정보', 'TEXT', 0, 2),
(7, 'case_material', '케이스 소재', 'TEXT', 1, 3),
(7, 'band_material', '밴드 소재', 'TEXT', 1, 4),
(7, 'size', '치수 (케이스 지름, 두께)', 'TEXT', 1, 5),
(7, 'water_resistance', '방수 등급', 'TEXT', 0, 6),
(7, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 7),
(7, 'made_in', '제조국', 'TEXT', 1, 8),
(7, 'quality_assurance', '품질보증기준 및 A/S 정보', 'TEXT', 1, 9);

-- ----------------------------------------
-- 3-8. 가구 (FURNITURE) - 8개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(8, 'product_name', '품목 및 품명', 'TEXT', 1, 1),
(8, 'certification', 'KC 인증 필 유무', 'TEXT', 1, 2),
(8, 'color', '색상', 'TEXT', 1, 3),
(8, 'material', '구성재질 (천연/인조가죽, 합성수지 등)', 'TEXT', 1, 4),
(8, 'size', '치수 (가로x세로x높이)', 'TEXT', 1, 5),
(8, 'delivery', '배송/설치비용', 'TEXT', 1, 6),
(8, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 7),
(8, 'quality_assurance', '품질보증기준', 'TEXT', 1, 8);

-- ----------------------------------------
-- 3-9. 영유아용품 (BABY_KIDS) - 10개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(9, 'product_name', '품목 및 품명', 'TEXT', 1, 1),
(9, 'model', '모델명', 'TEXT', 1, 2),
(9, 'certification', 'KC 인증 필 유무 (안전기준 대상 여부)', 'TEXT', 1, 3),
(9, 'size', '크기/치수', 'TEXT', 1, 4),
(9, 'weight', '중량', 'TEXT', 0, 5),
(9, 'color', '색상', 'TEXT', 1, 6),
(9, 'material', '재질', 'TEXT', 1, 7),
(9, 'age_range', '사용연령/체중 범위', 'TEXT', 1, 8),
(9, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 9),
(9, 'caution', '취급방법 및 주의사항', 'TEXT', 1, 10);

-- ----------------------------------------
-- 3-10. 스포츠용품 (SPORTS) - 7개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(10, 'product_name', '품명 및 모델명', 'TEXT', 1, 1),
(10, 'size', '크기/치수', 'TEXT', 1, 2),
(10, 'weight', '중량', 'TEXT', 0, 3),
(10, 'material', '재질', 'TEXT', 1, 4),
(10, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 5),
(10, 'made_in', '제조국', 'TEXT', 1, 6),
(10, 'quality_assurance', '품질보증기준', 'TEXT', 1, 7);

-- ----------------------------------------
-- 3-11. 디지털/가전 (DIGITAL) - 10개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(11, 'product_name', '품명 및 모델명', 'TEXT', 1, 1),
(11, 'certification', 'KC 인증 필 유무 (전파인증 포함)', 'TEXT', 1, 2),
(11, 'rated_voltage', '정격전압/소비전력', 'TEXT', 1, 3),
(11, 'energy_rating', '에너지소비효율등급', 'TEXT', 0, 4),
(11, 'release_date', '동일모델의 출시년월', 'TEXT', 1, 5),
(11, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 6),
(11, 'made_in', '제조국', 'TEXT', 1, 7),
(11, 'size', '크기', 'TEXT', 1, 8),
(11, 'weight', '무게', 'TEXT', 0, 9),
(11, 'quality_assurance', '품질보증기준 및 A/S 정보', 'TEXT', 1, 10);

-- ----------------------------------------
-- 3-12. 기타 재화 (ETC) - 6개 필드
-- ----------------------------------------
INSERT INTO notice_field (notice_category_id, field_code, field_name, field_type, required, sort_order) VALUES
(12, 'product_name', '품명 및 모델명', 'TEXT', 1, 1),
(12, 'certification', '법에 의한 인증·허가 등을 받았음을 확인할 수 있는 경우 그에 대한 사항', 'TEXT', 0, 2),
(12, 'manufacturer', '제조자/수입품의 경우 수입자', 'TEXT', 1, 3),
(12, 'made_in', '제조국', 'TEXT', 0, 4),
(12, 'quality_assurance', '품질보증기준', 'TEXT', 1, 5),
(12, 'cs_info', 'A/S 책임자와 전화번호', 'TEXT', 1, 6);

-- ============================================
-- 검증 쿼리 (주석 처리)
-- ============================================
-- SELECT nc.name_ko, COUNT(nf.id) as field_count
-- FROM notice_category nc
-- LEFT JOIN notice_field nf ON nc.id = nf.notice_category_id
-- GROUP BY nc.id, nc.name_ko
-- ORDER BY nc.id;
