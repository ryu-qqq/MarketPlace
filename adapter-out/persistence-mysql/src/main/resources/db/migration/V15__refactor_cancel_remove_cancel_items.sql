-- Cancel 리팩토링: CancelItem 제거, Cancel에 order_item_id + cancel_qty 통합
-- Cancel 1건 = OrderItem 1건의 1회 취소 요청

-- 1. cancels 테이블에 order_item_id, cancel_qty 컬럼 추가
ALTER TABLE cancels
    ADD COLUMN order_item_id VARCHAR(36) NULL AFTER order_id,
    ADD COLUMN cancel_qty INT NOT NULL DEFAULT 1 AFTER order_item_id;

-- 2. 기존 cancel_items 데이터를 cancels로 마이그레이션
UPDATE cancels c
    INNER JOIN cancel_items ci ON ci.cancel_id = c.id
SET c.order_item_id = ci.order_item_id,
    c.cancel_qty    = ci.cancel_qty;

-- 3. order_item_id NOT NULL 제약 추가
ALTER TABLE cancels
    MODIFY COLUMN order_item_id VARCHAR(36) NOT NULL;

-- 4. order_id 컬럼 제거 (Cancel은 더 이상 Order가 아닌 OrderItem 기준)
ALTER TABLE cancels
    DROP INDEX idx_cancels_order_id,
    DROP COLUMN order_id;

-- 5. order_item_id 인덱스 추가
CREATE INDEX idx_cancels_order_item_id ON cancels (order_item_id);

-- 6. cancel_items 테이블 삭제
DROP TABLE IF EXISTS cancel_items;
