package com.ryuqq.marketplace.domain.refund.aggregate;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.event.RefundCancelledEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundClaimCreatedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundClaimStatusChangedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCollectedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCollectingEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundCompletedEvent;
import com.ryuqq.marketplace.domain.refund.event.RefundRejectedEvent;
import com.ryuqq.marketplace.domain.refund.exception.RefundErrorCode;
import com.ryuqq.marketplace.domain.refund.exception.RefundException;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 환불 클레임 Aggregate Root. RefundClaim 1건 = OrderItem 1건의 1회 환불 요청. */
public class RefundClaim {

    private final RefundClaimId id;
    private final RefundClaimNumber claimNumber;
    private final OrderItemId orderItemId;
    private final long sellerId;
    private final int refundQty;
    private RefundStatus status;
    private RefundReason reason;
    private RefundInfo refundInfo;
    private ClaimShipment collectShipment;
    private HoldInfo holdInfo;
    private final String requestedBy;
    private String processedBy;
    private final Instant requestedAt;
    private Instant processedAt;
    private Instant completedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private RefundClaim(
            RefundClaimId id,
            RefundClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int refundQty,
            RefundStatus status,
            RefundReason reason,
            RefundInfo refundInfo,
            ClaimShipment collectShipment,
            HoldInfo holdInfo,
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
        this.refundQty = refundQty;
        this.status = status;
        this.reason = reason;
        this.refundInfo = refundInfo;
        this.collectShipment = collectShipment;
        this.holdInfo = holdInfo;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RefundClaim forNew(
            RefundClaimId id,
            RefundClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int refundQty,
            RefundReason reason,
            String requestedBy,
            Instant now) {
        validateRefundQty(refundQty);
        RefundClaim claim =
                new RefundClaim(
                        id,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        refundQty,
                        RefundStatus.REQUESTED,
                        reason,
                        null,
                        null,
                        null,
                        requestedBy,
                        null,
                        now,
                        null,
                        null,
                        now,
                        now);
        claim.registerEvent(new RefundClaimCreatedEvent(id, orderItemId, now));
        return claim;
    }

    public static RefundClaim reconstitute(
            RefundClaimId id,
            RefundClaimNumber claimNumber,
            OrderItemId orderItemId,
            long sellerId,
            int refundQty,
            RefundStatus status,
            RefundReason reason,
            RefundInfo refundInfo,
            ClaimShipment collectShipment,
            HoldInfo holdInfo,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new RefundClaim(
                id,
                claimNumber,
                orderItemId,
                sellerId,
                refundQty,
                status,
                reason,
                refundInfo,
                collectShipment,
                holdInfo,
                requestedBy,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                createdAt,
                updatedAt);
    }

    public void startCollecting(String processedBy, Instant now) {
        RefundStatus from = this.status;
        validateTransition(RefundStatus.COLLECTING);
        this.status = RefundStatus.COLLECTING;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new RefundCollectingEvent(id, orderItemId, now));
        registerEvent(
                new RefundClaimStatusChangedEvent(
                        id, orderItemId, from, RefundStatus.COLLECTING, now));
    }

    public void completeCollection(String processedBy, Instant now) {
        RefundStatus from = this.status;
        validateTransition(RefundStatus.COLLECTED);
        this.status = RefundStatus.COLLECTED;
        this.processedBy = processedBy;
        this.updatedAt = now;
        registerEvent(new RefundCollectedEvent(id, orderItemId, now));
        registerEvent(
                new RefundClaimStatusChangedEvent(
                        id, orderItemId, from, RefundStatus.COLLECTED, now));
    }

    public void complete(RefundInfo refundInfo, String processedBy, Instant now) {
        RefundStatus from = this.status;
        validateTransition(RefundStatus.COMPLETED);
        this.status = RefundStatus.COMPLETED;
        this.refundInfo = refundInfo;
        this.processedBy = processedBy;
        this.completedAt = now;
        this.updatedAt = now;
        registerEvent(new RefundCompletedEvent(id, orderItemId, now));
        registerEvent(
                new RefundClaimStatusChangedEvent(
                        id, orderItemId, from, RefundStatus.COMPLETED, now));
    }

    public void reject(String processedBy, Instant now) {
        RefundStatus from = this.status;
        validateTransition(RefundStatus.REJECTED);
        this.status = RefundStatus.REJECTED;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new RefundRejectedEvent(id, orderItemId, now));
        registerEvent(
                new RefundClaimStatusChangedEvent(
                        id, orderItemId, from, RefundStatus.REJECTED, now));
    }

    public void cancel(Instant now) {
        RefundStatus from = this.status;
        validateTransition(RefundStatus.CANCELLED);
        this.status = RefundStatus.CANCELLED;
        this.updatedAt = now;
        registerEvent(new RefundCancelledEvent(id, orderItemId, now));
        registerEvent(
                new RefundClaimStatusChangedEvent(
                        id, orderItemId, from, RefundStatus.CANCELLED, now));
    }

    public void hold(String holdReason, Instant now) {
        if (this.holdInfo != null) {
            throw new RefundException(RefundErrorCode.ALREADY_HOLD);
        }
        String reason = (holdReason != null && !holdReason.isBlank()) ? holdReason : "보류 처리";
        this.holdInfo = HoldInfo.of(reason, now);
        this.updatedAt = now;
    }

    public void releaseHold(Instant now) {
        if (this.holdInfo == null) {
            throw new RefundException(RefundErrorCode.NOT_HOLD_STATUS);
        }
        this.holdInfo = null;
        this.updatedAt = now;
    }

    public void updateReason(RefundReason reason, Instant now) {
        if (this.status != RefundStatus.REQUESTED) {
            throw new RefundException(RefundErrorCode.REASON_UPDATE_NOT_ALLOWED);
        }
        this.reason = reason;
        this.updatedAt = now;
    }

    private void validateTransition(RefundStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new RefundException(
                    RefundErrorCode.INVALID_STATUS_TRANSITION,
                    String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this.status, target));
        }
    }

    private static void validateRefundQty(int refundQty) {
        if (refundQty <= 0) {
            throw new RefundException(RefundErrorCode.INVALID_REFUND_QTY, "환불 수량은 1 이상이어야 합니다");
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

    public RefundClaimId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public RefundClaimNumber claimNumber() {
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

    public int refundQty() {
        return refundQty;
    }

    public RefundStatus status() {
        return status;
    }

    public RefundReason reason() {
        return reason;
    }

    public RefundInfo refundInfo() {
        return refundInfo;
    }

    public ClaimShipment collectShipment() {
        return collectShipment;
    }

    public HoldInfo holdInfo() {
        return holdInfo;
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
