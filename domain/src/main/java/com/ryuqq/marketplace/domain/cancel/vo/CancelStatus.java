package com.ryuqq.marketplace.domain.cancel.vo;

import java.util.EnumSet;
import java.util.Set;

/** 취소 상태. 상태 전이 규칙을 포함합니다. */
public enum CancelStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    COMPLETED,
    CANCELLED;

    private static final Set<CancelStatus> APPROVABLE = EnumSet.of(REQUESTED);
    private static final Set<CancelStatus> REJECTABLE = EnumSet.of(REQUESTED);
    private static final Set<CancelStatus> COMPLETABLE = EnumSet.of(APPROVED);
    private static final Set<CancelStatus> WITHDRAWABLE = EnumSet.of(REQUESTED);

    public boolean canTransitionTo(CancelStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<CancelStatus> getAllowedFrom(CancelStatus target) {
        return switch (target) {
            case APPROVED -> APPROVABLE;
            case REJECTED -> REJECTABLE;
            case COMPLETED -> COMPLETABLE;
            case CANCELLED -> WITHDRAWABLE;
            default -> EnumSet.noneOf(CancelStatus.class);
        };
    }
}
