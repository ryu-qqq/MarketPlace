package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 상품 그룹 목록 조회 API 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 */
@Schema(description = "상품 그룹 목록 응답")
public record ProductGroupListApiResponse(
        @Schema(description = "상품 그룹 ID") Long id,
        @Schema(description = "셀러 ID") Long sellerId,
        @Schema(description = "셀러명") String sellerName,
        @Schema(description = "브랜드 ID") Long brandId,
        @Schema(description = "브랜드명") String brandName,
        @Schema(description = "카테고리 ID") Long categoryId,
        @Schema(description = "카테고리명") String categoryName,
        @Schema(description = "카테고리 전체 경로") String categoryDisplayPath,
        @Schema(description = "카테고리 뎁스") int categoryDepth,
        @Schema(description = "상품 부문") String department,
        @Schema(
                        description =
                                "카테고리 그룹 - 고시정보(notice_category) 및 속성"
                                        + " 템플릿(category_attribute_template)과 연결되는 분류 그룹")
                String categoryGroup,
        @Schema(description = "상품 그룹명") String productGroupName,
        @Schema(description = "옵션 유형") String optionType,
        @Schema(description = "상태") String status,
        @Schema(description = "썸네일 URL") String thumbnailUrl,
        @Schema(description = "상품 수") int productCount,
        @Schema(description = "최저가") int minPrice,
        @Schema(description = "최고가") int maxPrice,
        @Schema(description = "최대 할인율") int maxDiscountRate,
        @Schema(description = "옵션 그룹 요약") List<OptionGroupSummaryApiResponse> optionGroups,
        @Schema(description = "생성일시 (ISO 8601)") String createdAt,
        @Schema(description = "수정일시 (ISO 8601)") String updatedAt) {}
