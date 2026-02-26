package com.ryuqq.marketplace.domain.settlement.vo;

import java.util.EnumSet;
import java.util.Set;

/** 정산 상태. 상태 전이 규칙을 포함합니다. */
public enum SettlementStatus {
    PENDING,
    HOLD,
    COMPLETED;

    private static final Set<SettlementStatus> COMPLETABLE = EnumSet.of(PENDING);
    private static final Set<SettlementStatus> HOLDABLE = EnumSet.of(PENDING);
    private static final Set<SettlementStatus> RELEASABLE = EnumSet.of(HOLD);

    public boolean canTransitionTo(SettlementStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<SettlementStatus> getAllowedFrom(SettlementStatus target) {
        return switch (target) {
            case COMPLETED -> COMPLETABLE;
            case HOLD -> HOLDABLE;
            case PENDING -> RELEASABLE;
        };
    }
}
