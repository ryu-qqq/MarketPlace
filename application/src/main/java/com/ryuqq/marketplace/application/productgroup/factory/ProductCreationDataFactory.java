package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductCreationData;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductCreations;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.List;
import org.springframework.stereotype.Component;

/** Product 생성 데이터 서브 팩토리. */
@Component
public class ProductCreationDataFactory {

    public ProductCreations create(
            List<ProductData> productDataList, SellerOptionGroups optionGroups) {
        List<SellerOptionValue> allOptionValues = optionGroups.allOptionValues();

        List<ProductCreationData> creationDataList =
                productDataList.stream()
                        .map(
                                productData -> {
                                    SkuCode skuCode = SkuCode.of(productData.skuCode());
                                    Money regularPrice = Money.of(productData.regularPrice());
                                    Money currentPrice = Money.of(productData.currentPrice());
                                    Money salePrice =
                                            productData.salePrice() > 0
                                                    ? Money.of(productData.salePrice())
                                                    : null;

                                    ProductId tempProductId = ProductId.forNew();

                                    List<ProductOptionMapping> optionMappings =
                                            productData.optionIndices().stream()
                                                    .map(
                                                            index -> {
                                                                SellerOptionValue optionValue =
                                                                        allOptionValues.get(index);
                                                                return ProductOptionMapping.forNew(
                                                                        tempProductId,
                                                                        optionValue.id());
                                                            })
                                                    .toList();

                                    return new ProductCreationData(
                                            skuCode,
                                            regularPrice,
                                            currentPrice,
                                            salePrice,
                                            productData.discountRate(),
                                            productData.stockQuantity(),
                                            productData.sortOrder(),
                                            optionMappings);
                                })
                        .toList();

        return ProductCreations.of(creationDataList);
    }

    public record ProductData(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int salePrice,
            int discountRate,
            int stockQuantity,
            int sortOrder,
            List<Integer> optionIndices) {}
}
