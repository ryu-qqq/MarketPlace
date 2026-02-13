package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * SearchSellerAdminApplicationsApiRequest - 셀러 관리자 가입 신청 목록 조회 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @param sellerIds 셀러 ID 목록 (슈퍼관리자: 선택, 셀러관리자: 서버에서 강제 적용)
 * @param status 상태 필터 (PENDING_APPROVAL, ACTIVE, INACTIVE, SUSPENDED, REJECTED)
 * @param searchField 검색 필드 (LOGIN_ID, NAME)
 * @param searchWord 검색어
 * @param sortKey 정렬 키 (createdAt)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @param startDate 검색 시작일 (YYYY-MM-DD)
 * @param endDate 검색 종료일 (YYYY-MM-DD)
 * @param page 페이지 번호 (0-based)
 * @param size 페이지 크기
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 목록 조회 요청 DTO")
public record SearchSellerAdminApplicationsApiRequest(
        @Parameter(
                        description = "셀러 ID 목록. 슈퍼관리자: 생략 시 전체 조회, 셀러관리자: 서버에서 자동 적용",
                        example = "1,2,3")
                List<Long> sellerIds,
        @Parameter(
                        description =
                                "상태 필터 목록 (PENDING_APPROVAL, ACTIVE, INACTIVE, SUSPENDED,"
                                        + " REJECTED). 복수 선택 가능",
                        example = "PENDING_APPROVAL")
                List<String> status,
        @Parameter(description = "검색 필드 (LOGIN_ID, NAME)", example = "loginId") String searchField,
        @Parameter(description = "검색어", example = "admin") String searchWord,
        @Parameter(description = "정렬 키 (createdAt)", example = "createdAt") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)", example = "DESC") String sortDirection,
        @Parameter(description = "검색 시작일 (YYYY-MM-DD)", example = "2025-01-01")
                @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD이어야 합니다.")
                String startDate,
        @Parameter(description = "검색 종료일 (YYYY-MM-DD)", example = "2025-12-31")
                @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD이어야 합니다.")
                String endDate,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
                Integer page,
        @Parameter(description = "페이지 크기", example = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
                Integer size) {}
