-- ================================================
-- V20250030: OptionGroup 시드 데이터
-- 사이즈 체계별 옵션 그룹 마스터 데이터
-- ================================================

-- ===========================================
-- 1. 신발 사이즈 체계 (SHOES)
-- ===========================================
INSERT INTO option_group (code, name_ko, name_en, type, status, description, sort_order) VALUES
-- EU 사이즈 (유럽 표준)
('SIZE_SHOES_EU', '신발 사이즈 (EU)', 'Shoe Size (EU)', 'STANDARD', 'ACTIVE', '유럽 표준 신발 사이즈 (35-48)', 1),

-- US 사이즈 (남성/여성/키즈)
('SIZE_SHOES_US_MEN', '신발 사이즈 (US 남성)', 'Shoe Size (US Men)', 'STANDARD', 'ACTIVE', '미국 남성 신발 사이즈 (6-15)', 2),
('SIZE_SHOES_US_WOMEN', '신발 사이즈 (US 여성)', 'Shoe Size (US Women)', 'STANDARD', 'ACTIVE', '미국 여성 신발 사이즈 (5-12)', 3),
('SIZE_SHOES_US_KIDS', '신발 사이즈 (US 아동)', 'Shoe Size (US Kids)', 'STANDARD', 'ACTIVE', '미국 아동 신발 사이즈 (1C-7Y)', 4),

-- UK 사이즈
('SIZE_SHOES_UK_MEN', '신발 사이즈 (UK 남성)', 'Shoe Size (UK Men)', 'STANDARD', 'ACTIVE', '영국 남성 신발 사이즈 (5-14)', 5),
('SIZE_SHOES_UK_WOMEN', '신발 사이즈 (UK 여성)', 'Shoe Size (UK Women)', 'STANDARD', 'ACTIVE', '영국 여성 신발 사이즈 (2.5-9)', 6),

-- KR 사이즈 (한국 mm)
('SIZE_SHOES_KR', '신발 사이즈 (KR)', 'Shoe Size (KR)', 'STANDARD', 'ACTIVE', '한국 신발 사이즈 mm (220-300)', 7),

-- IT 사이즈 (이탈리아)
('SIZE_SHOES_IT', '신발 사이즈 (IT)', 'Shoe Size (IT)', 'STANDARD', 'ACTIVE', '이탈리아 신발 사이즈 (34-47)', 8),

-- FR 사이즈 (프랑스)
('SIZE_SHOES_FR', '신발 사이즈 (FR)', 'Shoe Size (FR)', 'STANDARD', 'ACTIVE', '프랑스 신발 사이즈 (35-48)', 9),

-- JP 사이즈 (일본 cm)
('SIZE_SHOES_JP', '신발 사이즈 (JP)', 'Shoe Size (JP)', 'STANDARD', 'ACTIVE', '일본 신발 사이즈 cm (22.0-30.0)', 10),

-- ===========================================
-- 2. 의류 사이즈 체계 (CLOTHING)
-- ===========================================
-- International (S, M, L...)
('SIZE_CLOTHING_INT', '의류 사이즈 (국제)', 'Clothing Size (International)', 'STANDARD', 'ACTIVE', '국제 표준 의류 사이즈 (XXS-4XL)', 11),

-- KR 사이즈 (한국)
('SIZE_CLOTHING_KR_TOP', '의류 사이즈 (한국 상의)', 'Clothing Size (KR Top)', 'STANDARD', 'ACTIVE', '한국 상의 사이즈 (44-120)', 12),
('SIZE_CLOTHING_KR_BOTTOM', '의류 사이즈 (한국 하의)', 'Clothing Size (KR Bottom)', 'STANDARD', 'ACTIVE', '한국 하의 사이즈 (25-40)', 13),

-- EU 사이즈 (유럽)
('SIZE_CLOTHING_EU', '의류 사이즈 (EU)', 'Clothing Size (EU)', 'STANDARD', 'ACTIVE', '유럽 표준 의류 사이즈 (32-56)', 14),

-- US 사이즈 (미국)
('SIZE_CLOTHING_US', '의류 사이즈 (US)', 'Clothing Size (US)', 'STANDARD', 'ACTIVE', '미국 의류 사이즈 (0-20)', 15),

-- UK 사이즈 (영국)
('SIZE_CLOTHING_UK', '의류 사이즈 (UK)', 'Clothing Size (UK)', 'STANDARD', 'ACTIVE', '영국 의류 사이즈 (4-24)', 16),

-- IT 사이즈 (이탈리아)
('SIZE_CLOTHING_IT', '의류 사이즈 (IT)', 'Clothing Size (IT)', 'STANDARD', 'ACTIVE', '이탈리아 의류 사이즈 (36-54)', 17),

-- FR 사이즈 (프랑스)
('SIZE_CLOTHING_FR', '의류 사이즈 (FR)', 'Clothing Size (FR)', 'STANDARD', 'ACTIVE', '프랑스 의류 사이즈 (32-52)', 18),

-- JP 사이즈 (일본)
('SIZE_CLOTHING_JP', '의류 사이즈 (JP)', 'Clothing Size (JP)', 'STANDARD', 'ACTIVE', '일본 의류 사이즈 (5-23호)', 19),

-- ===========================================
-- 3. 데님/진 사이즈 (인치)
-- ===========================================
('SIZE_DENIM_WAIST', '데님 허리 사이즈', 'Denim Waist Size', 'STANDARD', 'ACTIVE', '데님 허리둘레 인치 (24-42)', 20),
('SIZE_DENIM_LENGTH', '데님 기장 사이즈', 'Denim Inseam Length', 'STANDARD', 'ACTIVE', '데님 안쪽 기장 인치 (28-36)', 21),

-- ===========================================
-- 4. 악세서리 사이즈 (반지, 벨트 등)
-- ===========================================
-- 반지 사이즈
('SIZE_RING_KR', '반지 사이즈 (한국)', 'Ring Size (KR)', 'STANDARD', 'ACTIVE', '한국 반지 사이즈 (1-30호)', 22),
('SIZE_RING_US', '반지 사이즈 (US)', 'Ring Size (US)', 'STANDARD', 'ACTIVE', '미국 반지 사이즈 (3-15)', 23),
('SIZE_RING_UK', '반지 사이즈 (UK)', 'Ring Size (UK)', 'STANDARD', 'ACTIVE', '영국 반지 사이즈 (F-Z)', 24),
('SIZE_RING_EU', '반지 사이즈 (EU)', 'Ring Size (EU)', 'STANDARD', 'ACTIVE', '유럽 반지 사이즈 (44-70)', 25),
('SIZE_RING_JP', '반지 사이즈 (JP)', 'Ring Size (JP)', 'STANDARD', 'ACTIVE', '일본 반지 사이즈 (1-30호)', 26),

-- 벨트 사이즈
('SIZE_BELT_CM', '벨트 사이즈 (cm)', 'Belt Size (cm)', 'STANDARD', 'ACTIVE', '벨트 길이 cm (70-130)', 27),
('SIZE_BELT_INCH', '벨트 사이즈 (inch)', 'Belt Size (inch)', 'STANDARD', 'ACTIVE', '벨트 길이 인치 (28-54)', 28),

-- ===========================================
-- 5. 기타 옵션 그룹 (색상, 소재 등)
-- ===========================================
('COLOR', '색상', 'Color', 'STANDARD', 'ACTIVE', '제품 색상', 30),
('MATERIAL', '소재', 'Material', 'STANDARD', 'ACTIVE', '제품 소재/원단', 31),
('PATTERN', '패턴', 'Pattern', 'STANDARD', 'ACTIVE', '제품 패턴/무늬', 32),

-- ===========================================
-- 6. 가전/가구 사이즈 (예시)
-- ===========================================
('SIZE_TV_INCH', 'TV 화면 크기', 'TV Screen Size', 'STANDARD', 'ACTIVE', 'TV 대각선 화면 크기 인치 (32-98)', 40),
('SIZE_REFRIGERATOR_LITER', '냉장고 용량', 'Refrigerator Capacity', 'STANDARD', 'ACTIVE', '냉장고 용량 리터 (200-900L)', 41),
('SIZE_WASHER_KG', '세탁기 용량', 'Washer Capacity', 'STANDARD', 'ACTIVE', '세탁기 용량 kg (7-24kg)', 42),
('SIZE_MATTRESS', '매트리스 사이즈', 'Mattress Size', 'STANDARD', 'ACTIVE', '매트리스 규격 (SS, S, Q, K)', 43);
