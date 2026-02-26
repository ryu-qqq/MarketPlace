package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request;

import jakarta.validation.constraints.NotNull;

/** 세토프 UpdateProductDescription 호환 요청 DTO. */
public record LegacyUpdateProductDescriptionRequest(
        @NotNull(message = "상세 설명은 필수입니다.") String detailDescription) {}
