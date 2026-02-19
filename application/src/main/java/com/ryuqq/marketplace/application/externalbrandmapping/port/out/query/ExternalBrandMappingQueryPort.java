package com.ryuqq.marketplace.application.externalbrandmapping.port.out.query;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import java.util.List;
import java.util.Optional;

/** ExternalBrandMapping 조회 포트. */
public interface ExternalBrandMappingQueryPort {

    Optional<ExternalBrandMapping> findById(ExternalBrandMappingId id);

    Optional<ExternalBrandMapping> findByExternalSourceIdAndExternalBrandCode(
            Long externalSourceId, String externalBrandCode);

    List<ExternalBrandMapping> findByExternalSourceId(Long externalSourceId);

    List<ExternalBrandMapping> findByCriteria(ExternalBrandMappingSearchParams params);

    long countByCriteria(ExternalBrandMappingSearchParams params);
}
