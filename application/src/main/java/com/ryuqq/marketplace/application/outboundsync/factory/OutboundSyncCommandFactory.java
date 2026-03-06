package com.ryuqq.marketplace.application.outboundsync.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 외부 연동 Outbox/OutboundProduct 객체 생성 Factory.
 *
 * <p>순수 객체 생성만 담당합니다. 조회/필터링은 호출측(Coordinator)에서 수행합니다.
 */
@Component
public class OutboundSyncCommandFactory {

    private final TimeProvider timeProvider;

    public OutboundSyncCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 셀러의 CONNECTED 채널들에 대한 OutboundSyncOutbox 목록 생성. */
    public List<OutboundSyncOutbox> createOutboxes(
            ProductGroupId productGroupId,
            SellerId sellerId,
            List<SellerSalesChannel> connectedChannels) {
        Instant now = timeProvider.now();
        return connectedChannels.stream()
                .map(
                        channel ->
                                OutboundSyncOutbox.forNew(
                                        productGroupId,
                                        channel.salesChannelId(),
                                        sellerId,
                                        SyncType.CREATE,
                                        "{}",
                                        now))
                .toList();
    }

    /** 지정된 SyncType으로 채널별 OutboundSyncOutbox 생성. (UPDATE/DELETE 등 범용) */
    public List<OutboundSyncOutbox> createOutboxesForSync(
            ProductGroupId productGroupId,
            SellerId sellerId,
            List<SalesChannelId> salesChannelIds,
            SyncType syncType) {
        Instant now = timeProvider.now();
        return salesChannelIds.stream()
                .map(
                        channelId ->
                                OutboundSyncOutbox.forNew(
                                        productGroupId, channelId, sellerId, syncType, "{}", now))
                .toList();
    }

    /** 채널 목록에 대한 OutboundProduct 목록 생성. (필터링은 호출측에서 수행) */
    public List<OutboundProduct> createOutboundProducts(
            ProductGroupId productGroupId, List<SellerSalesChannel> channels) {
        Instant now = timeProvider.now();
        return channels.stream()
                .map(
                        channel ->
                                OutboundProduct.forNew(
                                        productGroupId, channel.salesChannelId(), now))
                .toList();
    }
}
