package com.ryuqq.marketplace.application.inboundcategorymapping.manager;

import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.query.InboundCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundCategoryMapping Read Manager. */
@Component
public class InboundCategoryMappingReadManager {

    private final InboundCategoryMappingQueryPort queryPort;

    public InboundCategoryMappingReadManager(InboundCategoryMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public InboundCategoryMapping getById(long id) {
        InboundCategoryMappingId mappingId = InboundCategoryMappingId.of(id);
        return queryPort
                .findById(mappingId)
                .orElseThrow(() -> new InboundCategoryMappingNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public InboundCategoryMapping findBySourceIdAndCode(
            long inboundSourceId, String externalCategoryCode) {
        return queryPort
                .findByInboundSourceIdAndExternalCategoryCode(inboundSourceId, externalCategoryCode)
                .orElseThrow(
                        () ->
                                new InboundCategoryMappingNotFoundException(
                                        inboundSourceId, externalCategoryCode));
    }

    @Transactional(readOnly = true)
    public List<InboundCategoryMapping> findByInboundSourceId(long inboundSourceId) {
        return queryPort.findByInboundSourceId(inboundSourceId);
    }

    @Transactional(readOnly = true)
    public List<InboundCategoryMapping> findByInboundSourceIdAndCodes(
            long inboundSourceId, List<String> externalCategoryCodes) {
        return queryPort.findByInboundSourceIdAndExternalCategoryCodes(
                inboundSourceId, externalCategoryCodes);
    }

    @Transactional(readOnly = true)
    public List<InboundCategoryMapping> findByCriteria(
            InboundCategoryMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(InboundCategoryMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Optional<InboundCategoryMapping> findOptionalBySourceIdAndCode(
            long inboundSourceId, String externalCategoryCode) {
        return queryPort.findByInboundSourceIdAndExternalCategoryCode(
                inboundSourceId, externalCategoryCode);
    }
}
