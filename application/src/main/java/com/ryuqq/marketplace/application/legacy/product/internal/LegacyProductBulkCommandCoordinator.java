package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 호환 Product 일괄 수정 Coordinator.
 *
 * <p>레거시에서는 상품그룹 단위로 가격/진열/품절을 변경하지만, market 스키마에서는 Product(SKU) 단위입니다. 이 Coordinator가 상품그룹 내 모든
 * Product에 일괄 적용하는 역할을 합니다.
 */
@Component
public class LegacyProductBulkCommandCoordinator {

    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;

    public LegacyProductBulkCommandCoordinator(
            ProductReadManager productReadManager, ProductCommandManager productCommandManager) {
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
    }

    /** 상품그룹 내 모든 Product의 가격을 일괄 변경합니다. */
    @Transactional
    public void updatePriceAll(
            ProductGroupId productGroupId, Money regularPrice, Money currentPrice, Instant now) {
        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        for (Product product : products) {
            product.updatePrice(regularPrice, currentPrice, now);
        }
        productCommandManager.persistAll(products);
    }

    /** 상품그룹 내 모든 Product의 상태를 일괄 변경합니다. */
    @Transactional
    public void changeStatusAll(
            ProductGroupId productGroupId, ProductStatus targetStatus, Instant now) {
        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        for (Product product : products) {
            product.changeStatus(targetStatus, now);
        }
        productCommandManager.persistAll(products);
    }

    /**
     * 상품그룹 내 모든 Product를 품절 처리합니다 (SOLD_OUT + 재고 0).
     *
     * <p>레거시 호환: INACTIVE 상품은 먼저 ACTIVE로 전이 후 SOLD_OUT 처리합니다.
     */
    @Transactional
    public void markSoldOutAll(ProductGroupId productGroupId, Instant now) {
        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        for (Product product : products) {
            if (!product.status().isActive()) {
                product.activate(now);
            }
            product.markSoldOut(now);
            product.updateStock(0, now);
        }
        productCommandManager.persistAll(products);
    }

    /** 개별 Product의 재고를 수정합니다. */
    @Transactional
    public void updateStockByProductIds(
            ProductGroupId productGroupId, Map<ProductId, Integer> stockByProductId, Instant now) {
        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        Map<Long, Product> productById =
                products.stream().collect(Collectors.toMap(Product::idValue, Function.identity()));

        for (var entry : stockByProductId.entrySet()) {
            Product product = productById.get(entry.getKey().value());
            if (product != null) {
                product.updateStock(entry.getValue(), now);
            }
        }
        productCommandManager.persistAll(products);
    }
}
