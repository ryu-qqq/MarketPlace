package com.ryuqq.marketplace.application.refund.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
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

    public RefundCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 환불 요청 RefundClaim + RefundOutbox 생성. */
    public RefundWithOutbox createRefundRequest(
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

        return new RefundWithOutbox(claim, outbox);
    }

    /** 승인 시 RefundOutbox 생성. */
    public RefundOutbox createApproveOutbox(RefundClaim claim) {
        Instant now = timeProvider.now();
        return RefundOutbox.forNew(
                claim.orderItemId(),
                RefundOutboxType.APPROVE,
                RefundOutboxPayloadBuilder.approvePayload(claim.idValue()),
                now);
    }

    /** 거절 시 RefundOutbox 생성. */
    public RefundOutbox createRejectOutbox(RefundClaim claim) {
        Instant now = timeProvider.now();
        return RefundOutbox.forNew(
                claim.orderItemId(),
                RefundOutboxType.REJECT,
                RefundOutboxPayloadBuilder.rejectPayload(claim.idValue()),
                now);
    }

    public Instant now() {
        return timeProvider.now();
    }

    /** RefundClaim + RefundOutbox 묶음. */
    public record RefundWithOutbox(RefundClaim claim, RefundOutbox outbox) {}

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
    }
}
