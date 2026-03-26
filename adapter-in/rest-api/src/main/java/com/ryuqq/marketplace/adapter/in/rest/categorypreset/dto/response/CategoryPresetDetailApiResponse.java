package com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 카테고리 프리셋 상세 조회 응답 DTO. */
@Schema(description = "카테고리 프리셋 상세 응답")
public record CategoryPresetDetailApiResponse(
        @Schema(description = "프리셋 ID", example = "1001") Long id,
        @Schema(description = "Shop ID", example = "1") Long shopId,
        @Schema(description = "쇼핑몰명", example = "스마트스토어") String shopName,
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId,
        @Schema(description = "판매채널명", example = "네이버") String salesChannelName,
        @Schema(description = "계정 ID", example = "trexi001") String accountId,
        @Schema(description = "프리셋 이름", example = "식품 - 과자류 전송용") String presetName,
        @Schema(description = "매핑된 판매채널 카테고리") MappingCategoryResponse mappingCategory,
        @Schema(description = "매핑된 내부 카테고리 목록") List<InternalCategoryResponse> internalCategories,
        @Schema(description = "등록일", example = "2025-12-15 10:30:00") String createdAt,
        @Schema(description = "수정일", example = "2025-12-20 14:00:00") String updatedAt) {

    @Schema(description = "매핑된 판매채널 카테고리 정보")
    public record MappingCategoryResponse(
            @Schema(description = "외부 카테고리 코드", example = "50000123") String categoryCode,
            @Schema(description = "카테고리 경로", example = "식품 > 과자 > 스낵 > 젤리") String categoryPath) {}

    @Schema(description = "매핑된 내부 카테고리")
    public record InternalCategoryResponse(
            @Schema(description = "내부 카테고리 ID", example = "100") Long id,
            @Schema(description = "카테고리 경로", example = "식품 > 간식 > 과자류") String categoryPath) {}
}
