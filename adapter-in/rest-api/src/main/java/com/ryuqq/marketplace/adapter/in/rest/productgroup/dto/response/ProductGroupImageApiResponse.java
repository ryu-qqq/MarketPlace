package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 상품 그룹 이미지 API 응답 DTO. */
@Schema(description = "상품 그룹 이미지 응답")
public record ProductGroupImageApiResponse(
        @Schema(description = "이미지 ID", example = "1") Long id,
        @Schema(description = "원본 URL", example = "https://example.com/image.jpg") String originUrl,
        @Schema(description = "업로드 URL", example = "https://cdn.example.com/image.jpg")
                String uploadedUrl,
        @Schema(description = "이미지 유형 (THUMBNAIL, DETAIL)", example = "THUMBNAIL") String imageType,
        @Schema(description = "정렬 순서", example = "0") int sortOrder,
        @Schema(description = "이미지 Variant 목록 (멀티 사이즈 WEBP)")
                List<ImageVariantApiResponse> variants) {

    public ProductGroupImageApiResponse {
        variants = variants != null ? List.copyOf(variants) : List.of();
    }
}
