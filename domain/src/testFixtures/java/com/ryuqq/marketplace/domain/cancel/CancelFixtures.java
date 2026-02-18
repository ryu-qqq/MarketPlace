package com.ryuqq.marketplace.domain.cancel;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.aggregate.CancelItem;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelItemId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;
import java.util.List;

/**
 * Cancel 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Cancel 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelFixtures {

    private CancelFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CANCEL_ID = "01900000-0000-7000-8000-000000000001";
    private static final String DEFAULT_ORDER_ID = "ORD-20240101-0001";
    private static final String DEFAULT_REQUESTED_BY = "buyer@marketplace.com";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final long DEFAULT_ORDER_ITEM_ID = 1001L;
    private static final int DEFAULT_CANCEL_QTY = 2;

    // ===== ID Fixtures =====

    public static CancelId defaultCancelId() {
        return CancelId.of(DEFAULT_CANCEL_ID);
    }

    public static CancelId cancelId(String value) {
        return CancelId.of(value);
    }

    public static CancelNumber defaultCancelNumber() {
        return CancelNumber.of("CAN-20240101-0001");
    }

    // ===== VO Fixtures =====

    public static CancelReason defaultCancelReason() {
        return new CancelReason(CancelReasonType.CHANGE_OF_MIND, null);
    }

    public static CancelReason cancelReasonWithOther(String detail) {
        return new CancelReason(CancelReasonType.OTHER, detail);
    }

    public static CancelReason cancelReason(CancelReasonType reasonType) {
        return new CancelReason(reasonType, null);
    }

    public static CancelRefundInfo defaultCancelRefundInfo() {
        return CancelRefundInfo.of(
                Money.of(10000), "CARD", "REFUNDED", CommonVoFixtures.now(), "PG-REFUND-001");
    }

    public static CancelRefundInfo cancelRefundInfo(Money refundAmount) {
        return CancelRefundInfo.of(
                refundAmount, "CARD", "REFUNDED", CommonVoFixtures.now(), "PG-REFUND-001");
    }

    // ===== CancelItem Fixtures =====

    public static CancelItem defaultCancelItem() {
        return CancelItem.forNew(DEFAULT_ORDER_ITEM_ID, DEFAULT_CANCEL_QTY);
    }

    public static CancelItem cancelItem(long orderItemId, int cancelQty) {
        return CancelItem.forNew(orderItemId, cancelQty);
    }

    public static CancelItem reconstitutedCancelItem() {
        return CancelItem.reconstitute(
                CancelItemId.of(1L), DEFAULT_ORDER_ITEM_ID, DEFAULT_CANCEL_QTY);
    }

    public static List<CancelItem> defaultCancelItems() {
        return List.of(defaultCancelItem());
    }

    // ===== Cancel Aggregate Fixtures (forNew) =====

    public static Cancel newBuyerCancel() {
        return Cancel.forBuyerCancel(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                defaultCancelItems(),
                defaultCancelReason(),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    public static Cancel newSellerCancel() {
        return Cancel.forSellerCancel(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                defaultCancelItems(),
                cancelReason(CancelReasonType.OUT_OF_STOCK),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    // ===== Cancel Aggregate Fixtures (reconstitute) =====

    public static Cancel requestedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                CancelType.BUYER_CANCEL,
                CancelStatus.REQUESTED,
                defaultCancelReason(),
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                requestedAt,
                defaultCancelItems());
    }

    public static Cancel approvedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                CancelType.BUYER_CANCEL,
                CancelStatus.APPROVED,
                defaultCancelReason(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt,
                defaultCancelItems());
    }

    public static Cancel completedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                CancelType.BUYER_CANCEL,
                CancelStatus.COMPLETED,
                defaultCancelReason(),
                defaultCancelRefundInfo(),
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                processedAt,
                requestedAt,
                processedAt,
                defaultCancelItems());
    }

    public static Cancel rejectedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                CancelType.BUYER_CANCEL,
                CancelStatus.REJECTED,
                defaultCancelReason(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt,
                defaultCancelItems());
    }

    public static Cancel cancelledCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant cancelledAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                DEFAULT_ORDER_ID,
                CancelType.BUYER_CANCEL,
                CancelStatus.CANCELLED,
                defaultCancelReason(),
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                cancelledAt,
                defaultCancelItems());
    }
}
