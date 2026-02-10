package com.ryuqq.marketplace.application.categorypreset.port.out.query;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.util.List;
import java.util.Optional;

/** CategoryPreset Query Port. */
public interface CategoryPresetQueryPort {
    Optional<CategoryPreset> findById(CategoryPresetId id);

    List<CategoryPresetResult> findByCriteria(CategoryPresetSearchCriteria criteria);

    long countByCriteria(CategoryPresetSearchCriteria criteria);

    List<CategoryPreset> findAllByIds(List<Long> ids);

    Optional<Long> findSalesChannelCategoryIdByCode(Long salesChannelId, String categoryCode);
}
