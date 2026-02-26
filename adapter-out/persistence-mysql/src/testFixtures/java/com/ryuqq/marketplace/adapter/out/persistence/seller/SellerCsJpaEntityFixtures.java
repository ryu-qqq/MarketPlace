package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import java.time.Instant;
import java.time.LocalTime;

/**
 * SellerCsJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerCsJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerCsJpaEntityFixtures {

    private SellerCsJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_CS_PHONE = "02-1234-5678";
    public static final String DEFAULT_CS_MOBILE = "010-1234-5678";
    public static final String DEFAULT_CS_EMAIL = "cs@example.com";
    public static final LocalTime DEFAULT_OPERATING_START_TIME = LocalTime.of(9, 0);
    public static final LocalTime DEFAULT_OPERATING_END_TIME = LocalTime.of(18, 0);
    public static final String DEFAULT_OPERATING_DAYS = "MON,TUE,WED,THU,FRI";
    public static final String DEFAULT_KAKAO_CHANNEL_URL = "https://pf.kakao.com/_test";

    // ===== Entity Fixtures =====

    /** 활성 상태의 CS 정보 Entity 생성. */
    public static SellerCsJpaEntity activeEntity() {
        Instant now = Instant.now();
        return SellerCsJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_MOBILE,
                DEFAULT_CS_EMAIL,
                DEFAULT_OPERATING_START_TIME,
                DEFAULT_OPERATING_END_TIME,
                DEFAULT_OPERATING_DAYS,
                DEFAULT_KAKAO_CHANNEL_URL,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 CS 정보 Entity 생성. */
    public static SellerCsJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return SellerCsJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_MOBILE,
                DEFAULT_CS_EMAIL,
                DEFAULT_OPERATING_START_TIME,
                DEFAULT_OPERATING_END_TIME,
                DEFAULT_OPERATING_DAYS,
                DEFAULT_KAKAO_CHANNEL_URL,
                now,
                now,
                null);
    }

    /** 셀러 ID를 지정한 활성 상태 CS 정보 Entity 생성. */
    public static SellerCsJpaEntity activeEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerCsJpaEntity.create(
                null,
                sellerId,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_MOBILE,
                DEFAULT_CS_EMAIL,
                DEFAULT_OPERATING_START_TIME,
                DEFAULT_OPERATING_END_TIME,
                DEFAULT_OPERATING_DAYS,
                DEFAULT_KAKAO_CHANNEL_URL,
                now,
                now,
                null);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerCsJpaEntity newEntity() {
        Instant now = Instant.now();
        return SellerCsJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_MOBILE,
                DEFAULT_CS_EMAIL,
                DEFAULT_OPERATING_START_TIME,
                DEFAULT_OPERATING_END_TIME,
                DEFAULT_OPERATING_DAYS,
                DEFAULT_KAKAO_CHANNEL_URL,
                now,
                now,
                null);
    }

    /** 삭제된 상태 CS 정보 Entity 생성. */
    public static SellerCsJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return SellerCsJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_MOBILE,
                DEFAULT_CS_EMAIL,
                DEFAULT_OPERATING_START_TIME,
                DEFAULT_OPERATING_END_TIME,
                DEFAULT_OPERATING_DAYS,
                DEFAULT_KAKAO_CHANNEL_URL,
                now,
                now,
                now);
    }
}
