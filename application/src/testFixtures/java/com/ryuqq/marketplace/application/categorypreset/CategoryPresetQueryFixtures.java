package com.ryuqq.marketplace.application.categorypreset;

import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * CategoryPreset Query 테스트 Fixtures.
 *
 * <p>CategoryPreset 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CategoryPresetQueryFixtures {

    private CategoryPresetQueryFixtures() {}

    // ===== SearchParams Fixtures =====

    public static CategoryPresetSearchParams searchParams() {
        return CategoryPresetSearchParams.of(null, null, null, null, defaultCommonSearchParams());
    }

    public static CategoryPresetSearchParams searchParams(int page, int size) {
        return CategoryPresetSearchParams.of(
                null, null, null, null, commonSearchParams(page, size));
    }

    public static CategoryPresetSearchParams searchParams(List<Long> salesChannelIds) {
        return CategoryPresetSearchParams.of(
                salesChannelIds, null, null, null, defaultCommonSearchParams());
    }

    public static CategoryPresetSearchParams searchParams(
            List<Long> salesChannelIds, List<String> statuses) {
        return CategoryPresetSearchParams.of(
                salesChannelIds, statuses, null, null, defaultCommonSearchParams());
    }

    public static CategoryPresetSearchParams searchParams(String searchField, String searchWord) {
        return CategoryPresetSearchParams.of(
                null, null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static CategoryPresetSearchParams searchParams(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            int page,
            int size) {
        return CategoryPresetSearchParams.of(
                salesChannelIds, statuses, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static CategoryPresetResult categoryPresetResult(Long id) {
        Instant now = CommonVoFixtures.now();
        return new CategoryPresetResult(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                "테스트 카테고리 프리셋",
                "카테고리 경로",
                "TEST_CATEGORY_CODE",
                now);
    }

    public static CategoryPresetResult categoryPresetResult(Long id, String presetName) {
        Instant now = CommonVoFixtures.now();
        return new CategoryPresetResult(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "카테고리 경로",
                "TEST_CATEGORY_CODE",
                now);
    }

    public static CategoryPresetResult categoryPresetResult(
            Long id, Long shopId, Long salesChannelId) {
        Instant now = CommonVoFixtures.now();
        return new CategoryPresetResult(
                id,
                shopId,
                "테스트 Shop",
                salesChannelId,
                "테스트 판매채널",
                "test-account",
                "테스트 카테고리 프리셋",
                "카테고리 경로",
                "TEST_CATEGORY_CODE",
                now);
    }

    // ===== PageResult Fixtures =====

    public static CategoryPresetPageResult categoryPresetPageResult() {
        List<CategoryPresetResult> results =
                List.of(categoryPresetResult(1L), categoryPresetResult(2L));
        return CategoryPresetPageResult.of(results, 0, 20, 2L);
    }

    public static CategoryPresetPageResult categoryPresetPageResult(
            int page, int size, long totalCount) {
        List<CategoryPresetResult> results =
                List.of(categoryPresetResult(1L), categoryPresetResult(2L));
        return CategoryPresetPageResult.of(results, page, size, totalCount);
    }

    public static CategoryPresetPageResult emptyPageResult() {
        return CategoryPresetPageResult.of(List.of(), 0, 20, 0L);
    }

    // ===== DetailResult Fixtures =====

    public static CategoryPresetDetailResult categoryPresetDetailResult(Long id) {
        Instant now = CommonVoFixtures.now();
        CategoryPresetDetailResult.MappingCategory mappingCategory =
                new CategoryPresetDetailResult.MappingCategory("TEST_CAT_CODE", "카테고리 경로");
        List<CategoryPresetDetailResult.InternalCategory> internalCategories =
                List.of(
                        new CategoryPresetDetailResult.InternalCategory(100L, "내부 카테고리 A 경로"),
                        new CategoryPresetDetailResult.InternalCategory(200L, "내부 카테고리 B 경로"));
        return new CategoryPresetDetailResult(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                "테스트 카테고리 프리셋",
                mappingCategory,
                internalCategories,
                now,
                now);
    }
}
