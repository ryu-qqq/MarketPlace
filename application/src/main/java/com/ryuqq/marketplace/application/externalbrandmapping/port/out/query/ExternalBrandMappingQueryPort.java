package com.ryuqq.marketplace.application.externalbrandmapping.port.out.query;

import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** ExternalBrandMapping 조회 포트. */
public interface ExternalBrandMappingQueryPort {

    Optional<ExternalBrandMapping> findById(ExternalBrandMappingId id);

    Optional<ExternalBrandMapping> findByExternalSourceIdAndExternalBrandCode(
            Long externalSourceId, String externalBrandCode);

    List<ExternalBrandMapping> findByExternalSourceId(Long externalSourceId);

    List<ExternalBrandMapping> findByExternalSourceIdAndExternalBrandCodes(
            Long externalSourceId, List<String> externalBrandCodes);

    List<ExternalBrandMapping> findByCriteria(ExternalBrandMappingSearchCriteria criteria);

    long countByCriteria(ExternalBrandMappingSearchCriteria criteria);
}
