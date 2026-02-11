package com.ryuqq.marketplace.adapter.in.rest.categorypreset;

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.DeleteCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.RegisterCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.UpdateCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.query.SearchCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetApiResponse;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import java.time.Instant;
import java.util.List;

/**
 * CategoryPreset API 테스트 Fixtures.
 *
 * <p>CategoryPreset REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CategoryPresetApiFixtures {

    private CategoryPresetApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final String DEFAULT_CATEGORY_CODE = "50000123";
    public static final String DEFAULT_PRESET_NAME = "테스트 카테고리 프리셋";
    public static final List<Long> DEFAULT_INTERNAL_CATEGORY_IDS = List.of(10L, 11L, 12L);

    // ===== RegisterCategoryPresetApiRequest =====

    public static RegisterCategoryPresetApiRequest registerRequest() {
        return new RegisterCategoryPresetApiRequest(
                DEFAULT_SHOP_ID,
                DEFAULT_PRESET_NAME,
                DEFAULT_CATEGORY_CODE,
                DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static RegisterCategoryPresetApiRequest registerRequest(
            Long shopId, String categoryCode, String presetName) {
        return new RegisterCategoryPresetApiRequest(
                shopId, presetName, categoryCode, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static RegisterCategoryPresetApiRequest registerRequestWithCategories(
            List<Long> internalCategoryIds) {
        return new RegisterCategoryPresetApiRequest(
                DEFAULT_SHOP_ID,
                DEFAULT_PRESET_NAME,
                DEFAULT_CATEGORY_CODE,
                internalCategoryIds);
    }

    // ===== UpdateCategoryPresetApiRequest =====

    public static UpdateCategoryPresetApiRequest updateRequest() {
        return new UpdateCategoryPresetApiRequest(
                "수정된 프리셋명", DEFAULT_CATEGORY_CODE, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static UpdateCategoryPresetApiRequest updateRequest(String presetName) {
        return new UpdateCategoryPresetApiRequest(
                presetName, DEFAULT_CATEGORY_CODE, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static UpdateCategoryPresetApiRequest updateRequestWithCategories(
            List<Long> internalCategoryIds) {
        return new UpdateCategoryPresetApiRequest(
                "수정된 프리셋명", DEFAULT_CATEGORY_CODE, internalCategoryIds);
    }

    // ===== DeleteCategoryPresetsApiRequest =====

    public static DeleteCategoryPresetsApiRequest deleteRequest() {
        return new DeleteCategoryPresetsApiRequest(List.of(1L, 2L, 3L));
    }

    public static DeleteCategoryPresetsApiRequest deleteRequest(List<Long> ids) {
        return new DeleteCategoryPresetsApiRequest(ids);
    }

    // ===== SearchCategoryPresetsApiRequest =====

    public static SearchCategoryPresetsApiRequest searchRequest() {
        return new SearchCategoryPresetsApiRequest(
                null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchCategoryPresetsApiRequest searchRequest(int page, int size) {
        return new SearchCategoryPresetsApiRequest(
                null, null, null, null, null, null, null, null, page, size);
    }

    public static SearchCategoryPresetsApiRequest searchRequest(
            List<Long> salesChannelIds, String searchField, String searchWord) {
        return new SearchCategoryPresetsApiRequest(
                salesChannelIds,
                null,
                searchField,
                searchWord,
                null,
                null,
                "createdAt",
                "DESC",
                0,
                20);
    }

    // ===== CategoryPresetResult (Application) =====

    public static CategoryPresetResult categoryPresetResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new CategoryPresetResult(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                DEFAULT_PRESET_NAME,
                "식품 > 과자 > 스낵 > 젤리",
                DEFAULT_CATEGORY_CODE,
                now);
    }

    public static CategoryPresetResult categoryPresetResult(Long id, String presetName) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new CategoryPresetResult(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "식품 > 과자 > 스낵 > 젤리",
                DEFAULT_CATEGORY_CODE,
                now);
    }

    public static List<CategoryPresetResult> categoryPresetResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                categoryPresetResult(
                                        (long) i, DEFAULT_PRESET_NAME + "_" + i))
                .toList();
    }

    public static CategoryPresetPageResult pageResult(int count, int page, int size) {
        List<CategoryPresetResult> results = categoryPresetResults(count);
        return CategoryPresetPageResult.of(results, page, size, (long) count);
    }

    public static CategoryPresetPageResult emptyPageResult() {
        return CategoryPresetPageResult.of(List.of(), 0, 20, 0L);
    }

    // ===== CategoryPresetApiResponse =====

    public static CategoryPresetApiResponse apiResponse(Long id) {
        return new CategoryPresetApiResponse(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                DEFAULT_PRESET_NAME,
                "식품 > 과자 > 스낵 > 젤리",
                DEFAULT_CATEGORY_CODE,
                "2025-02-10T10:30:00+09:00");
    }

    public static CategoryPresetApiResponse apiResponse(Long id, String presetName) {
        return new CategoryPresetApiResponse(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "식품 > 과자 > 스낵 > 젤리",
                DEFAULT_CATEGORY_CODE,
                "2025-02-10T10:30:00+09:00");
    }
}
