-- OptionType.FREE_INPUT 제거: 기존 FREE_INPUT → NONE 일괄 변환
-- 이 마이그레이션은 product_group.option_type 컬럼만 대상으로 합니다.
-- seller_option_groups.input_type의 FREE_INPUT은 유지됩니다.

-- 1. 영향받는 행 수 확인 (Flyway 실행 로그에 기록)
SELECT COUNT(*) AS affected_rows FROM product_group WHERE option_type = 'FREE_INPUT';

-- 2. 마이그레이션 실행
UPDATE product_group SET option_type = 'NONE' WHERE option_type = 'FREE_INPUT';

-- Rollback SQL (수동 실행 시 사용):
-- UPDATE product_group SET option_type = 'FREE_INPUT' WHERE option_type = 'NONE' AND id IN (...);
