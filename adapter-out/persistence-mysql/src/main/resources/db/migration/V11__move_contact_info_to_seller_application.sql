-- ============================================
-- ContactInfo를 seller_addresses에서 seller_applications로 이동
-- ============================================

-- seller_applications 테이블에 담당자 연락처 컬럼 추가
ALTER TABLE seller_applications ADD COLUMN contact_name VARCHAR(50) NULL COMMENT '담당자명' AFTER cs_email;
ALTER TABLE seller_applications ADD COLUMN contact_phone VARCHAR(20) NULL COMMENT '담당자 연락처' AFTER contact_name;
ALTER TABLE seller_applications ADD COLUMN contact_email VARCHAR(100) NULL COMMENT '담당자 이메일' AFTER contact_phone;

-- seller_addresses 테이블에서 담당자 연락처 컬럼 삭제
ALTER TABLE seller_addresses DROP COLUMN contact_name;
ALTER TABLE seller_addresses DROP COLUMN contact_phone;
