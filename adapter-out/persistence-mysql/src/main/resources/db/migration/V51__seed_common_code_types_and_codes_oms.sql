-- ============================================
-- OMS 도메인 공통 코드 타입 및 공통 코드 시드 데이터
-- ============================================
-- Order, Cancel, Refund, Exchange, Settlement, Claim, Shipment 도메인 enum 기반
-- 프론트엔드 드롭다운/필터용

-- ============================================
-- 1. common_code_types (id: 12 ~ 33)
-- ============================================

INSERT INTO common_code_types (id, code, name, description, display_order, is_active) VALUES
-- Order
(12, 'ORDER_STATUS',          '주문 상태',       '주문 처리 상태',                    12, 1),
(13, 'PAYMENT_STATUS',        '결제 상태',       '주문 결제 상태',                    13, 1),
(14, 'PAYMENT_METHOD_TYPE',   '결제 수단',       '결제 방법 유형',                    14, 1),
(15, 'SITE_NAME',             '판매 채널',       '주문 유입 채널',                    15, 1),
(16, 'DELIVERY_STATUS',       '배송 상태',       '배송 추적 상태',                    16, 1),
(17, 'FULFILLMENT_TYPE',      '클레임 유형',     '취소/반품/교환 유형',               17, 1),
-- Cancel
(18, 'CANCEL_STATUS',         '취소 상태',       '취소 요청 처리 상태',               18, 1),
(19, 'CANCEL_TYPE',           '취소 유형',       '구매자/판매자 취소 구분',           19, 1),
(20, 'CANCEL_REASON_TYPE',    '취소 사유',       '취소 요청 사유 유형',               20, 1),
-- Refund
(21, 'REFUND_STATUS',         '반품 상태',       '반품 요청 처리 상태',               21, 1),
(22, 'REFUND_REASON_TYPE',    '반품 사유',       '반품 요청 사유 유형',               22, 1),
-- Exchange
(23, 'EXCHANGE_STATUS',       '교환 상태',       '교환 요청 처리 상태',               23, 1),
(24, 'EXCHANGE_REASON_TYPE',  '교환 사유',       '교환 요청 사유 유형',               24, 1),
-- Settlement
(25, 'SETTLEMENT_STATUS',     '정산 상태',       '정산 처리 상태',                    25, 1),
(26, 'DEDUCTION_TYPE',        '차감 유형',       '정산 차감 항목 유형',               26, 1),
(27, 'DEDUCTION_PAYER',       '차감 부담 주체',  '정산 차감 비용 부담 주체',          27, 1),
-- Claim (공유)
(28, 'CLAIM_TYPE',            '클레임 구분',     '반품/교환 구분',                    28, 1),
(29, 'CLAIM_SHIPMENT_STATUS', '클레임 배송 상태','클레임 수거/반송 배송 상태',        29, 1),
(30, 'CLAIM_SHIPMENT_METHOD', '클레임 배송 방식','클레임 수거/반송 배송 방식',        30, 1),
(31, 'FEE_PAYER',             '배송비 부담 주체','클레임 배송비 부담 주체',           31, 1),
-- Shipment
(32, 'SHIPMENT_STATUS',       '배송 상태',       '출고/배송 처리 상태',               32, 1),
(33, 'SHIPMENT_METHOD_TYPE',  '배송 방식',       '택배/퀵/방문 등 배송 방식',         33, 1);

-- ============================================
-- 2. common_codes
-- ============================================

-- ORDER_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(12, 'ORDERED',            '주문완료',     1, 1),
(12, 'PREPARING',          '상품준비중',   2, 1),
(12, 'SHIPPED',            '배송중',       3, 1),
(12, 'DELIVERED',          '배송완료',     4, 1),
(12, 'CONFIRMED',          '구매확정',     5, 1),
(12, 'CANCELLED',          '주문취소',     6, 1),
(12, 'CLAIM_IN_PROGRESS',  '클레임진행중', 7, 1),
(12, 'REFUNDED',           '환불완료',     8, 1),
(12, 'EXCHANGED',          '교환완료',     9, 1);

-- PAYMENT_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(13, 'PENDING',             '결제대기',     1, 1),
(13, 'COMPLETED',           '결제완료',     2, 1),
(13, 'PARTIALLY_REFUNDED',  '부분환불',     3, 1),
(13, 'FULLY_REFUNDED',      '전액환불',     4, 1),
(13, 'CANCELLED',           '결제취소',     5, 1);

-- PAYMENT_METHOD_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(14, 'CARD',             '신용카드',     1, 1),
(14, 'BANK_TRANSFER',    '계좌이체',     2, 1),
(14, 'VIRTUAL_ACCOUNT',  '가상계좌',     3, 1),
(14, 'PHONE',            '휴대폰결제',   4, 1),
(14, 'KAKAO_PAY',        '카카오페이',   5, 1),
(14, 'NAVER_PAY',        '네이버페이',   6, 1),
(14, 'TOSS_PAY',         '토스페이',     7, 1);

-- SITE_NAME
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(15, 'SETOF',    'SetOf',   1, 1),
(15, 'NAVER',    '네이버',  2, 1),
(15, 'COUPANG',  '쿠팡',    3, 1),
(15, 'KAKAO',    '카카오',  4, 1),
(15, 'ZIGZAG',   '지그재그', 5, 1);

-- DELIVERY_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(16, 'READY',      '배송준비',   1, 1),
(16, 'SHIPPED',    '배송출발',   2, 1),
(16, 'IN_TRANSIT', '배송중',     3, 1),
(16, 'DELIVERED',  '배송완료',   4, 1),
(16, 'FAILED',     '배송실패',   5, 1);

-- FULFILLMENT_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(17, 'CANCEL',   '취소', 1, 1),
(17, 'REFUND',   '반품', 2, 1),
(17, 'EXCHANGE', '교환', 3, 1);

-- CANCEL_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(18, 'REQUESTED', '취소요청',   1, 1),
(18, 'APPROVED',  '취소승인',   2, 1),
(18, 'REJECTED',  '취소거절',   3, 1),
(18, 'COMPLETED', '취소완료',   4, 1),
(18, 'CANCELLED', '취소철회',   5, 1);

-- CANCEL_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(19, 'BUYER_CANCEL',  '구매자 취소', 1, 1),
(19, 'SELLER_CANCEL', '판매자 취소', 2, 1);

-- CANCEL_REASON_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(20, 'CHANGE_OF_MIND',       '단순변심',       1, 1),
(20, 'WRONG_ORDER',          '주문실수',       2, 1),
(20, 'FOUND_CHEAPER',        '다른곳에서 저렴', 3, 1),
(20, 'DELIVERY_TOO_SLOW',    '배송 지연',      4, 1),
(20, 'OUT_OF_STOCK',         '품절',           5, 1),
(20, 'PRODUCT_DISCONTINUED', '상품 단종',      6, 1),
(20, 'PRICE_ERROR',          '가격 오류',      7, 1),
(20, 'SHIPPING_UNAVAILABLE', '배송 불가',      8, 1),
(20, 'PRODUCT_ISSUE',        '상품 문제',      9, 1),
(20, 'OTHER',                '기타',           10, 1);

-- REFUND_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(21, 'REQUESTED',  '반품요청',   1, 1),
(21, 'COLLECTING', '수거중',     2, 1),
(21, 'COLLECTED',  '수거완료',   3, 1),
(21, 'COMPLETED',  '반품완료',   4, 1),
(21, 'REJECTED',   '반품거절',   5, 1),
(21, 'CANCELLED',  '반품철회',   6, 1);

-- REFUND_REASON_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(22, 'CHANGE_OF_MIND',      '단순변심',           1, 1),
(22, 'WRONG_PRODUCT',       '오배송',             2, 1),
(22, 'DEFECTIVE',           '상품불량',           3, 1),
(22, 'DIFFERENT_FROM_DESC', '상품정보 상이',      4, 1),
(22, 'DELAYED_DELIVERY',    '배송 지연',          5, 1),
(22, 'OTHER',               '기타',               6, 1);

-- EXCHANGE_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(23, 'REQUESTED',  '교환요청',     1, 1),
(23, 'COLLECTING', '수거중',       2, 1),
(23, 'COLLECTED',  '수거완료',     3, 1),
(23, 'PREPARING',  '교환상품준비', 4, 1),
(23, 'SHIPPING',   '교환상품배송', 5, 1),
(23, 'COMPLETED',  '교환완료',     6, 1),
(23, 'REJECTED',   '교환거절',     7, 1),
(23, 'CANCELLED',  '교환철회',     8, 1);

-- EXCHANGE_REASON_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(24, 'SIZE_CHANGE',       '사이즈 변경',   1, 1),
(24, 'COLOR_CHANGE',      '색상 변경',     2, 1),
(24, 'OPTION_CHANGE',     '옵션 변경',     3, 1),
(24, 'WRONG_OPTION_SENT', '옵션 오배송',   4, 1),
(24, 'DEFECTIVE',         '상품불량',       5, 1),
(24, 'OTHER',             '기타',           6, 1);

-- SETTLEMENT_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(25, 'PENDING',   '정산대기', 1, 1),
(25, 'HOLD',      '정산보류', 2, 1),
(25, 'COMPLETED', '정산완료', 3, 1);

-- DEDUCTION_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(26, 'DISCOUNT', '할인',     1, 1),
(26, 'MILEAGE',  '마일리지', 2, 1),
(26, 'COUPON',   '쿠폰',     3, 1),
(26, 'POINT',    '포인트',   4, 1);

-- DEDUCTION_PAYER
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(27, 'SELLER',   '셀러 부담',   1, 1),
(27, 'PLATFORM', '플랫폼 부담', 2, 1);

-- CLAIM_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(28, 'REFUND',   '반품', 1, 1),
(28, 'EXCHANGE', '교환', 2, 1);

-- CLAIM_SHIPMENT_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(29, 'PENDING',    '수거대기', 1, 1),
(29, 'IN_TRANSIT', '수거중',   2, 1),
(29, 'DELIVERED',  '수거완료', 3, 1),
(29, 'FAILED',     '수거실패', 4, 1);

-- CLAIM_SHIPMENT_METHOD
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(30, 'COURIER',           '택배',       1, 1),
(30, 'QUICK',             '퀵서비스',   2, 1),
(30, 'VISIT',             '방문수거',   3, 1),
(30, 'AUTO_PICKUP',       '자동수거',   4, 1),
(30, 'DESIGNATED_COURIER','지정택배',   5, 1);

-- FEE_PAYER
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(31, 'SELLER', '판매자 부담', 1, 1),
(31, 'BUYER',  '구매자 부담', 2, 1);

-- SHIPMENT_STATUS
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(32, 'READY',      '출고대기',   1, 1),
(32, 'PREPARING',  '출고준비중', 2, 1),
(32, 'SHIPPED',    '출고완료',   3, 1),
(32, 'IN_TRANSIT', '배송중',     4, 1),
(32, 'DELIVERED',  '배송완료',   5, 1),
(32, 'FAILED',     '배송실패',   6, 1),
(32, 'CANCELLED',  '배송취소',   7, 1);

-- SHIPMENT_METHOD_TYPE
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(33, 'COURIER',            '택배',       1, 1),
(33, 'QUICK',              '퀵서비스',   2, 1),
(33, 'VISIT',              '방문수령',   3, 1),
(33, 'DESIGNATED_COURIER', '지정택배',   4, 1);
