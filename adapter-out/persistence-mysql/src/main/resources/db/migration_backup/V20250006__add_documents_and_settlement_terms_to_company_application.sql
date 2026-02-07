-- ============================================
-- Add Documents and Settlement Terms to Company Application
-- ============================================
-- Wave 1.5: 입점 신청 확장
-- - 첨부 서류 이미지 URL 3개 추가
-- - 정산 조건 5개 컬럼 추가
-- ============================================

-- 첨부 서류 이미지 URL
ALTER TABLE company_application
    ADD COLUMN business_license_image_url VARCHAR(500) NULL COMMENT '사업자등록증 이미지 URL' AFTER applicant_phone,
    ADD COLUMN ecommerce_registration_image_url VARCHAR(500) NULL COMMENT '통신판매업 신고증 이미지 URL' AFTER business_license_image_url,
    ADD COLUMN bank_account_image_url VARCHAR(500) NULL COMMENT '통장사본 이미지 URL' AFTER ecommerce_registration_image_url;

-- 정산 조건 (승인 시 설정)
ALTER TABLE company_application
    ADD COLUMN settlement_day INT NULL COMMENT '정산일 (1-31)' AFTER token_expires_at,
    ADD COLUMN commission_rate DECIMAL(5,2) NULL COMMENT '수수료율 (%)' AFTER settlement_day,
    ADD COLUMN bank_code VARCHAR(10) NULL COMMENT '은행코드' AFTER commission_rate,
    ADD COLUMN account_number VARCHAR(30) NULL COMMENT '계좌번호' AFTER bank_code,
    ADD COLUMN account_holder_name VARCHAR(50) NULL COMMENT '예금주명' AFTER account_number;

-- 상태 컬럼 COMMENT 업데이트 (EXPIRED 추가)
ALTER TABLE company_application
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '상태 (PENDING, APPROVED, REJECTED, COMPLETED, EXPIRED)';
