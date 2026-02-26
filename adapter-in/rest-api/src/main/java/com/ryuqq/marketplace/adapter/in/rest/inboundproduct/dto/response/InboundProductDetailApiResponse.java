package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 인바운드 상품 상세 조회 API 응답.
 *
 * @param status 인바운드 상품 상태
 * @param externalProductCode 외부 상품 코드
 * @param internalProductGroupId 내부 상품 그룹 ID (미변환 시 null)
 * @param products 내부 상품 목록
 */
@Schema(description = "인바운드 상품 상세 조회 응답")
public record InboundProductDetailApiResponse(
        @Schema(
                        description =
                                "인바운드 상품 상태 (RECEIVED, PENDING_MAPPING, MAPPED, CONVERTED,"
                                        + " CONVERT_FAILED)")
                String status,
        @Schema(description = "외부 상품 코드") String externalProductCode,
        @Schema(description = "내부 상품 그룹 ID (미변환 시 null)") Long internalProductGroupId,
        @Schema(description = "내부 상품 목록") List<ProductItemApiResponse> products) {

    /** 개별 상품(SKU) 정보. */
    @Schema(description = "개별 상품(SKU) 정보")
    public record ProductItemApiResponse(
            @Schema(description = "상품 ID") long productId,
            @Schema(description = "SKU 코드") String skuCode,
            @Schema(description = "정가") int regularPrice,
            @Schema(description = "현재가") int currentPrice,
            @Schema(description = "재고 수량") int stockQuantity,
            @Schema(description = "정렬 순서") int sortOrder,
            @Schema(description = "옵션 목록") List<OptionItemApiResponse> options) {}

    /** 옵션 매핑 정보. */
    @Schema(description = "옵션 매핑 정보")
    public record OptionItemApiResponse(
            @Schema(description = "옵션 그룹명") String optionGroupName,
            @Schema(description = "옵션 값명") String optionValueName) {}
}
