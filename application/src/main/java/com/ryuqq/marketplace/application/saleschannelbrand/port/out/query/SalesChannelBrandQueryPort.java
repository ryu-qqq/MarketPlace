package com.ryuqq.marketplace.application.saleschannelbrand.port.out.query;

import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import java.util.List;
import java.util.Optional;

/** SalesChannelBrand Query Port. */
public interface SalesChannelBrandQueryPort {
    Optional<SalesChannelBrand> findById(SalesChannelBrandId id);

    List<SalesChannelBrand> findByCriteria(SalesChannelBrandSearchCriteria criteria);

    long countByCriteria(SalesChannelBrandSearchCriteria criteria);

    boolean existsBySalesChannelIdAndExternalCode(Long salesChannelId, String externalBrandCode);
}
