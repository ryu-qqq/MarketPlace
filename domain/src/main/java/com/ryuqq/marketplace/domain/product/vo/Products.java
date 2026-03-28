package com.ryuqq.marketplace.domain.product.vo;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Product 컬렉션 VO.
 *
 * <p>ProductGroupImages, SellerOptionGroups와 동일한 패턴으로, Product 컬렉션을 관리하며 update 시 자체적으로 Diff를
 * 생성합니다.
 *
 * <p>Coordinator는 이 VO가 반환한 {@link ProductDiff}만 받아서 영속화를 처리합니다.
 */
public class Products {

    private final ProductGroupId productGroupId;
    private final List<Product> products;

    private Products(ProductGroupId productGroupId, List<Product> products) {
        this.productGroupId = productGroupId;
        this.products = products;
    }

    /** 영속성에서 복원 시 사용. */
    public static Products reconstitute(ProductGroupId productGroupId, List<Product> products) {
        return new Products(productGroupId, List.copyOf(products));
    }

    /**
     * ID 기반 diff.
     *
     * <p>entry의 productId로 기존 Product를 매칭합니다.
     *
     * <ul>
     *   <li>productId != null → retained: 가격/재고/SKU/정렬 갱신
     *   <li>productId == null → added: 신규 Product 생성
     *   <li>매칭 안 됨 → removed: soft delete
     * </ul>
     *
     * @param updateData resolve 완료된 수정 데이터
     * @return ProductDiff (added/removed/retained 분류 결과)
     */
    public ProductDiff update(ProductUpdateData updateData) {
        Map<Long, Product> existingById =
                products.stream().collect(Collectors.toMap(Product::idValue, p -> p));

        List<Product> retained = new ArrayList<>();
        List<Product> added = new ArrayList<>();
        Set<Long> matchedIds = new HashSet<>();

        for (ProductUpdateData.Entry entry : updateData.entries()) {
            if (entry.productId() != null) {
                Product existing = existingById.get(entry.productId());
                if (existing == null) {
                    throw new ProductNotFoundException(entry.productId());
                }
                SkuCode skuCode = entry.skuCode() != null ? entry.skuCode() : existing.skuCode();
                existing.update(
                        skuCode,
                        entry.regularPrice(),
                        entry.currentPrice(),
                        entry.stockQuantity(),
                        entry.sortOrder(),
                        updateData.updatedAt());

                if (!entry.resolvedOptionValueIds().isEmpty()) {
                    List<ProductOptionMapping> newMappings =
                            entry.resolvedOptionValueIds().stream()
                                    .map(
                                            valueId ->
                                                    ProductOptionMapping.forNew(
                                                            existing.id(), valueId))
                                    .toList();
                    existing.replaceOptionMappings(newMappings, updateData.updatedAt());
                }

                retained.add(existing);
                matchedIds.add(entry.productId());
            } else {
                ProductCreationData creationData =
                        new ProductCreationData(
                                entry.skuCode(),
                                entry.regularPrice(),
                                entry.currentPrice(),
                                entry.stockQuantity(),
                                entry.sortOrder(),
                                entry.resolvedOptionValueIds());
                added.add(creationData.toProduct(productGroupId, updateData.updatedAt()));
            }
        }

        List<Product> removed = new ArrayList<>();
        for (Product product : products) {
            if (!matchedIds.contains(product.idValue())) {
                product.delete(updateData.updatedAt());
                removed.add(product);
            }
        }

        return ProductDiff.of(added, removed, retained, updateData.updatedAt());
    }

    // ── 조회 ──

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public List<Product> toList() {
        return Collections.unmodifiableList(products);
    }

    public int size() {
        return products.size();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }
}
