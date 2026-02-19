package com.ryuqq.marketplace.application.externalcategorymapping.port.out.query;

import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** ExternalCategoryMapping 조회 포트. */
public interface ExternalCategoryMappingQueryPort {

    Optional<ExternalCategoryMapping> findById(ExternalCategoryMappingId id);

    Optional<ExternalCategoryMapping> findByExternalSourceIdAndExternalCategoryCode(
            Long externalSourceId, String externalCategoryCode);

    List<ExternalCategoryMapping> findByExternalSourceId(Long externalSourceId);

    List<ExternalCategoryMapping> findByExternalSourceIdAndExternalCategoryCodes(
            Long externalSourceId, List<String> externalCategoryCodes);

    List<ExternalCategoryMapping> findByCriteria(ExternalCategoryMappingSearchCriteria criteria);

    long countByCriteria(ExternalCategoryMappingSearchCriteria criteria);
}
