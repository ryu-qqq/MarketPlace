package com.ryuqq.marketplace.adapter.out.persistence.common.adapter;

import com.ryuqq.marketplace.application.common.dto.composite.ChannelShopContext;
import com.ryuqq.marketplace.application.common.port.out.ChannelShopQueryPort;
import com.ryuqq.marketplace.application.sellersaleschannel.port.out.query.SellerSalesChannelQueryPort;
import com.ryuqq.marketplace.application.shop.port.out.query.ShopQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.List;
import org.springframework.stereotype.Component;

/** 판매채널 + 매장 컴포짓 조회 Adapter. 기존 SellerSalesChannel/Shop Port를 조합합니다. */
@Component
public class ChannelShopQueryAdapter implements ChannelShopQueryPort {

    private final SellerSalesChannelQueryPort channelQueryPort;
    private final ShopQueryPort shopQueryPort;

    public ChannelShopQueryAdapter(
            SellerSalesChannelQueryPort channelQueryPort, ShopQueryPort shopQueryPort) {
        this.channelQueryPort = channelQueryPort;
        this.shopQueryPort = shopQueryPort;
    }

    @Override
    public ChannelShopContext getBySellerIdAndSalesChannelId(Long sellerId, Long salesChannelId) {
        SellerSalesChannel channel =
                channelQueryPort
                        .findBySellerIdAndSalesChannelId(
                                SellerId.of(sellerId), SalesChannelId.of(salesChannelId))
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "SellerSalesChannel not found: sellerId="
                                                        + sellerId
                                                        + ", salesChannelId="
                                                        + salesChannelId));

        List<Shop> shops = shopQueryPort.findActiveBySalesChannelId(salesChannelId);
        Shop shop =
                shops.stream()
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Active Shop not found: salesChannelId="
                                                        + salesChannelId));

        return new ChannelShopContext(channel, shop);
    }
}
