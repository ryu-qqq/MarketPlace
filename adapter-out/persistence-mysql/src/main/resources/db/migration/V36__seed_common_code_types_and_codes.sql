-- ============================================
-- 공통 코드 타입 및 공통 코드 시드 데이터
-- ============================================
-- 도메인 VO 이넘 기반 프론트엔드 드롭다운/필터용

-- ============================================
-- 1. common_code_types
-- ============================================

INSERT INTO common_code_types (id, code, name, description, display_order, is_active) VALUES
(1, 'APPLICATION_STATUS', '입점신청 상태', '입점 신청 상태', 1, 1),
(2, 'SELLER_ADMIN_STATUS', '셀러 관리자 상태', '셀러 관리자 계정 활성화 상태', 2, 1),
(3, 'ADDRESS_TYPE', '주소 유형', '출고지/반품지 등', 3, 1),
(4, 'SETTLEMENT_CYCLE', '정산 주기', '주간/격주/월간', 4, 1),
(5, 'CONTRACT_STATUS', '계약 상태', '셀러 계약 상태', 5, 1),
(6, 'SHIPPING_FEE_TYPE', '배송비 유형', '무료/유료/조건부 등', 6, 1),
(7, 'NON_RETURNABLE_CONDITION', '반품 불가 조건', '고객 변심 등 반품 불가 사유', 7, 1),
(8, 'DEPARTMENT', '상품 부문', '패션/뷰티/리빙 등', 8, 1),
(9, 'CATEGORY_GROUP', '카테고리 그룹', '고시정보 연동 카테고리 그룹', 9, 1),
(10, 'GENERIC_STATUS', '일반 상태', 'ACTIVE/INACTIVE 공통', 10, 1),
(11, 'OUTBOX_STATUS', 'Outbox 처리 상태', '대기/처리중/완료/실패', 11, 1);

-- ============================================
-- 2. common_codes
-- ============================================

-- APPLICATION_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(1, 'PENDING', '대기 중', 1, 1),
(1, 'APPROVED', '승인됨', 2, 1),
(1, 'REJECTED', '거절됨', 3, 1);

-- SELLER_ADMIN_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(2, 'PENDING_APPROVAL', '승인대기', 1, 1),
(2, 'ACTIVE', '활성', 2, 1),
(2, 'INACTIVE', '비활성', 3, 1),
(2, 'SUSPENDED', '정지', 4, 1),
(2, 'REJECTED', '거절', 5, 1);

-- ADDRESS_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(3, 'SHIPPING', '출고지', 1, 1),
(3, 'RETURN', '반품지', 2, 1);

-- SETTLEMENT_CYCLE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(4, 'WEEKLY', '주간 정산', 1, 1),
(4, 'BIWEEKLY', '격주 정산', 2, 1),
(4, 'MONTHLY', '월간 정산', 3, 1);

-- CONTRACT_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(5, 'ACTIVE', '활성', 1, 1),
(5, 'EXPIRED', '만료됨', 2, 1),
(5, 'TERMINATED', '해지됨', 3, 1);

-- SHIPPING_FEE_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(6, 'FREE', '무료배송', 1, 1),
(6, 'PAID', '유료배송', 2, 1),
(6, 'CONDITIONAL_FREE', '조건부 무료배송', 3, 1),
(6, 'QUANTITY_BASED', '수량별 배송비', 4, 1);

-- NON_RETURNABLE_CONDITION
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(7, 'OPENED_PACKAGING', '포장 개봉', 1, 1),
(7, 'USED_PRODUCT', '사용 흔적', 2, 1),
(7, 'TIME_EXPIRED', '시간 경과', 3, 1),
(7, 'DIGITAL_CONTENT', '디지털 콘텐츠', 4, 1),
(7, 'CUSTOM_MADE', '주문 제작', 5, 1),
(7, 'HYGIENE_PRODUCT', '위생 상품', 6, 1),
(7, 'PARTIAL_SET', '세트 일부', 7, 1),
(7, 'MISSING_TAG', '택/라벨 제거', 8, 1),
(7, 'DAMAGED_BY_CUSTOMER', '고객 과실 파손', 9, 1);

-- DEPARTMENT
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(8, 'FASHION', '패션', 1, 1),
(8, 'BEAUTY', '뷰티', 2, 1),
(8, 'LIVING', '리빙', 3, 1),
(8, 'FOOD', '식품', 4, 1),
(8, 'DIGITAL', '디지털', 5, 1),
(8, 'SPORTS', '스포츠', 6, 1),
(8, 'KIDS', '키즈', 7, 1),
(8, 'PET', '펫', 8, 1),
(8, 'CULTURE', '컬처', 9, 1),
(8, 'HEALTH', '헬스', 10, 1),
(8, 'ETC', '기타', 11, 1);

-- CATEGORY_GROUP (notice_category와 동일 순서)
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(9, 'CLOTHING', '의류', 1, 1),
(9, 'SHOES', '구두/신발', 2, 1),
(9, 'BAGS', '가방', 3, 1),
(9, 'ACCESSORIES', '패션잡화', 4, 1),
(9, 'COSMETICS', '화장품', 5, 1),
(9, 'JEWELRY', '귀금속/보석', 6, 1),
(9, 'WATCHES', '시계', 7, 1),
(9, 'FURNITURE', '가구', 8, 1),
(9, 'BABY_KIDS', '영유아용품', 9, 1),
(9, 'SPORTS', '스포츠용품', 10, 1),
(9, 'DIGITAL', '디지털/가전', 11, 1),
(9, 'ETC', '기타 재화', 12, 1);

-- GENERIC_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(10, 'ACTIVE', '활성', 1, 1),
(10, 'INACTIVE', '비활성', 2, 1);

-- OUTBOX_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(11, 'PENDING', '대기', 1, 1),
(11, 'PROCESSING', '처리중', 2, 1),
(11, 'COMPLETED', '완료', 3, 1),
(11, 'FAILED', '실패', 4, 1);
