package com.ryuqq.marketplace.adapter.out.persistence.product.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.id.ProductOptionMappingId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Product JPA Entity Mapper.
 *
 * <p>PER-MAP-001: @Component 매퍼, 순수 변환, 도메인 reconstitute() 사용.
 */
@Component
public class ProductJpaEntityMapper {

    public ProductJpaEntity toEntity(Product domain) {
        return ProductJpaEntity.create(
                domain.idValue(),
                domain.productGroupIdValue(),
                domain.skuCodeValue(),
                domain.regularPriceValue(),
                domain.currentPriceValue(),
                domain.salePriceValue(),
                domain.discountRate(),
                domain.stockQuantity(),
                domain.status().name(),
                domain.sortOrder(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public ProductOptionMappingJpaEntity toMappingEntity(ProductOptionMapping mapping) {
        return ProductOptionMappingJpaEntity.create(
                mapping.idValue(), mapping.productIdValue(), mapping.sellerOptionValueIdValue());
    }

    public ProductOptionMappingJpaEntity toMappingEntity(
            ProductOptionMapping mapping, Long productId) {
        return ProductOptionMappingJpaEntity.create(
                mapping.idValue(), productId, mapping.sellerOptionValueIdValue());
    }

    public Product toDomain(ProductJpaEntity entity, List<ProductOptionMappingJpaEntity> mappings) {
        List<ProductOptionMapping> domainMappings =
                mappings.stream().map(this::toMappingDomain).toList();

        return Product.reconstitute(
                ProductId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                SkuCode.of(entity.getSkuCode()),
                Money.of(entity.getRegularPrice()),
                Money.of(entity.getCurrentPrice()),
                entity.getSalePrice() != null ? Money.of(entity.getSalePrice()) : null,
                entity.getDiscountRate(),
                entity.getStockQuantity(),
                ProductStatus.valueOf(entity.getStatus()),
                entity.getSortOrder(),
                domainMappings,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ProductOptionMapping toMappingDomain(ProductOptionMappingJpaEntity entity) {
        return ProductOptionMapping.reconstitute(
                ProductOptionMappingId.of(entity.getId()),
                ProductId.of(entity.getProductId()),
                SellerOptionValueId.of(entity.getSellerOptionValueId()));
    }
}
