package com.ryuqq.marketplace.application.externalcategorymapping.port.out.query;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import java.util.List;
import java.util.Optional;

/** ExternalCategoryMapping 조회 포트. */
public interface ExternalCategoryMappingQueryPort {

    Optional<ExternalCategoryMapping> findById(ExternalCategoryMappingId id);

    Optional<ExternalCategoryMapping> findByExternalSourceIdAndExternalCategoryCode(
            Long externalSourceId, String externalCategoryCode);

    List<ExternalCategoryMapping> findByExternalSourceId(Long externalSourceId);

    List<ExternalCategoryMapping> findByCriteria(ExternalCategoryMappingSearchParams params);

    long countByCriteria(ExternalCategoryMappingSearchParams params);
}
