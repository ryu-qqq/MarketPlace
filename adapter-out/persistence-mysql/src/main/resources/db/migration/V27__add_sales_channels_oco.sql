-- 누락 판매 채널 추가
INSERT INTO sales_channel (id, channel_name, status, created_at, updated_at)
VALUES (15, 'OCO', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE channel_name = VALUES(channel_name);
