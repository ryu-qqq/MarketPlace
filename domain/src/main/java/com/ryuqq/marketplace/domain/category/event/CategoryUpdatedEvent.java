package com.ryuqq.marketplace.domain.category.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;

import java.time.Instant;

/**
 * Category Updated Event
 *
 * <p>카테고리 정보 변경 시 발행되는 도메인 이벤트</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryUpdatedEvent(
    Long categoryId,
    Instant occurredAt
) implements DomainEvent {

    /**
     * 현재 시각으로 이벤트 생성
     *
     * @param categoryId 카테고리 ID
     */
    public CategoryUpdatedEvent(Long categoryId) {
        this(categoryId, Instant.now());
    }

    /**
     * CategoryUpdatedEvent 생성 (발생 시각 자동 설정)
     *
     * @param categoryId 카테고리 ID
     * @return CategoryUpdatedEvent 인스턴스
     */
    public static CategoryUpdatedEvent of(Long categoryId) {
        return new CategoryUpdatedEvent(categoryId, Instant.now());
    }
}
