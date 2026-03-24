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

            // productId 매핑: optionManageCode → NONE fallback → 옵션명 텍스트 매칭
            Long productId =
                    resolveProductId(
                            item.externalOptionId(), item.externalOptionName(), productGroup);

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
     * <p>1순위: optionManageCode(=productId)를 Long.parseLong으로 역매핑.
     * 2순위: product가 1개뿐이면 해당 product 사용.
     * 3순위: 옵션명 텍스트로 product 역매핑 (SINGLE/COMBINATION).
     */
    private Long resolveProductId(
            String optionManageCode, String externalOptionName, ProductGroup productGroup) {
        // 1순위: optionManageCode가 숫자면 productId로 직접 사용
        if (optionManageCode != null && !optionManageCode.isBlank()) {
            try {
                return Long.parseLong(optionManageCode);
            } catch (NumberFormatException ignored) {
                // 숫자가 아닌 값이면 무시
            }
        }

        if (productGroup == null) {
            return null;
        }

        // 2순위: product가 1개뿐이면 해당 product 사용 (NONE/SINGLE 등 옵션 타입 무관)
        List<Product> products = productQueryPort.findByProductGroupId(productGroup.id());
        if (products.size() == 1) {
            return products.getFirst().idValue();
        }

        // 3순위: 옵션명 텍스트로 매칭 (네이버: "DEFAULT_ONE: 모눈" → "모눈"으로 product 역매핑)
        if (externalOptionName != null && !externalOptionName.isBlank()) {
            return resolveProductIdByOptionName(externalOptionName, productGroup);
        }

        return null;
    }

    /**
     * 옵션명 텍스트로 productId를 역매핑합니다.
     *
     * <p>네이버 productOption 형태: "그룹명: 값" 또는 "그룹명1: 값1, 그룹명2: 값2".
     * ProductGroup의 sellerOptionValues에서 옵션값 이름이 포함된 product를 찾습니다.
     */
    private Long resolveProductIdByOptionName(String externalOptionName, ProductGroup productGroup) {
        // 옵션값 이름 추출: "DEFAULT_ONE: 모눈" → "모눈"
        String optionValueName = extractOptionValueName(externalOptionName);
        if (optionValueName == null) {
            return null;
        }

        // ProductGroup의 sellerOptionValues에서 일치하는 값의 ID 찾기
        Long matchedOptionValueId = null;
        for (var group : productGroup.sellerOptionGroups()) {
            for (var value : group.optionValues()) {
                if (optionValueName.equals(value.optionValueName().value())) {
                    matchedOptionValueId = value.idValue();
                    break;
                }
            }
            if (matchedOptionValueId != null) break;
        }

        if (matchedOptionValueId == null) {
            return null;
        }

        // 해당 optionValueId를 가진 product 찾기
        List<Product> products =
                productQueryPort.findByProductGroupId(productGroup.id());
        Long targetValueId = matchedOptionValueId;
        return products.stream()
                .filter(
                        p ->
                                p.optionMappings().stream()
                                        .anyMatch(
                                                m ->
                                                        targetValueId.equals(
                                                                m.sellerOptionValueId().value())))
                .map(Product::idValue)
                .findFirst()
                .orElse(null);
    }

    private String extractOptionValueName(String externalOptionName) {
        // "DEFAULT_ONE: 모눈" → "모눈"
        // "색상: RED, 사이즈: XL" → 첫 번째 값만 ("RED")
        if (externalOptionName.contains(":")) {
            String afterColon = externalOptionName.split(":")[1].trim();
            // 쉼표가 있으면 첫 번째 값만
            if (afterColon.contains(",")) {
                return afterColon.split(",")[0].trim();
            }
            return afterColon;
        }
        return externalOptionName.trim();
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
