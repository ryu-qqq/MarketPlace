package com.ryuqq.marketplace.application.brandmapping.port.out.query;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** BrandMapping Query Port. */
public interface BrandMappingQueryPort {
    Optional<BrandMapping> findById(BrandMappingId id);

    List<BrandMapping> findByCriteria(BrandMappingSearchCriteria criteria);

    long countByCriteria(BrandMappingSearchCriteria criteria);

    boolean existsBySalesChannelBrandId(Long salesChannelBrandId);
}
