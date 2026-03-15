package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import java.util.List;

/**
 * 세토프 이미지 Variant 동기화 요청 DTO.
 *
 * @param sourceImageId 소스 이미지 ID
 * @param sourceType 이미지 소스 타입
 * @param variants Variant 목록
 */
public record SetofImageVariantSyncRequest(
        Long sourceImageId, String sourceType, List<VariantRequest> variants) {

    /** 방어적 복사. */
    public SetofImageVariantSyncRequest {
        variants = variants == null ? null : List.copyOf(variants);
    }

    /**
     * 개별 Variant 정보.
     *
     * @param variantType Variant 타입
     * @param resultAssetId 결과 에셋 ID
     * @param variantUrl 변환된 이미지 CDN URL
     * @param width 너비
     * @param height 높이
     */
    public record VariantRequest(
            String variantType,
            String resultAssetId,
            String variantUrl,
            Integer width,
            Integer height) {}
}
