-- exchange_claims 테이블에 보류 정보 컬럼 추가

ALTER TABLE exchange_claims
    ADD COLUMN hold_reason VARCHAR(500) NULL COMMENT '보류 사유',
    ADD COLUMN hold_at TIMESTAMP NULL COMMENT '보류 시각';
