package com.ryuqq.marketplace.domain.product.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;

/**
 * 상품 생성 데이터.
 *
 * <p>ProductGroup persist 후 ProductGroupId와 SellerOptionValueId 목록을 받아서 Product를 생성하기 위한 데이터입니다.
 * Registration/Update 양쪽에서 공통으로 사용합니다.
 *
 * <p>salePrice와 discountRate는 도메인 내부에서 자동 계산됩니다.
 *
 * <p>resolvedOptionValueIds는 이름 기반 resolve가 완료된 SellerOptionValueId 목록이며, toProduct 호출 시 직접
 * ProductOptionMapping을 생성합니다.
 */
public record ProductCreationData(
        SkuCode skuCode,
        Money regularPrice,
        Money currentPrice,
        int stockQuantity,
        int sortOrder,
        List<SellerOptionValueId> resolvedOptionValueIds) {

    /**
     * Product 도메인 객체 생성.
     *
     * @param productGroupId 확정된 ProductGroupId
     * @param now 생성 시각
     * @return Product 도메인 객체
     */
    public Product toProduct(ProductGroupId productGroupId, Instant now) {
        ProductId tempProductId = ProductId.forNew();
        List<ProductOptionMapping> optionMappings =
                resolvedOptionValueIds.stream()
                        .map(valueId -> ProductOptionMapping.forNew(tempProductId, valueId))
                        .toList();

        return Product.forNew(
                productGroupId,
                skuCode,
                regularPrice,
                currentPrice,
                stockQuantity,
                sortOrder,
                optionMappings,
                now);
    }
}
