-- category_mapping 테이블에 preset_id FK 추가
-- 프리셋 기반 매핑 관리를 위해 preset_id 추가, 기존 unique key 제거

ALTER TABLE category_mapping
    ADD COLUMN preset_id BIGINT NULL COMMENT '카테고리 프리셋 ID (FK)' AFTER id;

-- 기존 sales_channel_category_id 단일 유니크 제약 제거 (프리셋별 다중 매핑 허용)
ALTER TABLE category_mapping
    DROP INDEX uq_cm_sc_category;

-- 동일 프리셋 내 같은 내부 카테고리 중복 매핑 방지
ALTER TABLE category_mapping
    ADD UNIQUE KEY uq_cm_preset_internal (preset_id, internal_category_id);

-- preset_id 인덱스 추가
ALTER TABLE category_mapping
    ADD INDEX idx_cm_preset (preset_id);
