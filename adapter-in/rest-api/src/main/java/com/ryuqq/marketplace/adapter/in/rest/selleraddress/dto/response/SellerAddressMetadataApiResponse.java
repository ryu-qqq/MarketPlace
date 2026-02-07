package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 주소 메타데이터 조회 응답. */
@Schema(description = "셀러 주소 메타데이터 조회 응답")
public record SellerAddressMetadataApiResponse(
        @Schema(description = "전체 주소 수") long totalCount,
        @Schema(description = "출고지 수") long shippingCount,
        @Schema(description = "반품지 수") long returnCount,
        @Schema(description = "기본 출고지 설정 여부") boolean hasDefaultShipping,
        @Schema(description = "기본 반품지 설정 여부") boolean hasDefaultReturn) {}
