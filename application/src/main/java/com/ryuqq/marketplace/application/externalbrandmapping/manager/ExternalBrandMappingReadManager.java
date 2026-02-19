package com.ryuqq.marketplace.application.externalbrandmapping.manager;

import com.ryuqq.marketplace.application.externalbrandmapping.port.out.query.ExternalBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingNotFoundException;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalBrandMapping Read Manager. */
@Component
public class ExternalBrandMappingReadManager {

    private final ExternalBrandMappingQueryPort queryPort;

    public ExternalBrandMappingReadManager(ExternalBrandMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ExternalBrandMapping getById(long id) {
        ExternalBrandMappingId mappingId = ExternalBrandMappingId.of(id);
        return queryPort
                .findById(mappingId)
                .orElseThrow(() -> new ExternalBrandMappingNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ExternalBrandMapping findBySourceIdAndCode(
            long externalSourceId, String externalBrandCode) {
        return queryPort
                .findByExternalSourceIdAndExternalBrandCode(externalSourceId, externalBrandCode)
                .orElseThrow(
                        () ->
                                new ExternalBrandMappingNotFoundException(
                                        externalSourceId, externalBrandCode));
    }

    @Transactional(readOnly = true)
    public List<ExternalBrandMapping> findByExternalSourceId(long externalSourceId) {
        return queryPort.findByExternalSourceId(externalSourceId);
    }

    @Transactional(readOnly = true)
    public List<ExternalBrandMapping> findByExternalSourceIdAndCodes(
            long externalSourceId, List<String> externalBrandCodes) {
        return queryPort.findByExternalSourceIdAndExternalBrandCodes(
                externalSourceId, externalBrandCodes);
    }

    @Transactional(readOnly = true)
    public List<ExternalBrandMapping> findByCriteria(ExternalBrandMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ExternalBrandMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
