package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 상품 그룹 상세설명 API 응답 DTO. */
@Schema(description = "상품 그룹 상세설명 응답")
public record ProductGroupDescriptionApiResponse(
        @Schema(description = "상세설명 ID", example = "1") Long id,
        @Schema(description = "상세설명 내용", example = "<p>상품 상세 설명입니다.</p>") String content,
        @Schema(description = "CDN 경로", example = "https://cdn.example.com/desc/1") String cdnPath,
        @Schema(description = "상세설명 이미지 목록") List<DescriptionImageApiResponse> images) {}
