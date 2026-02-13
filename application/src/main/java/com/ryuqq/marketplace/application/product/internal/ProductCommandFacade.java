package com.ryuqq.marketplace.application.product.internal;

import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductOptionMappingCommandManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Command Facade.
 *
 * <p>Product 저장 → ProductOptionMapping 교체를 조율합니다.
 */
@Component
public class ProductCommandFacade {

    private final ProductCommandManager productCommandManager;
    private final ProductOptionMappingCommandManager optionMappingCommandManager;

    public ProductCommandFacade(
            ProductCommandManager productCommandManager,
            ProductOptionMappingCommandManager optionMappingCommandManager) {
        this.productCommandManager = productCommandManager;
        this.optionMappingCommandManager = optionMappingCommandManager;
    }

    /**
     * Product + OptionMapping 저장.
     *
     * <p>1. Product 저장 → productId 획득
     *
     * <p>2. 기존 매핑 삭제 (수정 시)
     *
     * <p>3. 새 매핑 저장
     *
     * @param product Product 도메인 객체
     * @return 저장된 productId
     */
    @Transactional
    public Long persist(Product product) {
        Long productId = productCommandManager.persist(product);

        if (product.idValue() != null) {
            optionMappingCommandManager.deleteByProductId(productId);
        }

        optionMappingCommandManager.persistAll(productId, product.optionMappings());

        return productId;
    }

    /**
     * Product 일괄 저장 + OptionMapping 교체.
     *
     * <p>1. Products 일괄 저장 → productIds 획득
     *
     * <p>2. 기존 상품의 매핑 삭제
     *
     * <p>3. 새 매핑 저장
     *
     * @param products Product 도메인 객체 목록
     */
    @Transactional
    public void persistAll(List<Product> products) {
        List<Long> productIds = productCommandManager.persistAll(products);

        List<Long> existingProductIds = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).idValue() != null) {
                existingProductIds.add(productIds.get(i));
            }
        }

        if (!existingProductIds.isEmpty()) {
            optionMappingCommandManager.deleteByProductIdIn(existingProductIds);
        }

        for (int i = 0; i < products.size(); i++) {
            optionMappingCommandManager.persistAll(
                    productIds.get(i), products.get(i).optionMappings());
        }
    }
}
