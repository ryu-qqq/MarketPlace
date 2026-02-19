package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 셀러 옵션 그룹 API 응답 DTO. */
@Schema(description = "셀러 옵션 그룹 응답")
public record SellerOptionGroupApiResponse(
        @Schema(description = "옵션 그룹 ID", example = "1") Long id,
        @Schema(description = "옵션 그룹명", example = "색상") String optionGroupName,
        @Schema(description = "표준 옵션 그룹 ID", example = "10", nullable = true)
                Long canonicalOptionGroupId,
        @Schema(
                        description = "입력 유형 (PREDEFINED: 사전 정의, FREE_INPUT: 자유 입력)",
                        example = "PREDEFINED")
                String inputType,
        @Schema(description = "정렬 순서", example = "0") int sortOrder,
        @Schema(description = "옵션 값 목록") List<SellerOptionValueApiResponse> optionValues) {}
