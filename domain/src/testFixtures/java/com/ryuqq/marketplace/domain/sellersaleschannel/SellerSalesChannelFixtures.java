package com.ryuqq.marketplace.domain.sellersaleschannel;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.sellersaleschannel.id.SellerSalesChannelId;
import com.ryuqq.marketplace.domain.sellersaleschannel.vo.ConnectionStatus;
import java.time.Instant;

/**
 * SellerSalesChannel 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SellerSalesChannel 관련 객체들을 생성합니다.
 */
public final class SellerSalesChannelFixtures {

    private SellerSalesChannelFixtures() {}

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

    // ===== SellerSalesChannel Aggregate Fixtures =====

    public static final long DEFAULT_SHOP_ID = 0L;

    /** 새 셀러 판매채널 생성 (CONNECTED 상태, ID 없음). */
    public static SellerSalesChannel newSellerSalesChannel() {
        return SellerSalesChannel.forNew(
                SellerId.of(DEFAULT_SELLER_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                CommonVoFixtures.now());
    }

    /** 지정된 sellerId로 새 셀러 판매채널 생성. */
    public static SellerSalesChannel newSellerSalesChannel(Long sellerId) {
        return SellerSalesChannel.forNew(
                SellerId.of(sellerId),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                CommonVoFixtures.now());
    }

    /** CONNECTED 상태의 셀러 판매채널 (DB에서 재구성). */
    public static SellerSalesChannel connectedSellerSalesChannel() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerSalesChannel.reconstitute(
                SellerSalesChannelId.of(DEFAULT_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                yesterday,
                yesterday);
    }

    /** 지정된 ID로 CONNECTED 상태의 셀러 판매채널 생성. */
    public static SellerSalesChannel connectedSellerSalesChannel(Long id) {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerSalesChannel.reconstitute(
                SellerSalesChannelId.of(id),
                SellerId.of(DEFAULT_SELLER_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                ConnectionStatus.CONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                yesterday,
                yesterday);
    }

    /** DISCONNECTED 상태의 셀러 판매채널. */
    public static SellerSalesChannel disconnectedSellerSalesChannel() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerSalesChannel.reconstitute(
                SellerSalesChannelId.of(2L),
                SellerId.of(DEFAULT_SELLER_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                ConnectionStatus.DISCONNECTED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                yesterday,
                yesterday);
    }

    /** SUSPENDED 상태의 셀러 판매채널. */
    public static SellerSalesChannel suspendedSellerSalesChannel() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerSalesChannel.reconstitute(
                SellerSalesChannelId.of(3L),
                SellerId.of(DEFAULT_SELLER_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                DEFAULT_CHANNEL_CODE,
                ConnectionStatus.SUSPENDED,
                DEFAULT_API_KEY,
                DEFAULT_API_SECRET,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_VENDOR_ID,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_SHOP_ID,
                yesterday,
                yesterday);
    }
}
