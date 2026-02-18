package com.ryuqq.marketplace.application.product.internal;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductOptionMappingCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Command Coordinator.
 *
 * <p>Product + OptionMapping 등록/수정을 조율합니다.
 *
 * <p>Command 기반 등록: {@link #register(RegisterProductsCommand)} — Factory → Product 생성 → persist
 *
 * <p>OptionGroup diff 기반 수정: {@link #updateWithDiff} — productId 기반 매칭 → retained/added/removed 분류
 * → persist
 */
@Component
public class ProductCommandCoordinator {

    private final ProductCommandFactory productCommandFactory;
    private final ProductCommandManager productCommandManager;
    private final ProductOptionMappingCommandManager optionMappingCommandManager;
    private final ProductReadManager productReadManager;

    public ProductCommandCoordinator(
            ProductCommandFactory productCommandFactory,
            ProductCommandManager productCommandManager,
            ProductOptionMappingCommandManager optionMappingCommandManager,
            ProductReadManager productReadManager) {
        this.productCommandFactory = productCommandFactory;
        this.productCommandManager = productCommandManager;
        this.optionMappingCommandManager = optionMappingCommandManager;
        this.productReadManager = productReadManager;
    }

    /**
     * Command 기반 Product + OptionMapping 등록.
     *
     * <p>Factory를 통해 도메인 객체를 생성한 후 persist합니다.
     *
     * @param command Product 등록 Command (productGroupId + products + allOptionValueIds)
     * @return 생성된 Product ID 목록
     */
    @Transactional
    public List<Long> register(RegisterProductsCommand command) {
        List<Product> products = productCommandFactory.createProducts(command);
        return register(products);
    }

    /**
     * 도메인 객체 기반 Product + OptionMapping 등록.
     *
     * @param products Product 도메인 객체 목록
     * @return 생성된 Product ID 목록
     */
    @Transactional
    public List<Long> register(List<Product> products) {
        List<Long> productIds = productCommandManager.persistAll(products);

        for (Product product : products) {
            optionMappingCommandManager.persistAll(product.optionMappings());
        }

        return productIds;
    }

    /**
     * SellerOption 수정 결과 기반 Product 수정.
     *
     * <p>productId 기반으로 기존 Product와 새 ProductData를 매칭합니다.
     *
     * <ul>
     *   <li>retained: productId가 non-null이고 기존에 존재 → 가격/재고/SKU/정렬 갱신
     *   <li>added: productId가 null → Product 신규 생성
     *   <li>removed: entries에 포함되지 않은 기존 Product → soft delete
     * </ul>
     *
     * @param pgId 상품 그룹 ID
     * @param entries Product diff 엔트리 목록 (productId 포함)
     * @param optionResult SellerOption 수정 결과 (resolved 활성 ValueId 목록 포함)
     */
    @Transactional
    public void updateWithDiff(
            ProductGroupId pgId,
            List<ProductDiffUpdateEntry> entries,
            SellerOptionUpdateResult optionResult) {
        Instant now = optionResult.occurredAt();
        List<SellerOptionValueId> allActiveValueIds = optionResult.resolvedActiveValueIds();

        List<Product> existingProducts = productReadManager.findByProductGroupId(pgId);
        Map<Long, Product> existingById =
                existingProducts.stream().collect(Collectors.toMap(Product::idValue, p -> p));

        List<Product> retained = new ArrayList<>();
        List<Product> added = new ArrayList<>();
        Set<Long> matchedProductIds = new HashSet<>();

        for (ProductDiffUpdateEntry entry : entries) {
            if (entry.productId() != null) {
                Product existing = existingById.get(entry.productId());
                if (existing == null) {
                    throw new ProductNotFoundException(entry.productId());
                }
                existing.update(
                        SkuCode.of(entry.skuCode()),
                        Money.of(entry.regularPrice()),
                        Money.of(entry.currentPrice()),
                        entry.stockQuantity(),
                        entry.sortOrder(),
                        now);
                retained.add(existing);
                matchedProductIds.add(entry.productId());
            } else {
                ProductCreationData creationData = productCommandFactory.toCreationData(entry);
                Product newProduct = creationData.toProduct(pgId, allActiveValueIds, now);
                added.add(newProduct);
            }
        }

        List<Product> removed = new ArrayList<>();
        for (Product product : existingProducts) {
            if (!matchedProductIds.contains(product.idValue())) {
                product.delete(now);
                removed.add(product);
            }
        }

        productCommandManager.persistAll(retained);
        productCommandManager.persistAll(removed);
        register(added);
    }
}
