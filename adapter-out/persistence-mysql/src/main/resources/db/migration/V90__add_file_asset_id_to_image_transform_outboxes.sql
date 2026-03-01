-- V90: image_transform_outboxes 테이블에 file_asset_id 컬럼 추가
-- FileFlow 다운로드 완료 시 획득한 Asset ID를 저장하여 변환 요청 시 재등록 없이 사용
ALTER TABLE image_transform_outboxes
    ADD COLUMN file_asset_id VARCHAR(100) DEFAULT NULL AFTER variant_type;
