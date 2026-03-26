-- order_items 테이블에 order_item_number 컬럼 추가
ALTER TABLE order_items
    ADD COLUMN order_item_number VARCHAR(50) NULL AFTER id;

-- 기존 데이터 마이그레이션: order_number + 순번으로 채움
UPDATE order_items oi
    INNER JOIN orders o ON oi.order_id = o.id
    INNER JOIN (
        SELECT id, order_id,
               ROW_NUMBER() OVER (PARTITION BY order_id ORDER BY id) AS seq
        FROM order_items
    ) ranked ON oi.id = ranked.id
SET oi.order_item_number = CONCAT(o.order_number, '-', LPAD(ranked.seq, 3, '0'));

-- NOT NULL 제약 추가
ALTER TABLE order_items
    MODIFY COLUMN order_item_number VARCHAR(50) NOT NULL;

-- 유니크 인덱스 추가
CREATE UNIQUE INDEX idx_order_items_order_item_number ON order_items (order_item_number);
