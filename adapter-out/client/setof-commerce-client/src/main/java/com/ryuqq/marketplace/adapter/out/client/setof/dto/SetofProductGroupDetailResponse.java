package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 세토프 상품 그룹 조회 응답 DTO.
 *
 * <p>GET /api/v2/admin/product-groups/{productGroupId} 응답을 매핑합니다. 필요한 필드만 매핑하고 나머지는 무시합니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SetofProductGroupDetailResponse(
        Long productGroupId, List<ProductResponse> products, List<ImageResponse> images) {

    /** 세토프 개별 상품 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductResponse(
            Long productId, String skuCode, List<SelectedOptionResponse> selectedOptions) {}

    /** 세토프 상품 선택 옵션 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SelectedOptionResponse(String optionGroupName, String optionValueName) {}

    /** 세토프 상품 이미지 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageResponse(
            Long imageId, String imageType, String imageUrl, Integer sortOrder) {}
}
