package com.ryuqq.marketplace.application.outboundsync;

import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.Set;

/**
 * OutboundSync Application 실행 컨텍스트 테스트 Fixtures.
 *
 * <p>OutboundSyncExecutionContext 관련 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OutboundSyncExecutionContextFixtures {

    private OutboundSyncExecutionContextFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final String SELLIC_CHANNEL_CODE = "SELLIC";
    public static final String NAVER_CHANNEL_CODE = "NAVER";

    // ===== CREATE 컨텍스트 =====

    /** SELLIC CREATE 실행 컨텍스트 */
    public static OutboundSyncExecutionContext sellicCreateContext() {
        OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.pendingOutbox();
        SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
        Shop shop = ShopFixtures.activeShop();
        return new OutboundSyncExecutionContext(
                outbox, channel, shop, DEFAULT_PRODUCT_GROUP_ID, SyncType.CREATE, Set.of());
    }

    /** SELLIC CREATE 실행 컨텍스트 (productGroupId 지정) */
    public static OutboundSyncExecutionContext sellicCreateContext(Long productGroupId) {
        OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.pendingOutbox();
        SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
        Shop shop = ShopFixtures.activeShop();
        return new OutboundSyncExecutionContext(
                outbox, channel, shop, productGroupId, SyncType.CREATE, Set.of());
    }

    // ===== UPDATE 컨텍스트 =====

    /** SELLIC UPDATE 실행 컨텍스트 */
    public static OutboundSyncExecutionContext sellicUpdateContext() {
        OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.newPendingUpdateOutbox();
        SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
        Shop shop = ShopFixtures.activeShop();
        return new OutboundSyncExecutionContext(
                outbox, channel, shop, DEFAULT_PRODUCT_GROUP_ID, SyncType.UPDATE, Set.of());
    }

    /** SELLIC UPDATE 실행 컨텍스트 (변경 영역 지정) */
    public static OutboundSyncExecutionContext sellicUpdateContext(Set<ChangedArea> changedAreas) {
        OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.newPendingUpdateOutbox();
        SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
        Shop shop = ShopFixtures.activeShop();
        return new OutboundSyncExecutionContext(
                outbox, channel, shop, DEFAULT_PRODUCT_GROUP_ID, SyncType.UPDATE, changedAreas);
    }

    // ===== DELETE 컨텍스트 =====

    /** SELLIC DELETE 실행 컨텍스트 */
    public static OutboundSyncExecutionContext sellicDeleteContext() {
        OutboundSyncOutbox outbox = OutboundSyncOutboxFixtures.newPendingDeleteOutbox();
        SellerSalesChannel channel = SellerSalesChannelFixtures.connectedSellerSalesChannel();
        Shop shop = ShopFixtures.activeShop();
        return new OutboundSyncExecutionContext(
                outbox, channel, shop, DEFAULT_PRODUCT_GROUP_ID, SyncType.DELETE, Set.of());
    }

    // ===== SalesChannelMappingResult 원시값 상수 =====
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "100";
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "200";
    public static final Long DEFAULT_CATEGORY_ID = 100L;
    public static final Long DEFAULT_BRAND_ID = 200L;
}
