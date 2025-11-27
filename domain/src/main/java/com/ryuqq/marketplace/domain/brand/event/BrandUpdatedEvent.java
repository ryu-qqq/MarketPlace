package com.ryuqq.marketplace.domain.brand.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/**
 * 브랜드 수정 도메인 이벤트
 *
 * <p>브랜드 정보가 수정되었을 때 발행됩니다.
 *
 * @param brandId 브랜드 ID
 * @param occurredAt 이벤트 발생 시각
 * @author ryu-qqq
 * @since 2025-11-27
 */
public record BrandUpdatedEvent(
    Long brandId,
    Instant occurredAt
) implements DomainEvent {

    public BrandUpdatedEvent {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    /**
     * BrandUpdatedEvent 생성 (발생 시각 자동 설정)
     *
     * @param brandId 브랜드 ID
     * @return BrandUpdatedEvent 인스턴스
     */
    public static BrandUpdatedEvent of(Long brandId) {
        return new BrandUpdatedEvent(brandId, Instant.now());
    }
}
