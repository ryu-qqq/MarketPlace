package com.ryuqq.marketplace.application.category.manager;

import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Category Read Manager. */
@Component
public class CategoryReadManager {

    private final CategoryQueryPort queryPort;

    public CategoryReadManager(CategoryQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Category getById(CategoryId id) {
        return queryPort.findById(id).orElseThrow(() -> new CategoryNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Category> findByCriteria(CategorySearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CategorySearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return queryPort.existsByCode(code);
    }

    @Transactional(readOnly = true)
    public List<Category> findAllByIds(List<Long> ids) {
        return queryPort.findAllByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<Long> expandWithDescendants(List<Long> categoryIds) {
        return queryPort.findDescendantIds(categoryIds);
    }
}
