package com.ryuqq.marketplace.adapter.out.persistence.inboundsource;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundSourceJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundSourceJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundSourceJpaEntityFixtures {

    private InboundSourceJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "SETOF";
    public static final String DEFAULT_NAME = "세토프 레거시";
    public static final String DEFAULT_TYPE = "LEGACY";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_DESCRIPTION = "레거시 Setof 상품 데이터 소스";

    // ===== Entity Fixtures =====

    /** 활성 상태의 InboundSource Entity 생성. */
    public static InboundSourceJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 InboundSource Entity 생성. */
    public static InboundSourceJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                id,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** 커스텀 코드를 가진 활성 상태 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static InboundSourceJpaEntity activeEntityWithCode(String code) {
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                code,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** 커스텀 이름을 가진 활성 상태 Entity 생성. ID는 null로 새 엔티티 생성. 코드는 자동 생성. */
    public static InboundSourceJpaEntity activeEntityWithName(String name) {
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                "SRC_NAME_" + SEQUENCE.getAndIncrement(),
                name,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** 비활성 상태 InboundSource Entity 생성. */
    public static InboundSourceJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                DEFAULT_CODE + "_INACTIVE_" + seq,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                "INACTIVE",
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** 커스텀 코드를 가진 비활성 상태 Entity 생성. */
    public static InboundSourceJpaEntity inactiveEntityWithCode(String code) {
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null, code, DEFAULT_NAME, DEFAULT_TYPE, "INACTIVE", DEFAULT_DESCRIPTION, now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static InboundSourceJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                DEFAULT_CODE + "_NEW_" + seq,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                DEFAULT_DESCRIPTION,
                now,
                now);
    }

    /** CRAWLING 타입 Entity 생성. */
    public static InboundSourceJpaEntity crawlingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null, "CRAWL_" + seq, "크롤링 소스", "CRAWLING", DEFAULT_STATUS, "크롤링 데이터 소스", now, now);
    }

    /** PARTNER 타입 Entity 생성. */
    public static InboundSourceJpaEntity partnerEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                "PARTNER_" + seq,
                "파트너 소스",
                "PARTNER",
                DEFAULT_STATUS,
                "파트너 연동 데이터 소스",
                now,
                now);
    }

    /** 설명이 없는 Entity 생성. */
    public static InboundSourceJpaEntity entityWithoutDescription() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null,
                DEFAULT_CODE + "_NODESC_" + seq,
                DEFAULT_NAME,
                DEFAULT_TYPE,
                DEFAULT_STATUS,
                null,
                now,
                now);
    }

    /** 커스텀 코드 + 타입을 가진 활성 상태 Entity 생성. */
    public static InboundSourceJpaEntity activeEntityWithCodeAndType(String code, String type) {
        Instant now = Instant.now();
        return InboundSourceJpaEntity.create(
                null, code, DEFAULT_NAME, type, DEFAULT_STATUS, DEFAULT_DESCRIPTION, now, now);
    }
}
