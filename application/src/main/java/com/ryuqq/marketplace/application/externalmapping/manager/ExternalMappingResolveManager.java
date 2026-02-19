package com.ryuqq.marketplace.application.externalmapping.manager;

import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.application.externalmapping.dto.query.ExternalMappingResolveQuery;
import com.ryuqq.marketplace.application.externalmapping.dto.response.ResolvedMappingResult;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 외부 매핑 통합 조회 Manager. */
@Component
public class ExternalMappingResolveManager {

    private final ExternalSourceReadManager sourceReadManager;
    private final ExternalBrandMappingReadManager brandMappingReadManager;
    private final ExternalCategoryMappingReadManager categoryMappingReadManager;

    public ExternalMappingResolveManager(
            ExternalSourceReadManager sourceReadManager,
            ExternalBrandMappingReadManager brandMappingReadManager,
            ExternalCategoryMappingReadManager categoryMappingReadManager) {
        this.sourceReadManager = sourceReadManager;
        this.brandMappingReadManager = brandMappingReadManager;
        this.categoryMappingReadManager = categoryMappingReadManager;
    }

    @Transactional(readOnly = true)
    public ResolvedMappingResult resolve(ExternalMappingResolveQuery query) {
        ExternalSource source = sourceReadManager.findByCode(query.externalSourceCode());
        long externalSourceId = source.idValue();

        ExternalBrandMapping brandMapping =
                brandMappingReadManager.findBySourceIdAndCode(
                        externalSourceId, query.externalBrandCode());

        ExternalCategoryMapping categoryMapping =
                categoryMappingReadManager.findBySourceIdAndCode(
                        externalSourceId, query.externalCategoryCode());

        return new ResolvedMappingResult(
                brandMapping.internalBrandId(),
                categoryMapping.internalCategoryId(),
                externalSourceId);
    }
}
