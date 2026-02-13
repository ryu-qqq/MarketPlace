package com.ryuqq.marketplace.domain.settlement.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementCompletedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementCreatedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementHeldEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementReleasedEvent;
import com.ryuqq.marketplace.domain.settlement.event.SettlementStatusChangedEvent;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementErrorCode;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementException;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.HoldInfo;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 정산 Aggregate Root. */
public class Settlement {

    private final SettlementId id;
    private final String orderId;
    private final long sellerId;
    private SettlementStatus status;
    private final SettlementAmounts amounts;
    private HoldInfo holdInfo;
    private final LocalDate expectedSettlementDay;
    private LocalDate settlementDay;
    private final Instant orderedAt;
    private final Instant deliveredAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private Settlement(
            SettlementId id,
            String orderId,
            long sellerId,
            SettlementStatus status,
            SettlementAmounts amounts,
            HoldInfo holdInfo,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant orderedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.status = status;
        this.amounts = amounts;
        this.holdInfo = holdInfo;
        this.expectedSettlementDay = expectedSettlementDay;
        this.settlementDay = settlementDay;
        this.orderedAt = orderedAt;
        this.deliveredAt = deliveredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Settlement forNew(
            SettlementId id,
            String orderId,
            long sellerId,
            SettlementAmounts amounts,
            LocalDate expectedSettlementDay,
            Instant orderedAt,
            Instant deliveredAt,
            Instant now) {
        Settlement settlement =
                new Settlement(
                        id,
                        orderId,
                        sellerId,
                        SettlementStatus.PENDING,
                        amounts,
                        null,
                        expectedSettlementDay,
                        null,
                        orderedAt,
                        deliveredAt,
                        now,
                        now);
        settlement.registerEvent(new SettlementCreatedEvent(id, orderId, now));
        return settlement;
    }

    public static Settlement reconstitute(
            SettlementId id,
            String orderId,
            long sellerId,
            SettlementStatus status,
            SettlementAmounts amounts,
            HoldInfo holdInfo,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant orderedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt) {
        return new Settlement(
                id,
                orderId,
                sellerId,
                status,
                amounts,
                holdInfo,
                expectedSettlementDay,
                settlementDay,
                orderedAt,
                deliveredAt,
                createdAt,
                updatedAt);
    }

    public void complete(LocalDate settlementDay, Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.COMPLETED);
        this.status = SettlementStatus.COMPLETED;
        this.settlementDay = settlementDay;
        this.updatedAt = now;
        registerEvent(new SettlementCompletedEvent(id, orderId, sellerId, now));
        registerEvent(
                new SettlementStatusChangedEvent(
                        id, orderId, from, SettlementStatus.COMPLETED, now));
    }

    public void hold(String reason, Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.HOLD);
        if (reason == null || reason.isBlank()) {
            throw new SettlementException(SettlementErrorCode.HOLD_REASON_REQUIRED);
        }
        this.status = SettlementStatus.HOLD;
        this.holdInfo = HoldInfo.of(reason, now);
        this.updatedAt = now;
        registerEvent(new SettlementHeldEvent(id, orderId, sellerId, reason, now));
        registerEvent(
                new SettlementStatusChangedEvent(id, orderId, from, SettlementStatus.HOLD, now));
    }

    public void releaseHold(Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.PENDING);
        this.status = SettlementStatus.PENDING;
        this.holdInfo = null;
        this.updatedAt = now;
        registerEvent(new SettlementReleasedEvent(id, orderId, sellerId, now));
        registerEvent(
                new SettlementStatusChangedEvent(id, orderId, from, SettlementStatus.PENDING, now));
    }

    private void validateTransition(SettlementStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new SettlementException(
                    SettlementErrorCode.INVALID_STATUS_TRANSITION,
                    String.format("%s 상태에서 %s 상태로 변경할 수 없습니다", this.status, target));
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

    public SettlementId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public String orderId() {
        return orderId;
    }

    public long sellerId() {
        return sellerId;
    }

    public SettlementStatus status() {
        return status;
    }

    public SettlementAmounts amounts() {
        return amounts;
    }

    public HoldInfo holdInfo() {
        return holdInfo;
    }

    public boolean isHold() {
        return holdInfo != null;
    }

    public LocalDate expectedSettlementDay() {
        return expectedSettlementDay;
    }

    public LocalDate settlementDay() {
        return settlementDay;
    }

    public Instant orderedAt() {
        return orderedAt;
    }

    public Instant deliveredAt() {
        return deliveredAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
