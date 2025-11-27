package com.ryuqq.marketplace.domain.brand.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/**
 * 브랜드 별칭 확정 도메인 이벤트
 *
 * <p>브랜드 별칭이 확정되었을 때 발행됩니다.
 *
 * @param brandId 브랜드 ID
 * @param aliasId 별칭 ID
 * @param normalizedAlias 정규화된 별칭
 * @param occurredAt 이벤트 발생 시각
 * @author ryu-qqq
 * @since 2025-11-27
 */
public record BrandAliasConfirmedEvent(
    Long brandId,
    Long aliasId,
    String normalizedAlias,
    Instant occurredAt
) implements DomainEvent {

    public BrandAliasConfirmedEvent {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (aliasId == null) {
            throw new IllegalArgumentException("aliasId is required");
        }
        if (normalizedAlias == null || normalizedAlias.isBlank()) {
            throw new IllegalArgumentException("normalizedAlias is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    /**
     * BrandAliasConfirmedEvent 생성 (발생 시각 자동 설정)
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @param normalizedAlias 정규화된 별칭
     * @return BrandAliasConfirmedEvent 인스턴스
     */
    public static BrandAliasConfirmedEvent of(Long brandId, Long aliasId, String normalizedAlias) {
        return new BrandAliasConfirmedEvent(brandId, aliasId, normalizedAlias, Instant.now());
    }
}
