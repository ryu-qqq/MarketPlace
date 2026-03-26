package com.ryuqq.marketplace.domain.settlement.vo;

import java.util.EnumSet;
import java.util.Set;

/**
 * 정산 상태.
 *
 * <p>CALCULATING → CONFIRMED → PAYOUT_REQUESTED → COMPLETED. CALCULATING/CONFIRMED ↔ HOLD (보류/해제).
 */
public enum SettlementStatus {

    /** 집계 중. */
    CALCULATING,

    /** 확정 (집계 완료). */
    CONFIRMED,

    /** 지급 요청됨. */
    PAYOUT_REQUESTED,

    /** 완료. */
    COMPLETED,

    /** 보류. */
    HOLD;

    private static final Set<SettlementStatus> CONFIRMABLE = EnumSet.of(CALCULATING);
    private static final Set<SettlementStatus> PAYOUT_REQUESTABLE = EnumSet.of(CONFIRMED);
    private static final Set<SettlementStatus> COMPLETABLE = EnumSet.of(PAYOUT_REQUESTED);
    private static final Set<SettlementStatus> HOLDABLE = EnumSet.of(CALCULATING, CONFIRMED);
    private static final Set<SettlementStatus> RELEASABLE_TO_CALCULATING = EnumSet.of(HOLD);

    public boolean canTransitionTo(SettlementStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<SettlementStatus> getAllowedFrom(SettlementStatus target) {
        return switch (target) {
            case CONFIRMED -> CONFIRMABLE;
            case PAYOUT_REQUESTED -> PAYOUT_REQUESTABLE;
            case COMPLETED -> COMPLETABLE;
            case HOLD -> HOLDABLE;
            case CALCULATING -> RELEASABLE_TO_CALCULATING;
        };
    }
}
