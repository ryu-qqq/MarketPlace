package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 엑셀 다운로드용 상품 그룹 API 응답 DTO.
 *
 * <p>기본 목록 정보에 이미지, 상품(SKU), 상세설명 CDN URL, 고시정보를 추가로 포함합니다.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 */
@Schema(description = "엑셀 다운로드용 상품 그룹 응답")
public record ProductGroupExcelApiResponse(
        @Schema(description = "상품 그룹 ID", example = "1") Long id,
        @Schema(description = "셀러 ID", example = "1") Long sellerId,
        @Schema(description = "셀러명", example = "테스트셀러") String sellerName,
        @Schema(description = "브랜드 ID", example = "1") Long brandId,
        @Schema(description = "브랜드명", example = "나이키") String brandName,
        @Schema(description = "카테고리 ID", example = "100") Long categoryId,
        @Schema(description = "카테고리명", example = "운동화") String categoryName,
        @Schema(description = "카테고리 전체 경로", example = "패션의류 > 신발 > 운동화") String categoryDisplayPath,
        @Schema(description = "카테고리 ID 경로", example = "1/5/23") String categoryIdPath,
        @Schema(description = "카테고리 뎁스", example = "2") int categoryDepth,
        @Schema(description = "상품 부문", example = "UNISEX") String department,
        @Schema(description = "카테고리 그룹", example = "SHOES") String categoryGroup,
        @Schema(description = "상품 그룹명", example = "나이키 에어맥스 90") String productGroupName,
        @Schema(description = "옵션 유형 (NONE, SINGLE, COMBINATION)", example = "COMBINATION")
                String optionType,
        @Schema(description = "상태 (DRAFT, ACTIVE, INACTIVE, SOLD_OUT, DELETED)", example = "ACTIVE")
                String status,
        @Schema(description = "썸네일 URL", example = "https://cdn.example.com/thumbnail.jpg")
                String thumbnailUrl,
        @Schema(description = "상품 수", example = "5") int productCount,
        @Schema(description = "최저가", example = "89000") int minPrice,
        @Schema(description = "최고가", example = "129000") int maxPrice,
        @Schema(description = "최대 할인율", example = "15") int maxDiscountRate,
        @Schema(description = "옵션 그룹 요약") List<OptionGroupSummaryApiResponse> optionGroups,
        @Schema(description = "생성일시 (KST)", example = "2026-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정일시 (KST)", example = "2026-01-20T14:00:00Z") String updatedAt,
        @Schema(description = "이미지 목록") List<ProductGroupImageApiResponse> images,
        @Schema(description = "상품(SKU) 목록") List<ProductExcelApiResponse> products,
        @Schema(description = "상세설명 CDN URL") String descriptionCdnUrl,
        @Schema(description = "고시정보") ProductNoticeApiResponse notice) {}
