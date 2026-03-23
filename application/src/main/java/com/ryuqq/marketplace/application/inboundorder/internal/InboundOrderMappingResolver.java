package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrders;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * InboundOrder 상품 매핑 해석기.
 *
 * <p>OutboundProduct 역조회를 통해 외부 상품 ID → 내부 상품 매핑을 bulk로 수행합니다.
 * externalOptionId(=skuCode)를 이용하여 개별 Product(SKU)까지 매핑합니다.
 */
@Component
public class InboundOrderMappingResolver {

    private final OutboundProductReadManager outboundProductReadManager;
    private final ProductGroupReadManager productGroupReadManager;

    public InboundOrderMappingResolver(
            OutboundProductReadManager outboundProductReadManager,
            ProductGroupReadManager productGroupReadManager) {
        this.outboundProductReadManager = outboundProductReadManager;
        this.productGroupReadManager = productGroupReadManager;
    }

    /**
     * InboundOrders의 미매핑 아이템에 대해 상품 매핑을 bulk로 수행합니다.
     *
     * @param orders 인바운드 주문 목록
     * @param salesChannelId 판매채널 ID
     * @param now 현재 시각
     */
    public void resolveAndApply(InboundOrders orders, long salesChannelId, Instant now) {
        Set<String> unmappedExtIds = orders.unmappedExternalProductIds();
        if (unmappedExtIds.isEmpty()) {
            applyStatusToAll(orders, now);
            return;
        }

        Map<String, OutboundProduct> outboundMap = buildOutboundMap(unmappedExtIds, salesChannelId);
        Map<Long, ProductGroup> productGroupMap = buildProductGroupMap(outboundMap);

        for (InboundOrder order : orders.all()) {
            applyMappingToItems(order, outboundMap, productGroupMap);
            applyStatus(order, now);
        }
    }

    private Map<String, OutboundProduct> buildOutboundMap(
            Set<String> externalProductIds, long salesChannelId) {
        List<OutboundProduct> outbounds =
                outboundProductReadManager.findByExternalProductIdsAndSalesChannelId(
                        externalProductIds, salesChannelId);
        return outbounds.stream()
                .collect(
                        Collectors.toMap(
                                OutboundProduct::externalProductId,
                                Function.identity(),
                                (a, b) -> a));
    }

    private Map<Long, ProductGroup> buildProductGroupMap(Map<String, OutboundProduct> outboundMap) {
        if (outboundMap.isEmpty()) {
            return Map.of();
        }
        List<ProductGroupId> productGroupIds =
                outboundMap.values().stream()
                        .map(OutboundProduct::productGroupId)
                        .distinct()
                        .toList();
        List<ProductGroup> productGroups = productGroupReadManager.findByIds(productGroupIds);
        return productGroups.stream()
                .collect(Collectors.toMap(ProductGroup::idValue, Function.identity()));
    }

    private void applyMappingToItems(
            InboundOrder order,
            Map<String, OutboundProduct> outboundMap,
            Map<Long, ProductGroup> productGroupMap) {
        Long resolvedSellerId = null;

        for (InboundOrderItem item : order.items()) {
            if (item.isMapped()) {
                continue;
            }

            OutboundProduct outbound = outboundMap.get(item.externalProductId());
            if (outbound == null) {
                continue;
            }

            Long productGroupId = outbound.productGroupIdValue();
            ProductGroup productGroup = productGroupMap.get(productGroupId);

            Long sellerId = productGroup != null ? productGroup.sellerIdValue() : null;
            Long brandId = productGroup != null ? productGroup.brandIdValue() : null;
            String productGroupName =
                    productGroup != null ? productGroup.productGroupNameValue() : null;

            // externalOptionId(=optionManageCode=productId)로 개별 Product(SKU) 매핑
            Long productId = resolveProductId(item.externalOptionId());

            item.applyMapping(
                    productGroupId, productId, sellerId, brandId, null, productGroupName);

            if (resolvedSellerId == null && sellerId != null) {
                resolvedSellerId = sellerId;
            }
        }

        if (resolvedSellerId != null && order.sellerId() == 0L) {
            order.assignSellerId(resolvedSellerId);
        }
    }

    /**
     * optionManageCode에서 productId를 추출합니다.
     * 네이버 상품 등록 시 sellerManagerCode에 product.id()를 전송하므로,
     * 주문에서 optionManageCode로 그대로 돌아옵니다.
     */
    private Long resolveProductId(String optionManageCode) {
        if (optionManageCode != null && !optionManageCode.isBlank()) {
            try {
                return Long.parseLong(optionManageCode);
            } catch (NumberFormatException ignored) {
                // 숫자가 아닌 값이면 매핑 불가
            }
        }
        return null;
    }

    private void applyStatusToAll(InboundOrders orders, Instant now) {
        for (InboundOrder order : orders.all()) {
            applyStatus(order, now);
        }
    }

    private void applyStatus(InboundOrder order, Instant now) {
        boolean allMapped = order.items().stream().allMatch(InboundOrderItem::isMapped);
        if (allMapped) {
            order.applyMapping(now);
        } else {
            order.markPendingMapping(now);
        }
    }
}
