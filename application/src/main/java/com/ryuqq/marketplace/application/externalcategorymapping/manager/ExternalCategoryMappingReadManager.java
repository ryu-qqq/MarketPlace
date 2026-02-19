package com.ryuqq.marketplace.application.externalcategorymapping.manager;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.port.out.query.ExternalCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalCategoryMapping Read Manager. */
@Component
public class ExternalCategoryMappingReadManager {

    private final ExternalCategoryMappingQueryPort queryPort;

    public ExternalCategoryMappingReadManager(ExternalCategoryMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ExternalCategoryMapping getById(long id) {
        ExternalCategoryMappingId mappingId = ExternalCategoryMappingId.of(id);
        return queryPort
                .findById(mappingId)
                .orElseThrow(() -> new ExternalCategoryMappingNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ExternalCategoryMapping findBySourceIdAndCode(
            long externalSourceId, String externalCategoryCode) {
        return queryPort
                .findByExternalSourceIdAndExternalCategoryCode(
                        externalSourceId, externalCategoryCode)
                .orElseThrow(
                        () ->
                                new ExternalCategoryMappingNotFoundException(
                                        externalSourceId, externalCategoryCode));
    }

    @Transactional(readOnly = true)
    public List<ExternalCategoryMapping> findByExternalSourceId(long externalSourceId) {
        return queryPort.findByExternalSourceId(externalSourceId);
    }

    @Transactional(readOnly = true)
    public List<ExternalCategoryMapping> findByCriteria(
            ExternalCategoryMappingSearchParams params) {
        return queryPort.findByCriteria(params);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ExternalCategoryMappingSearchParams params) {
        return queryPort.countByCriteria(params);
    }
}
