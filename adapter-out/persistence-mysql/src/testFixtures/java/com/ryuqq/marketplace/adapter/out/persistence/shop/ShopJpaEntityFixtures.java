package com.ryuqq.marketplace.adapter.out.persistence.shop;

import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ShopJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ShopJpaEntity 관련 객체들을 생성합니다.
 */
public final class ShopJpaEntityFixtures {

    private ShopJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_SHOP_NAME = "테스트 외부몰";
    public static final String DEFAULT_ACCOUNT_ID = "test-account-123";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 Shop Entity 생성 (ID null - DB 자동생성용). */
    public static ShopJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID + seq, STATUS_ACTIVE, now, now, null);
    }

    /** ID를 지정한 활성 상태 Shop Entity 생성. */
    public static ShopJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                id, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID, STATUS_ACTIVE, now, now, null);
    }

    /** 커스텀 Shop명을 가진 활성 상태 Shop Entity 생성. */
    public static ShopJpaEntity activeEntityWithName(String shopName, String accountId) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(null, shopName, accountId, STATUS_ACTIVE, now, now, null);
    }

    /** ID를 지정한 비활성 상태 Shop Entity 생성. */
    public static ShopJpaEntity inactiveEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                id, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID + seq, STATUS_INACTIVE, now, now, null);
    }

    /** 비활성 상태 Shop Entity 생성 (ID null - DB 자동생성용). */
    public static ShopJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID + seq, STATUS_INACTIVE, now, now, null);
    }

    /** ID를 지정한 삭제된 상태 Shop Entity 생성. */
    public static ShopJpaEntity deletedEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                id, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID + seq, STATUS_INACTIVE, now, now, now);
    }

    /** 삭제된 상태 Shop Entity 생성 (ID null - DB 자동생성용). */
    public static ShopJpaEntity deletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID + seq, STATUS_INACTIVE, now, now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ShopJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID, STATUS_ACTIVE, now, now, null);
    }

    /** 새로운 비활성 상태 Entity 생성 (ID는 null). */
    public static ShopJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID, STATUS_INACTIVE, now, now, null);
    }

    /** 새로운 삭제된 상태 Entity 생성 (ID는 null). */
    public static ShopJpaEntity newDeletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, DEFAULT_ACCOUNT_ID, STATUS_INACTIVE, now, now, now);
    }

    /** 커스텀 Shop명을 가진 비활성 상태 Shop Entity 생성. */
    public static ShopJpaEntity inactiveEntityWithName(String shopName, String accountId) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(null, shopName, accountId, STATUS_INACTIVE, now, now, null);
    }

    /** 커스텀 Shop명을 가진 삭제 상태 Shop Entity 생성. */
    public static ShopJpaEntity deletedEntityWithName(String shopName, String accountId) {
        Instant now = Instant.now();
        return ShopJpaEntity.create(null, shopName, accountId, STATUS_INACTIVE, now, now, now);
    }

    /** 특정 accountId를 가진 활성 상태 Shop Entity 생성. */
    public static ShopJpaEntity activeEntityWithAccountId(String accountId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ShopJpaEntity.create(
                null, "테스트 외부몰 " + seq, accountId, STATUS_ACTIVE, now, now, null);
    }
}
