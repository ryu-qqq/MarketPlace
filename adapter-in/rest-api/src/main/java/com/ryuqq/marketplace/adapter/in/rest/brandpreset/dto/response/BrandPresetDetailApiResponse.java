package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 브랜드 프리셋 상세 조회 응답 DTO. */
@Schema(description = "브랜드 프리셋 상세 응답")
public record BrandPresetDetailApiResponse(
        @Schema(description = "프리셋 ID", example = "1001") Long id,
        @Schema(description = "Shop ID", example = "1") Long shopId,
        @Schema(description = "쇼핑몰명", example = "스마트스토어") String shopName,
        @Schema(description = "판매채널 ID", example = "1") Long salesChannelId,
        @Schema(description = "판매채널명", example = "네이버") String salesChannelName,
        @Schema(description = "계정 ID", example = "trexi001") String accountId,
        @Schema(description = "프리셋 이름", example = "나이키 전송용") String presetName,
        @Schema(description = "매핑된 판매채널 브랜드") MappingBrandResponse mappingBrand,
        @Schema(description = "매핑된 내부 브랜드 목록") List<InternalBrandResponse> internalBrands,
        @Schema(description = "등록일", example = "2025-12-15 10:30:00") String createdAt,
        @Schema(description = "수정일", example = "2025-12-20 14:00:00") String updatedAt) {

    @Schema(description = "매핑된 판매채널 브랜드 정보")
    public record MappingBrandResponse(
            @Schema(description = "외부 브랜드 코드", example = "NK001") String brandCode,
            @Schema(description = "외부 브랜드명", example = "NIKE") String brandName) {}

    @Schema(description = "매핑된 내부 브랜드")
    public record InternalBrandResponse(
            @Schema(description = "내부 브랜드 ID", example = "100") Long id,
            @Schema(description = "브랜드명", example = "나이키") String brandName) {}
}
