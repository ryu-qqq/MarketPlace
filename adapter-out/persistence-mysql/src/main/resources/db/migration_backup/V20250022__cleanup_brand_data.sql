-- ============================================
-- Brand 데이터 정리 마이그레이션
-- ============================================
-- 목적: 신뢰성 없는 자동 생성 데이터 정리
-- 변경사항:
--   1. brand_alias 전체 삭제 (자동 생성, 검증 안됨)
--   2. 중복 브랜드 삭제 (TINY COTTONS)
--   3. department를 UNKNOWN으로 변경 (미분류 상태 명시)
--   4. data_quality_score를 30으로 하향 (LEGACY 품질)
-- ============================================

-- 1. 브랜드 별칭 전체 삭제
-- 이유: 자동 생성된 데이터로 confidence 1.0 일괄 설정, 충돌 케이스 존재
DELETE FROM brand_alias;

-- 2. 중복 브랜드 삭제
-- TINY COTTONS는 TINYCOTTONS와 동일 브랜드 (공백만 다름)
DELETE FROM brand WHERE name_en = 'TINY COTTONS';

-- 3. department를 UNKNOWN으로 변경
-- 이유: 모든 브랜드가 FASHION으로 일괄 설정됨 (뷰티, 리빙 브랜드도 포함)
-- UNKNOWN으로 변경하여 미분류 상태 명시
UPDATE brand
SET department = 'UNKNOWN',
    updated_at = NOW()
WHERE department = 'FASHION' OR department = '' OR department IS NULL;

-- 4. data_quality_score를 30으로 하향
-- 이유: LEGACY 데이터는 검증되지 않은 마이그레이션 데이터
-- 기존 50 → 30으로 하향하여 낮은 신뢰도 표시
UPDATE brand
SET data_quality_score = 30,
    updated_at = NOW()
WHERE data_quality_level = 'LEGACY';

-- ============================================
-- 정리 후 상태
-- ============================================
-- brand: 1,612개 (1개 중복 삭제)
-- brand_alias: 0개 (전체 삭제)
-- department: 모두 UNKNOWN
-- data_quality_score: 모두 30
-- ============================================
