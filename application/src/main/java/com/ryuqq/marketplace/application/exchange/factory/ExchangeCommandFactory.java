package com.ryuqq.marketplace.application.exchange.factory;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** ExchangeClaim 도메인 객체 생성 팩토리. */
@Component
public class ExchangeCommandFactory {

    private final TimeProvider timeProvider;
    private final ClaimHistoryFactory historyFactory;

    public ExchangeCommandFactory(TimeProvider timeProvider, ClaimHistoryFactory historyFactory) {
        this.timeProvider = timeProvider;
        this.historyFactory = historyFactory;
    }

    /** 교환 요청 ExchangeClaim + ClaimHistory 생성 (Outbox 없음 — 네이버에 호출할 API 없음). */
    public ExchangeClaimWithHistory createExchangeRequest(
            ExchangeRequestItem item, String requestedBy, long sellerId) {
        Instant now = timeProvider.now();
        ExchangeClaimId claimId = ExchangeClaimId.forNew(UUID.randomUUID().toString());
        ExchangeClaimNumber claimNumber = ExchangeClaimNumber.generate();
        OrderItemId orderItemId = OrderItemId.of(item.orderItemId());

        ExchangeOption exchangeOption =
                new ExchangeOption(
                        item.originalProductId(),
                        item.originalSkuCode(),
                        item.targetProductGroupId(),
                        item.targetProductId(),
                        item.targetSkuCode(),
                        item.targetQuantity());

        ExchangeClaim claim =
                ExchangeClaim.forNew(
                        claimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        item.exchangeQty(),
                        new ExchangeReason(item.reasonType(), item.reasonDetail()),
                        exchangeOption,
                        null,
                        null,
                        requestedBy,
                        now);

        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claimId.value(),
                        null,
                        "REQUESTED",
                        requestedBy,
                        requestedBy);

        return new ExchangeClaimWithHistory(claim, history);
    }

    /** 승인 시 ClaimHistory 생성 (Outbox 없음). */
    public ClaimHistory createApproveHistory(ExchangeClaim claim, String processedBy) {
        return historyFactory.createStatusChange(
                ClaimType.EXCHANGE,
                claim.idValue(),
                "REQUESTED",
                "COLLECTING",
                processedBy,
                processedBy);
    }

    /** 수거 완료 시 ExchangeOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createCollectBundle(ExchangeClaim claim, String processedBy) {
        ExchangeOutbox outbox = createCollectOutbox(claim);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claim.idValue(),
                        "COLLECTING",
                        "COLLECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 준비 완료 시 ClaimHistory 생성 (Outbox 없음). */
    public ClaimHistory createPrepareHistory(ExchangeClaim claim, String processedBy) {
        return historyFactory.createStatusChange(
                ClaimType.EXCHANGE,
                claim.idValue(),
                "COLLECTED",
                "PREPARING",
                processedBy,
                processedBy);
    }

    /** 재배송 시 ExchangeOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createShipBundle(
            ExchangeClaim claim, String deliveryCompany, String trackingNumber, String processedBy) {
        ExchangeOutbox outbox = createShipOutbox(claim, deliveryCompany, trackingNumber);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claim.idValue(),
                        "PREPARING",
                        "SHIPPING",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 완료 시 ClaimHistory 생성 (Outbox 없음). */
    public ClaimHistory createCompleteHistory(ExchangeClaim claim, String processedBy) {
        return historyFactory.createStatusChange(
                ClaimType.EXCHANGE,
                claim.idValue(),
                "SHIPPING",
                "COMPLETED",
                processedBy,
                processedBy);
    }

    /** 거절 시 ExchangeOutbox + ClaimHistory 생성 (fromStatus 동적). */
    public OutboxWithHistory createRejectBundle(
            ExchangeClaim claim, String fromStatus, String processedBy) {
        ExchangeOutbox outbox = createRejectOutbox(claim);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claim.idValue(),
                        fromStatus,
                        "REJECTED",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 시 ExchangeOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createHoldBundle(ExchangeClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        ExchangeOutbox outbox =
                ExchangeOutbox.forNew(
                        claim.orderItemId(),
                        ExchangeOutboxType.HOLD,
                        ExchangeOutboxPayloadBuilder.holdPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claim.idValue(),
                        claim.status().name(),
                        "HOLD",
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 보류 해제 시 ExchangeOutbox + ClaimHistory 생성. */
    public OutboxWithHistory createReleaseHoldBundle(ExchangeClaim claim, String processedBy) {
        Instant now = timeProvider.now();
        ExchangeOutbox outbox =
                ExchangeOutbox.forNew(
                        claim.orderItemId(),
                        ExchangeOutboxType.RELEASE_HOLD,
                        ExchangeOutboxPayloadBuilder.releaseHoldPayload(claim.idValue()),
                        now);
        ClaimHistory history =
                historyFactory.createStatusChange(
                        ClaimType.EXCHANGE,
                        claim.idValue(),
                        "HOLD",
                        claim.status().name(),
                        processedBy,
                        processedBy);
        return new OutboxWithHistory(outbox, history);
    }

    /** 환불 전환 시 ClaimHistory 생성 (fromStatus 동적, Outbox 없음). */
    public ClaimHistory createConvertToRefundHistory(
            ExchangeClaim claim, String fromStatus, String processedBy) {
        return historyFactory.createStatusChange(
                ClaimType.EXCHANGE,
                claim.idValue(),
                fromStatus,
                "CANCELLED",
                processedBy,
                processedBy);
    }

    public Instant now() {
        return timeProvider.now();
    }

    private ExchangeOutbox createCollectOutbox(ExchangeClaim claim) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxPayloadBuilder.collectPayload(claim.idValue()),
                now);
    }

    private ExchangeOutbox createShipOutbox(
            ExchangeClaim claim, String deliveryCompany, String trackingNumber) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.SHIP,
                ExchangeOutboxPayloadBuilder.shipPayload(
                        claim.idValue(), deliveryCompany, trackingNumber),
                now);
    }

    private ExchangeOutbox createRejectOutbox(ExchangeClaim claim) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.REJECT,
                ExchangeOutboxPayloadBuilder.rejectPayload(claim.idValue()),
                now);
    }

    /** ExchangeClaim + ClaimHistory 묶음 (교환 요청용). */
    public record ExchangeClaimWithHistory(ExchangeClaim claim, ClaimHistory history) {}

    /** ExchangeOutbox + ClaimHistory 묶음 (수거/재배송/거절용). */
    public record OutboxWithHistory(ExchangeOutbox outbox, ClaimHistory history) {}

    /** 교환 아웃박스 페이로드 빌더. */
    private static final class ExchangeOutboxPayloadBuilder {

        private ExchangeOutboxPayloadBuilder() {}

        static String collectPayload(String exchangeClaimId) {
            return "{\"exchangeClaimId\":\"" + exchangeClaimId + "\",\"action\":\"COLLECT\"}";
        }

        static String shipPayload(
                String exchangeClaimId, String deliveryCompany, String trackingNumber) {
            return "{\"exchangeClaimId\":\""
                    + exchangeClaimId
                    + "\",\"deliveryCompany\":\""
                    + deliveryCompany
                    + "\",\"trackingNumber\":\""
                    + trackingNumber
                    + "\"}";
        }

        static String rejectPayload(String exchangeClaimId) {
            return "{\"exchangeClaimId\":\"" + exchangeClaimId + "\",\"action\":\"REJECT\"}";
        }

        static String holdPayload(String exchangeClaimId) {
            return "{\"exchangeClaimId\":\"" + exchangeClaimId + "\",\"action\":\"HOLD\"}";
        }

        static String releaseHoldPayload(String exchangeClaimId) {
            return "{\"exchangeClaimId\":\"" + exchangeClaimId + "\",\"action\":\"RELEASE_HOLD\"}";
        }
    }
}
