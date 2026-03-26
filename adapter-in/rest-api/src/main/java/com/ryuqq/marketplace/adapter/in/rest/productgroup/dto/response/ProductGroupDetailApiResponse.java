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
        @Schema(description = "상품 그룹 ID", example = "1") Long id,
        @Schema(description = "셀러 ID", example = "1") Long sellerId,
        @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
        @Schema(description = "브랜드 ID", example = "1") Long brandId,
        @Schema(description = "브랜드명", example = "나이키") String brandName,
        @Schema(description = "카테고리 ID", example = "100") Long categoryId,
        @Schema(description = "카테고리명", example = "운동화") String categoryName,
        @Schema(description = "카테고리 전체 경로", example = "패션의류 > 신발 > 운동화") String categoryDisplayPath,
        @Schema(description = "카테고리 ID 경로", example = "1/5/23") String categoryIdPath,
        @Schema(description = "상품 그룹명", example = "나이키 에어맥스 90") String productGroupName,
        @Schema(description = "옵션 유형 (NONE, SINGLE, COMBINATION)", example = "COMBINATION")
                String optionType,
        @Schema(description = "상태 (DRAFT, ACTIVE, INACTIVE, SOLD_OUT, DELETED)", example = "ACTIVE")
                String status,
        @Schema(description = "생성일시 (KST)", example = "2026-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2026-01-20T14:00:00Z") String updatedAt,
        @Schema(description = "상품 그룹 이미지 목록") List<ProductGroupImageApiResponse> images,
        @Schema(description = "옵션-상품 매트릭스") ProductOptionMatrixApiResponse optionProductMatrix,
        @Schema(description = "배송 정책", nullable = true) ShippingPolicyApiResponse shippingPolicy,
        @Schema(description = "환불 정책", nullable = true) RefundPolicyApiResponse refundPolicy,
        @Schema(description = "상세설명", nullable = true)
                ProductGroupDescriptionApiResponse description,
        @Schema(description = "상품 고시정보", nullable = true) ProductNoticeApiResponse productNotice) {}
