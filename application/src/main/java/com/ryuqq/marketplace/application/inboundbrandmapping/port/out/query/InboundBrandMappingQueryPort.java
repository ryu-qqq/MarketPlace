package com.ryuqq.marketplace.application.inboundbrandmapping.port.out.query;

import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;

/** InboundBrandMapping 조회 포트. */
public interface InboundBrandMappingQueryPort {

    Optional<InboundBrandMapping> findById(InboundBrandMappingId id);

    Optional<InboundBrandMapping> findByInboundSourceIdAndExternalBrandCode(
            Long inboundSourceId, String externalBrandCode);

    List<InboundBrandMapping> findByInboundSourceId(Long inboundSourceId);

    List<InboundBrandMapping> findByInboundSourceIdAndExternalBrandCodes(
            Long inboundSourceId, List<String> externalBrandCodes);

    List<InboundBrandMapping> findByCriteria(InboundBrandMappingSearchCriteria criteria);

    long countByCriteria(InboundBrandMappingSearchCriteria criteria);
}
