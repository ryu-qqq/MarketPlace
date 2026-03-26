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
import com.ryuqq.marketplace.domain.settlement.vo.SettlementPeriod;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 정산 Aggregate Root.
 *
 * <p>셀러×정산주기별 N건의 Entry를 집계한 정산 단위.
 */
public class Settlement {

    private final SettlementId id;
    private final long sellerId;
    private SettlementStatus status;
    private final SettlementPeriod period;
    private SettlementAmounts amounts;
    private int entryCount;
    private HoldInfo holdInfo;
    private final LocalDate expectedSettlementDay;
    private LocalDate settlementDay;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private Settlement(
            SettlementId id,
            long sellerId,
            SettlementStatus status,
            SettlementPeriod period,
            SettlementAmounts amounts,
            int entryCount,
            HoldInfo holdInfo,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.status = status;
        this.period = period;
        this.amounts = amounts;
        this.entryCount = entryCount;
        this.holdInfo = holdInfo;
        this.expectedSettlementDay = expectedSettlementDay;
        this.settlementDay = settlementDay;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Entry 집계로 새 Settlement 생성 (CALCULATING 상태). */
    public static Settlement forNew(
            SettlementId id,
            long sellerId,
            SettlementPeriod period,
            SettlementAmounts amounts,
            int entryCount,
            LocalDate expectedSettlementDay,
            Instant now) {
        Settlement settlement =
                new Settlement(
                        id,
                        sellerId,
                        SettlementStatus.CALCULATING,
                        period,
                        amounts,
                        entryCount,
                        null,
                        expectedSettlementDay,
                        null,
                        now,
                        now);
        settlement.registerEvent(new SettlementCreatedEvent(id, sellerId, now));
        return settlement;
    }

    /** DB 복원용. */
    public static Settlement reconstitute(
            SettlementId id,
            long sellerId,
            SettlementStatus status,
            SettlementPeriod period,
            SettlementAmounts amounts,
            int entryCount,
            HoldInfo holdInfo,
            LocalDate expectedSettlementDay,
            LocalDate settlementDay,
            Instant createdAt,
            Instant updatedAt) {
        return new Settlement(
                id,
                sellerId,
                status,
                period,
                amounts,
                entryCount,
                holdInfo,
                expectedSettlementDay,
                settlementDay,
                createdAt,
                updatedAt);
    }

    /** CALCULATING → CONFIRMED. */
    public void confirm(Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.CONFIRMED);
        this.status = SettlementStatus.CONFIRMED;
        this.updatedAt = now;
        registerEvent(
                new SettlementStatusChangedEvent(
                        id, sellerId, from, SettlementStatus.CONFIRMED, now));
    }

    /** CONFIRMED → PAYOUT_REQUESTED. */
    public void requestPayout(Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.PAYOUT_REQUESTED);
        this.status = SettlementStatus.PAYOUT_REQUESTED;
        this.updatedAt = now;
        registerEvent(
                new SettlementStatusChangedEvent(
                        id, sellerId, from, SettlementStatus.PAYOUT_REQUESTED, now));
    }

    /** PAYOUT_REQUESTED → COMPLETED. */
    public void complete(LocalDate settlementDay, Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.COMPLETED);
        this.status = SettlementStatus.COMPLETED;
        this.settlementDay = settlementDay;
        this.updatedAt = now;
        registerEvent(new SettlementCompletedEvent(id, sellerId, now));
        registerEvent(
                new SettlementStatusChangedEvent(
                        id, sellerId, from, SettlementStatus.COMPLETED, now));
    }

    /** CALCULATING/CONFIRMED → HOLD. */
    public void hold(String reason, Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.HOLD);
        if (reason == null || reason.isBlank()) {
            throw new SettlementException(SettlementErrorCode.HOLD_REASON_REQUIRED);
        }
        this.status = SettlementStatus.HOLD;
        this.holdInfo = HoldInfo.of(reason, now);
        this.updatedAt = now;
        registerEvent(new SettlementHeldEvent(id, sellerId, reason, now));
        registerEvent(
                new SettlementStatusChangedEvent(id, sellerId, from, SettlementStatus.HOLD, now));
    }

    /** HOLD → CALCULATING. */
    public void releaseHold(Instant now) {
        SettlementStatus from = this.status;
        validateTransition(SettlementStatus.CALCULATING);
        this.status = SettlementStatus.CALCULATING;
        this.holdInfo = null;
        this.updatedAt = now;
        registerEvent(new SettlementReleasedEvent(id, sellerId, now));
        registerEvent(
                new SettlementStatusChangedEvent(
                        id, sellerId, from, SettlementStatus.CALCULATING, now));
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

    // --- Accessors ---

    public SettlementId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public long sellerId() {
        return sellerId;
    }

    public SettlementStatus status() {
        return status;
    }

    public SettlementPeriod period() {
        return period;
    }

    public SettlementAmounts amounts() {
        return amounts;
    }

    public int entryCount() {
        return entryCount;
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

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
