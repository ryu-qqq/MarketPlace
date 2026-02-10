package com.ryuqq.marketplace.application.categorymapping.manager;

import com.ryuqq.marketplace.application.categorymapping.port.out.query.CategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryMapping Read Manager. */
@Component
public class CategoryMappingReadManager {

    private final CategoryMappingQueryPort queryPort;

    public CategoryMappingReadManager(CategoryMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public CategoryMapping getById(CategoryMappingId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CategoryMappingNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<CategoryMapping> findByCriteria(CategoryMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CategoryMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelCategoryId(Long salesChannelCategoryId) {
        return queryPort.existsBySalesChannelCategoryId(salesChannelCategoryId);
    }
}
