package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 인바운드 상품 재수신 시 SKU/옵션명 → 내부 ID 매핑을 보관하는 불변 VO.
 *
 * <p>Coordinator가 기존 내부 상품 데이터를 조회하여 이 객체를 생성하고, Converter가 순수 변환에 사용합니다.
 */
public class InboundIdMaps {

    private final Map<String, Long> skuToProductId;
    private final Map<String, Long> optionGroupNameToId;
    private final Map<String, Map<String, Long>> optionGroupNameToValueIds;

    private InboundIdMaps(
            Map<String, Long> skuToProductId,
            Map<String, Long> optionGroupNameToId,
            Map<String, Map<String, Long>> optionGroupNameToValueIds) {
        this.skuToProductId = Map.copyOf(skuToProductId);
        this.optionGroupNameToId = Map.copyOf(optionGroupNameToId);
        this.optionGroupNameToValueIds = deepCopy(optionGroupNameToValueIds);
    }

    /** 기존 Products + SellerOptionGroups에서 ID 맵 구축. */
    public static InboundIdMaps from(
            List<Product> existingProducts, SellerOptionGroups existingOptions) {

        Map<String, Long> skuMap = new HashMap<>();
        for (Product product : existingProducts) {
            String sku = product.skuCodeValue();
            if (sku != null) {
                skuMap.put(sku, product.idValue());
            }
        }

        Map<String, Long> groupMap = new HashMap<>();
        Map<String, Map<String, Long>> valueMap = new HashMap<>();

        for (SellerOptionGroup group : existingOptions.groups()) {
            if (group.isDeleted()) {
                continue;
            }
            String groupName = group.optionGroupNameValue();
            groupMap.put(groupName, group.idValue());

            Map<String, Long> innerValueMap = new HashMap<>();
            for (SellerOptionValue value : group.optionValues()) {
                if (!value.isDeleted()) {
                    innerValueMap.put(value.optionValueNameValue(), value.idValue());
                }
            }
            valueMap.put(groupName, innerValueMap);
        }

        return new InboundIdMaps(skuMap, groupMap, valueMap);
    }

    /** SKU 코드로 기존 productId 조회. 없으면 null (신규). */
    public Long findProductId(String skuCode) {
        return skuToProductId.get(skuCode);
    }

    /** 옵션 그룹명으로 기존 sellerOptionGroupId 조회. 없으면 null (신규). */
    public Long findOptionGroupId(String optionGroupName) {
        return optionGroupNameToId.get(optionGroupName);
    }

    /** 옵션 그룹명에 속한 옵션 값별 ID 맵 조회. 없으면 빈 맵. */
    public Map<String, Long> findOptionValueIds(String optionGroupName) {
        return optionGroupNameToValueIds.getOrDefault(optionGroupName, Map.of());
    }

    private static Map<String, Map<String, Long>> deepCopy(Map<String, Map<String, Long>> source) {
        Map<String, Map<String, Long>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Long>> entry : source.entrySet()) {
            copy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Map.copyOf(copy);
    }
}
