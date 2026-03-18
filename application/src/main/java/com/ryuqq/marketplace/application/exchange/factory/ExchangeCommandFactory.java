package com.ryuqq.marketplace.application.exchange.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
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

    public ExchangeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 교환 요청 ExchangeClaim 생성 (Outbox 없음 — 네이버에 호출할 API 없음). */
    public ExchangeClaim createExchangeRequest(
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

        return ExchangeClaim.forNew(
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
    }

    /** 수거 완료 시 ExchangeOutbox 생성. */
    public ExchangeOutbox createCollectOutbox(ExchangeClaim claim) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxPayloadBuilder.collectPayload(claim.idValue()),
                now);
    }

    /** 재배송 시 ExchangeOutbox 생성. */
    public ExchangeOutbox createShipOutbox(
            ExchangeClaim claim, String deliveryCompany, String trackingNumber) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.SHIP,
                ExchangeOutboxPayloadBuilder.shipPayload(
                        claim.idValue(), deliveryCompany, trackingNumber),
                now);
    }

    /** 거절 시 ExchangeOutbox 생성. */
    public ExchangeOutbox createRejectOutbox(ExchangeClaim claim) {
        Instant now = timeProvider.now();
        return ExchangeOutbox.forNew(
                claim.orderItemId(),
                ExchangeOutboxType.REJECT,
                ExchangeOutboxPayloadBuilder.rejectPayload(claim.idValue()),
                now);
    }

    public Instant now() {
        return timeProvider.now();
    }

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
    }
}
