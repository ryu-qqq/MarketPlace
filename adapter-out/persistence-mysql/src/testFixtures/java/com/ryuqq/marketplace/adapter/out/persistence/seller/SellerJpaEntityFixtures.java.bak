package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SellerJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerJpaEntityFixtures {

    private SellerJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final String DEFAULT_DISPLAY_NAME = "테스트 셀러 스토어";
    public static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";
    public static final String DEFAULT_DESCRIPTION = "테스트 셀러 설명입니다.";
    public static final String DEFAULT_AUTH_TENANT_ID = "tenant-123";
    public static final String DEFAULT_AUTH_ORGANIZATION_ID = "org-456";

    // ===== Entity Fixtures =====

    /** 활성 상태의 셀러 Entity 생성. */
    public static SellerJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 셀러 Entity 생성. */
    public static SellerJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                id,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 인증 정보가 있는 활성 상태 셀러 Entity 생성. */
    public static SellerJpaEntity activeEntityWithAuth() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                DEFAULT_AUTH_TENANT_ID,
                DEFAULT_AUTH_ORGANIZATION_ID,
                now,
                now,
                null);
    }

    /** 커스텀 셀러명을 가진 활성 상태 셀러 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerJpaEntity activeEntityWithName(String sellerName, String displayName) {
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                sellerName,
                displayName,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 비활성 상태 셀러 Entity 생성. */
    public static SellerJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태 셀러 Entity 생성. */
    public static SellerJpaEntity deletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 로고 URL이 없는 Entity 생성. */
    public static SellerJpaEntity entityWithoutLogoUrl() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                null,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 설명이 없는 Entity 생성. */
    public static SellerJpaEntity entityWithoutDescription() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                null,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 로고 URL이 없는 새 Entity 생성 (ID는 null). */
    public static SellerJpaEntity newEntityWithoutLogoUrl() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                null,
                DEFAULT_DESCRIPTION,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 설명이 없는 새 Entity 생성 (ID는 null). */
    public static SellerJpaEntity newEntityWithoutDescription() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                null,
                true,
                null,
                null,
                now,
                now,
                null);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static SellerJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태의 새 Entity 생성 (ID는 null). */
    public static SellerJpaEntity newDeletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                now);
    }

    /** 커스텀 셀러명을 가진 비활성 상태 셀러 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerJpaEntity inactiveEntityWithName(String sellerName, String displayName) {
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                sellerName,
                displayName,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                null);
    }

    /** 커스텀 셀러명을 가진 삭제 상태 셀러 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerJpaEntity deletedEntityWithName(String sellerName, String displayName) {
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                sellerName,
                displayName,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                false,
                null,
                null,
                now,
                now,
                now);
    }

    /** authOrganizationId를 지정한 활성 상태 셀러 Entity 생성 (인증 테스트용). */
    public static SellerJpaEntity activeEntityWithOrganization(String authOrganizationId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerJpaEntity.create(
                null,
                "테스트 셀러 " + seq,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                true,
                DEFAULT_AUTH_TENANT_ID,
                authOrganizationId,
                now,
                now,
                null);
    }
}
