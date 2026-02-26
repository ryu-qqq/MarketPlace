package com.ryuqq.marketplace.domain.cancel.aggregate;

import com.ryuqq.marketplace.domain.cancel.id.CancelItemId;

/** 취소 대상 주문 상품. Cancel Aggregate 내부 구성 요소. */
public class CancelItem {

    private final CancelItemId id;
    private final long orderItemId;
    private final int cancelQty;

    private CancelItem(CancelItemId id, long orderItemId, int cancelQty) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.cancelQty = cancelQty;
    }

    public static CancelItem forNew(long orderItemId, int cancelQty) {
        if (cancelQty <= 0) {
            throw new IllegalArgumentException("취소 수량은 1 이상이어야 합니다");
        }
        return new CancelItem(CancelItemId.forNew(), orderItemId, cancelQty);
    }

    public static CancelItem reconstitute(CancelItemId id, long orderItemId, int cancelQty) {
        return new CancelItem(id, orderItemId, cancelQty);
    }

    public CancelItemId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long orderItemId() {
        return orderItemId;
    }

    public int cancelQty() {
        return cancelQty;
    }
}
