package com.ryuqq.marketplace.application.outboundproduct.internal;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundproduct.validator.ManualSyncProductsValidator;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 수동 전송에 필요한 조회 데이터를 일괄 수집하는 Read Facade. */
@Component
public class ManualSyncReadFacade {

    private final ShopReadManager shopReadManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final ManualSyncProductsValidator validator;
    private final OutboundProductReadManager outboundProductReadManager;
    private final OutboundSyncOutboxReadManager outboxReadManager;

    public ManualSyncReadFacade(
            ShopReadManager shopReadManager,
            ProductGroupReadManager productGroupReadManager,
            ManualSyncProductsValidator validator,
            OutboundProductReadManager outboundProductReadManager,
            OutboundSyncOutboxReadManager outboxReadManager) {
        this.shopReadManager = shopReadManager;
        this.productGroupReadManager = productGroupReadManager;
        this.validator = validator;
        this.outboundProductReadManager = outboundProductReadManager;
        this.outboxReadManager = outboxReadManager;
    }

    /** Command로부터 수동 전송에 필요한 모든 조회 데이터를 수집. */
    public ManualSyncContext resolve(ManualSyncProductsCommand command) {
        // 1. Shop ID → salesChannelId 일괄 변환 + salesChannelId → shopId 매핑
        List<ShopId> shopIds = command.shopIds().stream().map(ShopId::of).toList();
        List<Shop> shops = shopReadManager.findByIds(shopIds);
        Set<Long> salesChannelIds =
                shops.stream().map(Shop::salesChannelId).collect(Collectors.toSet());
        Map<Long, Long> shopIdBySalesChannelId =
                shops.stream()
                        .collect(
                                Collectors.toMap(
                                        Shop::salesChannelId,
                                        shop -> shop.id().value(),
                                        (a, b) -> a));

        // 2. ProductGroup 일괄 조회
        List<ProductGroupId> pgIds =
                command.productGroupIds().stream().map(ProductGroupId::of).toList();
        List<ProductGroup> productGroups = productGroupReadManager.findByIds(pgIds);

        // 3. 셀러별 CONNECTED 채널 일괄 조회
        Set<SellerId> sellerIds =
                productGroups.stream().map(ProductGroup::sellerId).collect(Collectors.toSet());
        Map<Long, Set<Long>> connectedChannelIdsBySellerId =
                validator.findConnectedChannelIdsBySellerIds(sellerIds);

        // 4. OutboundProduct 일괄 조회 → (pgId:channelId) 키 셋
        List<OutboundProduct> existingProducts =
                outboundProductReadManager.findByProductGroupIds(command.productGroupIds());
        Set<String> existingProductKeys =
                existingProducts.stream()
                        .map(op -> op.productGroupIdValue() + ":" + op.salesChannelIdValue())
                        .collect(Collectors.toSet());

        // 5. PENDING Outbox 일괄 조회 → (pgId:channelId) 키 셋
        List<OutboundSyncOutbox> pendingOutboxes =
                outboxReadManager.findPendingByProductGroupIds(new HashSet<>(pgIds));
        Set<String> pendingKeys =
                pendingOutboxes.stream()
                        .map(ob -> ob.productGroupIdValue() + ":" + ob.salesChannelIdValue())
                        .collect(Collectors.toSet());

        return new ManualSyncContext(
                salesChannelIds,
                shopIdBySalesChannelId,
                productGroups,
                connectedChannelIdsBySellerId,
                existingProductKeys,
                pendingKeys);
    }
}
