package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 세토프 커머스 상품 그룹 상세설명 등록/수정 요청 DTO.
 *
 * <p>POST /api/v2/admin/product-groups/{id}/description (등록)
 *
 * <p>PUT /api/v2/admin/product-groups/{id}/description (수정)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SetofDescriptionRequest(
        String content, List<DescriptionImageRequest> descriptionImages) {

    /** 방어적 복사. */
    public SetofDescriptionRequest {
        descriptionImages = descriptionImages == null ? null : List.copyOf(descriptionImages);
    }

    /** 상세설명 이미지 요청. */
    public record DescriptionImageRequest(String imageUrl, int sortOrder) {}
}
