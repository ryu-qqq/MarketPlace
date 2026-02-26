package com.ryuqq.marketplace.adapter.in.rest.category.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/** 카테고리 검색 요청 DTO. */
public record SearchCategoriesApiRequest(
        @Parameter(description = "부모 카테고리 ID", example = "1") Long parentId,
        @Parameter(description = "계층 깊이", example = "0") Integer depth,
        @Parameter(description = "리프 노드 여부", example = "true") Boolean leaf,
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)", example = "ACTIVE")
                List<String> statuses,
        @Parameter(
                        description =
                                "부문 필터 (FASHION, BEAUTY, LIVING, FOOD, DIGITAL, SPORTS, KIDS,"
                                        + " PET, CULTURE, HEALTH, ETC)",
                        example = "FASHION")
                List<String> departments,
        @Parameter(
                        description =
                                "카테고리 그룹 필터 - 고시정보 연결용 (CLOTHING, SHOES, BAGS,"
                                        + " ACCESSORIES, COSMETICS, JEWELRY, WATCHES, FURNITURE,"
                                        + " DIGITAL, SPORTS, BABY_KIDS, ETC)",
                        example = "CLOTHING")
                List<String> categoryGroups,
        @Parameter(description = "검색 필드 (CODE, NAME_KO, NAME_EN)", example = "NAME_KO")
                String searchField,
        @Parameter(description = "검색어", example = "패션의류") String searchWord,
        @Parameter(
                        description =
                                "정렬 키 (SORT_ORDER, CREATED_AT, NAME_KO, CODE). 기본값:"
                                        + " SORT_ORDER",
                        example = "SORT_ORDER")
                String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC). 기본값: ASC", example = "ASC")
                String sortDirection,
        @Parameter(description = "페이지 번호 (0부터). 기본값: 0", example = "0") Integer page,
        @Parameter(description = "페이지 크기. 기본값: 20", example = "20") Integer size) {}
