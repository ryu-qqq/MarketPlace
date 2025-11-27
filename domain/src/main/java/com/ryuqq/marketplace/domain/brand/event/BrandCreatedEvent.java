package com.ryuqq.marketplace.domain.brand.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/**
 * 브랜드 생성 도메인 이벤트
 *
 * <p>새로운 브랜드가 생성되었을 때 발행됩니다.
 *
 * @param brandId 브랜드 ID
 * @param code 브랜드 코드
 * @param canonicalName 정규화된 브랜드 이름
 * @param occurredAt 이벤트 발생 시각
 * @author ryu-qqq
 * @since 2025-11-27
 */
public record BrandCreatedEvent(
    Long brandId,
    String code,
    String canonicalName,
    Instant occurredAt
) implements DomainEvent {

    public BrandCreatedEvent {
        // brandId는 새로 생성된 Brand의 경우 null일 수 있음 (영속화 전)
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (canonicalName == null || canonicalName.isBlank()) {
            throw new IllegalArgumentException("canonicalName is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    /**
     * BrandCreatedEvent 생성 (발생 시각 자동 설정)
     *
     * @param brandId 브랜드 ID
     * @param code 브랜드 코드
     * @param canonicalName 정규화된 브랜드 이름
     * @return BrandCreatedEvent 인스턴스
     */
    public static BrandCreatedEvent of(Long brandId, String code, String canonicalName) {
        return new BrandCreatedEvent(brandId, code, canonicalName, Instant.now());
    }
}
