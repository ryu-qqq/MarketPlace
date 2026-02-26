package com.ryuqq.marketplace.application.brandpreset.port.out.query;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.util.List;
import java.util.Optional;

/** BrandPreset Query Port. */
public interface BrandPresetQueryPort {
    Optional<BrandPreset> findById(BrandPresetId id);

    List<BrandPresetResult> findByCriteria(BrandPresetSearchCriteria criteria);

    long countByCriteria(BrandPresetSearchCriteria criteria);

    List<BrandPreset> findAllByIds(List<Long> ids);

    Optional<Long> findSalesChannelIdBySalesChannelBrandId(Long salesChannelBrandId);
}
