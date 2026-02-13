package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 상품 그룹 상세 조회 API 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 */
@Schema(description = "상품 그룹 상세 응답")
public record ProductGroupDetailApiResponse(
        @Schema(description = "상품 그룹 ID") Long id,
        @Schema(description = "셀러 ID") Long sellerId,
        @Schema(description = "셀러명") String sellerName,
        @Schema(description = "브랜드 ID") Long brandId,
        @Schema(description = "브랜드명") String brandName,
        @Schema(description = "카테고리 ID") Long categoryId,
        @Schema(description = "카테고리명") String categoryName,
        @Schema(description = "카테고리 전체 경로") String categoryDisplayPath,
        @Schema(description = "상품 그룹명") String productGroupName,
        @Schema(description = "옵션 유형") String optionType,
        @Schema(description = "상태") String status,
        @Schema(description = "생성일시 (ISO 8601)") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)") String updatedAt,
        @Schema(description = "상품 그룹 이미지 목록") List<ProductGroupImageApiResponse> images,
        @Schema(description = "옵션-상품 매트릭스") ProductOptionMatrixApiResponse optionProductMatrix,
        @Schema(description = "배송 정책", nullable = true) ShippingPolicyApiResponse shippingPolicy,
        @Schema(description = "환불 정책", nullable = true) RefundPolicyApiResponse refundPolicy,
        @Schema(description = "상세설명", nullable = true)
                ProductGroupDescriptionApiResponse description,
        @Schema(description = "상품 고시정보", nullable = true) ProductNoticeApiResponse productNotice) {}
