-- qnas 테이블에 order_id, question_title 컬럼 추가
ALTER TABLE qnas
    ADD COLUMN order_id        BIGINT       NULL AFTER product_group_id,
    ADD COLUMN question_title  VARCHAR(500) NULL AFTER external_qna_id;

-- order_id 인덱스 추가
CREATE INDEX idx_qnas_order_id ON qnas (order_id);
