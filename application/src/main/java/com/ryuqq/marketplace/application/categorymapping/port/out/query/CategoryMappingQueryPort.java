package com.ryuqq.marketplace.application.categorymapping.port.out.query;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** CategoryMapping Query Port. */
public interface CategoryMappingQueryPort {
    Optional<CategoryMapping> findById(CategoryMappingId id);

    List<CategoryMapping> findByCriteria(CategoryMappingSearchCriteria criteria);

    long countByCriteria(CategoryMappingSearchCriteria criteria);

    boolean existsBySalesChannelCategoryId(Long salesChannelCategoryId);
}
