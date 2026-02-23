package com.ryuqq.marketplace.application.inboundbrandmapping.manager;

import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.query.InboundBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingNotFoundException;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundBrandMapping Read Manager. */
@Component
public class InboundBrandMappingReadManager {

    private final InboundBrandMappingQueryPort queryPort;

    public InboundBrandMappingReadManager(InboundBrandMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public InboundBrandMapping getById(long id) {
        InboundBrandMappingId mappingId = InboundBrandMappingId.of(id);
        return queryPort
                .findById(mappingId)
                .orElseThrow(() -> new InboundBrandMappingNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public InboundBrandMapping findBySourceIdAndCode(
            long inboundSourceId, String externalBrandCode) {
        return queryPort
                .findByInboundSourceIdAndExternalBrandCode(inboundSourceId, externalBrandCode)
                .orElseThrow(
                        () ->
                                new InboundBrandMappingNotFoundException(
                                        inboundSourceId, externalBrandCode));
    }

    @Transactional(readOnly = true)
    public List<InboundBrandMapping> findByInboundSourceId(long inboundSourceId) {
        return queryPort.findByInboundSourceId(inboundSourceId);
    }

    @Transactional(readOnly = true)
    public List<InboundBrandMapping> findByInboundSourceIdAndCodes(
            long inboundSourceId, List<String> externalBrandCodes) {
        return queryPort.findByInboundSourceIdAndExternalBrandCodes(
                inboundSourceId, externalBrandCodes);
    }

    @Transactional(readOnly = true)
    public List<InboundBrandMapping> findByCriteria(InboundBrandMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(InboundBrandMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Optional<InboundBrandMapping> findOptionalBySourceIdAndCode(
            long inboundSourceId, String externalBrandCode) {
        return queryPort.findByInboundSourceIdAndExternalBrandCode(
                inboundSourceId, externalBrandCode);
    }
}
