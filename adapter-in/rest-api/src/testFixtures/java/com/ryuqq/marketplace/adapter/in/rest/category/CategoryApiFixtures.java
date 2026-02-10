package com.ryuqq.marketplace.adapter.in.rest.category;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.SearchCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResult;
import java.time.Instant;
import java.util.List;

/**
 * Category API 테스트 Fixtures.
 *
 * <p>Category REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CategoryApiFixtures {

    private CategoryApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "CAT001";
    public static final String DEFAULT_NAME_KO = "테스트카테고리";
    public static final String DEFAULT_NAME_EN = "TestCategory";
    public static final Long DEFAULT_PARENT_ID = null;
    public static final int DEFAULT_DEPTH = 1;
    public static final String DEFAULT_PATH = "/1";
    public static final int DEFAULT_SORT_ORDER = 1;
    public static final boolean DEFAULT_LEAF = false;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_DEPARTMENT = "FASHION";
    public static final String DEFAULT_CATEGORY_GROUP = "CLOTHING";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== SearchCategoriesApiRequest =====

    public static SearchCategoriesApiRequest searchRequest() {
        return new SearchCategoriesApiRequest(
                null, null, null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchCategoriesApiRequest searchRequest(
            List<String> statuses,
            List<String> departments,
            String searchField,
            String searchWord,
            int page,
            int size) {
        return new SearchCategoriesApiRequest(
                null,
                null,
                null,
                statuses,
                departments,
                null,
                searchField,
                searchWord,
                "createdAt",
                "DESC",
                page,
                size);
    }

    public static SearchCategoriesApiRequest searchRequestWithCategoryGroups(
            List<String> categoryGroups, int page, int size) {
        return new SearchCategoriesApiRequest(
                null,
                null,
                null,
                null,
                null,
                categoryGroups,
                null,
                null,
                "createdAt",
                "DESC",
                page,
                size);
    }

    // ===== CategoryResult (Application) =====

    public static CategoryResult categoryResult(Long id) {
        return new CategoryResult(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static CategoryResult categoryResult(Long id, String nameKo, String status) {
        return new CategoryResult(
                id,
                "CAT" + String.format("%03d", id),
                nameKo,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                "/" + id,
                (int) (long) id,
                DEFAULT_LEAF,
                status,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static CategoryResult categoryResultWithGroup(Long id, String categoryGroup) {
        return new CategoryResult(
                id,
                "CAT" + String.format("%03d", id),
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                "/" + id,
                (int) (long) id,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                categoryGroup,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<CategoryResult> categoryResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> categoryResult((long) i, "카테고리_" + i, DEFAULT_STATUS))
                .toList();
    }

    public static CategoryPageResult pageResult(int count, int page, int size) {
        List<CategoryResult> results = categoryResults(count);
        return CategoryPageResult.of(results, page, size, count);
    }

    public static CategoryPageResult emptyPageResult() {
        return CategoryPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== CategoryApiResponse =====

    public static CategoryApiResponse apiResponse(Long id) {
        return new CategoryApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }

    public static CategoryApiResponse apiResponseWithGroup(Long id, String categoryGroup) {
        return new CategoryApiResponse(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                categoryGroup,
                null,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }
}
