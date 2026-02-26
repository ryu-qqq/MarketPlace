package com.ryuqq.marketplace.application.saleschannelcategory.port.out.query;

import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.List;
import java.util.Optional;

/** SalesChannelCategory Query Port. */
public interface SalesChannelCategoryQueryPort {
    Optional<SalesChannelCategory> findById(SalesChannelCategoryId id);

    List<SalesChannelCategory> findByCriteria(SalesChannelCategorySearchCriteria criteria);

    long countByCriteria(SalesChannelCategorySearchCriteria criteria);

    boolean existsBySalesChannelIdAndExternalCode(Long salesChannelId, String externalCategoryCode);
}
