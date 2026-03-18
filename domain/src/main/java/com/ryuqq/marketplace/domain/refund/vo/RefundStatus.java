package com.ryuqq.marketplace.domain.refund.vo;

import java.util.EnumSet;
import java.util.Set;

/** 환불 클레임 상태. 상태 전이 규칙을 포함합니다. */
public enum RefundStatus {
    REQUESTED,
    COLLECTING,
    COLLECTED,
    COMPLETED,
    REJECTED,
    CANCELLED;

    private static final Set<RefundStatus> COLLECTIBLE = EnumSet.of(REQUESTED);
    private static final Set<RefundStatus> COLLECTION_COMPLETABLE = EnumSet.of(COLLECTING);
    private static final Set<RefundStatus> COMPLETABLE = EnumSet.of(COLLECTED);
    private static final Set<RefundStatus> REJECTABLE =
            EnumSet.of(REQUESTED, COLLECTING, COLLECTED);
    private static final Set<RefundStatus> CANCELLABLE = EnumSet.of(REQUESTED, COLLECTING);
    private static final Set<RefundStatus> ACTIVE = EnumSet.of(REQUESTED, COLLECTING, COLLECTED);

    /** 진행 중인 상태인지 확인. COMPLETED/REJECTED/CANCELLED는 종료 상태. */
    public boolean isActive() {
        return ACTIVE.contains(this);
    }

    public boolean canTransitionTo(RefundStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<RefundStatus> getAllowedFrom(RefundStatus target) {
        return switch (target) {
            case COLLECTING -> COLLECTIBLE;
            case COLLECTED -> COLLECTION_COMPLETABLE;
            case COMPLETED -> COMPLETABLE;
            case REJECTED -> REJECTABLE;
            case CANCELLED -> CANCELLABLE;
            default -> EnumSet.noneOf(RefundStatus.class);
        };
    }
}
