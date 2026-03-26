package com.ryuqq.marketplace.domain.cancel;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.id.CancelOutboxId;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxStatus;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

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
    private static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final long DEFAULT_SELLER_ID = 10L;
    private static final int DEFAULT_CANCEL_QTY = 2;
    private static final String DEFAULT_REQUESTED_BY = "buyer@marketplace.com";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";

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

    // ===== Cancel Aggregate Fixtures (forNew) =====

    public static Cancel newBuyerCancel() {
        return Cancel.forBuyerCancel(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
                defaultCancelReason(),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    public static Cancel newSellerCancel() {
        return Cancel.forSellerCancel(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                requestedAt);
    }

    public static Cancel approvedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                processedAt);
    }

    public static Cancel completedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                processedAt);
    }

    public static Cancel rejectedCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                processedAt);
    }

    public static Cancel cancelledCancel() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant cancelledAt = CommonVoFixtures.now();
        return Cancel.reconstitute(
                defaultCancelId(),
                defaultCancelNumber(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
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
                cancelledAt);
    }

    // ===== CancelOutbox ID Fixtures =====

    public static CancelOutboxId defaultCancelOutboxId() {
        return CancelOutboxId.of(1L);
    }

    public static CancelOutboxId newCancelOutboxId() {
        return CancelOutboxId.forNew();
    }

    // ===== CancelOutbox Aggregate Fixtures =====

    public static CancelOutbox newCancelOutbox() {
        return CancelOutbox.forNew(
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                CancelOutboxType.APPROVE,
                "{\"status\":\"APPROVED\"}",
                CommonVoFixtures.now());
    }

    public static CancelOutbox newCancelOutbox(CancelOutboxType outboxType) {
        return CancelOutbox.forNew(
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                outboxType,
                "{\"status\":\"APPROVED\"}",
                CommonVoFixtures.now());
    }

    public static CancelOutbox pendingCancelOutbox() {
        Instant now = CommonVoFixtures.now();
        return CancelOutbox.reconstitute(
                defaultCancelOutboxId(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                CancelOutboxType.APPROVE,
                CancelOutboxStatus.PENDING,
                "{\"status\":\"APPROVED\"}",
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                "COBO:" + DEFAULT_ORDER_ITEM_ID + ":APPROVE:" + now.toEpochMilli());
    }

    public static CancelOutbox processingCancelOutbox() {
        Instant now = CommonVoFixtures.now();
        CancelOutbox outbox = pendingCancelOutbox();
        outbox.startProcessing(now);
        return outbox;
    }

    public static CancelOutbox completedCancelOutbox() {
        Instant now = CommonVoFixtures.now();
        CancelOutbox outbox = processingCancelOutbox();
        outbox.complete(now);
        return outbox;
    }

    public static CancelOutbox failedCancelOutbox() {
        Instant now = CommonVoFixtures.now();
        Instant createdAt = CommonVoFixtures.yesterday();
        return CancelOutbox.reconstitute(
                defaultCancelOutboxId(),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                CancelOutboxType.APPROVE,
                CancelOutboxStatus.FAILED,
                "{\"status\":\"APPROVED\"}",
                3,
                3,
                createdAt,
                now,
                now,
                "외부 API 오류",
                1L,
                "COBO:" + DEFAULT_ORDER_ITEM_ID + ":APPROVE:" + createdAt.toEpochMilli());
    }
}
