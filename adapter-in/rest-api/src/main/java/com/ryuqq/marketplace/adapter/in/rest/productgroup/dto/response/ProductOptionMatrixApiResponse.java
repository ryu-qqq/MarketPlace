package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 옵션-상품 매트릭스 API 응답 DTO. */
@Schema(description = "옵션-상품 매트릭스 응답")
public record ProductOptionMatrixApiResponse(
        @Schema(description = "옵션 그룹 목록") List<SellerOptionGroupApiResponse> optionGroups,
        @Schema(description = "상품(SKU) 목록") List<ProductDetailApiResponse> products) {}
