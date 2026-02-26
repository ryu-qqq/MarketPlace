package com.ryuqq.marketplace.domain.refund.aggregate;

import com.ryuqq.marketplace.domain.refund.id.RefundItemId;

/** 환불 대상 주문 상품. RefundClaim Aggregate 내부 구성 요소. */
public class RefundItem {

    private final RefundItemId id;
    private final long orderItemId;
    private final int refundQty;

    private RefundItem(RefundItemId id, long orderItemId, int refundQty) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.refundQty = refundQty;
    }

    public static RefundItem forNew(long orderItemId, int refundQty) {
        if (refundQty <= 0) {
            throw new IllegalArgumentException("환불 수량은 1 이상이어야 합니다");
        }
        return new RefundItem(RefundItemId.forNew(), orderItemId, refundQty);
    }

    public static RefundItem reconstitute(RefundItemId id, long orderItemId, int refundQty) {
        return new RefundItem(id, orderItemId, refundQty);
    }

    public RefundItemId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long orderItemId() {
        return orderItemId;
    }

    public int refundQty() {
        return refundQty;
    }
}
