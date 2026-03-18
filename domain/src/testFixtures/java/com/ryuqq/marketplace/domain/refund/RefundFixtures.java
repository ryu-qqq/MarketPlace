package com.ryuqq.marketplace.domain.refund;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;

/**
 * Refund 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 RefundClaim 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class RefundFixtures {

    private RefundFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_REFUND_CLAIM_ID = "01900000-0000-7000-8000-000000000010";
    private static final String DEFAULT_REFUND_CLAIM_NUMBER = "RFD-20260218-0001";
    private static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final long DEFAULT_SELLER_ID = 10L;
    private static final int DEFAULT_REFUND_QTY = 1;
    private static final String DEFAULT_REQUESTED_BY = "customer@marketplace.com";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final String DEFAULT_HOLD_REASON = "추가 확인 필요";

    // ===== ID Fixtures =====

    public static RefundClaimId defaultRefundClaimId() {
        return RefundClaimId.of(DEFAULT_REFUND_CLAIM_ID);
    }

    public static RefundClaimId refundClaimId(String value) {
        return RefundClaimId.of(value);
    }

    public static RefundClaimNumber defaultRefundClaimNumber() {
        return RefundClaimNumber.of(DEFAULT_REFUND_CLAIM_NUMBER);
    }

    public static OrderItemId defaultOrderItemId() {
        return OrderItemId.of(DEFAULT_ORDER_ITEM_ID);
    }

    // ===== VO Fixtures =====

    public static RefundReason defaultRefundReason() {
        return RefundReason.of(RefundReasonType.CHANGE_OF_MIND, "단순 변심입니다");
    }

    public static RefundReason refundReason(RefundReasonType type, String detail) {
        return RefundReason.of(type, detail);
    }

    public static RefundReason defectiveRefundReason() {
        return RefundReason.of(RefundReasonType.DEFECTIVE, "상품 불량입니다");
    }

    public static RefundInfo defaultRefundInfo() {
        return RefundInfo.fullRefund(Money.of(10000), "CARD", CommonVoFixtures.now());
    }

    public static RefundInfo partialRefundInfo() {
        return RefundInfo.partialRefund(
                Money.of(10000), Money.of(3000), "왕복 배송비 차감", "CARD", CommonVoFixtures.now());
    }

    public static RefundInfo refundInfo(Money originalAmount, Money deductionAmount) {
        return RefundInfo.of(
                originalAmount,
                originalAmount.subtract(deductionAmount),
                deductionAmount,
                "배송비 차감",
                "CARD",
                CommonVoFixtures.now());
    }

    public static HoldInfo defaultHoldInfo() {
        return HoldInfo.of(DEFAULT_HOLD_REASON, CommonVoFixtures.now());
    }

    public static HoldInfo holdInfo(String reason) {
        return HoldInfo.of(reason, CommonVoFixtures.now());
    }

    // ===== RefundClaim Aggregate Fixtures (forNew) =====

    public static RefundClaim newRefundClaim() {
        return RefundClaim.forNew(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                defaultRefundReason(),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    // ===== RefundClaim Aggregate Fixtures (reconstitute) =====

    public static RefundClaim requestedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.REQUESTED,
                defaultRefundReason(),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                requestedAt);
    }

    public static RefundClaim collectingRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.COLLECTING,
                defaultRefundReason(),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt);
    }

    public static RefundClaim collectedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.COLLECTED,
                defaultRefundReason(),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt);
    }

    public static RefundClaim completedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant completedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.COMPLETED,
                defaultRefundReason(),
                defaultRefundInfo(),
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                completedAt,
                completedAt,
                requestedAt,
                completedAt);
    }

    public static RefundClaim rejectedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.REJECTED,
                defaultRefundReason(),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt);
    }

    public static RefundClaim cancelledRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant updatedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                RefundStatus.CANCELLED,
                defaultRefundReason(),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                updatedAt);
    }

    public static RefundClaim holdRefundClaim() {
        RefundClaim claim = requestedRefundClaim();
        claim.hold(DEFAULT_HOLD_REASON, CommonVoFixtures.now());
        return claim;
    }
}
