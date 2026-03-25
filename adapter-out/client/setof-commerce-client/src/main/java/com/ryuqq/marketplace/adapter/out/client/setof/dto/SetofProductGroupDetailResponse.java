package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 세토프 상품 그룹 조회 응답 DTO.
 *
 * <p>GET /api/v2/admin/product-groups/{productGroupId} 응답을 매핑합니다.
 * V2 응답 구조: { "data": { "id": ..., "products": [...], "optionProductMatrix": { "products": [...] }, "images": [...] } }
 *
 * <p>products 편의 접근자는 data.optionProductMatrix.products -> data.products 순서로 폴백합니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SetofProductGroupDetailResponse(Data data) {

    /** 응답 data 내부 구조. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            @JsonProperty("id") Long productGroupId,
            List<ProductResponse> products,
            OptionProductMatrix optionProductMatrix,
            List<ImageResponse> images) {}

    /** optionProductMatrix 내부 구조. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionProductMatrix(List<ProductResponse> products) {}

    /** 세토프 개별 상품 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductResponse(
            @JsonProperty("id") Long productId,
            String skuCode,
            @JsonProperty("options") List<SelectedOptionResponse> selectedOptions) {}

    /** 세토프 상품 선택 옵션 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SelectedOptionResponse(String optionGroupName, String optionValueName) {}

    /** 세토프 상품 이미지 응답. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageResponse(
            @JsonProperty("id") Long imageId,
            String imageType,
            String imageUrl,
            Integer sortOrder) {}

    // ===== 편의 접근자 (기존 호출부 호환) =====

    /** data.id 편의 접근자. */
    public Long productGroupId() {
        return data != null ? data.productGroupId() : null;
    }

    /**
     * products 편의 접근자.
     *
     * <p>optionProductMatrix.products가 있으면 우선 사용하고, 없으면 data.products로 폴백합니다.
     */
    public List<ProductResponse> products() {
        if (data == null) {
            return List.of();
        }
        if (data.optionProductMatrix() != null
                && data.optionProductMatrix().products() != null
                && !data.optionProductMatrix().products().isEmpty()) {
            return data.optionProductMatrix().products();
        }
        return data.products() != null ? data.products() : List.of();
    }

    /** data.images 편의 접근자. */
    public List<ImageResponse> images() {
        return data != null && data.images() != null ? data.images() : List.of();
    }
}
