package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * SearchSellerApplicationsApiRequest - 셀러 입점 신청 목록 검색 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 입점 신청 검색 요청 DTO")
public record SearchSellerApplicationsApiRequest(
        @Parameter(
                        description = "신청 상태 목록 (PENDING, APPROVED, REJECTED). 복수 선택 가능",
                        example = "PENDING")
                List<String> status,
        @Parameter(
                        description = "검색 필드 (COMPANY_NAME, REPRESENTATIVE_NAME)",
                        example = "companyName")
                String searchField,
        @Parameter(description = "검색어", example = "테스트") String searchWord,
        @Parameter(description = "정렬 키", example = "appliedAt") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)", example = "DESC") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
                Integer page,
        @Parameter(description = "페이지 크기", example = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
                Integer size) {}
