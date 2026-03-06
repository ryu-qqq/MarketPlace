package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SellerSalesChannelJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerSalesChannelJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerSalesChannelJpaEntityFixtures {

    private SellerSalesChannelJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 10L;
    public static final String DEFAULT_CHANNEL_CODE = "MUSTIT";
    public static final String DEFAULT_API_KEY = "test-api-key";
    public static final String DEFAULT_API_SECRET = "test-api-secret";
    public static final String DEFAULT_ACCESS_TOKEN = "test-access-token";
    public static final String DEFAULT_VENDOR_ID = "vendor-001";
    public static final String DEFAULT_DISPLAY_NAME = "테스트 판매채널";
    public static final long DEFAULT_SHOP_ID = 0L;

    // ===== Entity Fixtures =====

    /** CONNECTED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity connectedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** ID를 지정한 CONNECTED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity connectedEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** sellerId를 지정한 CONNECTED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity connectedEntityWithSellerId(Long sellerId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                sellerId,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** DISCONNECTED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity disconnectedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.DISCONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** sellerId를 지정한 DISCONNECTED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity disconnectedEntityWithSellerId(Long sellerId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                sellerId,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.DISCONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** SUSPENDED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity suspendedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.SUSPENDED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** sellerId를 지정한 SUSPENDED 상태의 셀러 판매채널 Entity 생성. */
    public static SellerSalesChannelJpaEntity suspendedEntityWithSellerId(Long sellerId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                sellerId,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.SUSPENDED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerSalesChannelJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** API 정보가 없는 Entity 생성. */
    public static SellerSalesChannelJpaEntity entityWithoutApiCredentials() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                null,
                null,
                null,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }

    /** vendorId가 없는 Entity 생성. */
    public static SellerSalesChannelJpaEntity entityWithoutVendorId() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SellerSalesChannelJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                "MUSTIT_" + seq,
                SellerSalesChannelJpaEntity.ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                null,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                now,
                now);
    }
}
