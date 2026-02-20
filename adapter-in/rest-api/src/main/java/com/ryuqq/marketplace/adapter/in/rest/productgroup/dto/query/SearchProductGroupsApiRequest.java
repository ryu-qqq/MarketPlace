package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchProductGroupsApiRequest - 상품 그룹 페이지 조회 API Request.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>API-DTO-010: Request DTO 조회 네이밍 규칙 (Search*ApiRequest).
 */
@Schema(description = "상품 그룹 페이지 조회 요청")
public record SearchProductGroupsApiRequest(
        @Parameter(
                        description = "상태 필터 (DRAFT, ACTIVE, INACTIVE, SOLDOUT, DELETED)",
                        example = "ACTIVE")
                @Schema(description = "상태 필터", nullable = true)
                List<String> statuses,
        @Parameter(description = "셀러 ID 필터", example = "1")
                @Schema(description = "셀러 ID 필터", nullable = true)
                List<Long> sellerIds,
        @Parameter(description = "브랜드 ID 필터", example = "1")
                @Schema(description = "브랜드 ID 필터", nullable = true)
                List<Long> brandIds,
        @Parameter(description = "카테고리 ID 필터", example = "100")
                @Schema(description = "카테고리 ID 필터", nullable = true)
                List<Long> categoryIds,
        @Parameter(description = "상품 그룹 ID 필터", example = "1")
                @Schema(description = "상품 그룹 ID 필터", nullable = true)
                List<Long> productGroupIds,
        @Parameter(
                        description = "검색 필드 (NAME, CATEGORY_NAME, BRAND_NAME)",
                        example = "NAME",
                        schema = @Schema(allowableValues = {"NAME", "CATEGORY_NAME", "BRAND_NAME"}))
                @Schema(description = "검색 필드", nullable = true)
                String searchField,
        @Parameter(description = "검색어", example = "나이키")
                @Schema(description = "검색어", nullable = true)
                String searchWord,
        @Parameter(description = "조회 시작일 (yyyy-MM-dd)", example = "2025-01-01")
                @Schema(description = "조회 시작일", nullable = true)
                LocalDate startDate,
        @Parameter(description = "조회 종료일 (yyyy-MM-dd)", example = "2025-12-31")
                @Schema(description = "조회 종료일", nullable = true)
                LocalDate endDate,
        @Parameter(
                        description = "정렬 키 (CREATED_AT, UPDATED_AT, NAME). 기본값: CREATED_AT",
                        example = "CREATED_AT",
                        schema = @Schema(allowableValues = {"CREATED_AT", "UPDATED_AT", "NAME"}))
                @Schema(description = "정렬 키", nullable = true)
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: DESC", example = "DESC")
                @Schema(description = "정렬 방향", nullable = true)
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작). 기본값: 0", example = "0")
                @Schema(description = "페이지 번호 (0부터 시작)", minimum = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20")
                @Schema(description = "페이지 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
