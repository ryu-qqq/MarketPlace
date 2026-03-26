package com.ryuqq.marketplace.domain.exchange.vo;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** 교환 상태. 상태 전이 규칙을 포함합니다. */
public enum ExchangeStatus {
    REQUESTED,
    COLLECTING,
    COLLECTED,
    PREPARING,
    SHIPPING,
    COMPLETED,
    REJECTED,
    CANCELLED;

    private static final Set<ExchangeStatus> COLLECTABLE = EnumSet.of(REQUESTED);
    private static final Set<ExchangeStatus> COLLECTION_COMPLETABLE = EnumSet.of(COLLECTING);
    private static final Set<ExchangeStatus> PREPARABLE = EnumSet.of(COLLECTED);
    private static final Set<ExchangeStatus> SHIPPABLE = EnumSet.of(PREPARING);
    private static final Set<ExchangeStatus> COMPLETABLE = EnumSet.of(SHIPPING);
    private static final Set<ExchangeStatus> REJECTABLE =
            EnumSet.of(REQUESTED, COLLECTING, COLLECTED, PREPARING);
    private static final Set<ExchangeStatus> CANCELLABLE = EnumSet.of(REQUESTED, COLLECTING);
    private static final Set<ExchangeStatus> ACTIVE =
            EnumSet.of(REQUESTED, COLLECTING, COLLECTED, PREPARING, SHIPPING);

    /** 진행 중인 상태인지 확인. COMPLETED/REJECTED/CANCELLED는 종료 상태. */
    public boolean isActive() {
        return ACTIVE.contains(this);
    }

    /** 정상 완료 상태인지 확인. */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /** 종료 상태인지 확인. */
    public boolean isTerminal() {
        return !isActive();
    }

    public boolean canTransitionTo(ExchangeStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<ExchangeStatus> getAllowedFrom(ExchangeStatus target) {
        return switch (target) {
            case COLLECTING -> COLLECTABLE;
            case COLLECTED -> COLLECTION_COMPLETABLE;
            case PREPARING -> PREPARABLE;
            case SHIPPING -> SHIPPABLE;
            case COMPLETED -> COMPLETABLE;
            case REJECTED -> REJECTABLE;
            case CANCELLED -> CANCELLABLE;
            default -> EnumSet.noneOf(ExchangeStatus.class);
        };
    }

    public static List<ExchangeStatus> fromStringList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream().map(ExchangeStatus::valueOf).toList();
    }
}
