package com.ryuqq.marketplace.adapter.out.persistence.shop;

import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ShopJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ShopJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShopJpaEntityFixtures {

    private ShopJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_SHOP_NAME = "테스트 외부몰";
    public static final String DEFAULT_ACCOUNT_ID = "test-account-123";
    public static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    public static final String DEFAULT_STATUS_INACTIVE = "INACTIVE";

    // ===== Entity Fixtures =====

    /** 기본 Shop Entity 생성 (ID 없음, ACTIVE 상태). */
    public static ShopJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME + "-" + seq,
                DEFAULT_ACCOUNT_ID + "-" + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now,
                null);
    }

    /** ID를 지정한 ACTIVE 상태 Shop Entity 생성. */
    public static ShopJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                DEFAULT_STATUS_ACTIVE,
                now,
                now,
                null);
    }

    /** 기본 ID(1L)의 ACTIVE 상태 Shop Entity 생성. */
    public static ShopJpaEntity activeEntity() {
        return activeEntity(DEFAULT_ID);
    }

    /** ID를 지정한 INACTIVE 상태 Shop Entity 생성. */
    public static ShopJpaEntity inactiveEntity(Long id) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                DEFAULT_STATUS_INACTIVE,
                now,
                now,
                null);
    }

    /** 기본 ID(1L)의 INACTIVE 상태 Shop Entity 생성. */
    public static ShopJpaEntity inactiveEntity() {
        return inactiveEntity(DEFAULT_ID);
    }

    /** 소프트 삭제된 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity newDeletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        Instant deletedAt = now.minusSeconds(3600);
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME + "-deleted-" + seq,
                DEFAULT_ACCOUNT_ID + "-deleted-" + seq,
                DEFAULT_STATUS_INACTIVE,
                deletedAt,
                now,
                deletedAt);
    }

    /** 비활성 상태 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME + "-inactive-" + seq,
                DEFAULT_ACCOUNT_ID + "-inactive-" + seq,
                DEFAULT_STATUS_INACTIVE,
                now,
                now,
                null);
    }

    /** ID를 지정한 소프트 삭제된 Shop Entity 생성. */
    public static ShopJpaEntity deletedEntity(Long id) {
        Instant now = Instant.now();
        Instant deletedAt = now.minusSeconds(3600);
        return ShopJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                DEFAULT_STATUS_INACTIVE,
                deletedAt,
                now,
                deletedAt);
    }

    /** 기본 ID의 소프트 삭제된 Shop Entity 생성. */
    public static ShopJpaEntity deletedEntity() {
        return deletedEntity(DEFAULT_ID);
    }

    /** 특정 accountId를 가진 ACTIVE 상태 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity activeEntityWithAccountId(String accountId) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                accountId,
                DEFAULT_STATUS_ACTIVE,
                now,
                now,
                null);
    }

    /** 특정 accountId를 가진 소프트 삭제된 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity deletedEntityWithAccountId(String accountId) {
        Instant now = Instant.now();
        Instant deletedAt = now.minusSeconds(3600);
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_NAME,
                accountId,
                DEFAULT_STATUS_INACTIVE,
                deletedAt,
                now,
                deletedAt);
    }

    /** 특정 shopName과 accountId를 가진 ACTIVE 상태 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity activeEntityWithName(String shopName, String accountId) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                shopName,
                accountId,
                DEFAULT_STATUS_ACTIVE,
                now,
                now,
                null);
    }

    /** 특정 salesChannelId를 가진 ACTIVE 상태 Shop Entity 생성 (ID 없음). */
    public static ShopJpaEntity activeEntityWithSalesChannelId(Long salesChannelId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null,
                salesChannelId,
                DEFAULT_SHOP_NAME + "-" + seq,
                DEFAULT_ACCOUNT_ID + "-" + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now,
                null);
    }
}
