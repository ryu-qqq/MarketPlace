package com.ryuqq.marketplace.domain.legacy.product.aggregate;

import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 레거시 상품 컬렉션 래퍼.
 *
 * <p>기존 상품 목록을 감싸며, 새로운 상품 목록과의 diff 계산을 제공합니다. productId 기준으로 매칭하여 변경/추가/삭제를 판단합니다.
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
     * 옵션/상품 diff: 새로운 상품 목록과 비교하여 persist할 대상을 계산합니다.
     *
     * <p>productId 기준 매칭:
     *
     * <ul>
     *   <li>기존에만 있는 productId → soft-delete (product + options)
     *   <li>새 요청에만 있는 productId(null) → insert (product + options)
     *   <li>양쪽 모두 있는 productId → 옵션 비교 후 변경분만 persist
     * </ul>
     *
     * @param newProducts 새로운 상품 목록
     * @param changedAt 변경 시각
     * @return diff 결과
     */
    public ProductDiffResult diff(List<LegacyProduct> newProducts, Instant changedAt) {
        Map<Long, LegacyProduct> existingById = new LinkedHashMap<>();
        for (LegacyProduct product : products) {
            if (product.idValue() != null) {
                existingById.put(product.idValue(), product);
            }
        }

        List<LegacyProduct> productsToPersist = new ArrayList<>();
        List<LegacyProductOption> optionsToPersist = new ArrayList<>();

        for (LegacyProduct newProduct : newProducts) {
            Long productId = newProduct.idValue();
            if (productId == null) {
                // 신규 상품
                productsToPersist.add(newProduct);
                optionsToPersist.addAll(newProduct.options());
            } else {
                LegacyProduct existing = existingById.remove(productId);
                if (existing != null) {
                    // 기존 상품 → 재고 변경 + 옵션 diff
                    if (existing.stockQuantity() != newProduct.stockQuantity()) {
                        existing.updateStock(newProduct.stockQuantity());
                        productsToPersist.add(existing);
                    }
                    List<LegacyProductOption> optionDiff =
                            diffOptions(existing.options(), newProduct.options(), changedAt);
                    optionsToPersist.addAll(optionDiff);
                }
            }
        }

        // 기존에만 있는 상품 → soft-delete
        for (LegacyProduct remaining : existingById.values()) {
            remaining.delete(changedAt);
            productsToPersist.add(remaining);
            for (LegacyProductOption option : remaining.options()) {
                option.delete(changedAt);
                optionsToPersist.add(option);
            }
        }

        return new ProductDiffResult(productsToPersist, optionsToPersist);
    }

    private List<LegacyProductOption> diffOptions(
            List<LegacyProductOption> existingOptions,
            List<LegacyProductOption> newOptions,
            Instant changedAt) {
        Map<String, LegacyProductOption> existingByKey = new LinkedHashMap<>();
        for (LegacyProductOption option : existingOptions) {
            existingByKey.put(optionKey(option), option);
        }

        List<LegacyProductOption> toPersist = new ArrayList<>();

        for (LegacyProductOption newOption : newOptions) {
            String key = optionKey(newOption);
            LegacyProductOption existing = existingByKey.remove(key);
            if (existing == null) {
                // 신규 옵션
                toPersist.add(newOption);
            }
            // 동일한 key의 옵션은 변경 없음 → skip
        }

        // 기존에만 있는 옵션 → soft-delete
        for (LegacyProductOption remaining : existingByKey.values()) {
            remaining.delete(changedAt);
            toPersist.add(remaining);
        }

        return toPersist;
    }

    private String optionKey(LegacyProductOption option) {
        return option.optionGroupId().value() + ":" + option.optionDetailId().value();
    }

    public List<LegacyProduct> products() {
        return products;
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    /** 상품/옵션 diff 결과. */
    public record ProductDiffResult(
            List<LegacyProduct> productsToPersist, List<LegacyProductOption> optionsToPersist) {

        public boolean hasChanges() {
            return !productsToPersist.isEmpty() || !optionsToPersist.isEmpty();
        }
    }
}
