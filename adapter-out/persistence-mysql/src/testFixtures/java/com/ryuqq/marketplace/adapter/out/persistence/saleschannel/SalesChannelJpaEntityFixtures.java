package com.ryuqq.marketplace.adapter.out.persistence.saleschannel;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SalesChannelJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannelJpaEntity 관련 객체들을 생성합니다.
 */
public final class SalesChannelJpaEntityFixtures {

    private SalesChannelJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CHANNEL_NAME = "테스트 채널";
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 SalesChannel Entity 생성. */
    public static SalesChannelJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, "테스트 채널 " + seq, "ACTIVE", now, now);
    }

    /** ID를 지정한 활성 상태 SalesChannel Entity 생성. */
    public static SalesChannelJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(id, "테스트 채널 " + seq, "ACTIVE", now, now);
    }

    /** 커스텀 채널명을 가진 활성 상태 SalesChannel Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SalesChannelJpaEntity activeEntityWithName(String channelName) {
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, channelName, "ACTIVE", now, now);
    }

    /** 비활성 상태 SalesChannel Entity 생성. */
    public static SalesChannelJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, "테스트 채널 " + seq, "INACTIVE", now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SalesChannelJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, "테스트 채널 " + seq, "ACTIVE", now, now);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static SalesChannelJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, "테스트 채널 " + seq, "INACTIVE", now, now);
    }

    /** 커스텀 채널명을 가진 비활성 상태 SalesChannel Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SalesChannelJpaEntity inactiveEntityWithName(String channelName) {
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(null, channelName, "INACTIVE", now, now);
    }

    /** ID를 지정한 비활성 상태 SalesChannel Entity 생성. */
    public static SalesChannelJpaEntity inactiveEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelJpaEntity.create(id, "테스트 채널 " + seq, "INACTIVE", now, now);
    }
}
