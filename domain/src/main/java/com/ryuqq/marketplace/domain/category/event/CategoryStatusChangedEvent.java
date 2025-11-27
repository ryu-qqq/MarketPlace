package com.ryuqq.marketplace.domain.category.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;

import java.time.Instant;

/**
 * Category Status Changed Event
 *
 * <p>카테고리 상태 변경 시 발행되는 도메인 이벤트</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryStatusChangedEvent(
    Long categoryId,
    String oldStatus,
    String newStatus,
    Instant occurredAt
) implements DomainEvent {

    /**
     * 현재 시각으로 이벤트 생성
     *
     * @param categoryId 카테고리 ID
     * @param oldStatus 이전 상태
     * @param newStatus 새 상태
     */
    public CategoryStatusChangedEvent(Long categoryId, String oldStatus, String newStatus) {
        this(categoryId, oldStatus, newStatus, Instant.now());
    }
}
