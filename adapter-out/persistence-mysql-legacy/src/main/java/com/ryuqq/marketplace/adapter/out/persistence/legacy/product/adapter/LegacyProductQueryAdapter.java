package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacyProductQueryPort;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product 조회 Adapter.
 *
 * <p>레거시 Entity → 표준 Product 도메인 변환. stock_quantity는 product 테이블에서 직접 조회.
 */
@Component
public class LegacyProductQueryAdapter implements LegacyProductQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;

    public LegacyProductQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<Product> findByProductGroupId(long productGroupId) {
        List<LegacyProductEntity> productEntities =
                queryDslRepository.findProductsByProductGroupId(productGroupId);

        if (productEntities.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = productEntities.stream().map(LegacyProductEntity::getId).toList();

        List<LegacyProductOptionEntity> optionEntities =
                queryDslRepository.findProductOptionsByProductIds(productIds);
        Map<Long, List<ProductOptionMapping>> mappingsByProductId = new LinkedHashMap<>();
        for (LegacyProductOptionEntity opt : optionEntities) {
            mappingsByProductId
                    .computeIfAbsent(opt.getProductId(), k -> new ArrayList<>())
                    .add(ProductOptionMapping.reconstitute(
                            null,
                            ProductId.of(opt.getProductId()),
                            SellerOptionValueId.of(opt.getOptionDetailId()),
                            DeletionStatus.active()));
        }

        return productEntities.stream()
                .map(entity -> Product.reconstitute(
                        ProductId.of(entity.getId()),
                        ProductGroupId.of(entity.getProductGroupId()),
                        SkuCode.of(""),
                        Money.zero(),
                        Money.zero(),
                        Money.zero(),
                        0,
                        entity.getStockQuantity() != null ? entity.getStockQuantity() : 0,
                        "Y".equals(entity.getSoldOutYn())
                                ? ProductStatus.SOLD_OUT : ProductStatus.ACTIVE,
                        0,
                        mappingsByProductId.getOrDefault(entity.getId(), List.of()),
                        null,
                        null))
                .toList();
    }
}
