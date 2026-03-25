package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command.RegisterSalesChannelCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query.SearchSalesChannelCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryApiResponse;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import java.time.Instant;
import java.util.List;

/**
 * SalesChannelCategory API 테스트 Fixtures.
 *
 * <p>SalesChannelCategory REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class SalesChannelCategoryApiFixtures {

    private SalesChannelCategoryApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "의류";
    public static final Long DEFAULT_PARENT_ID = 0L;
    public static final Integer DEFAULT_DEPTH = 0;
    public static final String DEFAULT_PATH = "1";
    public static final Integer DEFAULT_SORT_ORDER = 1;
    public static final Boolean DEFAULT_LEAF = false;
    public static final String DEFAULT_DISPLAY_PATH = "식품 > 과자 > 스낵 > 젤리";

    // ===== RegisterSalesChannelCategoryApiRequest =====

    public static RegisterSalesChannelCategoryApiRequest registerRequest() {
        return new RegisterSalesChannelCategoryApiRequest(
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_DISPLAY_PATH);
    }

    public static RegisterSalesChannelCategoryApiRequest registerRequest(
            String code, String name, Long parentId, int depth) {
        return new RegisterSalesChannelCategoryApiRequest(
                code,
                name,
                parentId,
                depth,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_DISPLAY_PATH);
    }

    // ===== SearchSalesChannelCategoriesApiRequest =====

    public static SearchSalesChannelCategoriesApiRequest searchRequest() {
        return new SearchSalesChannelCategoriesApiRequest(
                null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchSalesChannelCategoriesApiRequest searchRequest(
            List<String> statuses, String searchField, String searchWord, int page, int size) {
        return new SearchSalesChannelCategoriesApiRequest(
                statuses,
                searchField,
                searchWord,
                null,
                null,
                null,
                "createdAt",
                "DESC",
                page,
                size);
    }

    // ===== SalesChannelCategoryResult (Application) =====

    public static SalesChannelCategoryResult categoryResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SalesChannelCategoryResult(
                id,
                1L,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "ACTIVE",
                now,
                now);
    }

    public static SalesChannelCategoryResult categoryResult(
            Long id, Long salesChannelId, String externalCode, String externalName) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new SalesChannelCategoryResult(
                id,
                salesChannelId,
                externalCode,
                externalName,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "ACTIVE",
                now,
                now);
    }

    public static List<SalesChannelCategoryResult> categoryResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                categoryResult(
                                        (long) i,
                                        1L,
                                        "CAT" + String.format("%03d", i),
                                        "카테고리_" + i))
                .toList();
    }

    public static SalesChannelCategoryPageResult pageResult(int count, int page, int size) {
        List<SalesChannelCategoryResult> results = categoryResults(count);
        return SalesChannelCategoryPageResult.of(results, page, size, count);
    }

    public static SalesChannelCategoryPageResult emptyPageResult() {
        return SalesChannelCategoryPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== SalesChannelCategoryApiResponse =====

    public static SalesChannelCategoryApiResponse apiResponse(Long id) {
        return new SalesChannelCategoryApiResponse(
                id,
                1L,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "ACTIVE",
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }

    public static SalesChannelCategoryApiResponse apiResponse(
            Long id, String externalCode, String externalName) {
        return new SalesChannelCategoryApiResponse(
                id,
                1L,
                externalCode,
                externalName,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "ACTIVE",
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }
}
