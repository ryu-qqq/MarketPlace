package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 외부채널 카테고리 등록 API 요청 DTO. */
@Schema(description = "외부채널 카테고리 등록 요청")
public record RegisterSalesChannelCategoryApiRequest(
        @Schema(
                        description = "외부 카테고리 코드",
                        example = "CAT001",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String externalCategoryCode,
        @Schema(
                        description = "외부 카테고리명",
                        example = "의류",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String externalCategoryName,
        @Schema(
                        description = "부모 카테고리 ID (최상위는 0)",
                        example = "0",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                Long parentId,
        @Schema(
                        description = "카테고리 깊이 (0부터 시작)",
                        example = "0",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                @Min(0)
                Integer depth,
        @Schema(description = "카테고리 경로", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank
                String path,
        @Schema(description = "정렬 순서", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                @Min(0)
                Integer sortOrder,
        @Schema(
                        description = "리프 노드 여부",
                        example = "false",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull
                Boolean leaf,
        @Schema(description = "표시용 이름 경로", example = "식품 > 과자 > 스낵 > 젤리") String displayPath) {}
