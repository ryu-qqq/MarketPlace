package com.ryuqq.marketplace.adapter.in.rest.seller.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * SearchSellersApiRequest - 셀러 검색 요청 DTO (Offset 페이징).
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-007: @Parameter 어노테이션 (Query Parameters).
 *
 * <p>API-DTO-010: Offset 페이징은 Search{Bc}ApiRequest 네이밍.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "셀러 검색 요청 DTO (Offset 페이징)")
public record SearchSellersApiRequest(
        @Parameter(description = "활성화 여부 (null: 전체, true: 활성, false: 비활성)", example = "true")
                Boolean active,
        @Parameter(
                        description =
                                "검색 필드 (SELLER_NAME, REGISTRATION_NUMBER, COMPANY_NAME,"
                                        + " REPRESENTATIVE_NAME)",
                        example = "sellerName",
                        schema =
                                @Schema(
                                        allowableValues = {
                                            "SELLER_NAME",
                                            "REGISTRATION_NUMBER",
                                            "COMPANY_NAME",
                                            "REPRESENTATIVE_NAME"
                                        }))
                String searchField,
        @Parameter(description = "검색어", example = "테스트") String searchWord,
        @Parameter(description = "정렬 기준", example = "createdAt") String sortKey,
        @Parameter(description = "정렬 방향 (ASC/DESC)", example = "DESC") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
                Integer page,
        @Parameter(description = "페이지 크기 (1~100)", example = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
                Integer size) {}
