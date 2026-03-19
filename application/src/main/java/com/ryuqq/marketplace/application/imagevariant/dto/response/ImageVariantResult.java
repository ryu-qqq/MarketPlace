package com.ryuqq.marketplace.application.imagevariant.dto.response;

import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;

/**
 * 이미지 Variant 조회 결과 DTO.
 *
 * @param variantType Variant 타입
 * @param resultAssetId FileFlow 변환 결과 에셋 ID
 * @param variantUrl 변환된 이미지 CDN URL
 * @param width 너비
 * @param height 높이
 */
public record ImageVariantResult(
        ImageVariantType variantType,
        String resultAssetId,
        String variantUrl,
        Integer width,
        Integer height) {

    public static ImageVariantResult from(ImageVariant variant) {
        return new ImageVariantResult(
                variant.variantType(),
                variant.resultAssetIdValue(),
                variant.variantUrlValue(),
                variant.width(),
                variant.height());
    }
}
