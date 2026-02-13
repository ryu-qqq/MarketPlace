package com.ryuqq.marketplace.application.product.assembler;

import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.List;
import org.springframework.stereotype.Component;

/** Product Assembler. */
@Component
public class ProductAssembler {

    public ProductResult toResult(Product product) {
        return ProductResult.from(product);
    }

    public List<ProductResult> toResults(List<Product> products) {
        return products.stream().map(this::toResult).toList();
    }
}
