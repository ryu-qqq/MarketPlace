package com.ryuqq.marketplace.domain.exchange.aggregate;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCancelledEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeClaimCreatedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeClaimStatusChangedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCollectedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCollectingEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeCompletedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangePreparingEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeRejectedEvent;
import com.ryuqq.marketplace.domain.exchange.event.ExchangeShippingEvent;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeErrorCode;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeException;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 교환 클레임 Aggregate Root. ExchangeClaim 1건 = OrderItem 1건의 1회 교환 요청. */
public class ExchangeClaim {

    private final ExchangeClaimId id;
    private final ExchangeClaimNumber claimNumber;
    private final OrderItemId orderItemId;
    private final long sellerId;
    private final int exchangeQty;
    private ExchangeStatus status;
    private ExchangeReason reason;
    private ExchangeOption exchangeOption;
    private AmountAdjustment amountAdjustment;
    private final ClaimShipment collectShipment;
    private HoldInfo holdInfo;
    private String linkedOrderId;
    private final String requestedBy;
    private String processedBy;
    private final Instant requestedAt;
    private Instant processedAt;
    private Instant completedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private ExchangeClaim(
            ExchangeClaimId id,
            ExchangeClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int exchangeQty,
            ExchangeStatus status,
            ExchangeReason reason,
            ExchangeOption exchangeOption,
            AmountAdjustment amountAdjustment,
            ClaimShipment collectShipment,
            HoldInfo holdInfo,
            String linkedOrderId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.claimNumber = claimNumber;
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.exchangeQty = exchangeQty;
        this.status = status;
        this.reason = reason;
        this.exchangeOption = exchangeOption;
        this.amountAdjustment = amountAdjustment;
        this.collectShipment = collectShipment;
        this.holdInfo = holdInfo;
        this.linkedOrderId = linkedOrderId;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ExchangeClaim forNew(
            ExchangeClaimId id,
            ExchangeClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int exchangeQty,
            ExchangeReason reason,
            ExchangeOption exchangeOption,
            AmountAdjustment amountAdjustment,
            ClaimShipment collectShipment,
            String requestedBy,
            Instant now) {
        validateExchangeQty(exchangeQty);
        ExchangeClaim claim =
                new ExchangeClaim(
                        id,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        exchangeQty,
                        ExchangeStatus.REQUESTED,
                        reason,
                        exchangeOption,
                        amountAdjustment,
                        collectShipment,
                        null,
                        null,
                        requestedBy,
                        null,
                        now,
                        null,
                        null,
                        now,
                        now);
        claim.registerEvent(new ExchangeClaimCreatedEvent(id, orderItemId, now));
        return claim;
    }

    public static ExchangeClaim reconstitute(
            ExchangeClaimId id,
            ExchangeClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int exchangeQty,
            ExchangeStatus status,
            ExchangeReason reason,
            ExchangeOption exchangeOption,
            AmountAdjustment amountAdjustment,
            ClaimShipment collectShipment,
            HoldInfo holdInfo,
            String linkedOrderId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new ExchangeClaim(
                id,
                claimNumber,
                orderItemId,
                sellerId,
                exchangeQty,
                status,
                reason,
                exchangeOption,
                amountAdjustment,
                collectShipment,
                holdInfo,
                linkedOrderId,
                requestedBy,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                createdAt,
                updatedAt);
    }

    public void startCollecting(String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.COLLECTING);
        this.status = ExchangeStatus.COLLECTING;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new ExchangeCollectingEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.COLLECTING, now));
    }

    public void completeCollection(String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.COLLECTED);
        this.status = ExchangeStatus.COLLECTED;
        this.processedBy = processedBy;
        this.updatedAt = now;
        registerEvent(new ExchangeCollectedEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.COLLECTED, now));
    }

    public void startPreparing(String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.PREPARING);
        this.status = ExchangeStatus.PREPARING;
        this.processedBy = processedBy;
        this.updatedAt = now;
        registerEvent(new ExchangePreparingEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.PREPARING, now));
    }

    public void startShipping(String linkedOrderId, String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.SHIPPING);
        this.status = ExchangeStatus.SHIPPING;
        this.linkedOrderId = linkedOrderId;
        this.processedBy = processedBy;
        this.updatedAt = now;
        registerEvent(new ExchangeShippingEvent(id, orderItemId, linkedOrderId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.SHIPPING, now));
    }

    public void complete(String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.COMPLETED);
        this.status = ExchangeStatus.COMPLETED;
        this.processedBy = processedBy;
        this.completedAt = now;
        this.updatedAt = now;
        registerEvent(new ExchangeCompletedEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.COMPLETED, now));
    }

    public void reject(String processedBy, Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.REJECTED);
        this.status = ExchangeStatus.REJECTED;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new ExchangeRejectedEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.REJECTED, now));
    }

    public void cancel(Instant now) {
        ExchangeStatus from = this.status;
        validateTransition(ExchangeStatus.CANCELLED);
        this.status = ExchangeStatus.CANCELLED;
        this.updatedAt = now;
        registerEvent(new ExchangeCancelledEvent(id, orderItemId, now));
        registerEvent(
                new ExchangeClaimStatusChangedEvent(
                        id, orderItemId, from, ExchangeStatus.CANCELLED, now));
    }

    public void updateOption(
            ExchangeOption exchangeOption, AmountAdjustment amountAdjustment, Instant now) {
        if (this.status != ExchangeStatus.REQUESTED) {
            throw new ExchangeException(ExchangeErrorCode.TARGET_UPDATE_NOT_ALLOWED);
        }
        this.exchangeOption = exchangeOption;
        this.amountAdjustment = amountAdjustment;
        this.updatedAt = now;
    }

    public void hold(String holdReason, Instant now) {
        if (this.holdInfo != null) {
            throw new ExchangeException(ExchangeErrorCode.ALREADY_HOLD);
        }
        String reason = (holdReason != null && !holdReason.isBlank()) ? holdReason : "보류 처리";
        this.holdInfo = HoldInfo.of(reason, now);
        this.updatedAt = now;
    }

    public void releaseHold(Instant now) {
        if (this.holdInfo == null) {
            throw new ExchangeException(ExchangeErrorCode.NOT_HOLD_STATUS);
        }
        this.holdInfo = null;
        this.updatedAt = now;
    }

    public void updateReason(ExchangeReason reason, Instant now) {
        if (this.status != ExchangeStatus.REQUESTED) {
            throw new ExchangeException(ExchangeErrorCode.REASON_UPDATE_NOT_ALLOWED);
        }
        this.reason = reason;
        this.updatedAt = now;
    }

    private void validateTransition(ExchangeStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new ExchangeException(
                    ExchangeErrorCode.INVALID_STATUS_TRANSITION,
                    String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this.status, target));
        }
    }

    private static void validateExchangeQty(int exchangeQty) {
        if (exchangeQty <= 0) {
            throw new ExchangeException(
                    ExchangeErrorCode.INVALID_EXCHANGE_QTY, "교환 수량은 1 이상이어야 합니다");
        }
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    public boolean isHold() {
        return holdInfo != null;
    }

    public ExchangeClaimId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public ExchangeClaimNumber claimNumber() {
        return claimNumber;
    }

    public String claimNumberValue() {
        return claimNumber.value();
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public String orderItemIdValue() {
        return orderItemId.value();
    }

    public long sellerId() {
        return sellerId;
    }

    public int exchangeQty() {
        return exchangeQty;
    }

    public ExchangeStatus status() {
        return status;
    }

    public ExchangeReason reason() {
        return reason;
    }

    public ExchangeOption exchangeOption() {
        return exchangeOption;
    }

    public AmountAdjustment amountAdjustment() {
        return amountAdjustment;
    }

    public ClaimShipment collectShipment() {
        return collectShipment;
    }

    public HoldInfo holdInfo() {
        return holdInfo;
    }

    public String linkedOrderId() {
        return linkedOrderId;
    }

    public String requestedBy() {
        return requestedBy;
    }

    public String processedBy() {
        return processedBy;
    }

    public Instant requestedAt() {
        return requestedAt;
    }

    public Instant processedAt() {
        return processedAt;
    }

    public Instant completedAt() {
        return completedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
