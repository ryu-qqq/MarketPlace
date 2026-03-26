package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductOptionCommandManager;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 Product(SKU) Command Coordinator.
 *
 * <p>표준 ProductCommandCoordinator와 동일한 패턴. 옵션 resolve된 SellerOptionValueId를 받아 Product +
 * OptionMapping 저장.
 */
@Component
public class LegacyProductCommandCoordinator {

    private final ProductCommandFactory productCommandFactory;
    private final LegacyProductCommandManager productCommandManager;
    private final LegacyProductOptionCommandManager productOptionCommandManager;

    public LegacyProductCommandCoordinator(
            ProductCommandFactory productCommandFactory,
            LegacyProductCommandManager productCommandManager,
            LegacyProductOptionCommandManager productOptionCommandManager) {
        this.productCommandFactory = productCommandFactory;
        this.productCommandManager = productCommandManager;
        this.productOptionCommandManager = productOptionCommandManager;
    }

    /** 이름 기반 옵션 resolve 후 등록. 표준 ProductCommandCoordinator.registerWithOptionResolve와 동일. */
    @Transactional
    public List<Long> registerWithOptionResolve(
            long productGroupId,
            List<RegisterProductsCommand.ProductData> products,
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> allOptionValueIds,
            Instant createdAt) {
        ProductGroupId pgId = ProductGroupId.of(productGroupId);

        List<ProductCreationData> creationDataList =
                productCommandFactory.toCreationDataList(products, optionGroups, allOptionValueIds);

        List<Product> domainProducts =
                creationDataList.stream().map(data -> data.toProduct(pgId, createdAt)).toList();

        return register(domainProducts);
    }

    /** 도메인 객체 기반 Product + OptionMapping 등록. */
    @Transactional
    public List<Long> register(List<Product> products) {
        List<Long> productIds = new ArrayList<>();
        for (Product product : products) {
            Long productId = productCommandManager.persist(product);
            productIds.add(productId);

            ProductId pId = ProductId.of(productId);
            for (ProductOptionMapping mapping : product.optionMappings()) {
                ProductOptionMapping withProductId =
                        ProductOptionMapping.forNew(pId, mapping.sellerOptionValueId());
                productOptionCommandManager.persist(withProductId);
            }
        }
        return productIds;
    }

    /** 수정 시 사용. 기존 product/option soft delete 후 새로 등록. */
    @Transactional
    public List<Long> update(
            long productGroupId,
            List<RegisterProductsCommand.ProductData> products,
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> allOptionValueIds,
            Instant createdAt) {
        productCommandManager.softDeleteByProductGroupId(productGroupId);
        return registerWithOptionResolve(
                productGroupId, products, optionGroups, allOptionValueIds, createdAt);
    }
}
