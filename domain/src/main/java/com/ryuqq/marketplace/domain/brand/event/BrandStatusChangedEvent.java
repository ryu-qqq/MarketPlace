package com.ryuqq.marketplace.domain.brand.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/**
 * 브랜드 상태 변경 도메인 이벤트
 *
 * <p>브랜드의 상태가 변경되었을 때 발행됩니다.
 *
 * @param brandId 브랜드 ID
 * @param oldStatus 이전 상태 (BrandStatus name)
 * @param newStatus 새로운 상태 (BrandStatus name)
 * @param occurredAt 이벤트 발생 시각
 * @author ryu-qqq
 * @since 2025-11-27
 */
public record BrandStatusChangedEvent(
    Long brandId,
    String oldStatus,
    String newStatus,
    Instant occurredAt
) implements DomainEvent {

    public BrandStatusChangedEvent {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (oldStatus == null || oldStatus.isBlank()) {
            throw new IllegalArgumentException("oldStatus is required");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    /**
     * BrandStatusChangedEvent 생성 (발생 시각 자동 설정)
     *
     * @param brandId 브랜드 ID
     * @param oldStatus 이전 상태
     * @param newStatus 새로운 상태
     * @return BrandStatusChangedEvent 인스턴스
     */
    public static BrandStatusChangedEvent of(Long brandId, String oldStatus, String newStatus) {
        return new BrandStatusChangedEvent(brandId, oldStatus, newStatus, Instant.now());
    }
}
