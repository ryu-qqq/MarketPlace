package com.ryuqq.marketplace.domain.legacy.product.aggregate;

import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductDiff;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOptionDiff;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 레거시 상품 컬렉션 래퍼.
 *
 * <p>기존 상품 목록을 감싸며, 새로운 상품 목록과의 diff 계산을 제공합니다. productId 기준으로 매칭하여 추가/삭제/유지를 판단합니다. 유지 대상 상품의 옵션은
 * optionGroupId:optionDetailId 기준으로 nested diff를 수행합니다.
 */
public class LegacyProducts {

    private final List<LegacyProduct> products;

    public LegacyProducts(List<LegacyProduct> products) {
        this.products = List.copyOf(products);
    }

    /**
     * 재고 업데이트: productId 기준으로 기존 상품의 재고를 변경합니다.
     *
     * @param stockUpdates productId → stockQuantity 매핑
     * @return persist 대상 상품 목록
     */
    public List<LegacyProduct> applyStockUpdates(Map<Long, Integer> stockUpdates) {
        List<LegacyProduct> toPersist = new ArrayList<>();
        for (LegacyProduct product : products) {
            Integer newStock = stockUpdates.get(product.idValue());
            if (newStock != null && newStock != product.stockQuantity()) {
                product.updateStock(newStock);
                toPersist.add(product);
            }
        }
        return toPersist;
    }

    /**
     * 상품/옵션 수정: 새로운 상품 목록과 비교하여 추가/삭제/유지를 판단하고 상태를 갱신합니다.
     *
     * <p>productId 기준 매칭:
     *
     * <ul>
     *   <li>새 요청에만 있는 productId(null) → added (신규 INSERT)
     *   <li>양쪽 모두 있는 productId → retained (재고 갱신 + 옵션 nested diff)
     *   <li>기존에만 있는 productId → removed (soft-delete)
     * </ul>
     *
     * @param newProducts 새로운 상품 목록
     * @param changedAt 변경 시각
     * @return diff 결과 (added/removed/retained + 옵션 diff)
     */
    public LegacyProductDiff update(List<LegacyProduct> newProducts, Instant changedAt) {
        Map<Long, LegacyProduct> existingById = new LinkedHashMap<>();
        for (LegacyProduct product : products) {
            if (product.idValue() != null) {
                existingById.put(product.idValue(), product);
            }
        }

        List<LegacyProduct> added = new ArrayList<>();
        List<LegacyProduct> retained = new ArrayList<>();

        List<LegacyProductOption> allAddedOptions = new ArrayList<>();
        List<LegacyProductOption> allRemovedOptions = new ArrayList<>();
        List<LegacyProductOption> allRetainedOptions = new ArrayList<>();

        for (LegacyProduct newProduct : newProducts) {
            Long productId = newProduct.idValue();
            if (productId == null) {
                added.add(newProduct);
                allAddedOptions.addAll(newProduct.options());
            } else {
                LegacyProduct existing = existingById.remove(productId);
                if (existing != null) {
                    if (existing.stockQuantity() != newProduct.stockQuantity()) {
                        existing.updateStock(newProduct.stockQuantity());
                    }
                    retained.add(existing);

                    diffOptions(
                            existing.options(),
                            newProduct.options(),
                            changedAt,
                            allAddedOptions,
                            allRemovedOptions,
                            allRetainedOptions);
                }
            }
        }

        List<LegacyProduct> removed = new ArrayList<>();
        for (LegacyProduct remaining : existingById.values()) {
            remaining.delete(changedAt);
            removed.add(remaining);
            for (LegacyProductOption option : remaining.options()) {
                option.delete(changedAt);
                allRemovedOptions.add(option);
            }
        }

        LegacyProductOptionDiff optionDiff =
                LegacyProductOptionDiff.of(
                        allAddedOptions, allRemovedOptions, allRetainedOptions, changedAt);

        return LegacyProductDiff.of(added, removed, retained, optionDiff, changedAt);
    }

    /**
     * 옵션 nested diff: optionGroupId:optionDetailId 기준으로 비교합니다.
     *
     * <p>유지 대상 옵션은 additionalPrice가 변경되었으면 in-place 갱신합니다.
     */
    private void diffOptions(
            List<LegacyProductOption> existingOptions,
            List<LegacyProductOption> newOptions,
            Instant changedAt,
            List<LegacyProductOption> addedAccumulator,
            List<LegacyProductOption> removedAccumulator,
            List<LegacyProductOption> retainedAccumulator) {

        Map<String, LegacyProductOption> existingByKey = new LinkedHashMap<>();
        for (LegacyProductOption option : existingOptions) {
            existingByKey.put(optionKey(option), option);
        }

        for (LegacyProductOption newOption : newOptions) {
            String key = optionKey(newOption);
            LegacyProductOption existing = existingByKey.remove(key);
            if (existing != null) {
                if (existing.additionalPrice() != newOption.additionalPrice()) {
                    existing.updateAdditionalPrice(newOption.additionalPrice());
                }
                retainedAccumulator.add(existing);
            } else {
                addedAccumulator.add(newOption);
            }
        }

        for (LegacyProductOption remaining : existingByKey.values()) {
            remaining.delete(changedAt);
            removedAccumulator.add(remaining);
        }
    }

    private String optionKey(LegacyProductOption option) {
        LegacyOptionGroupId groupId = option.optionGroupId();
        LegacyOptionDetailId detailId = option.optionDetailId();
        return groupId.value() + ":" + detailId.value();
    }

    public List<LegacyProduct> products() {
        return products;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
