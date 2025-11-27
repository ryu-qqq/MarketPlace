package com.ryuqq.marketplace.domain.category.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;

import java.time.Instant;

/**
 * Category Created Event
 *
 * <p>카테고리 생성 시 발행되는 도메인 이벤트</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryCreatedEvent(
    Long categoryId,
    String code,
    String name,
    Instant occurredAt
) implements DomainEvent {

    /**
     * 현재 시각으로 이벤트 생성
     *
     * @param categoryId 카테고리 ID
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     */
    public CategoryCreatedEvent(Long categoryId, String code, String name) {
        this(categoryId, code, name, Instant.now());
    }
}
