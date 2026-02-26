package com.ryuqq.marketplace.domain.refund;

import com.ryuqq.marketplace.domain.claim.ClaimFixtures;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundItem;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.id.RefundItemId;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import java.util.List;

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
    private static final String DEFAULT_REFUND_CLAIM_ID = "REFUND-CLAIM-0001";
    private static final String DEFAULT_REFUND_CLAIM_NUMBER = "RFD-20260218-0001";
    private static final String DEFAULT_ORDER_ID = "ORDER-0001";
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

    public static RefundItemId defaultRefundItemId() {
        return RefundItemId.of(1L);
    }

    public static RefundItemId newRefundItemId() {
        return RefundItemId.forNew();
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

    public static ClaimShipment defaultCollectShipment() {
        return ClaimShipment.forNew(
                ClaimShipmentId.of("CLAIM-SHIP-REFUND-0001"),
                ClaimFixtures.defaultClaimShipmentMethod(),
                ClaimFixtures.defaultShippingFeeInfo(),
                ClaimFixtures.senderContactInfo(),
                ClaimFixtures.receiverContactInfo());
    }

    // ===== RefundItem Fixtures =====

    public static RefundItem defaultRefundItem() {
        return RefundItem.forNew(1001L, 1);
    }

    public static RefundItem refundItem(long orderItemId, int qty) {
        return RefundItem.forNew(orderItemId, qty);
    }

    public static RefundItem reconstitutedRefundItem() {
        return RefundItem.reconstitute(defaultRefundItemId(), 1001L, 1);
    }

    public static List<RefundItem> defaultRefundItems() {
        return List.of(defaultRefundItem());
    }

    public static List<RefundItem> multipleRefundItems() {
        return List.of(RefundItem.forNew(1001L, 1), RefundItem.forNew(1002L, 2));
    }

    // ===== RefundClaim Aggregate Fixtures =====

    public static RefundClaim newRefundClaim() {
        return RefundClaim.forNew(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                defaultRefundItems(),
                defaultRefundReason(),
                defaultCollectShipment(),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    public static RefundClaim requestedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.REQUESTED,
                defaultRefundReason(),
                null,
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                requestedAt,
                defaultRefundItems());
    }

    public static RefundClaim collectingRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.COLLECTING,
                defaultRefundReason(),
                null,
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt,
                defaultRefundItems());
    }

    public static RefundClaim collectedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.COLLECTED,
                defaultRefundReason(),
                null,
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt,
                defaultRefundItems());
    }

    public static RefundClaim completedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant completedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.COMPLETED,
                defaultRefundReason(),
                defaultRefundInfo(),
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                completedAt,
                completedAt,
                requestedAt,
                completedAt,
                defaultRefundItems());
    }

    public static RefundClaim rejectedRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.REJECTED,
                defaultRefundReason(),
                null,
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                requestedAt,
                processedAt,
                null,
                requestedAt,
                processedAt,
                defaultRefundItems());
    }

    public static RefundClaim cancelledRefundClaim() {
        Instant requestedAt = CommonVoFixtures.yesterday();
        Instant updatedAt = CommonVoFixtures.now();
        return RefundClaim.reconstitute(
                defaultRefundClaimId(),
                defaultRefundClaimNumber(),
                DEFAULT_ORDER_ID,
                RefundStatus.CANCELLED,
                defaultRefundReason(),
                null,
                defaultCollectShipment(),
                null,
                DEFAULT_REQUESTED_BY,
                null,
                requestedAt,
                null,
                null,
                requestedAt,
                updatedAt,
                defaultRefundItems());
    }

    public static RefundClaim holdRefundClaim() {
        RefundClaim claim = requestedRefundClaim();
        claim.hold(DEFAULT_HOLD_REASON, CommonVoFixtures.now());
        return claim;
    }
}
