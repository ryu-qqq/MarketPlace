package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

/**
 * 상품 그룹 ID 응답.
 *
 * <p>상품 그룹 등록/수정 후 생성된 ID를 반환합니다.
 */
public record ProductGroupIdApiResponse(Long productGroupId) {

    public static ProductGroupIdApiResponse of(Long productGroupId) {
        return new ProductGroupIdApiResponse(productGroupId);
    }
}
