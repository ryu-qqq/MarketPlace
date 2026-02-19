package com.ryuqq.marketplace.adapter.in.rest.externalmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.command.ResolveExternalMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.response.ResolvedMappingApiResponse;
import com.ryuqq.marketplace.application.externalmapping.dto.query.ExternalMappingResolveQuery;
import com.ryuqq.marketplace.application.externalmapping.dto.response.ResolvedMappingResult;
import org.springframework.stereotype.Component;

/** ExternalMapping API 변환 매퍼. */
@Component
public class ExternalMappingApiMapper {

    public ExternalMappingResolveQuery toQuery(ResolveExternalMappingApiRequest request) {
        return new ExternalMappingResolveQuery(
                request.externalSourceCode(),
                request.externalBrandCode(),
                request.externalCategoryCode());
    }

    public ResolvedMappingApiResponse toResponse(ResolvedMappingResult result) {
        return new ResolvedMappingApiResponse(
                result.internalBrandId(), result.internalCategoryId(), result.externalSourceId());
    }
}
