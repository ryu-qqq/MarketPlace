-- ============================================
-- common_codes (common_code_type_id, code) 유니크 제약 추가
-- ============================================
-- 도메인 불변식: commonCodeTypeId와 code 조합은 유일해야 함

ALTER TABLE common_codes
    ADD UNIQUE KEY uk_common_codes_type_code (common_code_type_id, code);
