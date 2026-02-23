package com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 이미지 Variant 조회 API 응답 DTO.
 *
 * @param variantType Variant 타입 (SMALL_WEBP, MEDIUM_WEBP, LARGE_WEBP, ORIGINAL_WEBP)
 * @param variantUrl 변환된 이미지 CDN URL
 * @param width 너비
 * @param height 높이
 */
@Schema(description = "이미지 변형(배리언트) 조회 응답. 해상도별 WebP URL 및 크기 정보를 담습니다.")
public record ImageVariantApiResponse(
        @Schema(
                        description = "변형 타입 (SMALL_WEBP, MEDIUM_WEBP, LARGE_WEBP, ORIGINAL_WEBP)",
                        example = "SMALL_WEBP")
                String variantType,
        @Schema(description = "변환된 이미지 CDN URL", example = "https://cdn.example.com/variant.webp")
                String variantUrl,
        @Schema(description = "너비(픽셀). 원본은 null", example = "300") Integer width,
        @Schema(description = "높이(픽셀). 원본은 null", example = "300") Integer height) {

    public static ImageVariantApiResponse from(ImageVariantResult result) {
        return new ImageVariantApiResponse(
                result.variantType().name(), result.variantUrl(), result.width(), result.height());
    }
}
