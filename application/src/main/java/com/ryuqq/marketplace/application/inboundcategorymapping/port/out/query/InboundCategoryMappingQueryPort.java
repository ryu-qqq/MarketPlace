package com.ryuqq.marketplace.application.inboundcategorymapping.port.out.query;

import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** InboundCategoryMapping 조회 포트. */
public interface InboundCategoryMappingQueryPort {

    Optional<InboundCategoryMapping> findById(InboundCategoryMappingId id);

    Optional<InboundCategoryMapping> findByInboundSourceIdAndExternalCategoryCode(
            Long inboundSourceId, String externalCategoryCode);

    List<InboundCategoryMapping> findByInboundSourceId(Long inboundSourceId);

    List<InboundCategoryMapping> findByInboundSourceIdAndExternalCategoryCodes(
            Long inboundSourceId, List<String> externalCategoryCodes);

    List<InboundCategoryMapping> findByCriteria(InboundCategoryMappingSearchCriteria criteria);

    long countByCriteria(InboundCategoryMappingSearchCriteria criteria);
}
