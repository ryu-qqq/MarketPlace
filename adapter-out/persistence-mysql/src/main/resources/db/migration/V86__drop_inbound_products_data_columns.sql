-- InboundProduct 순수 매핑 레지스트리 전환: 상품 데이터 컬럼 제거
-- 동기 변환 방식으로 전환하여 데이터를 저장하지 않고 즉시 내부 ProductGroup을 생성/갱신합니다.
ALTER TABLE inbound_products
    DROP COLUMN product_name,
    DROP COLUMN regular_price,
    DROP COLUMN current_price,
    DROP COLUMN option_type,
    DROP COLUMN description_html,
    DROP COLUMN raw_payload,
    DROP COLUMN retry_count;
