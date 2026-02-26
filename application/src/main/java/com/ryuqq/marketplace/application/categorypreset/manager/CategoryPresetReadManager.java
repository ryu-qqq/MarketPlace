package com.ryuqq.marketplace.application.categorypreset.manager;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetQueryPort;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryPreset Read Manager. */
@Component
public class CategoryPresetReadManager {

    private final CategoryPresetQueryPort queryPort;

    public CategoryPresetReadManager(CategoryPresetQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public CategoryPreset getById(CategoryPresetId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CategoryPresetNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<CategoryPresetResult> findByCriteria(CategoryPresetSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CategoryPresetSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public List<CategoryPreset> findAllByIds(List<Long> ids) {
        return queryPort.findAllByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<Long> findSalesChannelCategoryIdByCode(
            Long salesChannelId, String categoryCode) {
        return queryPort.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);
    }
}
