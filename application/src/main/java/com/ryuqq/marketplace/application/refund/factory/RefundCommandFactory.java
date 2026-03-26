package com.ryuqq.marketplace.application.refund.factory;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.common.util.OutboxPayloadUtils;
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
import java.util.Map;
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
                        orderItemId.value(),
                        null,
                        "REQUESTED",
                        requestedBy,
                        requestedBy);

        return new RefundBundle(claim, outbox, history);
    }

    /** 환불 요청 시 OrderItem 상태 전환에 필요한 시간 컨텍스트. */
    public StatusChangeContext<OrderItemId> createRequestOrderItemContext(String orderItemId) {
        return new StatusChangeContext<>(OrderItemId.of(orderItemId), timeProvider.now());
    }

    /** 승인 시 claim 상태 변경 + RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createApproveBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        claim.startCollecting(processedBy, now);
        RefundOutbox outbox = createOutbox(claim, RefundOutboxType.APPROVE, now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        claim.orderItemIdValue(),
                        "REQUESTED",
                        "COLLECTING",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 수거 완료 시 claim 상태 변경 + RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createCollectBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        claim.completeCollection(processedBy, now);
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
                        claim.orderItemIdValue(),
                        "COLLECTING",
                        "COLLECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 시 claim 상태 변경 + RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createHoldBundle(RefundClaim claim, String memo, String processedBy) {
        Instant now = timeProvider.now();
        claim.hold(memo, now);
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
                        claim.orderItemIdValue(),
                        claim.status().name(),
                        "HOLD",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 해제 시 claim 상태 변경 + RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createReleaseHoldBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        claim.releaseHold(now);
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
                        claim.orderItemIdValue(),
                        "HOLD",
                        claim.status().name(),
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 거절 시 claim 상태 변경 + RefundOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createRejectBundle(RefundClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        claim.reject(processedBy, now);
        RefundOutbox outbox =
                RefundOutbox.forNew(
                        claim.orderItemId(),
                        RefundOutboxType.REJECT,
                        RefundOutboxPayloadBuilder.rejectPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        claim.idValue(),
                        claim.orderItemIdValue(),
                        "REQUESTED",
                        "REJECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 아웃박스 상태 변경에 필요한 시간 컨텍스트 생성. */
    public StatusChangeContext<Long> createOutboxChangeContext(Long outboxId) {
        return new StatusChangeContext<>(outboxId, timeProvider.now());
    }

    /** PENDING 아웃박스 조회 기준 시간 계산. */
    public Instant calculatePendingThreshold(int delaySeconds) {
        return timeProvider.now().minusSeconds(delaySeconds);
    }

    /** 타임아웃 아웃박스 조회 기준 시간 계산. */
    public Instant calculateTimeoutThreshold(long timeoutSeconds) {
        return timeProvider.now().minusSeconds(timeoutSeconds);
    }

    private RefundOutbox createOutbox(RefundClaim claim, RefundOutboxType type, Instant now) {
        String payload =
                switch (type) {
                    case APPROVE -> RefundOutboxPayloadBuilder.approvePayload(claim.idValue());
                    case REJECT -> RefundOutboxPayloadBuilder.rejectPayload(claim.idValue());
                    default -> throw new IllegalArgumentException("지원하지 않는 타입: " + type);
                };
        return RefundOutbox.forNew(claim.orderItemId(), type, payload, now);
    }

    /** RefundClaim + RefundOutbox + ClaimHistory 묶음 (환불 요청용). */
    public record RefundBundle(RefundClaim claim, RefundOutbox outbox, ClaimHistory history) {}

    /** RefundOutbox + ClaimHistory 묶음 (승인/거절용). */
    public record OutboxWithHistory(RefundOutbox outbox, ClaimHistory history) {}

    /** 환불 아웃박스 페이로드 빌더. */
    private static final class RefundOutboxPayloadBuilder {

        private RefundOutboxPayloadBuilder() {}

        static String requestPayload(String refundClaimId, int refundQty) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "refundQty", refundQty));
        }

        static String approvePayload(String refundClaimId) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "action", "APPROVE"));
        }

        static String rejectPayload(String refundClaimId) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "action", "REJECT"));
        }

        static String collectPayload(String refundClaimId) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "action", "COLLECT"));
        }

        static String holdPayload(String refundClaimId) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "action", "HOLD"));
        }

        static String releaseHoldPayload(String refundClaimId) {
            return OutboxPayloadUtils.mapToJson(
                    Map.of("refundClaimId", refundClaimId, "action", "RELEASE_HOLD"));
        }
    }
}
