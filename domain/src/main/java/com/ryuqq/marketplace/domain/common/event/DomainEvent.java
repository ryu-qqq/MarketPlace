package com.ryuqq.marketplace.domain.common.event;

import java.time.Instant;

/** 도메인 이벤트 마커 인터페이스. 모든 도메인 이벤트는 이 인터페이스를 구현합니다. */
public interface DomainEvent {

    /**
     * 이벤트 발생 시각
     *
     * <p>Aggregate의 Clock에서 획득한 시간을 사용합니다.
     *
     * @return 이벤트가 생성된 시각
     */
    Instant occurredAt();

    /**
     * 이벤트 타입 식별자
     *
     * <p>기본 구현은 클래스 단순명을 반환합니다.
     *
     * <p>이벤트 라우팅 및 로깅에 사용됩니다.
     *
     * @return 이벤트 타입 문자열
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}
