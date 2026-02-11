package com.ryuqq.marketplace.application.brandpreset;

import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * BrandPreset Query 테스트 Fixtures.
 *
 * <p>BrandPreset 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class BrandPresetQueryFixtures {

    private BrandPresetQueryFixtures() {}

    // ===== SearchParams Fixtures =====

    public static BrandPresetSearchParams searchParams() {
        return BrandPresetSearchParams.of(null, null, null, null, defaultCommonSearchParams());
    }

    public static BrandPresetSearchParams searchParams(int page, int size) {
        return BrandPresetSearchParams.of(null, null, null, null, commonSearchParams(page, size));
    }

    public static BrandPresetSearchParams searchParams(List<Long> salesChannelIds) {
        return BrandPresetSearchParams.of(
                salesChannelIds, null, null, null, defaultCommonSearchParams());
    }

    public static BrandPresetSearchParams searchParams(
            List<Long> salesChannelIds, List<String> statuses) {
        return BrandPresetSearchParams.of(
                salesChannelIds, statuses, null, null, defaultCommonSearchParams());
    }

    public static BrandPresetSearchParams searchParams(String searchField, String searchWord) {
        return BrandPresetSearchParams.of(
                null, null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static BrandPresetSearchParams searchParams(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            int page,
            int size) {
        return BrandPresetSearchParams.of(
                salesChannelIds, statuses, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static BrandPresetResult brandPresetResult(Long id) {
        Instant now = CommonVoFixtures.now();
        return new BrandPresetResult(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                "테스트 브랜드 프리셋",
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                now);
    }

    public static BrandPresetResult brandPresetResult(Long id, String presetName) {
        Instant now = CommonVoFixtures.now();
        return new BrandPresetResult(
                id,
                1L,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                now);
    }

    public static BrandPresetResult brandPresetResult(Long id, Long shopId, Long salesChannelId) {
        Instant now = CommonVoFixtures.now();
        return new BrandPresetResult(
                id,
                shopId,
                "테스트 Shop",
                salesChannelId,
                "테스트 판매채널",
                "test-account",
                "테스트 브랜드 프리셋",
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                now);
    }

    // ===== PageResult Fixtures =====

    public static BrandPresetPageResult brandPresetPageResult() {
        List<BrandPresetResult> results = List.of(brandPresetResult(1L), brandPresetResult(2L));
        return BrandPresetPageResult.of(results, 0, 20, 2L);
    }

    public static BrandPresetPageResult brandPresetPageResult(int page, int size, long totalCount) {
        List<BrandPresetResult> results = List.of(brandPresetResult(1L), brandPresetResult(2L));
        return BrandPresetPageResult.of(results, page, size, totalCount);
    }

    public static BrandPresetPageResult emptyPageResult() {
        return BrandPresetPageResult.of(List.of(), 0, 20, 0L);
    }
}
