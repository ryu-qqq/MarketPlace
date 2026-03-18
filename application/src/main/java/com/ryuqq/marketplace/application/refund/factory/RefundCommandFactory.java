package com.ryuqq.marketplace.application.refund.factory;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** RefundClaim 도메인 객체 생성 팩토리. */
@Component
public class RefundCommandFactory {

    private final TimeProvider timeProvider;
    private final ClaimHistoryFactory historyFactory;

    public RefundCommandFactory(TimeProvider timeProvider, ClaimHistoryFactory historyFactory) {
        this.timeProvider = timeProvider;
        this.historyFactory = historyFactory;
    }

    /** 환불 요청 RefundClaim + RefundOutbox + ClaimHistory 생성. */
    public RefundBundle createRefundRequest(
            RefundRequestItem item, String requestedBy, long sellerId) {
        Instant now = timeProvider.now();
        RefundClaimId claimId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber claimNumber = RefundClaimNumber.generate();
        OrderItemId orderItemId = OrderItemId.of(item.orderItemId());

        RefundClaim claim =
                RefundClaim.forNew(
                        claimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        item.refundQty(),
                        new RefundReason(item.reasonType(), item.reasonDetail()),
                        requestedBy,
                        now);

        RefundOutbox outbox =
                RefundOutbox.forNew(
                        orderItemId,
                        RefundOutboxType.REQUEST,
                        RefundOutboxPayloadBuilder.requestPayload(
                                claimId.value(), item.refundQty()),
                        now);

        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claimId.value(),
                        null,
                        "REQUESTED",
                        requestedBy,
                        requestedBy);

        return new RefundBundle(claim, outbox, history);
    }

    /** 승인 시 RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createApproveBundle(RefundClaim claim, String processedBy) {
        RefundOutbox outbox = createApproveOutbox(claim);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        "REQUESTED",
                        "COLLECTING",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 수거 완료 시 RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createCollectBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        RefundOutbox outbox =
                RefundOutbox.forNew(
                        claim.orderItemId(),
                        RefundOutboxType.COLLECT,
                        RefundOutboxPayloadBuilder.collectPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        "COLLECTING",
                        "COLLECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 시 RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createHoldBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        RefundOutbox outbox =
                RefundOutbox.forNew(
                        claim.orderItemId(),
                        RefundOutboxType.HOLD,
                        RefundOutboxPayloadBuilder.holdPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        claim.status().name(),
                        "HOLD",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 해제 시 RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createReleaseHoldBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        RefundOutbox outbox =
                RefundOutbox.forNew(
                        claim.orderItemId(),
                        RefundOutboxType.RELEASE_HOLD,
                        RefundOutboxPayloadBuilder.releaseHoldPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        "HOLD",
                        claim.status().name(),
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 거절 시 RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createRejectBundle(RefundClaim claim, String processedBy) {
        RefundOutbox outbox = createRejectOutbox(claim);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        "REQUESTED",
                        "REJECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    public Instant now() {
        return timeProvider.now();
    }

    private RefundOutbox createApproveOutbox(RefundClaim claim) {
        Instant now = timeProvider.now();
        return RefundOutbox.forNew(
                claim.orderItemId(),
                RefundOutboxType.APPROVE,
                RefundOutboxPayloadBuilder.approvePayload(claim.idValue()),
                now);
    }

    private RefundOutbox createRejectOutbox(RefundClaim claim) {
        Instant now = timeProvider.now();
        return RefundOutbox.forNew(
                claim.orderItemId(),
                RefundOutboxType.REJECT,
                RefundOutboxPayloadBuilder.rejectPayload(claim.idValue()),
                now);
    }

    /** RefundClaim + RefundOutbox + ClaimHistory 묶음 (환불 요청용). */
    public record RefundBundle(RefundClaim claim, RefundOutbox outbox, ClaimHistory history) {}

    /** RefundOutbox + ClaimHistory 묶음 (승인/거절용). */
    public record OutboxWithHistory(RefundOutbox outbox, ClaimHistory history) {}

    /** 환불 아웃박스 페이로드 빌더. */
    private static final class RefundOutboxPayloadBuilder {

        private RefundOutboxPayloadBuilder() {}

        static String requestPayload(String refundClaimId, int refundQty) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"refundQty\":" + refundQty + "}";
        }

        static String approvePayload(String refundClaimId) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"action\":\"APPROVE\"}";
        }

        static String rejectPayload(String refundClaimId) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"action\":\"REJECT\"}";
        }

        static String collectPayload(String refundClaimId) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"action\":\"COLLECT\"}";
        }

        static String holdPayload(String refundClaimId) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"action\":\"HOLD\"}";
        }

        static String releaseHoldPayload(String refundClaimId) {
            return "{\"refundClaimId\":\"" + refundClaimId + "\",\"action\":\"RELEASE_HOLD\"}";
        }
    }
}
