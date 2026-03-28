package com.ryuqq.marketplace.domain.cancel.aggregate;

import com.ryuqq.marketplace.domain.cancel.event.CancelApprovedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelCompletedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelCreatedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelRejectedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelStatusChangedEvent;
import com.ryuqq.marketplace.domain.cancel.event.CancelWithdrawnEvent;
import com.ryuqq.marketplace.domain.cancel.exception.CancelErrorCode;
import com.ryuqq.marketplace.domain.cancel.exception.CancelException;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 취소 Aggregate Root. Cancel 1건 = OrderItem 1건의 1회 취소 요청. */
public class Cancel {

    private final CancelId id;
    private final CancelNumber cancelNumber;
    private final OrderItemId orderItemId;
    private final long sellerId;
    private final int cancelQty;
    private final CancelType type;
    private CancelStatus status;
    private final CancelReason reason;
    private CancelRefundInfo refundInfo;
    private final String requestedBy;
    private String processedBy;
    private final Instant requestedAt;
    private Instant processedAt;
    private Instant completedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private Cancel(
            CancelId id,
            CancelNumber cancelNumber,
            OrderItemId orderItemId,
            long sellerId,
            int cancelQty,
            CancelType type,
            CancelStatus status,
            CancelReason reason,
            CancelRefundInfo refundInfo,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.cancelNumber = cancelNumber;
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.cancelQty = cancelQty;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.refundInfo = refundInfo;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Cancel forBuyerCancel(
            CancelId id,
            CancelNumber cancelNumber,
            OrderItemId orderItemId,
            long sellerId,
            int cancelQty,
            CancelReason reason,
            String requestedBy,
            Instant now) {
        validateCancelQty(cancelQty);
        Cancel cancel =
                new Cancel(
                        id,
                        cancelNumber,
                        orderItemId,
                        sellerId,
                        cancelQty,
                        CancelType.BUYER_CANCEL,
                        CancelStatus.REQUESTED,
                        reason,
                        null,
                        requestedBy,
                        null,
                        now,
                        null,
                        null,
                        now,
                        now);
        cancel.registerEvent(new CancelCreatedEvent(id, orderItemId, CancelType.BUYER_CANCEL, now));
        return cancel;
    }

    public static Cancel forSellerCancel(
            CancelId id,
            CancelNumber cancelNumber,
            OrderItemId orderItemId,
            long sellerId,
            int cancelQty,
            CancelReason reason,
            String requestedBy,
            Instant now) {
        validateCancelQty(cancelQty);
        Cancel cancel =
                new Cancel(
                        id,
                        cancelNumber,
                        orderItemId,
                        sellerId,
                        cancelQty,
                        CancelType.SELLER_CANCEL,
                        CancelStatus.APPROVED,
                        reason,
                        null,
                        requestedBy,
                        requestedBy,
                        now,
                        now,
                        null,
                        now,
                        now);
        cancel.registerEvent(
                new CancelCreatedEvent(id, orderItemId, CancelType.SELLER_CANCEL, now));
        cancel.registerEvent(
                new CancelStatusChangedEvent(
                        id, orderItemId, CancelStatus.REQUESTED, CancelStatus.APPROVED, now));
        return cancel;
    }

    public static Cancel reconstitute(
            CancelId id,
            CancelNumber cancelNumber,
            OrderItemId orderItemId,
            long sellerId,
            int cancelQty,
            CancelType type,
            CancelStatus status,
            CancelReason reason,
            CancelRefundInfo refundInfo,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new Cancel(
                id,
                cancelNumber,
                orderItemId,
                sellerId,
                cancelQty,
                type,
                status,
                reason,
                refundInfo,
                requestedBy,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                createdAt,
                updatedAt);
    }

    public void approve(String processedBy, Instant now) {
        CancelStatus from = this.status;
        validateTransition(CancelStatus.APPROVED);
        this.status = CancelStatus.APPROVED;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new CancelApprovedEvent(id, orderItemId, now));
        registerEvent(
                new CancelStatusChangedEvent(id, orderItemId, from, CancelStatus.APPROVED, now));
    }

    public void reject(String processedBy, Instant now) {
        CancelStatus from = this.status;
        validateTransition(CancelStatus.REJECTED);
        this.status = CancelStatus.REJECTED;
        this.processedBy = processedBy;
        this.processedAt = now;
        this.updatedAt = now;
        registerEvent(new CancelRejectedEvent(id, orderItemId, now));
        registerEvent(
                new CancelStatusChangedEvent(id, orderItemId, from, CancelStatus.REJECTED, now));
    }

    public void complete(CancelRefundInfo refundInfo, String processedBy, Instant now) {
        CancelStatus from = this.status;
        validateTransition(CancelStatus.COMPLETED);
        this.status = CancelStatus.COMPLETED;
        this.refundInfo = refundInfo;
        this.processedBy = processedBy;
        this.completedAt = now;
        this.updatedAt = now;
        registerEvent(new CancelCompletedEvent(id, orderItemId, now));
        registerEvent(
                new CancelStatusChangedEvent(id, orderItemId, from, CancelStatus.COMPLETED, now));
    }

    public void withdraw(Instant now) {
        CancelStatus from = this.status;
        validateTransition(CancelStatus.CANCELLED);
        this.status = CancelStatus.CANCELLED;
        this.updatedAt = now;
        registerEvent(new CancelWithdrawnEvent(id, orderItemId, now));
        registerEvent(
                new CancelStatusChangedEvent(id, orderItemId, from, CancelStatus.CANCELLED, now));
    }

    private void validateTransition(CancelStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new CancelException(
                    CancelErrorCode.INVALID_STATUS_TRANSITION,
                    String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this.status, target));
        }
    }

    private static void validateCancelQty(int cancelQty) {
        if (cancelQty <= 0) {
            throw new CancelException(CancelErrorCode.INVALID_CANCEL_QTY, "취소 수량은 1 이상이어야 합니다");
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

    public CancelId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public CancelNumber cancelNumber() {
        return cancelNumber;
    }

    public String cancelNumberValue() {
        return cancelNumber.value();
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public Long orderItemIdValue() {
        return orderItemId.value();
    }

    public long sellerId() {
        return sellerId;
    }

    public int cancelQty() {
        return cancelQty;
    }

    public CancelType type() {
        return type;
    }

    public CancelStatus status() {
        return status;
    }

    public CancelReason reason() {
        return reason;
    }

    public CancelRefundInfo refundInfo() {
        return refundInfo;
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
