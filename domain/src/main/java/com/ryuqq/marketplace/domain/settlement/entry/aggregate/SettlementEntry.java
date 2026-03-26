package com.ryuqq.marketplace.domain.settlement.entry.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.entry.event.SettlementEntryConfirmedEvent;
import com.ryuqq.marketplace.domain.settlement.entry.event.SettlementEntryCreatedEvent;
import com.ryuqq.marketplace.domain.settlement.entry.event.SettlementEntryStatusChangedEvent;
import com.ryuqq.marketplace.domain.settlement.entry.exception.SettlementEntryErrorCode;
import com.ryuqq.marketplace.domain.settlement.entry.exception.SettlementEntryException;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryAmounts;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntrySourceReference;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 정산 원장 Aggregate Root.
 *
 * <p>개별 거래(구매확정, 취소, 환불, 교환)에 대한 정산 원장 기록. Entry에는 항상 양수 금액 저장, EntryType으로 방향(매출/역분개)을 표현합니다.
 */
public class SettlementEntry {

    private final SettlementEntryId id;
    private final long sellerId;
    private final EntryType entryType;
    private EntryStatus status;
    private final EntryAmounts amounts;
    private final EntrySourceReference source;
    private final SettlementEntryId reversalOfEntryId;
    private SettlementId settlementId;
    private final Instant eligibleAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> events = new ArrayList<>();

    private SettlementEntry(
            SettlementEntryId id,
            long sellerId,
            EntryType entryType,
            EntryStatus status,
            EntryAmounts amounts,
            EntrySourceReference source,
            SettlementEntryId reversalOfEntryId,
            SettlementId settlementId,
            Instant eligibleAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.entryType = entryType;
        this.status = status;
        this.amounts = amounts;
        this.source = source;
        this.reversalOfEntryId = reversalOfEntryId;
        this.settlementId = settlementId;
        this.eligibleAt = eligibleAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 판매 Entry 생성 (구매확정 시). */
    public static SettlementEntry forSales(
            SettlementEntryId id,
            long sellerId,
            EntryAmounts amounts,
            EntrySourceReference source,
            Instant eligibleAt,
            Instant now) {
        SettlementEntry entry =
                new SettlementEntry(
                        id,
                        sellerId,
                        EntryType.SALES,
                        EntryStatus.PENDING,
                        amounts,
                        source,
                        null,
                        null,
                        eligibleAt,
                        now,
                        now);
        entry.registerEvent(
                new SettlementEntryCreatedEvent(
                        id, sellerId, EntryType.SALES, source.orderItemId(), now));
        return entry;
    }

    /** 역분개 Entry 생성 (클레임 완료 시). */
    public static SettlementEntry forReversal(
            SettlementEntryId id,
            long sellerId,
            EntryType entryType,
            EntryAmounts amounts,
            EntrySourceReference source,
            SettlementEntryId reversalOfEntryId,
            Instant now) {
        SettlementEntry entry =
                new SettlementEntry(
                        id,
                        sellerId,
                        entryType,
                        EntryStatus.PENDING,
                        amounts,
                        source,
                        reversalOfEntryId,
                        null,
                        now,
                        now,
                        now);
        entry.registerEvent(
                new SettlementEntryCreatedEvent(
                        id, sellerId, entryType, source.orderItemId(), now));
        return entry;
    }

    /** 수동 조정 Entry 생성. */
    public static SettlementEntry forAdjustment(
            SettlementEntryId id,
            long sellerId,
            EntryAmounts amounts,
            EntrySourceReference source,
            Instant now) {
        SettlementEntry entry =
                new SettlementEntry(
                        id,
                        sellerId,
                        EntryType.ADJUSTMENT,
                        EntryStatus.PENDING,
                        amounts,
                        source,
                        null,
                        null,
                        now,
                        now,
                        now);
        entry.registerEvent(
                new SettlementEntryCreatedEvent(
                        id, sellerId, EntryType.ADJUSTMENT, source.orderItemId(), now));
        return entry;
    }

    /** DB 복원용. */
    public static SettlementEntry reconstitute(
            SettlementEntryId id,
            long sellerId,
            EntryType entryType,
            EntryStatus status,
            EntryAmounts amounts,
            EntrySourceReference source,
            SettlementEntryId reversalOfEntryId,
            SettlementId settlementId,
            Instant eligibleAt,
            Instant createdAt,
            Instant updatedAt) {
        return new SettlementEntry(
                id,
                sellerId,
                entryType,
                status,
                amounts,
                source,
                reversalOfEntryId,
                settlementId,
                eligibleAt,
                createdAt,
                updatedAt);
    }

    /** PENDING → CONFIRMED. eligibleAt 이후에만 호출 가능. */
    public void confirm(Instant now) {
        EntryStatus from = this.status;
        validateTransition(EntryStatus.CONFIRMED);
        this.status = EntryStatus.CONFIRMED;
        this.updatedAt = now;
        registerEvent(new SettlementEntryConfirmedEvent(id, sellerId, now));
        registerEvent(new SettlementEntryStatusChangedEvent(id, from, EntryStatus.CONFIRMED, now));
    }

    /** PENDING → HOLD. 보류 처리. */
    public void hold(Instant now) {
        EntryStatus from = this.status;
        validateTransition(EntryStatus.HOLD);
        this.status = EntryStatus.HOLD;
        this.updatedAt = now;
        registerEvent(new SettlementEntryStatusChangedEvent(id, from, EntryStatus.HOLD, now));
    }

    /** HOLD → PENDING. 보류 해제. */
    public void releaseHold(Instant now) {
        EntryStatus from = this.status;
        validateTransition(EntryStatus.PENDING);
        this.status = EntryStatus.PENDING;
        this.updatedAt = now;
        registerEvent(new SettlementEntryStatusChangedEvent(id, from, EntryStatus.PENDING, now));
    }

    /** CONFIRMED → SETTLED. Settlement에 포함될 때 호출. */
    public void markSettled(SettlementId settlementId, Instant now) {
        EntryStatus from = this.status;
        validateTransition(EntryStatus.SETTLED);
        this.status = EntryStatus.SETTLED;
        this.settlementId = settlementId;
        this.updatedAt = now;
        registerEvent(new SettlementEntryStatusChangedEvent(id, from, EntryStatus.SETTLED, now));
    }

    private void validateTransition(EntryStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new SettlementEntryException(
                    SettlementEntryErrorCode.INVALID_STATUS_TRANSITION,
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

    public SettlementEntryId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public long sellerId() {
        return sellerId;
    }

    public EntryType entryType() {
        return entryType;
    }

    public EntryStatus status() {
        return status;
    }

    public EntryAmounts amounts() {
        return amounts;
    }

    public EntrySourceReference source() {
        return source;
    }

    public SettlementEntryId reversalOfEntryId() {
        return reversalOfEntryId;
    }

    public SettlementId settlementId() {
        return settlementId;
    }

    public Instant eligibleAt() {
        return eligibleAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
