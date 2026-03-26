package com.ryuqq.marketplace.application.product.internal;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductOptionMappingCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.ProductDiff;
import com.ryuqq.marketplace.domain.product.vo.ProductUpdateData;
import com.ryuqq.marketplace.domain.product.vo.Products;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Command Coordinator.
 *
 * <p>Product + OptionMapping 등록/수정을 조율합니다. 비즈니스 판단(diff)은 도메인 {@link Products} VO에 위임하고, 이름 기반 옵션
 * resolve는 {@link ProductCommandFactory}에 위임합니다.
 *
 * <p>도메인 객체 기반 등록: {@link #register(List)}
 *
 * <p>이름 기반 옵션 resolve 등록: {@link #registerWithOptionResolve}
 *
 * <p>도메인 diff 기반 수정: {@link #update}
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
     * 도메인 객체 기반 Product + OptionMapping 등록.
     *
     * @param products Product 도메인 객체 목록
     * @return 생성된 Product ID 목록
     */
    @Transactional
    public List<Long> register(List<Product> products) {
        List<Long> productIds = productCommandManager.persistAll(products);

        for (int i = 0; i < products.size(); i++) {
            optionMappingCommandManager.persistAllForProduct(
                    productIds.get(i), products.get(i).optionMappings());
        }

        return productIds;
    }

    /**
     * 등록 데이터 기반 Product 생성 + 이름 기반 옵션 resolve + 등록.
     *
     * <p>Factory에서 이름 → SellerOptionValueId resolve를 수행한 후 Product 도메인 객체를 생성합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param products 상품 등록 데이터 목록
     * @param optionGroups 옵션 그룹 등록 데이터 (이름 → ID resolve용)
     * @param allOptionValueIds persist된 SellerOptionValueId 목록 (그룹 순서대로 플랫)
     * @param createdAt 생성 시각
     * @return 생성된 Product ID 목록
     */
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

    /**
     * 도메인 diff 기반 Product 수정.
     *
     * <p>기존 Products를 조회하고, 도메인 VO가 diff를 판단한 후 결과를 영속화합니다.
     *
     * @param pgId 상품 그룹 ID
     * @param updateData resolve 완료된 수정 데이터
     * @return 신규 추가된 Product ID 목록 (없으면 빈 리스트)
     */
    @Transactional
    public List<Long> update(ProductGroupId pgId, ProductUpdateData updateData) {
        Products existing =
                Products.reconstitute(pgId, productReadManager.findByProductGroupId(pgId));
        ProductDiff diff = existing.update(updateData);
        productCommandManager.persistAll(diff.allDirtyProducts());
        for (Product removed : diff.removed()) {
            optionMappingCommandManager.persistAll(removed.optionMappings());
        }
        return register(diff.added());
    }
}
