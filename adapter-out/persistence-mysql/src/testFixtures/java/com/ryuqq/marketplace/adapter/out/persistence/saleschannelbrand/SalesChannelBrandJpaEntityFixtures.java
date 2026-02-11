package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SalesChannelBrandJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannelBrandJpaEntity 관련 객체들을 생성합니다.
 */
public final class SalesChannelBrandJpaEntityFixtures {

    private SalesChannelBrandJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BRAND-001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "테스트 브랜드";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 SalesChannelBrand Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 SalesChannelBrand Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 salesChannelId를 가진 활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntityWithSalesChannel(Long salesChannelId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                salesChannelId,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 커스텀 외부 브랜드 코드를 가진 활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntityWithCode(String externalBrandCode) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                externalBrandCode,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 커스텀 외부 브랜드 이름을 가진 활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntityWithName(String externalBrandName) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                externalBrandName,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 비활성 상태 SalesChannelBrand Entity 생성. */
    public static SalesChannelBrandJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_INACTIVE,
                now,
                now);
    }

    /** ID를 지정한 비활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity inactiveEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_INACTIVE,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SalesChannelBrandJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 salesChannelId와 externalBrandCode를 가진 새 Entity 생성. */
    public static SalesChannelBrandJpaEntity newEntityWithCodeAndChannel(
            Long salesChannelId, String externalBrandCode) {
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                salesChannelId,
                externalBrandCode,
                DEFAULT_EXTERNAL_BRAND_NAME,
                STATUS_ACTIVE,
                now,
                now);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static SalesChannelBrandJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + "-" + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + " " + seq,
                STATUS_INACTIVE,
                now,
                now);
    }

    /** 커스텀 파라미터를 가진 활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity activeEntityWithParams(
            Long salesChannelId, String externalBrandCode, String externalBrandName) {
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null, salesChannelId, externalBrandCode, externalBrandName, STATUS_ACTIVE, now, now);
    }

    /** 커스텀 파라미터를 가진 비활성 상태 Entity 생성. */
    public static SalesChannelBrandJpaEntity inactiveEntityWithParams(
            Long salesChannelId, String externalBrandCode, String externalBrandName) {
        Instant now = Instant.now();
        return SalesChannelBrandJpaEntity.create(
                null,
                salesChannelId,
                externalBrandCode,
                externalBrandName,
                STATUS_INACTIVE,
                now,
                now);
    }
}
