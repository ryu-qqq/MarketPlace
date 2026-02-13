package com.ryuqq.marketplace.domain.exchange.vo;

import java.util.EnumSet;
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
            EnumSet.of(REQUESTED, COLLECTED, PREPARING);
    private static final Set<ExchangeStatus> CANCELLABLE = EnumSet.of(REQUESTED, COLLECTING);

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
}
