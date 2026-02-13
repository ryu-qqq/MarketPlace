package com.ryuqq.marketplace.application.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import java.util.List;

/** ProductOptionMapping Command Port. */
public interface ProductOptionMappingCommandPort {

    void deleteByProductId(Long productId);

    void deleteByProductIdIn(List<Long> productIds);

    void persistAll(Long productId, List<ProductOptionMapping> mappings);
}
