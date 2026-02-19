package com.ryuqq.marketplace.application.externalmapping.port.in.query;

import com.ryuqq.marketplace.application.externalmapping.dto.query.ExternalMappingResolveQuery;
import com.ryuqq.marketplace.application.externalmapping.dto.response.ResolvedMappingResult;

/** 외부 매핑 통합 조회 UseCase. */
public interface ResolveExternalMappingUseCase {

    ResolvedMappingResult execute(ExternalMappingResolveQuery query);
}
