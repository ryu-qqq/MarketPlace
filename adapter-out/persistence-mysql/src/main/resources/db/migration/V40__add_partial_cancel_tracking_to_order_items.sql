-- 부분취소/부분반품 수량 추적 컬럼 추가
ALTER TABLE order_items
    ADD COLUMN cancelled_qty INT NOT NULL DEFAULT 0 COMMENT '취소 완료 수량',
    ADD COLUMN returned_qty  INT NOT NULL DEFAULT 0 COMMENT '반품 완료 수량';

-- 기존 CANCELLED 상태의 order_items는 cancelledQty를 quantity와 동일하게 보정
UPDATE order_items
SET cancelled_qty = quantity
WHERE order_item_status = 'CANCELLED';

-- 기존 RETURNED 상태의 order_items는 returnedQty를 quantity와 동일하게 보정
UPDATE order_items
SET returned_qty = quantity
WHERE order_item_status = 'RETURNED';

-- order_item_histories 테이블에 수량 컬럼 추가
ALTER TABLE order_item_histories
    ADD COLUMN quantity INT NOT NULL DEFAULT 0 COMMENT '변경 수량 (부분취소/부분반품 시)';
