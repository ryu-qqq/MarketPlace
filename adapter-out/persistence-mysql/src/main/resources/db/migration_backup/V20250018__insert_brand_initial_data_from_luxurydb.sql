-- Brand 초기 데이터 마이그레이션
-- Source: luxurydb.brand → Target: market.brand
-- 중복 제거: 대소문자 무시 영문명 기준, 명시적 중복 ID 제외
-- 제외 ID: 3910(AloYoga-중복), 5460(HOUSE OF SUNNY-중복), 5405(welldone-중복)

-- ============================================================
-- Brand 마스터 데이터 삽입
-- ============================================================

INSERT INTO brand (
    code,
    canonical_name,
    name_ko,
    name_en,
    short_name,
    country,
    department,
    is_luxury,
    status,
    official_website,
    logo_url,
    description,
    data_quality_level,
    data_quality_score,
    version,
    created_at,
    updated_at
)
SELECT
    -- code: BRAND_ID 기반 (유니크 보장)
    CONCAT('brand-', src.BRAND_ID) AS code,
    -- canonical_name: 영문명 + 한글명 조합 (대소문자 충돌 방지)
    CONCAT(src.DISPLAY_ENGLISH_NAME, ' (', src.DISPLAY_KOREAN_NAME, ')') AS canonical_name,
    -- name_ko: 한글명
    src.DISPLAY_KOREAN_NAME AS name_ko,
    -- name_en: 영문명
    src.DISPLAY_ENGLISH_NAME AS name_en,
    -- short_name: NULL (추후 설정)
    NULL AS short_name,
    -- country: NULL (luxurydb에 없음)
    NULL AS country,
    -- department: FASHION (기본값)
    'FASHION' AS department,
    -- is_luxury: 0 (추후 분류 필요)
    0 AS is_luxury,
    -- status: ACTIVE
    'ACTIVE' AS status,
    -- official_website: NULL
    NULL AS official_website,
    -- logo_url: 아이콘 이미지
    CASE
        WHEN src.BRAND_ICON_IMAGE_URL != '' THEN src.BRAND_ICON_IMAGE_URL
        ELSE NULL
    END AS logo_url,
    -- description: NULL
    NULL AS description,
    -- data_quality_level: LEGACY (레거시 마이그레이션)
    'LEGACY' AS data_quality_level,
    -- data_quality_score: 50 (기본 품질)
    50 AS data_quality_score,
    -- version: 0
    0 AS version,
    -- created_at: 원본 INSERT_DATE
    COALESCE(src.INSERT_DATE, NOW()) AS created_at,
    -- updated_at: 원본 UPDATE_DATE
    COALESCE(src.UPDATE_DATE, NOW()) AS updated_at
FROM luxurydb.brand src
WHERE src.DELETE_YN = 'N'
  AND src.DISPLAY_YN = 'Y'
  -- 명시적 중복 제외
  AND src.BRAND_ID NOT IN (
      3910,  -- AloYoga (Alo Yoga와 중복)
      5460,  -- HOUSE OF SUNNY (House of Sunny와 중복)
      5405   -- welldone (WE11DONE과 중복)
  )
ORDER BY src.BRAND_ID;

-- ============================================================
-- Brand Alias 자동 생성 (한글명 → 영문 브랜드 매핑)
-- ============================================================

INSERT INTO brand_alias (
    brand_id,
    alias_name,
    normalized_alias,
    source_type,
    seller_id,
    mall_code,
    confidence,
    status,
    created_at,
    updated_at
)
SELECT
    b.id AS brand_id,
    src.DISPLAY_KOREAN_NAME AS alias_name,
    -- normalized_alias: 공백 제거 + 소문자
    LOWER(REPLACE(src.DISPLAY_KOREAN_NAME, ' ', '')) AS normalized_alias,
    'MIGRATION' AS source_type,
    0 AS seller_id,
    'GLOBAL' AS mall_code,
    1.0000 AS confidence,
    'CONFIRMED' AS status,
    NOW() AS created_at,
    NOW() AS updated_at
FROM luxurydb.brand src
INNER JOIN brand b ON b.code = CONCAT('brand-', src.BRAND_ID)
WHERE src.DELETE_YN = 'N'
  AND src.DISPLAY_YN = 'Y'
  AND src.BRAND_ID NOT IN (3910, 5460, 5405)
  AND src.DISPLAY_KOREAN_NAME IS NOT NULL
  AND src.DISPLAY_KOREAN_NAME != '';

-- ============================================================
-- Brand Alias 추가: 영문명 변형 (공백 제거 버전)
-- 예: "Alo Yoga" → "aloyoga" 별칭 추가
-- ============================================================

INSERT INTO brand_alias (
    brand_id,
    alias_name,
    normalized_alias,
    source_type,
    seller_id,
    mall_code,
    confidence,
    status,
    created_at,
    updated_at
)
SELECT
    b.id AS brand_id,
    REPLACE(b.name_en, ' ', '') AS alias_name,
    LOWER(REPLACE(b.name_en, ' ', '')) AS normalized_alias,
    'MIGRATION' AS source_type,
    0 AS seller_id,
    'GLOBAL' AS mall_code,
    1.0000 AS confidence,
    'CONFIRMED' AS status,
    NOW() AS created_at,
    NOW() AS updated_at
FROM brand b
WHERE b.name_en LIKE '% %'  -- 공백이 있는 영문명만
  AND NOT EXISTS (
      SELECT 1 FROM brand_alias ba
      WHERE ba.brand_id = b.id
        AND ba.normalized_alias = LOWER(REPLACE(b.name_en, ' ', ''))
  );

-- ============================================================
-- 데이터 품질 수정: BABOR 한글명 오류 수정
-- BABOR는 독일 화장품 브랜드 (바보르가 정확)
-- ============================================================

UPDATE brand
SET name_ko = '바보르',
    updated_at = NOW()
WHERE canonical_name = 'BABOR';

-- ============================================================
-- 마이그레이션 검증 쿼리 (주석 처리)
-- ============================================================

-- SELECT COUNT(*) AS total_brands FROM brand;
-- SELECT COUNT(*) AS total_aliases FROM brand_alias;
-- SELECT b.canonical_name, COUNT(ba.id) AS alias_count
-- FROM brand b LEFT JOIN brand_alias ba ON b.id = ba.brand_id
-- GROUP BY b.id ORDER BY alias_count DESC LIMIT 10;
