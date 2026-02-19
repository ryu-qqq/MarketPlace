package com.ryuqq.marketplace.application.externalmapping.service.query;

import com.ryuqq.marketplace.application.externalmapping.dto.query.ExternalMappingResolveQuery;
import com.ryuqq.marketplace.application.externalmapping.dto.response.ResolvedMappingResult;
import com.ryuqq.marketplace.application.externalmapping.manager.ExternalMappingResolveManager;
import com.ryuqq.marketplace.application.externalmapping.port.in.query.ResolveExternalMappingUseCase;
import org.springframework.stereotype.Service;

/** 외부 매핑 통합 조회 Service. */
@Service
public class ResolveExternalMappingService implements ResolveExternalMappingUseCase {

    private final ExternalMappingResolveManager resolveManager;

    public ResolveExternalMappingService(ExternalMappingResolveManager resolveManager) {
        this.resolveManager = resolveManager;
    }

    @Override
    public ResolvedMappingResult execute(ExternalMappingResolveQuery query) {
        return resolveManager.resolve(query);
    }
}
