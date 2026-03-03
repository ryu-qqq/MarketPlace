package com.ryuqq.marketplace.application.category.port.out.query;

import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import java.util.List;
import java.util.Optional;

/** Category Query Port. */
public interface CategoryQueryPort {
    Optional<Category> findById(CategoryId id);

    List<Category> findByCriteria(CategorySearchCriteria criteria);

    long countByCriteria(CategorySearchCriteria criteria);

    boolean existsByCode(String code);

    List<Category> findAllByIds(List<Long> ids);

    List<Long> findDescendantIds(List<Long> ancestorCategoryIds);
}
