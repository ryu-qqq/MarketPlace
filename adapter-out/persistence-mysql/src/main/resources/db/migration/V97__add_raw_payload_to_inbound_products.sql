-- InboundProduct에 상품 데이터(payload) 저장 컬럼 추가
-- PENDING_MAPPING 상태에서 매핑 완료 후 크롤러 재수신 없이 CONVERTED까지 처리하기 위함
ALTER TABLE inbound_products
    ADD COLUMN raw_payload JSON AFTER status;
