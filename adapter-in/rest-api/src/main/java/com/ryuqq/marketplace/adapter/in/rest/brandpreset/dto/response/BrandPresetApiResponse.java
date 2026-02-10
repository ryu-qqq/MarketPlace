package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 브랜드 프리셋 조회 응답 DTO. */
@Schema(description = "브랜드 프리셋 응답")
public record BrandPresetApiResponse(
        @Schema(description = "프리셋 ID", example = "1001") Long id,
        @Schema(description = "Shop ID", example = "1") Long shopId,
        @Schema(description = "쇼핑몰명", example = "스마트스토어") String shopName,
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId,
        @Schema(description = "판매채널명", example = "네이버") String salesChannelName,
        @Schema(description = "계정 ID", example = "trexi001") String accountId,
        @Schema(description = "프리셋 이름", example = "나이키 전송용") String presetName,
        @Schema(description = "브랜드명", example = "나이키") String brandName,
        @Schema(description = "브랜드 코드", example = "NIKE-KR") String brandCode,
        @Schema(description = "등록일", example = "2025-12-15T10:30:00+09:00") String createdAt) {}
