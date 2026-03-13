-- =============================================================
-- V9: 이미지 URL 컬럼 길이 500 -> 1000 확장
-- =============================================================
-- 사유: 레거시 데이터 중 외부 이미지 프록시(storebot.info) URL이
--       500자를 초과하는 케이스 존재 (최대 680자 확인)

ALTER TABLE description_images MODIFY COLUMN origin_url VARCHAR(1000) NOT NULL;
ALTER TABLE description_images MODIFY COLUMN uploaded_url VARCHAR(1000);

ALTER TABLE image_transform_outboxes MODIFY COLUMN uploaded_url VARCHAR(1000) NOT NULL;

ALTER TABLE image_upload_outboxes MODIFY COLUMN origin_url VARCHAR(1000) NOT NULL;

ALTER TABLE image_variants MODIFY COLUMN variant_url VARCHAR(1000) NOT NULL;

ALTER TABLE product_group_images MODIFY COLUMN origin_url VARCHAR(1000) NOT NULL;
ALTER TABLE product_group_images MODIFY COLUMN uploaded_url VARCHAR(1000);

ALTER TABLE outbound_product_images MODIFY COLUMN origin_url VARCHAR(1000) NOT NULL;
ALTER TABLE outbound_product_images MODIFY COLUMN external_url VARCHAR(1000);
