-- seller_sales_channels: 중복 인덱스 제거
-- UNIQUE KEY (seller_id, sales_channel_id)가 이미 seller_id 단일 인덱스를 커버합니다.
DROP INDEX idx_seller_sales_channels_seller_id ON seller_sales_channels;

-- seller_sales_channels: 저선택도 단일 인덱스를 복합 인덱스로 교체
-- connection_status는 3개 값(CONNECTED/DISCONNECTED/SUSPENDED)만 가지므로 단일 인덱스 비효율적
DROP INDEX idx_seller_sales_channels_connection_status ON seller_sales_channels;

-- external_product_sync_outboxes: 중복 인덱스 정리 + 복합 인덱스 추가
-- status 단일 인덱스 제거 (status_retry 복합 인덱스가 커버)
DROP INDEX idx_external_product_sync_outboxes_status ON external_product_sync_outboxes;

-- 쿼리 패턴에 맞는 복합 인덱스 추가 (product_group_id + status)
CREATE INDEX idx_external_product_sync_outboxes_product_group_status
    ON external_product_sync_outboxes (product_group_id, status);
