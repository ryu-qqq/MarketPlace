package com.ryuqq.marketplace.domain.cancel.vo;

import java.util.EnumSet;
import java.util.List;
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
    private static final Set<CancelStatus> TERMINAL = EnumSet.of(COMPLETED, REJECTED, CANCELLED);

    /** 완료 상태인지 확인. COMPLETED만 정상 완료. */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /** 종료 상태인지 확인. COMPLETED/REJECTED/CANCELLED는 종료 상태. */
    public boolean isTerminal() {
        return TERMINAL.contains(this);
    }

    /** 진행 중인 상태인지 확인. */
    public boolean isActive() {
        return !isTerminal();
    }

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

    public static List<CancelStatus> fromStringList(List<String> values) {
        if (values == null || values.isEmpty()) { return List.of(); }
        return values.stream().map(CancelStatus::valueOf).toList();
    }
}
