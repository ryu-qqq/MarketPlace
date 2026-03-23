package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.product.port.out.query.ProductQueryPort;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrders;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
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
    private final ProductQueryPort productQueryPort;

    public InboundOrderMappingResolver(
            OutboundProductReadManager outboundProductReadManager,
            ProductGroupReadManager productGroupReadManager,
            ProductQueryPort productQueryPort) {
        this.outboundProductReadManager = outboundProductReadManager;
        this.productGroupReadManager = productGroupReadManager;
        this.productQueryPort = productQueryPort;
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

            // productId 매핑: NONE이면 유일한 product 사용, 그 외는 optionManageCode(=productId)
            Long productId = resolveProductId(item.externalOptionId(), productGroup);

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
     * productId를 결정합니다.
     *
     * <p>NONE 옵션 타입: product가 1개이므로 직접 조회하여 매핑.
     * SINGLE/COMBINATION: optionManageCode(=productId)를 Long.parseLong으로 역매핑.
     */
    private Long resolveProductId(String optionManageCode, ProductGroup productGroup) {
        // 1순위: optionManageCode가 숫자면 productId로 직접 사용 (SINGLE/COMBINATION)
        if (optionManageCode != null && !optionManageCode.isBlank()) {
            try {
                return Long.parseLong(optionManageCode);
            } catch (NumberFormatException ignored) {
                // 숫자가 아닌 값이면 무시
            }
        }

        // 2순위: NONE 옵션 타입이면 유일한 product 조회
        if (productGroup != null && productGroup.optionType() == OptionType.NONE) {
            List<Product> products =
                    productQueryPort.findByProductGroupId(productGroup.id());
            if (products.size() == 1) {
                return products.getFirst().idValue();
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
