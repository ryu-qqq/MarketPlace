package com.ryuqq.marketplace.application.product.port.out.query;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import java.util.Optional;

/** Product Query Port. */
public interface ProductQueryPort {

    Optional<Product> findById(ProductId id);

    List<Product> findByProductGroupId(ProductGroupId productGroupId);
}
