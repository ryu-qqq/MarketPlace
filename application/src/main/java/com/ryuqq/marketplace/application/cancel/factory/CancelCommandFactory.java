package com.ryuqq.marketplace.application.cancel.factory;

import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** Cancel 도메인 객체 생성 팩토리. */
@Component
public class CancelCommandFactory {

    private final TimeProvider timeProvider;

    public CancelCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 판매자 취소 Cancel + CancelOutbox 생성. */
    public CancelWithOutbox createSellerCancel(
            SellerCancelItem item, String requestedBy, long sellerId) {
        Instant now = timeProvider.now();
        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();

        OrderItemId orderItemId = OrderItemId.of(item.orderItemId());

        Cancel cancel =
                Cancel.forSellerCancel(
                        cancelId,
                        cancelNumber,
                        orderItemId,
                        sellerId,
                        item.cancelQty(),
                        new CancelReason(item.reasonType(), item.reasonDetail()),
                        requestedBy,
                        now);

        CancelOutbox outbox =
                CancelOutbox.forNew(
                        orderItemId,
                        CancelOutboxType.SELLER_CANCEL,
                        CancelOutboxPayloadBuilder.sellerCancelPayload(
                                cancelId.value(), item.cancelQty()),
                        now);

        return new CancelWithOutbox(cancel, outbox);
    }

    /** 승인 시 CancelOutbox 생성. */
    public CancelOutbox createApproveOutbox(Cancel cancel) {
        Instant now = timeProvider.now();
        return CancelOutbox.forNew(
                cancel.orderItemId(),
                CancelOutboxType.APPROVE,
                CancelOutboxPayloadBuilder.approvePayload(cancel.idValue()),
                now);
    }

    /** 거절 시 CancelOutbox 생성. */
    public CancelOutbox createRejectOutbox(Cancel cancel) {
        Instant now = timeProvider.now();
        return CancelOutbox.forNew(
                cancel.orderItemId(),
                CancelOutboxType.REJECT,
                CancelOutboxPayloadBuilder.rejectPayload(cancel.idValue()),
                now);
    }

    public Instant now() {
        return timeProvider.now();
    }

    /** Cancel + CancelOutbox 묶음. */
    public record CancelWithOutbox(Cancel cancel, CancelOutbox outbox) {}

    /** 취소 아웃박스 페이로드 빌더. */
    private static final class CancelOutboxPayloadBuilder {

        private CancelOutboxPayloadBuilder() {}

        static String sellerCancelPayload(String cancelId, int cancelQty) {
            return "{\"cancelId\":\"" + cancelId + "\",\"cancelQty\":" + cancelQty + "}";
        }

        static String approvePayload(String cancelId) {
            return "{\"cancelId\":\"" + cancelId + "\",\"action\":\"APPROVE\"}";
        }

        static String rejectPayload(String cancelId) {
            return "{\"cancelId\":\"" + cancelId + "\",\"action\":\"REJECT\"}";
        }
    }
}
