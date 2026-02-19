package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 상품 그룹 ID 응답.
 *
 * <p>상품 그룹 등록/수정 후 생성된 ID를 반환합니다.
 */
@Schema(description = "상품 그룹 ID 응답")
public record ProductGroupIdApiResponse(
        @Schema(description = "상품 그룹 ID", example = "1") Long productGroupId) {

    public static ProductGroupIdApiResponse of(Long productGroupId) {
        return new ProductGroupIdApiResponse(productGroupId);
    }
}
