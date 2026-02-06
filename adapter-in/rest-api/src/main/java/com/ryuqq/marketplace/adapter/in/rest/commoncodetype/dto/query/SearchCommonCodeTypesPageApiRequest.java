package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * SearchCommonCodeTypesPageApiRequest - 공통 코드 타입 페이지 조회 API Request.
 *
 * <p>공통 코드 타입 목록을 페이지 기반으로 조회하는 REST API 요청 DTO입니다.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>API-DTO-010: Request DTO 조회 네이밍 규칙 (Search*ApiRequest).
 *
 * <p>기본값 처리는 Mapper에서 수행합니다. Request DTO에서는 기본값 설정 금지.
 *
 * @param active 활성화 여부 필터 (null이면 전체 조회)
 * @param searchField 검색 필드 (null: 전체 필드, code: 코드, name: 이름)
 * @param searchWord 검색어
 * @param type 공통 코드 값(CommonCodeValue) 필터 - 있으면 해당 값을 가진 공통코드를 갖는 타입만 조회, null이면 미적용
 * @param sortKey 정렬 키 (CREATED_AT, DISPLAY_ORDER, CODE)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기 (최대 100)
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "공통 코드 타입 페이지 조회 요청")
public record SearchCommonCodeTypesPageApiRequest(
        @Parameter(description = "활성화 여부 필터", example = "true")
                @Schema(description = "활성화 여부 필터", nullable = true)
                Boolean active,
        @Parameter(
                        description = "검색 필드 (null: 전체 필드)",
                        example = "code",
                        schema = @Schema(allowableValues = {"code", "name"}))
                String searchField,
        @Parameter(description = "검색어", example = "결제") String searchWord,
        @Parameter(
                        description = "공통 코드 값(CommonCodeValue) 필터 - 해당 값을 가진 공통코드를 갖는 타입만 조회 (선택)",
                        example = "CARD")
                @Schema(description = "공통 코드 값 필터 (선택)", nullable = true)
                String type,
        @Parameter(description = "정렬 키 (CREATED_AT, DISPLAY_ORDER, CODE)", example = "CREATED_AT")
                @Schema(description = "정렬 키", nullable = true)
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)", example = "DESC")
                @Schema(description = "정렬 방향", nullable = true)
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                @Schema(description = "페이지 번호 (0부터 시작)", minimum = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Parameter(description = "페이지 크기", example = "20")
                @Schema(description = "페이지 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
