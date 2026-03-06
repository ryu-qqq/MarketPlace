-- V89: ImageUploadOutbox에 download_task_id 컬럼 추가 (2-Scheduler 패턴 지원)
ALTER TABLE image_upload_outboxes ADD COLUMN download_task_id VARCHAR(100) DEFAULT NULL;
