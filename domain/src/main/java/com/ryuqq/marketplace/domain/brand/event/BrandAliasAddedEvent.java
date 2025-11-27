package com.ryuqq.marketplace.domain.brand.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/**
 * 브랜드 별칭 추가 도메인 이벤트
 *
 * <p>브랜드에 새로운 별칭이 추가되었을 때 발행됩니다.
 *
 * @param brandId 브랜드 ID
 * @param aliasId 별칭 ID
 * @param originalAlias 원본 별칭
 * @param normalizedAlias 정규화된 별칭
 * @param sourceType 별칭 출처 타입 (AliasSourceType name)
 * @param occurredAt 이벤트 발생 시각
 * @author ryu-qqq
 * @since 2025-11-27
 */
public record BrandAliasAddedEvent(
    Long brandId,
    Long aliasId,
    String originalAlias,
    String normalizedAlias,
    String sourceType,
    Instant occurredAt
) implements DomainEvent {

    public BrandAliasAddedEvent {
        // brandId는 새로 생성된 Brand의 경우 null일 수 있음 (영속화 전)
        // aliasId는 새로 생성된 BrandAlias의 경우 null일 수 있음 (영속화 전)
        if (originalAlias == null || originalAlias.isBlank()) {
            throw new IllegalArgumentException("originalAlias is required");
        }
        if (normalizedAlias == null || normalizedAlias.isBlank()) {
            throw new IllegalArgumentException("normalizedAlias is required");
        }
        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException("sourceType is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    /**
     * BrandAliasAddedEvent 생성 (발생 시각 자동 설정)
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @param originalAlias 원본 별칭
     * @param normalizedAlias 정규화된 별칭
     * @param sourceType 별칭 출처 타입
     * @return BrandAliasAddedEvent 인스턴스
     */
    public static BrandAliasAddedEvent of(
            Long brandId,
            Long aliasId,
            String originalAlias,
            String normalizedAlias,
            String sourceType
    ) {
        return new BrandAliasAddedEvent(
                brandId,
                aliasId,
                originalAlias,
                normalizedAlias,
                sourceType,
                Instant.now()
        );
    }
}
