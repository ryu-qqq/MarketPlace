-- Brand Integration Test Cleanup
-- 테스트 후 데이터 정리

DELETE FROM brand_alias WHERE brand_id IN (SELECT id FROM brand WHERE 1=1);
DELETE FROM brand WHERE 1=1;
