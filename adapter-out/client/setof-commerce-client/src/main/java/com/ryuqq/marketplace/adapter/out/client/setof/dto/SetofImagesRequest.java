package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import java.util.List;

/**
 * 세토프 커머스 상품 그룹 이미지 등록/수정 요청 DTO.
 *
 * <p>POST /api/v2/admin/product-groups/{id}/images (등록)
 *
 * <p>PUT /api/v2/admin/product-groups/{id}/images (수정)
 */
public record SetofImagesRequest(List<ImageRequest> images) {

    /** 방어적 복사. */
    public SetofImagesRequest {
        images = images == null ? null : List.copyOf(images);
    }

    /** 이미지 요청. */
    public record ImageRequest(String imageType, String imageUrl, int sortOrder) {}
}
