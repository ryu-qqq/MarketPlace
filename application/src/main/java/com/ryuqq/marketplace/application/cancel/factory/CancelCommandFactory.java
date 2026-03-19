package com.ryuqq.marketplace.application.cancel.factory;

import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Cancel 도메인 객체 생성 팩토리. */
@Component
public class CancelCommandFactory {

    private final TimeProvider timeProvider;
    private final ClaimHistoryFactory historyFactory;

    public CancelCommandFactory(TimeProvider timeProvider, ClaimHistoryFactory historyFactory) {
        this.timeProvider = timeProvider;
        this.historyFactory = historyFactory;
    }

    /** 판매자 취소 Cancel + CancelOutbox + ClaimHistory 생성. */
    public CancelBundle createSellerCancel(
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

        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.CANCEL,
                        cancelId.value(),
                        null,
                        "REQUESTED",
                        requestedBy,
                        requestedBy);

        return new CancelBundle(cancel, outbox, history, now);
    }

    /** 승인 시 CancelOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createApproveBundle(Cancel cancel, String processedBy) {
        Instant now = timeProvider.now();
        CancelOutbox outbox =
                CancelOutbox.forNew(
                        cancel.orderItemId(),
                        CancelOutboxType.APPROVE,
                        CancelOutboxPayloadBuilder.approvePayload(cancel.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.CANCEL,
                        cancel.idValue(),
                        "REQUESTED",
                        "APPROVED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history, now);
    }

    /** 거절 시 CancelOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createRejectBundle(Cancel cancel, String processedBy) {
        Instant now = timeProvider.now();
        CancelOutbox outbox =
                CancelOutbox.forNew(
                        cancel.orderItemId(),
                        CancelOutboxType.REJECT,
                        CancelOutboxPayloadBuilder.rejectPayload(cancel.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.CANCEL,
                        cancel.idValue(),
                        "REQUESTED",
                        "REJECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history, now);
    }

    /** 현재 시간 반환. Outbox 상태 전환 등 Factory 외부에서 시간이 필요한 경우 사용. */
    public Instant now() {
        return timeProvider.now();
    }

    /** PENDING 아웃박스 조회 기준 시간 계산. */
    public Instant calculateBeforeTime(int delaySeconds) {
        return timeProvider.now().minusSeconds(delaySeconds);
    }

    /** PROCESSING 타임아웃 기준 시간 계산. */
    public Instant calculateTimeoutThreshold(long timeoutSeconds) {
        return timeProvider.now().minusSeconds(timeoutSeconds);
    }

    /** Cancel + CancelOutbox + ClaimHistory + 생성 시간 묶음 (판매자 취소용). */
    public record CancelBundle(
            Cancel cancel, CancelOutbox outbox, ClaimHistory history, Instant changedAt) {}

    /** CancelOutbox + ClaimHistory + 변경 시간 묶음 (승인/거절용). */
    public record OutboxWithHistory(CancelOutbox outbox, ClaimHistory history, Instant changedAt) {}

    /** 취소 아웃박스 페이로드 빌더. Map 기반 안전 직렬화. */
    static final class CancelOutboxPayloadBuilder {

        private CancelOutboxPayloadBuilder() {}

        static String sellerCancelPayload(String cancelId, int cancelQty) {
            return mapToJson(Map.of("cancelId", cancelId, "cancelQty", cancelQty));
        }

        static String approvePayload(String cancelId) {
            return mapToJson(Map.of("cancelId", cancelId, "action", "APPROVE"));
        }

        static String rejectPayload(String cancelId) {
            return mapToJson(Map.of("cancelId", cancelId, "action", "REJECT"));
        }

        private static String mapToJson(Map<String, Object> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(entry.getKey())).append("\":");
                Object value = entry.getValue();
                if (value instanceof String s) {
                    sb.append("\"").append(escapeJson(s)).append("\"");
                } else {
                    sb.append(value);
                }
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }

        private static String escapeJson(String value) {
            return value.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
