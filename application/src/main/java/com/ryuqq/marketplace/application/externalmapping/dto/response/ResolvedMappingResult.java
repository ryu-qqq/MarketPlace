package com.ryuqq.marketplace.application.externalmapping.dto.response;

/** 외부 매핑 통합 조회 결과. */
public record ResolvedMappingResult(
        long internalBrandId, long internalCategoryId, long externalSourceId) {}
