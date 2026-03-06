package com.ryuqq.marketplace.application.productgroup.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import java.util.Optional;

/** ProductGroup Query Port. */
public interface ProductGroupQueryPort {

    Optional<ProductGroup> findById(ProductGroupId id);

    List<ProductGroup> findByIds(List<ProductGroupId> ids);

    List<ProductGroup> findByIdsAndSellerId(List<ProductGroupId> ids, long sellerId);

    List<ProductGroup> findByCriteria(ProductGroupSearchCriteria criteria);

    long countByCriteria(ProductGroupSearchCriteria criteria);
}
