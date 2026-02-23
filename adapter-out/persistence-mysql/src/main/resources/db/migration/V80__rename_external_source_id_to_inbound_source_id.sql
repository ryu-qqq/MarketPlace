-- V80: external_source_id 컬럼을 inbound_source_id로 rename
ALTER TABLE inbound_brand_mapping CHANGE COLUMN external_source_id inbound_source_id BIGINT NOT NULL COMMENT '인바운드 소스 ID';
ALTER TABLE inbound_category_mapping CHANGE COLUMN external_source_id inbound_source_id BIGINT NOT NULL COMMENT '인바운드 소스 ID';
ALTER TABLE inbound_products CHANGE COLUMN external_source_id inbound_source_id BIGINT NOT NULL COMMENT '인바운드 소스 ID';
