package com.ryuqq.marketplace.application.externalmapping.dto.query;

/** 외부 매핑 통합 조회 쿼리. */
public record ExternalMappingResolveQuery(
        String externalSourceCode, String externalBrandCode, String externalCategoryCode) {}
