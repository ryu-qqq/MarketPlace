package com.ryuqq.marketplace.domain.settlement.entry.vo;

import java.util.EnumSet;
import java.util.Set;

/**
 * 정산 원장 상태.
 *
 * <p>PENDING → CONFIRMED → SETTLED. PENDING ↔ HOLD (보류/해제).
 */
public enum EntryStatus {

    /** 대기 (확정 가능 시점 도래 전). */
    PENDING,

    /** 보류. */
    HOLD,

    /** 확정 (집계 대상). */
    CONFIRMED,

    /** 정산 완료 (Settlement에 포함됨). */
    SETTLED;

    private static final Set<EntryStatus> CONFIRMABLE = EnumSet.of(PENDING);
    private static final Set<EntryStatus> SETTLEABLE = EnumSet.of(CONFIRMED);
    private static final Set<EntryStatus> HOLDABLE = EnumSet.of(PENDING);
    private static final Set<EntryStatus> RELEASABLE = EnumSet.of(HOLD);

    public boolean canTransitionTo(EntryStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<EntryStatus> getAllowedFrom(EntryStatus target) {
        return switch (target) {
            case CONFIRMED -> CONFIRMABLE;
            case SETTLED -> SETTLEABLE;
            case HOLD -> HOLDABLE;
            case PENDING -> RELEASABLE;
        };
    }
}
