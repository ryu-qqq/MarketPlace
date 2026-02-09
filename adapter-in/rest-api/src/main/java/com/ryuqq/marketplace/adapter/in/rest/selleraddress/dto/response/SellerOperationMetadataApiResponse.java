package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 셀러 운영 메타데이터 조회 응답. */
@Schema(description = "셀러 운영 메타데이터 조회 응답")
public record SellerOperationMetadataApiResponse(
        @Schema(description = "전체 주소 수") long totalCount,
        @Schema(description = "출고지 수") long shippingCount,
        @Schema(description = "반품지 수") long returnCount,
        @Schema(description = "기본 출고지 설정 여부") boolean hasDefaultShipping,
        @Schema(description = "기본 반품지 설정 여부") boolean hasDefaultReturn,
        @Schema(description = "배송정책 수") long shippingPolicyCount,
        @Schema(description = "환불정책 수") long refundPolicyCount,
        @Schema(description = "기본 배송정책 설정 여부") boolean hasDefaultShippingPolicy,
        @Schema(description = "기본 환불정책 설정 여부") boolean hasDefaultRefundPolicy) {}
