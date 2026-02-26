package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.ProductCreations;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.List;
import org.springframework.stereotype.Component;

/** Product 생성 데이터 서브 팩토리. */
@Component
public class ProductCreationDataFactory {

    public ProductCreations create(List<ProductData> productDataList) {
        List<ProductCreationData> creationDataList =
                productDataList.stream()
                        .map(
                                productData ->
                                        new ProductCreationData(
                                                SkuCode.of(productData.skuCode()),
                                                Money.of(productData.regularPrice()),
                                                Money.of(productData.currentPrice()),
                                                productData.stockQuantity(),
                                                productData.sortOrder(),
                                                productData.resolvedOptionValueIds()))
                        .toList();

        return ProductCreations.of(creationDataList);
    }

    public record ProductData(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SellerOptionValueId> resolvedOptionValueIds) {}
}
