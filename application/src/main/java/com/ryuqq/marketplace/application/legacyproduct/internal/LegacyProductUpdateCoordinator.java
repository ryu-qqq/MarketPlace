package com.ryuqq.marketplace.application.legacyproduct.internal;

/**
 * 레거시 상품 수정 Coordinator 추상 클래스.
 *
 * <p>세토프 PK → 내부 ID 변환 로직을 공통으로 제공합니다. InboundProduct는 수정하지 않습니다 (최초 변환 이후 개별 수정 시 동기화 불필요).
 */
public abstract class LegacyProductUpdateCoordinator {

    protected final LegacyProductIdResolver idResolver;

    protected LegacyProductUpdateCoordinator(LegacyProductIdResolver idResolver) {
        this.idResolver = idResolver;
    }

    /** 세토프 PK → 내부 productGroupId 변환. */
    protected long resolveInternalId(long setofProductGroupId) {
        return idResolver.resolve(setofProductGroupId).internalProductGroupId();
    }
}
