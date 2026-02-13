package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;

/**
 * 상품 생성 데이터.
 *
 * <p>ProductGroup persist 후 ProductGroupId를 받아서 Product를 생성하기 위한 데이터입니다. Registration/Update 양쪽에서
 * 공통으로 사용합니다.
 */
public record ProductCreationData(
        SkuCode skuCode,
        Money regularPrice,
        Money currentPrice,
        Money salePrice,
        int discountRate,
        int stockQuantity,
        int sortOrder,
        List<ProductOptionMapping> optionMappings) {

    public Product toProduct(ProductGroupId productGroupId, Instant now) {
        return Product.forNew(
                productGroupId,
                skuCode,
                regularPrice,
                currentPrice,
                salePrice,
                discountRate,
                stockQuantity,
                sortOrder,
                optionMappings,
                now);
    }
}
